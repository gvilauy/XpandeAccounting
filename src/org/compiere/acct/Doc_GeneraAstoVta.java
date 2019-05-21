package org.compiere.acct;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctConfig;
import org.xpande.financial.model.MZPago;
import org.xpande.retail.model.MZGeneraAstoVta;
import org.xpande.retail.model.MZGeneraAstoVtaSumMP;
import org.xpande.retail.model.MZGeneraAstoVtaSumTax;
import org.xpande.retail.model.MZRetailConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Contabilización de documentos: Generación de Asientos de Venta POS.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 5/14/19.
 */
public class Doc_GeneraAstoVta extends Doc {

    private MZGeneraAstoVta generaAstoVta = null;
    private MDocType docType = null;

    /**
     *  Constructor
     */
    public Doc_GeneraAstoVta(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_GeneraAstoVta(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZGeneraAstoVta.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.generaAstoVta = (MZGeneraAstoVta) getPO();
        setDateDoc(this.generaAstoVta.getDateDoc());
        setDateAcct(this.generaAstoVta.getDateAcct());

        this.docType = (MDocType) this.generaAstoVta.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        return null;
    }

    @Override
    public BigDecimal getBalance() {
        return Env.ZERO;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);


        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MTax taxCreditos = null;
            BigDecimal totalTaxAmtCreditos = Env.ZERO, totalTaxBaseAmtCreditos = Env.ZERO;
            int creditosAccountID = 0, creditosBaseAccountID = 0;

            MZRetailConfig retailConfig = MZRetailConfig.getDefault(getCtx(), null);
            if ((retailConfig == null) || (retailConfig.get_ID() <= 0)){
                p_Error = "Falta parametrización en Configuracion de Retail";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            // DR - Cuentas de Medios de Pago
            List<MZGeneraAstoVtaSumMP> sumMPList = this.generaAstoVta.getLineasMediosPago();
            for (MZGeneraAstoVtaSumMP sumMP: sumMPList){

                int cCurrencyID = sumMP.getC_Currency_ID();
                BigDecimal amtMP = sumMP.getAmtTotal1();
                if ((sumMP.getAmtTotal2() != null) && (sumMP.getAmtTotal2().compareTo(Env.ZERO) > 0)){
                    cCurrencyID = sumMP.getC_Currency_2_ID();
                    amtMP = sumMP.getAmtTotal2();
                    this.setIsMultiCurrency(true);
                }

                // Obtengo info de este tipo de linea
                sql = " select a.c_receivable_acct, a.c_bpartner_id, a.m_product_id, b.iscredito " +
                        " from z_sistecolinea_acct a " +
                        " inner join z_sistecotipolineapazos b on a.z_sistecotipolineapazos_id = b.z_sistecotipolineapazos_id " +
                        " where b.value ='" + sumMP.getCodTipoLineaPOS() + "' " +
                        " and a.c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                        " and a.c_currency_id =" + cCurrencyID;

                pstmt = DB.prepareStatement(sql, null);
                rs = pstmt.executeQuery();

                if (rs.next()){

                    int accountID = rs.getInt("c_receivable_acct");
                    if (accountID <= 0){
                        p_Error = "No se indica Contabilidad para Tipo de Linea : " + sumMP.getCodTipoLineaPOS();
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }

                    if (rs.getString("iscredito").equalsIgnoreCase("N")){
                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, amtMP, null);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                        }
                    }
                    else{

                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, amtMP);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                        }

                        creditosBaseAccountID = accountID;
                        totalTaxBaseAmtCreditos = totalTaxBaseAmtCreditos.add(amtMP);

                        // Cuando este medio de pago va al crédito en el asiento de ventas, debo obtener valor de impuesto según tasa parametrizada
                        // en configuraciones de retail. Este monto lo imputo a la cuenta de venta de dicha tasa de impuesto.
                        if (retailConfig.getC_Tax_ID() <= 0){
                            p_Error = "No se indica Tasa de Impuestos para Cŕeditos en Asientos de Venta, en configuración de retail";
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }

                        if (taxCreditos == null){
                            taxCreditos = (MTax) retailConfig.getC_Tax();
                        }
                        if (creditosAccountID <= 0){
                            sql = " select t_due_acct from c_tax_acct where c_tax_id =" + retailConfig.getC_Tax_ID();
                            creditosAccountID = DB.getSQLValueEx(null, sql);
                            if (creditosAccountID <= 0){

                                p_Error = "No se indica Cuenta Contable de Venta de Tasa de Impuesto : " + taxCreditos.getName();
                                log.log(Level.SEVERE, p_Error);
                                fact = null;
                                facts.add(fact);
                                return facts;
                            }
                        }

                        BigDecimal taxAmt = amtMP.multiply(taxCreditos.getRate().divide(Env.ONEHUNDRED, 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);

                        FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), creditosAccountID), cCurrencyID, null, taxAmt);
                        if (fl2 != null){
                            fl2.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                        }

                        totalTaxAmtCreditos = totalTaxAmtCreditos.add(taxAmt);
                    }

                }
                else{
                    p_Error = "No se indica Contabilidad para Tipo de Linea : " + sumMP.getCodTipoLineaPOS();
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }

                DB.close(rs, pstmt);
            }

            // CR - Cuentas de Impuestos
            List<MZGeneraAstoVtaSumTax> sumTaxList = this.generaAstoVta.getLineasImpuestos();
            for (MZGeneraAstoVtaSumTax sumTax: sumTaxList){

                MTaxCategory taxCategory = (MTaxCategory) sumTax.getC_TaxCategory();

                // CUENTA DE VENTA POR MONTO BASE DE IMPUESTO

                if (sumTax.getTaxBaseAmt().compareTo(Env.ZERO) > 0){

                    sql = " select p_revenue_acct " +
                            " from z_retailconfigprodacct " +
                            " where z_retailconfig_id =" + retailConfig.get_ID() +
                            " and c_taxcategory_id =" + taxCategory.get_ID();
                    int accountVtasID = DB.getSQLValueEx(null, sql);
                    if (accountVtasID <= 0){
                        p_Error = "No se indica Cuenta Contable de Venta para Impuesto en configuraciones de retail : " + taxCategory.getName();
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }

                    BigDecimal taxBaseAmt = sumTax.getTaxBaseAmt();
                    if (accountVtasID == creditosBaseAccountID){

                        // DR- Base impuesto (lo doy vuelta)
                        FactLine fl3 = fact.createLine(null, MAccount.get(getCtx(), accountVtasID), getC_Currency_ID(), totalTaxBaseAmtCreditos, null);
                        if (fl3 != null){
                            fl3.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                        }

                        taxBaseAmt = taxBaseAmt.subtract(totalTaxBaseAmtCreditos);
                    }

                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountVtasID), getC_Currency_ID(), null, taxBaseAmt);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                    }
                }

                // CUENTA DE IMPUESTO POR MONTO DE IMPUESTO

                if (sumTax.getTaxAmt().compareTo(Env.ZERO) > 0){

                    sql = " select a.t_due_acct " +
                            " from c_tax_acct a " +
                            " inner join c_tax t on a.c_tax_id = t.c_tax_id " +
                            " inner join c_taxcategory b on t.c_taxcategory_id = b.c_taxcategory_id and t.isdefault='Y' " +
                            " where t.c_taxcategory_id =" + sumTax.getC_TaxCategory_ID() +
                            " and a.c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                            " order by t.updated desc ";

                    int accountID = DB.getSQLValueEx(null, sql);
                    if (accountID <= 0){
                        p_Error = "No se indica Cuenta Contable de Venta para Impuesto : " + taxCategory.getName();
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }

                    BigDecimal taxAmt = sumTax.getTaxAmt();
                    if (accountID == creditosAccountID){
                        taxAmt = taxAmt.subtract(totalTaxAmtCreditos);
                    }

                    FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, taxAmt);
                    if (fl2 != null){
                        fl2.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                    }


                }

            }

            // Redondeo
            if ((this.generaAstoVta.getAmtRounding() != null) && (this.generaAstoVta.getAmtRounding().compareTo(Env.ZERO) != 0)){
                boolean isDebit = false;
                BigDecimal amtRounding = this.generaAstoVta.getAmtRounding();
                if (amtRounding.compareTo(Env.ZERO) < 0){
                    isDebit = true;
                    amtRounding = amtRounding.negate();
                }

                // Cuenta de Redondeo segun cargo parametrizado en configuraciones contables
                MZAcctConfig acctConfig = MZAcctConfig.getDefault(getCtx(), null);
                if ((acctConfig == null) || (acctConfig.get_ID() <= 0)){
                    p_Error = "Falta parametrizaciones en Configuraciones Contables";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }
                if (acctConfig.getCharge_Rounding_ID() <= 0){
                    p_Error = "Falta indicar cargo por concepto de Redondeo en Configuraciones Contables";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }

                sql = " select CH_Revenue_Acct  " +
                        " from C_Charge_Acct  " +
                        " where C_Charge_ID =" + acctConfig.getCharge_Rounding_ID() +
                        " and C_AcctSchema_ID =" + this.generaAstoVta.getC_AcctSchema_ID();
                int accountID = DB.getSQLValueEx(null, sql);

                if (accountID <= 0){
                    p_Error = "Falta indicar cuenta contable para cargo de redondeo.";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }

                if (isDebit){
                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), amtRounding, null);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                    }
                }
                else{
                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, amtRounding);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                    }
                }
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }

        facts.add(fact);
        return facts;
    }

}
