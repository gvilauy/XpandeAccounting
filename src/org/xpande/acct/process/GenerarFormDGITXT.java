package org.xpande.acct.process;

import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZGeneraFormDGI;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/25/18.
 */
public class GenerarFormDGITXT extends SvrProcess {

    MZGeneraFormDGI generaFormDGI = null;

    @Override
    protected void prepare() {
        this.generaFormDGI = new MZGeneraFormDGI(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        String message = this.generaFormDGI.generateFile();

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }
}
