package org.xpande.acct.process;

import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctBrowser;
import org.xpande.acct.model.MZAcctBrowserBal;

/**
 * Proceso para obtener detalle de una cuenta en la consulta de Balance en el Navegador Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 4/9/19.
 */
public class ObtenerDetalleBalance extends SvrProcess {

    private MZAcctBrowserBal acctBrowserBal = null;

    @Override
    protected void prepare() {

        this.acctBrowserBal = new MZAcctBrowserBal(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        MZAcctBrowser acctBrowser = (MZAcctBrowser) this.acctBrowserBal.getZ_AcctBrowser();

        String message = acctBrowser.getDetalleBalance(this.acctBrowserBal);

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }

}
