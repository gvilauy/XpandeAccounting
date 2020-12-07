package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/6/20.
 */
public class MZAcctBrowFiltroBP extends X_Z_AcctBrowFiltroBP{

    public MZAcctBrowFiltroBP(Properties ctx, int Z_AcctBrowFiltroBP_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroBP_ID, trxName);
    }

    public MZAcctBrowFiltroBP(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
