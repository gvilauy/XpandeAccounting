package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para configuraciones contables del Core.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/11/18.
 */
public class MZAcctConfig extends X_Z_AcctConfig {

    public MZAcctConfig(Properties ctx, int Z_AcctConfig_ID, String trxName) {
        super(ctx, Z_AcctConfig_ID, trxName);
    }

    public MZAcctConfig(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /***
     * Obtiene modelo único de configuración de modulo contable.
     * Xpande. Created by Gabriel Vila on 7/11/18.
     * @param ctx
     * @param trxName
     * @return
     */
    public static MZAcctConfig getDefault(Properties ctx, String trxName){

        MZAcctConfig model = new Query(ctx, I_Z_AcctConfig.Table_Name, "", trxName).first();

        return model;
    }

}
