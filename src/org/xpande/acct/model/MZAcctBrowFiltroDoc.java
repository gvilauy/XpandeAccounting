package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/6/20.
 */
public class MZAcctBrowFiltroDoc extends X_Z_AcctBrowFiltroDoc{

    public MZAcctBrowFiltroDoc(Properties ctx, int Z_AcctBrowFiltroDoc_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroDoc_ID, trxName);
    }

    public MZAcctBrowFiltroDoc(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
