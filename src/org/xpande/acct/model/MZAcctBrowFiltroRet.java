package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/6/20.
 */
public class MZAcctBrowFiltroRet extends X_Z_AcctBrowFiltroRet{

    public MZAcctBrowFiltroRet(Properties ctx, int Z_AcctBrowFiltroRet_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroRet_ID, trxName);
    }

    public MZAcctBrowFiltroRet(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
