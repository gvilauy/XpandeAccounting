package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.MZMovBanco;
import org.xpande.financial.model.MZMovBancoLin;
import org.xpande.financial.model.MZTransfBancaria;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/25/20.
 */
public class Doc_TransfBancaria extends Doc{

    private MZTransfBancaria transfBancaria = null;
    private MDocType docType = null;

    /***
     * Constructor
     * @param ass
     * @param clazz
     * @param rs
     * @param defaultDocumentType
     * @param trxName
     */
    public Doc_TransfBancaria(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /***
     * Constructor
     * @param ass
     * @param rs
     * @param trxName
     */
    public Doc_TransfBancaria(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZTransfBancaria.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.transfBancaria = (MZTransfBancaria) getPO();
        setDateDoc(this.transfBancaria.getDateDoc());
        setDateAcct(this.transfBancaria.getDateAcct());
        setAmount(Doc.AMTTYPE_Gross, this.transfBancaria.getAmtTotalTo());

        this.docType = (MDocType) this.transfBancaria.getC_DocType();
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

        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        if (this.transfBancaria.getC_Currency_ID_To() != this.transfBancaria.getC_Currency_ID()){
            this.setIsMultiCurrency(true);
        }

        // DR - Cuenta Bancaria Destino. Importe y cuenta contable ACCTTYPE_BankAsset
        int accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankAsset, this.transfBancaria.getC_BankAccount_To_ID(), as, null);
        if (accountID <= 0){
            MBankAccount bankAccount = new MBankAccount(getCtx(), this.transfBancaria.getC_BankAccount_To_ID(), null);
            p_Error = "No se obtuvo Cuenta Contable (BankAsset) asociada a la Cuenta Bancaria : " + bankAccount.getName();
            log.log(Level.SEVERE, p_Error);
            facts.add(null);
            return facts;
        }
        MAccount acctBankDr = MAccount.get(getCtx(), accountID);
        FactLine fl1 = fact.createLine (null, acctBankDr, this.transfBancaria.getC_Currency_ID_To(), this.transfBancaria.getAmtTotalTo(), null);
        if (fl1 != null){
            fl1.setAD_Org_ID(this.transfBancaria.getAD_Org_ID());
            if (this.transfBancaria.getC_Currency_ID_To() != as.getC_Currency_ID()){
                if (this.transfBancaria.getC_Currency_ID() == as.getC_Currency_ID()){
                    fl1.setAmtAcctDr(this.transfBancaria.getAmtTotal());
                }
            }
            fl1.saveEx();
        }

        // CR - Cuenta Bancaria Origen. Importe y cuenta contable ACCTTYPE_BankAsset
        accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankAsset, this.transfBancaria.getC_BankAccount_ID(), as, null);
        if (accountID <= 0){
            MBankAccount bankAccount = (MBankAccount) this.transfBancaria.getC_BankAccount();
            p_Error = "No se obtuvo Cuenta Contable (BankAsset) asociada a la Cuenta Bancaria : " + bankAccount.getName();
            log.log(Level.SEVERE, p_Error);
            facts.add(null);
            return facts;
        }
        MAccount acctBankCr = MAccount.get(getCtx(), accountID);
        FactLine fl2 = fact.createLine (null, acctBankCr, this.transfBancaria.getC_Currency_ID(), null, this.transfBancaria.getAmtTotal());
        if (fl2 != null){
            fl2.setAD_Org_ID(this.transfBancaria.getAD_Org_ID());
            if (this.transfBancaria.getC_Currency_ID() != as.getC_Currency_ID()){
                if (this.transfBancaria.getC_Currency_ID_To() == as.getC_Currency_ID()){
                    fl2.setAmtAcctCr(this.transfBancaria.getAmtTotalTo());
                }
            }
            fl2.saveEx();
        }

        facts.add(fact);
        return facts;
    }

}
