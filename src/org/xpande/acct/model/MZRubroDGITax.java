package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para asociación de tasa de impuesto con rubro de formulario de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/23/18.
 */
public class MZRubroDGITax extends X_Z_RubroDGITax {

    public MZRubroDGITax(Properties ctx, int Z_RubroDGITax_ID, String trxName) {
        super(ctx, Z_RubroDGITax_ID, trxName);
    }

    public MZRubroDGITax(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
