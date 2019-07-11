package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para detalle de movimientos en el proceso de Diferencia de Cambio
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/11/19.
 */
public class MZDifCambioDet extends X_Z_DifCambioDet {

    public MZDifCambioDet(Properties ctx, int Z_DifCambioDet_ID, String trxName) {
        super(ctx, Z_DifCambioDet_ID, trxName);
    }

    public MZDifCambioDet(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
