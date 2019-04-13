package org.xpande.acct.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para lineas leídas desde archivo en interface de Carga de Asientos Contables.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 4/11/19.
 */
public class MZLoadJournalFile extends X_Z_LoadJournalFile {

    public MZLoadJournalFile(Properties ctx, int Z_LoadJournalFile_ID, String trxName) {
        super(ctx, Z_LoadJournalFile_ID, trxName);
    }

    public MZLoadJournalFile(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }
}
