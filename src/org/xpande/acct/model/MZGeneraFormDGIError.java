package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para guardar inconsistencias halladas en el proceso de generación de un Formulario de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/21/18.
 */
public class MZGeneraFormDGIError extends X_Z_GeneraFormDGIError {

    public MZGeneraFormDGIError(Properties ctx, int Z_GeneraFormDGIError_ID, String trxName) {
        super(ctx, Z_GeneraFormDGIError_ID, trxName);
    }

    public MZGeneraFormDGIError(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
