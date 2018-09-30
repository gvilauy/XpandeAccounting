package org.compiere.acct;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
import org.compiere.util.Env;
import org.xpande.financial.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Contabilización de documentos: Pagos y Cobranzas.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/30/18.
 */
public class Doc_Pago extends Doc {

    private MZPago pago = null;
    private MDocType docType = null;
    private BigDecimal amtMediosPago = Env.ZERO;

    /**
     *  Constructor
     */
    public Doc_Pago(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_Pago(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZPago.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.pago = (MZPago) getPO();
        setDateDoc(pago.getDateDoc());
        setDateAcct(pago.getDateDoc());
        setC_Currency_ID(pago.getC_Currency_ID());
        setC_BPartner_ID(pago.getC_BPartner_ID());


        this.docType = (MDocType) pago.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        // Total del documento es igual al total de medios de pago, ya que para Pagos no se consideraran Resguardos en este asiento.
        //setAmount(Doc.AMTTYPE_Gross, pago.getPayAmt());
        setAmount(Doc.AMTTYPE_Gross, this.amtMediosPago);

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

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        // Contabilización de un PAGO
        if (!this.pago.isSOTrx()){

            // DR : Monto total del Pago - Cuenta del Socio de Negocio
            int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
            fact.createLine(null, MAccount.get(getCtx(), payables_ID), getC_Currency_ID(), grossAmt, null);

            // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria.
            for (int i = 0; i < p_lines.length; i++)
            {
                BigDecimal amt = p_lines[i].getAmtSource();
                fact.createLine (p_lines[i], getAccount(Doc.ACCTTYPE_BankInTransit, as), getC_Currency_ID(), null, amt);
            }
        }
        else{
            // Contabilización de un COBRO

        }

        facts.add(fact);
        return facts;
    }


    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 8/28/18.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZPagoMedioPago> medioPagoList = this.pago.getMediosPago();

        this.amtMediosPago = Env.ZERO;

        for (MZPagoMedioPago medioPago: medioPagoList){

            DocLine docLine = new DocLine(medioPago, this);
            docLine.setAmount(medioPago.getTotalAmtMT());
            list.add(docLine);

            this.amtMediosPago = this.amtMediosPago.add(medioPago.getTotalAmtMT());
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }


}
