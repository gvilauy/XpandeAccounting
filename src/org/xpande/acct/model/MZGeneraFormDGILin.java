package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para lineas del proceso de generación de un Fomrmulario de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/21/18.
 */
public class MZGeneraFormDGILin extends X_Z_GeneraFormDGILin {

    public MZGeneraFormDGILin(Properties ctx, int Z_GeneraFormDGILin_ID, String trxName) {
        super(ctx, Z_GeneraFormDGILin_ID, trxName);
    }

    public MZGeneraFormDGILin(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
