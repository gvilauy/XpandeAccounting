package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/6/20.
 */
public class MZAcctBrowFiltroTax extends X_Z_AcctBrowFiltroTax{

    public MZAcctBrowFiltroTax(Properties ctx, int Z_AcctBrowFiltroTax_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroTax_ID, trxName);
    }

    public MZAcctBrowFiltroTax(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
