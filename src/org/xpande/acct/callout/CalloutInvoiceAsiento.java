package org.xpande.acct.callout;

import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Callouts para asiento contable manual en comprobantes de compra.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/26/18.
 */
public class CalloutInvoiceAsiento extends CalloutEngine {


    /**
     * 	Set CurrencyRate from DateAcct, C_ConversionType_ID, C_Currency_ID
     *	@param ctx context
     *	@param WindowNo window no
     *	@param mTab tab
     *	@param mField field
     *	@param value value
     *	@return null or error message
     */
    public String rate (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
    {
        if (value == null)
            return "";

        if ((Integer) mTab.getValue("C_InvoiceID") == null){
            return "";
        }

        int cInvoiceID = ((Integer) mTab.getValue("C_InvoiceID")).intValue();

        MInvoice invoice = new MInvoice(ctx, cInvoiceID, null);

        if ((invoice == null) || (invoice.get_ID() <= 0)){
            return "";
        }

        MAcctSchema as = MClient.get(ctx, invoice.getAD_Client_ID()).getAcctSchema();

        BigDecimal currencyRate = Env.ONE;

        if (invoice.getC_Currency_ID() != as.getC_Currency_ID()){

            currencyRate = MConversionRate.getRate(invoice.getC_Currency_ID(), as.getC_Currency_ID(), invoice.getDateAcct(), 114, invoice.getAD_Client_ID(), 0);
            if (currencyRate == null) currencyRate = Env.ZERO;
        }

        mTab.setValue("CurrencyRate", currencyRate);

        return "";
    }

    /**
     *  Convert the source amount to accounted amount (AmtAcctDr/Cr)
     *  Called when source amount (AmtSourceCr/Dr) or rate changes
     *	@param ctx context
     *	@param WindowNo window no
     *	@param mTab tab
     *	@param mField field
     *	@param value value
     *	@return null or error message
     */
    public String amt (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
    {
        String colName = mField.getColumnName();
        if (value == null || isCalloutActive())
            return "";

        int cInvoiceID = ((Integer) mTab.getValue("C_InvoiceID")).intValue();

        MInvoice invoice = new MInvoice(ctx, cInvoiceID, null);

        if ((invoice == null) || (invoice.get_ID() <= 0)){
            return "";
        }

        MAcctSchema as = MClient.get(ctx, invoice.getAD_Client_ID()).getAcctSchema();

        int Precision = as.getStdPrecision();

        BigDecimal currencyRate = (BigDecimal)mTab.getValue("CurrencyRate");
        if (currencyRate == null)
        {
            currencyRate = Env.ONE;
            mTab.setValue("CurrencyRate", currencyRate);
        }

        //  AmtAcct = AmtSource * CurrencyRate  ==> Precision
        BigDecimal AmtSourceDr = (BigDecimal)mTab.getValue("AmtSourceDr");
        if (AmtSourceDr == null)
            AmtSourceDr = Env.ZERO;
        BigDecimal AmtSourceCr = (BigDecimal)mTab.getValue("AmtSourceCr");
        if (AmtSourceCr == null)
            AmtSourceCr = Env.ZERO;

        BigDecimal AmtAcctDr = AmtSourceDr.multiply(currencyRate);
        AmtAcctDr = AmtAcctDr.setScale(Precision, BigDecimal.ROUND_HALF_UP);
        mTab.setValue("AmtAcctDr", AmtAcctDr);
        BigDecimal AmtAcctCr = AmtSourceCr.multiply(currencyRate);
        AmtAcctCr = AmtAcctCr.setScale(Precision, BigDecimal.ROUND_HALF_UP);
        mTab.setValue("AmtAcctCr", AmtAcctCr);

        return "";
    }

    /**
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     * @return
     */
    public String account (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)	// idempiere 344 - nmicoud
    {
        String colName = mField.getColumnName();
        if (value == null || isCalloutActive())
            return "";

        if (colName.equals("Account_ID") || colName.equals("M_Product_ID") || colName.equals("C_Activity_ID"))
        {
            mTab.setValue("C_ValidCombination_ID", null);
        }

        return "";
    }

    public String alias (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)	// idempiere 344 - nmicoud
    {
        String colName = mField.getColumnName();
        if (value == null || isCalloutActive())
            return "";

        Integer Combi_ID = ((Integer)value).intValue();

        if (colName.equals("C_ValidCombination_ID"))
        {
            MAccount combi = new MAccount(ctx, Combi_ID, null);
            mTab.setValue("Account_ID", combi.getAccount_ID() != 0 ? combi.getAccount_ID() : null);
            mTab.setValue("M_Product_ID", combi.getM_Product_ID() != 0 ? combi.getM_Product_ID() : null);
            mTab.setValue("AD_Org_ID", combi.getAD_Org_ID() != 0 ? combi.getAD_Org_ID() : null);
            mTab.setValue("C_Activity_ID", combi.getC_Activity_ID() != 0 ? combi.getC_Activity_ID() : null);
        }
        return "";
    }

}
