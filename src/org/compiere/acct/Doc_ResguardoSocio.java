package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.financial.model.MZResguardoSocio;
import org.xpande.financial.model.MZResguardoSocioRet;
import org.xpande.financial.model.MZRetencionSocio;
import org.xpande.financial.model.MZRetencionSocioAcct;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Contabilización de Documentos: Resguardos emitidos, contra-resguardos emitidos.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/27/18.
 */
public class Doc_ResguardoSocio extends Doc {

    private MZResguardoSocio resguardoSocio = null;

    /**
     *  Constructor
     */
    public Doc_ResguardoSocio(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_ResguardoSocio(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZResguardoSocio.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.resguardoSocio = (MZResguardoSocio) getPO();
        setDateDoc(resguardoSocio.getDateDoc());
        setDateAcct(resguardoSocio.getDateAcct());

        setPeriod();

        setC_Currency_ID(resguardoSocio.getC_Currency_ID());
        setC_BPartner_ID(resguardoSocio.getC_BPartner_ID());
        setAmount(Doc.AMTTYPE_Gross, resguardoSocio.getTotalAmt());

        MDocType docType = (MDocType) resguardoSocio.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines(resguardoSocio);

        log.fine("Lines=" + p_lines.length);
        return null;

    }

    @Override
    public BigDecimal getBalance() {

        BigDecimal retValue = Env.ZERO;
        StringBuffer sb = new StringBuffer (" [");

        //  Total
        retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
        sb.append(getAmount(Doc.AMTTYPE_Gross));

        //  - Header Charge
        retValue = retValue.subtract(getAmount(Doc.AMTTYPE_Charge));
        sb.append("-").append(getAmount(Doc.AMTTYPE_Charge));

        //  Lineas
        for (int i = 0; i < p_lines.length; i++)
        {
            retValue = retValue.subtract(p_lines[i].getAmtSource());
            sb.append("-").append(p_lines[i].getAmtSource());
        }
        sb.append("]");

        log.fine(toString() + " Balance=" + retValue + sb.toString());
        return retValue;
    }


    /***
     * Resguardo: DR - Total del resguardo. Cuenta contable V_Liability del Socio de Negocio
     *          : CR - Importe de cada linea de retención del resguardo. Cuenta asociada a la Retención.
     * Contra-Resguardo : Al contrario del Resguardo.
     * Xpande. Created by Gabriel Vila on 8/28/18.
     * @param as accounting schema
     * @return
     */
    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);


        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        // Cuando es un Resguardo: DR
        // Cuando es un ContraResguardo: CR
        // Total del resguardo - Cuenta contable del socio de negocio
        if (grossAmt.signum() != 0){
            int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);

            if (this.getDocumentType().equalsIgnoreCase("RGU")){
                fact.createLine(null, MAccount.get(getCtx(), payables_ID), getC_Currency_ID(), grossAmt, null);
            }
            else{
                fact.createLine(null, MAccount.get(getCtx(), payables_ID), getC_Currency_ID(), null, grossAmt);
            }
        }

        // CR - Lineas de retenciones aplicadas en este resguardo - Monto de cada linea - Cuenta contable asociada a la retencion.
        for (int i = 0; i < p_lines.length; i++)
        {
            MZResguardoSocioRet ret = new MZResguardoSocioRet(getCtx(), p_lines[i].get_ID(), this.getTrxName());
            MZRetencionSocio retencionSocio = (MZRetencionSocio) ret.getZ_RetencionSocio();
            MZRetencionSocioAcct retAcct = retencionSocio.getAcct();
            MAccount acctRetCr = MAccount.get(getCtx(), retAcct.getRT_RetencionEmitida_Acct());

            BigDecimal amt = p_lines[i].getAmtSource();

            FactLine fl1 = null;

            if (this.getDocumentType().equalsIgnoreCase("RGU")){
                fl1 = fact.createLine (p_lines[i], acctRetCr, getC_Currency_ID(), null, amt);
            }
            else{
                fl1 = fact.createLine (p_lines[i], acctRetCr, getC_Currency_ID(), amt, null);
            }

            // Detalle de asiento
            if (fl1 != null){
                fl1.setAD_Org_ID(this.resguardoSocio.getAD_Org_ID());
                fl1.saveEx();
                MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                factDet.setFact_Acct_ID(fl1.get_ID());
                factDet.setAD_Org_ID(this.resguardoSocio.getAD_Org_ID());
                factDet.setZ_ResguardoSocio_ID(this.resguardoSocio.get_ID());
                factDet.setZ_RetencionSocio_ID(retencionSocio.get_ID());
                factDet.saveEx();
            }
        }

        facts.add(fact);
        return facts;
    }


    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 8/28/18.
     * @param resguardoSocio
     * @return
     */
    private DocLine[] loadLines (MZResguardoSocio resguardoSocio)
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZResguardoSocioRet> rets = resguardoSocio.getResguardoRets();

        for (MZResguardoSocioRet ret: rets){

            MZRetencionSocio retencionSocio = (MZRetencionSocio) ret.getZ_RetencionSocio();
            //MAccount account =

            DocLine docLine = new DocLine(ret, this);
            docLine.setAmount(ret.getAmtRetencion());
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }

}
