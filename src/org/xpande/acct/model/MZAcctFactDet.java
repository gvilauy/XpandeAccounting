package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para asociar información detallada a un registro de un asiento contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 10/8/18.
 */
public class MZAcctFactDet extends X_Z_AcctFactDet {

    public MZAcctFactDet(Properties ctx, int Z_AcctFactDet_ID, String trxName) {
        super(ctx, Z_AcctFactDet_ID, trxName);
    }

    public MZAcctFactDet(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
