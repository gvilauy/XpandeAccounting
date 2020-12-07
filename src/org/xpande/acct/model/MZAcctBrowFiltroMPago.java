package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 12/6/20.
 */
public class MZAcctBrowFiltroMPago extends X_Z_AcctBrowFiltroMPago{

    public MZAcctBrowFiltroMPago(Properties ctx, int Z_AcctBrowFiltroMPago_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroMPago_ID, trxName);
    }

    public MZAcctBrowFiltroMPago(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
