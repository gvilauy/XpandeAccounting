package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para linea de documento de apertura de saldos contables.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/18/20.
 */
public class MZAcctAperturaLin extends X_Z_AcctAperturaLin{

    public MZAcctAperturaLin(Properties ctx, int Z_AcctAperturaLin_ID, String trxName) {
        super(ctx, Z_AcctAperturaLin_ID, trxName);
    }

    public MZAcctAperturaLin(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
