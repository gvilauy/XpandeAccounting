package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 9/20/19.
 */
public class MZGeneraFormDGIResg extends X_Z_GeneraFormDGIResg {

    public MZGeneraFormDGIResg(Properties ctx, int Z_GeneraFormDGIResg_ID, String trxName) {
        super(ctx, Z_GeneraFormDGIResg_ID, trxName);
    }

    public MZGeneraFormDGIResg(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
