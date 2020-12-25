package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/10/20.
 */
public class Doc_MovBanco extends Doc {

    private MZMovBanco movBanco = null;
    private MDocType docType = null;

    /***
     * Constructor
     * @param ass
     * @param clazz
     * @param rs
     * @param defaultDocumentType
     * @param trxName
     */
    public Doc_MovBanco(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /***
     * Constructor
     * @param ass
     * @param rs
     * @param trxName
     */
    public Doc_MovBanco(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZMovBanco.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.movBanco = (MZMovBanco) getPO();
        setDateDoc(this.movBanco.getDateDoc());
        setDateAcct(this.movBanco.getDateAcct());
        setAmount(Doc.AMTTYPE_Gross, this.movBanco.getTotalAmt());

        this.docType = (MDocType) this.movBanco.getC_DocType();
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

        List<MZMovBancoLin> movBancoLinList = this.movBanco.getLines();

        for (MZMovBancoLin movBancoLin: movBancoLinList){
            DocLine docLine = new DocLine(movBancoLin, this);
            docLine.setAmount(movBancoLin.getTotalAmtMT());
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

        boolean debitoBancario = true;
        if (this.docType.getDocBaseType().equalsIgnoreCase("BCR")){
            debitoBancario = false;
        }

        // Total del documento - Cuenta contable de la cuenta bancaria destino del movimiento
        int accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankAsset, this.movBanco.getC_BankAccount_ID(), as, null);;
        if (accountID <= 0){
            MBankAccount bankAccount = (MBankAccount) this.movBanco.getC_BankAccount();
            p_Error = "No se obtuvo Cuenta Contable (BankAsset) asociada a la Cuenta Bancaria : " + bankAccount.getName();
            log.log(Level.SEVERE, p_Error);
            fact = null;
            facts.add(fact);
            return facts;
        }
        MAccount acctBankCr = MAccount.get(getCtx(), accountID);

        FactLine fl1 = null;
        if (debitoBancario){
            fl1 = fact.createLine (null, acctBankCr, getC_Currency_ID(), null, grossAmt);
        }
        else{
            fl1 = fact.createLine (null, acctBankCr, getC_Currency_ID(),  grossAmt, null);
        }

        if (fl1 != null){
            fl1.setAD_Org_ID(this.movBanco.getAD_Org_ID());
        }

        // Monto de Lineas del documento - Cuenta contable del cargo
        for (int i = 0; i < p_lines.length; i++){

            MZMovBancoLin mzMovBancoLin = new MZMovBancoLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            BigDecimal amtSource = mzMovBancoLin.getTotalAmt();
            BigDecimal amtMT = mzMovBancoLin.getTotalAmtMT();

            MAccount acctCharge = MCharge.getAccount(mzMovBancoLin.getC_Charge_ID(), as, amtSource);
            if ((acctCharge == null) || (acctCharge.get_ID() <= 0)){
                MCharge charge = (MCharge) mzMovBancoLin.getC_Charge();
                p_Error = "No se obtuvo Cuenta Contable asociada al Cargo Contable : " + charge.getName();
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            FactLine fl2 = null;
            if (debitoBancario){
                fl2 = fact.createLine(p_lines[i], acctCharge, mzMovBancoLin.getC_Currency_ID(), amtSource, null);
            }
            else{
                fl2 = fact.createLine(p_lines[i], acctCharge, mzMovBancoLin.getC_Currency_ID(), null, amtSource);
            }

            if (fl2 != null){
                fl2.setAD_Org_ID(this.movBanco.getAD_Org_ID());
            }
        }

        facts.add(fact);
        return facts;
    }

}
