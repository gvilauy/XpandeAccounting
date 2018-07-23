package org.xpande.acct.process;

import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctBrowser;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/20/18.
 */
public class ExecuteAcctBrowser extends SvrProcess {

    private MZAcctBrowser acctBrowser = null;

    @Override
    protected void prepare() {
        this.acctBrowser = new MZAcctBrowser(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        String message = this.acctBrowser.executeBrowser();

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }
}
