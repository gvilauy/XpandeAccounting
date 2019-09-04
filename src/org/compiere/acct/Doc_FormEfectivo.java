package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.model.MZDifCambio;
import org.xpande.acct.model.MZDifCambioLin;
import org.xpande.retail.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Contabilizacion de formularios de movimientos de efectivo en Retail.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/30/19.
 */
public class Doc_FormEfectivo extends Doc {

    private MZFormEfectivo formEfectivo = null;
    private MDocType docType = null;

    /**
     *  Constructor
     */
    public Doc_FormEfectivo(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_FormEfectivo(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZFormEfectivo.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.formEfectivo = (MZFormEfectivo) getPO();

        setDateDoc(this.formEfectivo.getDateDoc());
        setDateAcct(this.formEfectivo.getDateAcct());

        this.docType = (MDocType) this.formEfectivo.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        log.fine("Lines=" + p_lines.length);

        return null;
    }

    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 8/28/18.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZFormEfectivoLin> formEfectivoLinList = this.formEfectivo.getLines();

        for (MZFormEfectivoLin formEfectivoLin: formEfectivoLinList){
            DocLine docLine = new DocLine(formEfectivoLin, this);
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }


    @Override
    public BigDecimal getBalance() {

        return Env.ZERO;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        // Configuracion de retail
        MZRetailConfig retailConfig = MZRetailConfig.getDefault(getCtx(), null);

        // Recorro lineas del documento a contabilizar
        for (int i = 0; i < p_lines.length; i++){

            // Modelo de linea
            MZFormEfectivoLin formEfectivoLin = new MZFormEfectivoLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            // MONEDA UNO
            if ((formEfectivoLin.getAmtSubtotal1() != null) && (formEfectivoLin.getAmtSubtotal1().compareTo(Env.ZERO) != 0)){

                // Obtengo cuenta contable del concepto de esta linea para moneda uno
                MZRetailConfigForEfe configForEfe = (MZRetailConfigForEfe) formEfectivoLin.getZ_RetailConfigForEfe();
                X_Z_RetailConfForEfe_Acct confForEfe_acct = configForEfe.getAccountConfig(this.formEfectivo.getAD_Org_ID(), as.get_ID(), formEfectivoLin.getC_Currency_ID());

                // Si tengo cuenta (no es obligatoria)
                if ((confForEfe_acct != null) && (confForEfe_acct.get_ID() > 0)){

                    MAccount acctMoneda1 = MAccount.get(getCtx(), confForEfe_acct.getAccount_Acct());

                    // Al debe o haber según configuracion contable del concepto (importe moneda 1)
                    FactLine f1 = null;
                    if (confForEfe_acct.isDebito()){
                        f1 = fact.createLine(p_lines[i], acctMoneda1, formEfectivoLin.getC_Currency_ID(), formEfectivoLin.getAmtSubtotal1(), null);
                    }
                    else{
                        f1 = fact.createLine(p_lines[i], acctMoneda1, formEfectivoLin.getC_Currency_ID(), null, formEfectivoLin.getAmtSubtotal1());
                    }
                    if (f1 != null){
                        f1.setAD_Org_ID(this.formEfectivo.getAD_Org_ID());
                    }
                }
            }

            // MONEDA DOS
            if ((formEfectivoLin.getAmtSubtotal2() != null) && (formEfectivoLin.getAmtSubtotal2().compareTo(Env.ZERO) != 0)){

                // Obtengo cuenta contable del concepto de esta linea para moneda dos
                MZRetailConfigForEfe configForEfe = (MZRetailConfigForEfe) formEfectivoLin.getZ_RetailConfigForEfe();
                X_Z_RetailConfForEfe_Acct confForEfe_acct = configForEfe.getAccountConfig(this.formEfectivo.getAD_Org_ID(), as.get_ID(), formEfectivoLin.getC_Currency_2_ID());

                // Si tengo cuenta (no es obligatoria)
                if ((confForEfe_acct != null) && (confForEfe_acct.get_ID() > 0)){

                    MAccount acctMoneda2 = MAccount.get(getCtx(), confForEfe_acct.getAccount_Acct());

                    // Al debe o haber según configuracion contable del concepto (importe moneda 2)
                    FactLine f1 = null;
                    if (confForEfe_acct.isDebito()){
                        f1 = fact.createLine(p_lines[i], acctMoneda2, formEfectivoLin.getC_Currency_2_ID() , formEfectivoLin.getAmtSubtotal2(), null);
                    }
                    else{
                        f1 = fact.createLine(p_lines[i], acctMoneda2, formEfectivoLin.getC_Currency_2_ID(), null, formEfectivoLin.getAmtSubtotal2());
                    }
                    if (f1 != null){
                        f1.setAD_Org_ID(this.formEfectivo.getAD_Org_ID());
                    }
                }
            }
        }

        // Cuenta de balanceo en ambas monedas
        if (this.formEfectivo.getAmtBalanceo().compareTo(Env.ZERO) != 0){

            X_Z_RetailForEfe_Acct forEfeAcct = retailConfig.getFormEfectivoAcct(as.get_ID(), 142);
            if ((forEfeAcct != null) && (forEfeAcct.get_ID() > 0)){

                MAccount acctMoneda1 = MAccount.get(getCtx(), forEfeAcct.getP_Revenue_Acct());

                BigDecimal amtBalanceo = this.formEfectivo.getAmtBalanceo();

                FactLine f1 = null;
                if (this.formEfectivo.getAmtBalanceo().compareTo(Env.ZERO) < 0){
                    amtBalanceo = this.formEfectivo.getAmtBalanceo().negate();
                    f1 = fact.createLine(null, acctMoneda1, 142,  null, amtBalanceo);
                }
                else {
                    f1 = fact.createLine(null, acctMoneda1, 142, this.formEfectivo.getAmtBalanceo(), null);
                }
                if (f1 != null){
                    f1.setAD_Org_ID(this.formEfectivo.getAD_Org_ID());
                }
            }

        }
        if (this.formEfectivo.getAmtBalanceo2().compareTo(Env.ZERO) != 0){

            X_Z_RetailForEfe_Acct forEfeAcct = retailConfig.getFormEfectivoAcct(as.get_ID(), 100);
            if ((forEfeAcct != null) && (forEfeAcct.get_ID() > 0)){

                MAccount acctMoneda2 = MAccount.get(getCtx(), forEfeAcct.getP_Revenue_Acct());

                BigDecimal amtBalanceo2 = this.formEfectivo.getAmtBalanceo2();

                FactLine f1 = null;
                if (this.formEfectivo.getAmtBalanceo2().compareTo(Env.ZERO) < 0){
                    amtBalanceo2 = this.formEfectivo.getAmtBalanceo2().negate();
                    f1 = fact.createLine(null, acctMoneda2, 100,  null, amtBalanceo2);
                }
                else {
                    f1 = fact.createLine(null, acctMoneda2, 100, this.formEfectivo.getAmtBalanceo2(), null);
                }
                if (f1 != null){
                    f1.setAD_Org_ID(this.formEfectivo.getAD_Org_ID());
                }
            }
        }

        // Diferencias en ambas monedas
        if (this.formEfectivo.getDifferenceAmt().compareTo(Env.ZERO) != 0){

            X_Z_RetailForEfe_Acct forEfeAcct = retailConfig.getFormEfectivoAcct(as.get_ID(), 142);
            if ((forEfeAcct != null) && (forEfeAcct.get_ID() > 0)){

                MAccount acctMoneda1 = MAccount.get(getCtx(), forEfeAcct.getP_Revenue_Acct());

                BigDecimal amtDif = this.formEfectivo.getDifferenceAmt();

                FactLine f1 = null;
                if (this.formEfectivo.getDifferenceAmt().compareTo(Env.ZERO) < 0){
                    amtDif = this.formEfectivo.getDifferenceAmt().negate();
                    f1 = fact.createLine(null, acctMoneda1, 142,  null, amtDif);
                }
                else {
                    f1 = fact.createLine(null, acctMoneda1, 142, this.formEfectivo.getDifferenceAmt(), null);
                }
                if (f1 != null){
                    f1.setAD_Org_ID(this.formEfectivo.getAD_Org_ID());
                }
            }

        }
        if (this.formEfectivo.getDifferenceAmt2().compareTo(Env.ZERO) != 0){

            X_Z_RetailForEfe_Acct forEfeAcct = retailConfig.getFormEfectivoAcct(as.get_ID(), 100);
            if ((forEfeAcct != null) && (forEfeAcct.get_ID() > 0)){

                MAccount acctMoneda2 = MAccount.get(getCtx(), forEfeAcct.getP_Revenue_Acct());

                BigDecimal amtDif2 = this.formEfectivo.getDifferenceAmt2();

                FactLine f1 = null;
                if (this.formEfectivo.getDifferenceAmt2().compareTo(Env.ZERO) < 0){
                    amtDif2 = this.formEfectivo.getDifferenceAmt2().negate();
                    f1 = fact.createLine(null, acctMoneda2, 100,  null, amtDif2);
                }
                else {
                    f1 = fact.createLine(null, acctMoneda2, 100, this.formEfectivo.getDifferenceAmt2(), null);
                }
                if (f1 != null){
                    f1.setAD_Org_ID(this.formEfectivo.getAD_Org_ID());
                }
            }
        }

        facts.add(fact);
        return facts;
    }
}
