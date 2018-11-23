package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para configuración de Rubros para Formularios de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/23/18.
 */
public class MZAcctConfigRubroDGI extends X_Z_AcctConfigRubroDGI {

    public MZAcctConfigRubroDGI(Properties ctx, int Z_AcctConfigRubroDGI_ID, String trxName) {
        super(ctx, Z_AcctConfigRubroDGI_ID, trxName);
    }

    public MZAcctConfigRubroDGI(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
