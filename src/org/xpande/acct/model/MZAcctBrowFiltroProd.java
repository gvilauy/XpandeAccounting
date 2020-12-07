package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/6/20.
 */
public class MZAcctBrowFiltroProd extends X_Z_AcctBrowFiltroProd{

    public MZAcctBrowFiltroProd(Properties ctx, int Z_AcctBrowFiltroProd_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroProd_ID, trxName);
    }

    public MZAcctBrowFiltroProd(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
