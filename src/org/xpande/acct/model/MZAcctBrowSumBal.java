package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para resumen de consulta de Balance Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 3/12/19.
 */
public class MZAcctBrowSumBal extends X_Z_AcctBrowSumBal {

    public MZAcctBrowSumBal(Properties ctx, int Z_AcctBrowSumBal_ID, String trxName) {
        super(ctx, Z_AcctBrowSumBal_ID, trxName);
    }

    public MZAcctBrowSumBal(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
