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


        facts.add(fact);
        return facts;
    }

}
