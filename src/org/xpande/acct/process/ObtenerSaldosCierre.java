package org.xpande.acct.process;

import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctCierre;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 6/13/20.
 */
public class ObtenerSaldosCierre extends SvrProcess {

    MZAcctCierre acctCierre = null;

    @Override
    protected void prepare() {
        this.acctCierre = new MZAcctCierre(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        this.acctCierre.getData();

        return "OK";
    }

}
