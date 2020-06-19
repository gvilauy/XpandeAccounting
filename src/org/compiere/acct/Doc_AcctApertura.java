package org.compiere.acct;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
import org.compiere.model.MElementValue;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctApertura;
import org.xpande.acct.model.MZAcctAperturaLin;
import org.xpande.acct.model.MZAcctCierre;
import org.xpande.acct.model.MZAcctCierreLin;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/18/20.
 */
public class Doc_AcctApertura extends Doc{

    private MZAcctApertura acctApertura = null;

    /**
     *  Constructor
     */
    public Doc_AcctApertura(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_AcctApertura(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZAcctApertura.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.acctApertura = (MZAcctApertura) getPO();
        setDateDoc(acctApertura.getDateDoc());
        setDateAcct(acctApertura.getDateAcct());

        setPeriod();

        MAcctSchema acctSchema = (MAcctSchema) this.acctApertura.getC_AcctSchema();
        setC_Currency_ID(acctSchema.getC_Currency_ID());

        MDocType docType = (MDocType) this.acctApertura.getC_DocType();
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

        //  Lineas
        for (int i = 0; i < p_lines.length; i++)
        {
            MZAcctAperturaLin acctAperturaLin = new MZAcctAperturaLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());
            BigDecimal amtLinea = acctAperturaLin.getAmtAcctDr().subtract(acctAperturaLin.getAmtAcctCr());
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

        // Habilito el multi-moneda
        this.setIsMultiCurrency(true);

        // DR - CR : Lineas de asiento de cierre
        for (int i = 0; i < p_lines.length; i++)
        {
            MZAcctAperturaLin acctAperturaLin = new MZAcctAperturaLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            BigDecimal amtDR =  acctAperturaLin.getAmtSourceDr();
            BigDecimal amtCR = acctAperturaLin.getAmtSourceCr();
            this.setC_Currency_ID(acctAperturaLin.getC_Currency_ID());

            MAccount acctLin = MAccount.getByAccount(getCtx(), this.acctApertura.getAD_Client_ID(), acctAperturaLin.getC_ElementValue_ID(), null);
            if ((acctLin == null) || (acctLin.get_ID() <= 0)){
                MElementValue elementValue = (MElementValue) acctAperturaLin.getC_ElementValue();
                p_Error = "No se pudo obtener Combinacion Contable para cuenta : " + elementValue.getValue() + " - " + elementValue.getName();
                log.log(Level.SEVERE, p_Error);
                return facts;
            }

            FactLine fl1 = null;
            if (amtDR.compareTo(Env.ZERO) != 0){
                fl1 = fact.createLine (p_lines[i], acctLin, getC_Currency_ID(), amtDR, null);
            }
            else if (amtCR.compareTo(Env.ZERO) != 0){
                fl1 = fact.createLine (p_lines[i], acctLin, getC_Currency_ID(), null, amtCR);
            }

            if (fl1 != null){
                fl1.setAD_Org_ID(this.acctApertura.getAD_Org_ID());
                fl1.setAmtAcctDr(acctAperturaLin.getAmtAcctDr());
                fl1.setAmtAcctCr(acctAperturaLin.getAmtAcctCr());

                if (acctAperturaLin.getC_BPartner_ID() > 0){
                    fl1.setC_BPartner_ID(acctAperturaLin.getC_BPartner_ID());
                }

                if ((acctAperturaLin.getCurrencyRate() != null) && (acctAperturaLin.getCurrencyRate().compareTo(Env.ZERO) > 0)){
                    fl1.set_ValueOfColumn("CurrencyRate", acctAperturaLin.getCurrencyRate());
                }

                fl1.saveEx();
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

        List<MZAcctAperturaLin> aperturaLinList = this.acctApertura.getLines();

        for (MZAcctAperturaLin acctAperturaLin: aperturaLinList){

            DocLine docLine = new DocLine(acctAperturaLin, this);
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }

}
