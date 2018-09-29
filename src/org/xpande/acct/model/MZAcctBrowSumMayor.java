package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para sumarización de resultados por cuenta en la consulta de Mayor contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/24/18.
 */
public class MZAcctBrowSumMayor extends X_Z_AcctBrowSumMayor {

    public MZAcctBrowSumMayor(Properties ctx, int Z_AcctBrowSumMayor_ID, String trxName) {
        super(ctx, Z_AcctBrowSumMayor_ID, trxName);
    }

    public MZAcctBrowSumMayor(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
