package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo de linea de dpcumentos de cierre de saldos contables.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/13/20.
 */
public class MZAcctCierreLin extends X_Z_AcctCierreLin{

    public MZAcctCierreLin(Properties ctx, int Z_AcctCierreLin_ID, String trxName) {
        super(ctx, Z_AcctCierreLin_ID, trxName);
    }

    public MZAcctCierreLin(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
