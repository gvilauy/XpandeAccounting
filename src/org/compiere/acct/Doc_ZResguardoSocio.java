package org.compiere.acct;

import org.compiere.model.MAcctSchema;
import org.compiere.model.MInvoice;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/27/18.
 */
public class Doc_ZResguardoSocio extends Doc {

    /**
     *  Constructor
     */
    public Doc_ZResguardoSocio(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_ZResguardoSocio(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MInvoice.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {
        return null;
    }

    @Override
    public BigDecimal getBalance() {
        return null;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {
        return null;
    }
}
