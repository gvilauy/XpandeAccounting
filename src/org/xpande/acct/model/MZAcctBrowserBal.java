package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para detalle de consulta de Balance Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 3/12/19.
 */
public class MZAcctBrowserBal extends X_Z_AcctBrowserBal {

    public MZAcctBrowserBal(Properties ctx, int Z_AcctBrowserBal_ID, String trxName) {
        super(ctx, Z_AcctBrowserBal_ID, trxName);
    }

    public MZAcctBrowserBal(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
