package org.xpande.acct.model;

import org.compiere.model.MInvoice;

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

        return true;
    }
}
