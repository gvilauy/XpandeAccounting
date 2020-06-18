package org.compiere.acct;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
import org.compiere.model.MElementValue;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctCierre;
import org.xpande.acct.model.MZAcctCierreLin;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/17/20.
 */
public class Doc_AcctCierre extends Doc {

    private MZAcctCierre acctCierre = null;

    /**
     *  Constructor
     */
    public Doc_AcctCierre(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_AcctCierre(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZAcctCierre.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.acctCierre = (MZAcctCierre) getPO();
        setDateDoc(acctCierre.getDateDoc());
        setDateAcct(acctCierre.getDateAcct());

        setPeriod();

        MAcctSchema acctSchema = (MAcctSchema) this.acctCierre.getC_AcctSchema();
        setC_Currency_ID(acctSchema.getC_Currency_ID());

        MDocType docType = (MDocType) this.acctCierre.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = this.loadLines();

        log.fine("Lines=" + p_lines.length);
        return null;
    }

    @Override
    public BigDecimal getBalance() {

        BigDecimal retValue = Env.ZERO;
        StringBuffer sb = new StringBuffer (" [");

        //  Total

        // Para cierre de cuentas diferenciales, el total es el resultado del ejercicio
        if (this.acctCierre.getDocBaseType().equalsIgnoreCase("CJD")){
            if (this.acctCierre.getTotalAmt().compareTo(Env.ZERO) > 0){
                retValue = retValue.subtract(this.acctCierre.getTotalAmt());
            }
            else{
                retValue = retValue.add(this.acctCierre.getTotalAmt().negate());
            }

            sb.append(this.acctCierre.getTotalAmt());
        }

        //  Lineas
        for (int i = 0; i < p_lines.length; i++)
        {
            MZAcctCierreLin acctCierreLin = new MZAcctCierreLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());
            BigDecimal amtLinea = acctCierreLin.getAmtAcctDrTo().subtract(acctCierreLin.getAmtAcctCrTo());
            retValue = retValue.add((amtLinea));
            sb.append("-").append(amtLinea);
        }
        sb.append("]");

        log.fine(toString() + " Balance=" + retValue + sb.toString());
        return retValue;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        // Para cierre de cuentas integrales, habilito el multi-moneda
        if (this.acctCierre.getDocBaseType().equalsIgnoreCase("CJI")){
            this.setIsMultiCurrency(true);
        }

        // DR - CR : Lineas de asiento de cierre
        for (int i = 0; i < p_lines.length; i++)
        {
            MZAcctCierreLin acctCierreLin = new MZAcctCierreLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            BigDecimal amtDR =  acctCierreLin.getAmtAcctDrTo();
            BigDecimal amtCR = acctCierreLin.getAmtAcctCrTo();

            // Para cierre de cuentas inegrales, los montos y moneda dependen de la cuenta
            if (this.acctCierre.getDocBaseType().equalsIgnoreCase("CJI")){
                amtDR = acctCierreLin.getAmtSourceDrTo();
                amtCR = acctCierreLin.getAmtSourceCrTo();
                this.setC_Currency_ID(acctCierreLin.getC_Currency_ID());
            }

            MAccount acctLin = MAccount.getByAccount(getCtx(), this.acctCierre.getAD_Client_ID(), acctCierreLin.getC_ElementValue_ID(), null);
            if ((acctLin == null) || (acctLin.get_ID() <= 0)){
                MElementValue elementValue = (MElementValue) acctCierreLin.getC_ElementValue();
                p_Error = "No se pudo obtener Combinacion Contable para cuenta : " + elementValue.getValue() + " - " + elementValue.getName();
                log.log(Level.SEVERE, p_Error);
                return facts;
            }

            FactLine fl1 = null;
            if (amtDR.compareTo(Env.ZERO) != 0){
                fact.createLine (p_lines[i], acctLin, getC_Currency_ID(), amtDR, null);
            }
            else if (amtCR.compareTo(Env.ZERO) != 0){
                fact.createLine (p_lines[i], acctLin, getC_Currency_ID(), null, amtCR);
            }

            if (fl1 != null){
                fl1.setAD_Org_ID(this.acctCierre.getAD_Org_ID());
            }
        }

        // Resultado del ejercicio en cierre de cuentas diferenciales
        if (this.acctCierre.getDocBaseType().equalsIgnoreCase("CJD")){

            int resultadoEjeID = getValidCombination_ID (Doc.ACCTYPE_CJ_ResEjercicio, as);
            if (resultadoEjeID <= 0){
                p_Error = "Falta parametrizar Cuenta Contable para Resultado del Ejercicio en Esquema Contable por Defecto.";
                log.log(Level.SEVERE, p_Error);
                return facts;
            }

            FactLine fl2 = null;
            // DR o CR según signo del monto de resultado del ejercicio
            if (this.acctCierre.getTotalAmt().compareTo(Env.ZERO) < 0){
                fl2 = fact.createLine (null, MAccount.get(getCtx(), resultadoEjeID), getC_Currency_ID(), this.acctCierre.getTotalAmt().negate(), null);
            }
            else{
                fl2 = fact.createLine (null, MAccount.get(getCtx(), resultadoEjeID), getC_Currency_ID(), null, this.acctCierre.getTotalAmt());
            }

            if (fl2 != null){
                fl2.setAD_Org_ID(this.acctCierre.getAD_Org_ID());
            }
        }

        facts.add(fact);
        return facts;

    }

    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 6/17/20.
     * @param resguardoSocio
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZAcctCierreLin> cierreLinList = this.acctCierre.getLines();

        for (MZAcctCierreLin acctCierreLin: cierreLinList){

            DocLine docLine = new DocLine(acctCierreLin, this);
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }

}
