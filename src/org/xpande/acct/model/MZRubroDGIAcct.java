package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para asociar una cuenta contable utilizada en asientos manuales, con un Rubro de DGI para generación de formularios.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 1/8/19.
 */
public class MZRubroDGIAcct extends X_Z_RubroDGIAcct {

    public MZRubroDGIAcct(Properties ctx, int Z_RubroDGIAcct_ID, String trxName) {
        super(ctx, Z_RubroDGIAcct_ID, trxName);
    }

    public MZRubroDGIAcct(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
