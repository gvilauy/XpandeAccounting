package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo de filtro de cuenta contable en el Navagador Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/26/18.
 */
public class MZAcctBrowFiltroCta extends X_Z_AcctBrowFiltroCta {

    public MZAcctBrowFiltroCta(Properties ctx, int Z_AcctBrowFiltroCta_ID, String trxName) {
        super(ctx, Z_AcctBrowFiltroCta_ID, trxName);
    }

    public MZAcctBrowFiltroCta(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
