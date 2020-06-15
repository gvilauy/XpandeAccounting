package org.xpande.acct.callout;

import org.compiere.model.*;
import java.util.Properties;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/15/20.
 */
public class CalloutAccounting extends CalloutEngine {

    /***
     * Setea tipo de documento base, según ID de documento recibido.
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     * @return
     */
    public String setDocBaseType (Properties ctx, int WindowNo, GridTab mTab, GridField mField, Object value)
    {
        if (value == null){
            return "";
        }

        int cDocTypeID = ((Integer) value).intValue();

        MDocType docType = new MDocType(ctx, cDocTypeID, null);

        if ((docType == null) || (docType.get_ID() <= 0)){
            return "";
        }

        mTab.setValue("DocBaseType", docType.getDocBaseType());

        return "";
    }

}
