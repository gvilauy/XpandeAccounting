package org.compiere.acct;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctConfig;
import org.xpande.financial.model.MZMPagoIdentProd;
import org.xpande.financial.model.MZMedioPago;
import org.xpande.financial.model.MZMedioPagoIdent;
import org.xpande.financial.model.MZPago;
import org.xpande.retail.model.*;

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

            MZPosVendor posVendor = (MZPosVendor) this.generaAstoVta.getZ_PosVendor();

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

                // Si este medio de pago tiene un identificador y el mismo esta configurado para que no se contabilice, no lo hago.
                if (sumMP.getZ_MedioPagoIdent_ID() > 0){
                    MZMedioPagoIdent medioPagoIdent = new MZMedioPagoIdent(getCtx(), sumMP.getZ_MedioPagoIdent_ID(), null);
                    if (!medioPagoIdent.isContabilizar()){
                        continue;
                    }
                }
                else{
                    // Si no tengo identificador, pero tengo medio de pago y el mismo esta marcado para que no se contabilice, no lo hago.
                    if (sumMP.getZ_MedioPago_ID() > 0){
                        MZMedioPago medioPago = new MZMedioPago(getCtx(), sumMP.getZ_MedioPago_ID(), null);
                        if (!medioPago.isContabilizar()){
                            continue;
                        }
                    }
                }

                int cCurrencyID = sumMP.getC_Currency_ID();
                BigDecimal amtMP = sumMP.getAmtTotal1();
                BigDecimal currencyRate = Env.ONE;
                if ((sumMP.getAmtTotal2() != null) && (sumMP.getAmtTotal2().compareTo(Env.ZERO) > 0)){
                    cCurrencyID = sumMP.getC_Currency_2_ID();
                    amtMP = sumMP.getAmtTotal2();
                    this.setIsMultiCurrency(true);
                    currencyRate = sumMP.getCurrencyRate();
                    if (currencyRate == null) currencyRate = Env.ONE;
                }

                // Obtengo info de cuenta contable a utilizar segun proveedor de pos
                if (posVendor.getValue().equalsIgnoreCase("SISTECO")){

                    // Obtengo contabilidad asociada al tipo de linea, si es que tiene.
                    sql = " select a.c_receivable_acct, a.c_bpartner_id, a.m_product_id, b.iscredito " +
                            " from z_sistecolinea_acct a " +
                            " inner join z_sistecotipolineapazos b on a.z_sistecotipolineapazos_id = b.z_sistecotipolineapazos_id " +
                            " where b.value ='" + sumMP.getCodTipoLineaPOS() + "' " +
                            " and a.c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                            " and a.c_currency_id =" + cCurrencyID;
                    pstmt = DB.prepareStatement(sql, null);
                    rs = pstmt.executeQuery();

                    int accountID = 0, cBpartnerID = 0, mProductID = 0;
                    boolean esDebito = true;

                    if (rs.next()){
                        accountID = rs.getInt("c_receivable_acct");
                        if (accountID <= 0){
                            p_Error = "No se indica Cuenta Contable para Tipo de Linea : " + sumMP.getCodTipoLineaPOS();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                        cBpartnerID = rs.getInt("c_bpartner_id");
                        mProductID = rs.getInt("m_product_id");
                        esDebito = (rs.getString("iscredito").equalsIgnoreCase("N")) ? true : false;
                    }
                    DB.close(rs, pstmt);

                    // Si no tengo cuenta contable por tipo de linea, la obtengo segun contabilidad de medio de pago
                    if (accountID <= 0){

                        // Busco Identificador segun codigo del medio de pago
                        int zMedioPagoID = 0, zMedioPagoIdentID = 0, zMPagoIdentProdID = 0;

                        sql = " select z_mediopago_id, z_mediopagoident_id, z_mpagoidentprod_id " +
                                " from z_sistecotipotarjeta " +
                                " where value ='" + sumMP.getCodMedioPagoPOS() + "'";
                        pstmt = DB.prepareStatement(sql, null);
                        rs = pstmt.executeQuery();

                        if (rs.next()){
                            zMedioPagoID = rs.getInt("z_mediopago_id");
                            zMedioPagoIdentID = rs.getInt("z_mediopagoident_id");
                            zMPagoIdentProdID = rs.getInt("z_mpagoidentprod_id");
                        }
                        DB.close(rs, pstmt);

                        if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                            if (zMedioPagoIdentID > 0){
                                MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                                cBpartnerID = pagoIdent.getC_BPartner_ID();
                                if (zMPagoIdentProdID > 0){
                                    MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                    mProductID = pagoIdentProd.getM_Product_ID();
                                }
                                // Cuenta contable del identificador si es que tengo
                                sql = " select mp_recibidos_acct " +
                                        " from z_mpagoident_acct " +
                                        " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                        " and c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                accountID = DB.getSQLValueEx(null, sql);
                            }
                            if (accountID <= 0){
                                if (zMedioPagoID > 0){
                                    // Cuenta contable directo del medio de pago
                                    sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                            " from z_mediopago_acct " +
                                            " where z_mediopago_id =" + zMedioPagoID +
                                            " and c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                                            " and c_currency_id =" + cCurrencyID;
                                    pstmt = DB.prepareStatement(sql, null);
                                    rs = pstmt.executeQuery();

                                    if (rs.next()){
                                        accountID = rs.getInt("mp_recibidos_acct");
                                        if (rs.getInt("c_bpartner_id") > 0){
                                            cBpartnerID = rs.getInt("c_bpartner_id");
                                        }
                                        if (rs.getInt("m_product_id") > 0){
                                            mProductID = rs.getInt("m_product_id");
                                        }
                                    }
                                    DB.close(rs, pstmt);
                                }
                            }
                        }
                        else {
                            // Obtengo cuenta segun medio de pago
                            sql = " select z_mediopago_id " +
                                    " from z_sistecomediopago " +
                                    " where value ='" + sumMP.getCodMedioPagoPOS() + "'";
                            zMedioPagoID = DB.getSQLValueEx(null, sql);

                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "No se indica Cuenta Contable para Tipo de Linea : " + sumMP.getCodTipoLineaPOS() + ", " +
                                    "Medio de Pago : " + sumMP.getCodMedioPagoPOS() + " - " + sumMP.getNomMedioPagoPOS();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }
                    if (esDebito){
                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, amtMP, null);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                            if (cBpartnerID > 0){
                                fl1.setC_BPartner_ID(cBpartnerID);
                            }
                            if (mProductID > 0){
                                fl1.setM_Product_ID(mProductID);
                            }
                            if (currencyRate.compareTo(Env.ONE) > 0){
                                fl1.set_ValueOfColumn("CurrencyRate", currencyRate);
                                //fl1.setAmtAcctDr(fl1.getAmtSourceDr().multiply(currencyRate).setScale(2, RoundingMode.HALF_UP));
                                fl1.setAmtAcctDr(sumMP.getAmtTotal1());
                            }
                        }
                    }
                    else{
                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, amtMP);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                            if (cBpartnerID > 0){
                                fl1.setC_BPartner_ID(cBpartnerID);
                            }
                            if (mProductID > 0){
                                fl1.setM_Product_ID(mProductID);
                            }
                            if (currencyRate.compareTo(Env.ONE) > 0){
                                fl1.set_ValueOfColumn("CurrencyRate", currencyRate);
                                //fl1.setAmtAcctCr(fl1.getAmtSourceCr().multiply(currencyRate).setScale(2, RoundingMode.HALF_UP));
                                fl1.setAmtAcctCr(sumMP.getAmtTotal1());
                            }
                        }
                    }

                }
                else if (posVendor.getValue().equalsIgnoreCase("SCANNTECH")){

                    // Busco Identificador segun codigo del medio de pago
                    int zMedioPagoID = 0, zMedioPagoIdentID = 0, zMPagoIdentProdID = 0;
                    int accountID = 0, cBpartnerID = 0, mProductID = 0;
                    boolean esDebito = true;

                    // Primero como Tipo de Crédito.
                    sql = " select z_mediopago_id, z_mediopagoident_id, z_mpagoidentprod_id" +
                            " from z_stechcreditos where value ='" + sumMP.getCodMedioPagoPOS() + "'";
                    pstmt = DB.prepareStatement(sql, null);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        zMedioPagoID = rs.getInt("z_mediopago_id");
                        zMedioPagoIdentID = rs.getInt("z_mediopagoident_id");
                        zMPagoIdentProdID = rs.getInt("z_mpagoidentprod_id");
                    }
                    DB.close(rs, pstmt);

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        // Obtengo cuenta segun medio de pago
                        sql = " select z_mediopago_id " +
                                " from z_stechmediopago " +
                                " where value ='" + sumMP.getCodMedioPagoPOS() + "'";
                        zMedioPagoID = DB.getSQLValueEx(null, sql);

                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + this.generaAstoVta.getC_AcctSchema_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);

                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "No se indica Cuenta Contable para Medio de Pago : " + sumMP.getCodMedioPagoPOS() + " - " + sumMP.getNomMedioPagoPOS();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }
                    if (esDebito){
                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, amtMP, null);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                            if (cBpartnerID > 0){
                                fl1.setC_BPartner_ID(cBpartnerID);
                            }
                            if (mProductID > 0){
                                fl1.setM_Product_ID(mProductID);
                            }
                            if (currencyRate.compareTo(Env.ONE) > 0){
                                fl1.set_ValueOfColumn("CurrencyRate", currencyRate);
                                //fl1.setAmtAcctDr(fl1.getAmtSourceDr().multiply(currencyRate).setScale(2, RoundingMode.HALF_UP));
                                fl1.setAmtAcctDr(sumMP.getAmtTotal1());
                            }
                        }
                    }
                    else{
                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, amtMP);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.generaAstoVta.getAD_Org_ID());
                            if (cBpartnerID > 0){
                                fl1.setC_BPartner_ID(cBpartnerID);
                            }
                            if (mProductID > 0){
                                fl1.setM_Product_ID(mProductID);
                            }
                            if (currencyRate.compareTo(Env.ONE) > 0){
                                fl1.set_ValueOfColumn("CurrencyRate", currencyRate);
                                //fl1.setAmtAcctCr(fl1.getAmtSourceCr().multiply(currencyRate).setScale(2, RoundingMode.HALF_UP));
                                fl1.setAmtAcctCr(sumMP.getAmtTotal1());
                            }
                        }
                    }
                }
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
