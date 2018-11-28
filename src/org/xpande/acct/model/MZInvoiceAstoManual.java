package org.xpande.acct.model;

import org.compiere.model.*;
import org.compiere.util.Msg;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para asientos contables manuales en comprobantes de compra.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/26/18.
 */
public class MZInvoiceAstoManual extends X_Z_InvoiceAstoManual {

    public MZInvoiceAstoManual(Properties ctx, int Z_InvoiceAstoManual_ID, String trxName) {
        super(ctx, Z_InvoiceAstoManual_ID, trxName);
    }

    public MZInvoiceAstoManual(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    @Override
    protected boolean beforeSave(boolean newRecord) {

        if (newRecord){

            MInvoice invoice = (MInvoice) this.getC_Invoice();
            this.setAD_Org_ID(invoice.getAD_Org_ID());
            this.setC_BPartner_ID(invoice.getC_BPartner_ID());
            this.setDateAcct(invoice.getDateAcct());
            this.setC_Currency_ID(invoice.getC_Currency_ID());

        }

        if (!getOrCreateCombination())
            return false;

        if (getC_ValidCombination_ID() <= 0)
        {
            log.saveError("SaveError", Msg.parseTranslation(getCtx(),
                    "@FillMandatory@" + "@C_ValidCombination_ID@"));
            return false;
        }

        fillDimensionsFromCombination();

        return true;
    }


    /** Update combination and optionally **/
    private boolean getOrCreateCombination()
    {
        if (getC_ValidCombination_ID() == 0
                || (!is_new() && (is_ValueChanged(COLUMNNAME_Account_ID)
                || is_ValueChanged(COLUMNNAME_M_Product_ID)
                || is_ValueChanged(COLUMNNAME_C_BPartner_ID)
                || is_ValueChanged(COLUMNNAME_AD_Org_ID)
                || is_ValueChanged(COLUMNNAME_C_Activity_ID))))
        {

            // Validate all mandatory combinations are set
            MAcctSchema as = MClient.get(getCtx(), this.getAD_Client_ID()).getAcctSchema();
            String errorFields = "";
            for (MAcctSchemaElement elem : MAcctSchemaElement.getAcctSchemaElements(as)) {
                if (! elem.isMandatory())
                    continue;
                String et = elem.getElementType();
                if (MAcctSchemaElement.ELEMENTTYPE_Account.equals(et) && getAccount_ID() == 0)
                    errorFields += "@" +  COLUMNNAME_Account_ID + "@, ";
                if (MAcctSchemaElement.ELEMENTTYPE_Activity.equals(et) && getC_Activity_ID() == 0)
                    errorFields += "@" + COLUMNNAME_C_Activity_ID + "@, ";
                if (MAcctSchemaElement.ELEMENTTYPE_BPartner.equals(et) && getC_BPartner_ID()  == 0)
                    errorFields += "@" + COLUMNNAME_C_BPartner_ID + "@, ";
                if (MAcctSchemaElement.ELEMENTTYPE_Organization.equals(et) && getAD_Org_ID() == 0)
                    errorFields += "@" + COLUMNNAME_AD_Org_ID + "@, ";
                if (MAcctSchemaElement.ELEMENTTYPE_Product.equals(et) && getM_Product_ID()  == 0)
                    errorFields += "@" + COLUMNNAME_M_Product_ID + "@, ";
            }
            if (errorFields.length() > 0)
            {
                log.saveError("Error", Msg.parseTranslation(getCtx(), "@IsMandatory@: " + errorFields.substring(0, errorFields.length() - 2)));
                return false;
            }

            MAccount account = MAccount.get(getCtx(),
                    getAD_Client_ID(),
                    getAD_Org_ID(),
                    as.getC_AcctSchema_ID(),
                    getAccount_ID(),
                    0,
                    getM_Product_ID(),
                    getC_BPartner_ID(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    getC_Activity_ID(),
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    get_TrxName());

            if (account != null)
            {
                account.saveEx();
                setC_ValidCombination_ID(account.get_ID());
            }
        }
        return true;
    }

    /** Fill Accounting Dimensions from line combination **/
    private void fillDimensionsFromCombination()
    {
        if (getC_ValidCombination_ID() > 0)
        {
            MAccount account = new MAccount(getCtx(), getC_ValidCombination_ID(), get_TrxName());
            setAD_Org_ID(account.getAD_Org_ID());
            setAccount_ID(account.getAccount_ID());
            setM_Product_ID( account.getM_Product_ID());
            setC_BPartner_ID( account.getC_BPartner_ID());
            setC_Activity_ID( account.getC_Activity_ID());
        }
    }

}
