package org.xpande.acct.process;

import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctBrowser;
import org.xpande.acct.model.MZAcctBrowserBal;
import org.xpande.acct.model.MZDifCambio;

/**
 * Proceso para obtener movimientos para el proceso de Diferencia de Cambio.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/11/19.
 */
public class ObtenerDifCambio extends SvrProcess {

    private MZDifCambio difCambio = null;

    @Override
    protected void prepare() {

        this.difCambio = new MZDifCambio(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        String message = this.difCambio.getData();

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }

}
