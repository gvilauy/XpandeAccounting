package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.MZMovBanco;
import org.xpande.financial.model.MZMovBancoLin;
import org.xpande.financial.model.MZMovCaja;
import org.xpande.financial.model.MZMovCajaLin;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/22/20.
 */
public class Doc_MovCaja extends Doc{

    private MZMovCaja movCaja = null;
    private MDocType docType = null;

    /***
     * Constructor
     * @param ass
     * @param clazz
     * @param rs
     * @param defaultDocumentType
     * @param trxName
     */
    public Doc_MovCaja(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /***
     * Constructor
     * @param ass
     * @param rs
     * @param trxName
     */
    public Doc_MovCaja(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZMovCaja.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.movCaja = (MZMovCaja) getPO();
        setDateDoc(this.movCaja.getDateDoc());
        setDateAcct(this.movCaja.getDateAcct());
        setAmount(Doc.AMTTYPE_Gross, this.movCaja.getTotalAmt());
        setC_CashBook_ID(this.movCaja.getC_CashBook_ID());

        this.docType = (MDocType) this.movCaja.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        return null;
    }

    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 11/10/20.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZMovCajaLin> movCajaLinList = this.movCaja.getLines();

        for (MZMovCajaLin movCajaLin: movCajaLinList){
            DocLine docLine = new DocLine(movCajaLin, this);
            docLine.setAmount(movCajaLin.getTotalAmtMT());
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }

    @Override
    public BigDecimal getBalance() {

        BigDecimal retValue = Env.ZERO;
        StringBuffer sb = new StringBuffer (" [");

        //  Total
        retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
        sb.append(getAmount(Doc.AMTTYPE_Gross));

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

        boolean debitoCaja = true;
        if (this.docType.getDocBaseType().equalsIgnoreCase("CCR")){
            debitoCaja = false;
        }

        // Total del documento - Cuenta contable de la caja destino del movimiento
        int accountID = getValidCombination_ID(Doc.ACCTTYPE_CashExpense, as);
        if (accountID <= 0){
            MCashBook cashBook = (MCashBook) this.movCaja.getC_CashBook();
            p_Error = "No se obtuvo Cuenta Contable (CashExpense) asociada a la caja : " + cashBook.getName();
            log.log(Level.SEVERE, p_Error);
            facts.add(null);
            return facts;
        }
        MAccount acctCr = MAccount.get(getCtx(), accountID);

        FactLine fl1 = null;
        if (debitoCaja){
            fl1 = fact.createLine (null, acctCr, getC_Currency_ID(), null, grossAmt);
        }
        else {
            fl1 = fact.createLine (null, acctCr, getC_Currency_ID(), grossAmt, null);
        }

        if (fl1 != null){
            fl1.setAD_Org_ID(this.movCaja.getAD_Org_ID());
        }

        // Monto de Lineas del documento - Cuenta contable del cargo
        for (int i = 0; i < p_lines.length; i++){

            MZMovCajaLin mzMovCajaLin = new MZMovCajaLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            BigDecimal amtSource = mzMovCajaLin.getTotalAmt();
            BigDecimal amtMT = mzMovCajaLin.getTotalAmtMT();

            MAccount acctCharge = MCharge.getAccount(mzMovCajaLin.getC_Charge_ID(), as, amtSource);
            if ((acctCharge == null) || (acctCharge.get_ID() <= 0)){
                MCharge charge = (MCharge) mzMovCajaLin.getC_Charge();
                p_Error = "No se obtuvo Cuenta Contable asociada al Cargo Contable : " + charge.getName();
                log.log(Level.SEVERE, p_Error);
                facts.add(null);
                return facts;
            }

            FactLine fl2 = null;
            if (debitoCaja){
                fl2 = fact.createLine(p_lines[i], acctCharge, mzMovCajaLin.getC_Currency_ID(), amtSource, null);
            }
            else {
                fl2 = fact.createLine(p_lines[i], acctCharge, mzMovCajaLin.getC_Currency_ID(), null, amtSource);
            }

            if (fl2 != null){
                fl2.setAD_Org_ID(this.movCaja.getAD_Org_ID());
            }
        }

        facts.add(fact);
        return facts;
    }

}
