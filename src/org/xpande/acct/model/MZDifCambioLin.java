package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para lineas de resumen de movimientos en proceso de diferencia de cambio.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/11/19.
 */
public class MZDifCambioLin extends X_Z_DifCambioLin {

    public MZDifCambioLin(Properties ctx, int Z_DifCambioLin_ID, String trxName) {
        super(ctx, Z_DifCambioLin_ID, trxName);
    }

    public MZDifCambioLin(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
