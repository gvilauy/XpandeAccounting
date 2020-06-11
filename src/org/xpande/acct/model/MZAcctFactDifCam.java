package org.xpande.acct.model;

import org.compiere.model.Query;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Modelo para guardar historial de diferencias de cambio y tipo de cambio, a registros contables.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/11/20.
 */
public class MZAcctFactDifCam extends X_Z_AcctFactDifCam{

    public MZAcctFactDifCam(Properties ctx, int Z_AcctFactDifCam_ID, String trxName) {
        super(ctx, Z_AcctFactDifCam_ID, trxName);
    }

    public MZAcctFactDifCam(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }


    /***
     * Obtiene modelo segun ID de registro contable recibido y tal que sea el último a determinada fecha dentro del historial.
     * @param ctx
     * @param factAcctID
     * @param dateTrx
     * @param trxName
     * @return
     */
    public static MZAcctFactDifCam getLastByFactID(Properties ctx, int factAcctID, Timestamp dateTrx, String trxName){

        String whereClause = X_Z_AcctFactDifCam.COLUMNNAME_Fact_Acct_ID + " =" + factAcctID +
                " AND " + X_Z_AcctFactDifCam.COLUMNNAME_DateTrx + " <= '" + dateTrx + "' ";

        MZAcctFactDifCam model = new Query(ctx, I_Z_AcctFactDifCam.Table_Name, whereClause, trxName).setOrderBy(" DateTrx Desc ").first();

        return model;
    }

}
