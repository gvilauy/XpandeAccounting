package org.xpande.acct.process;

import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctBrowser;

/**
 * Proceso para cargar cuentas contables en la grilla de filtros, según texto filtro ingresado.
 * Separador de cuentas: ;
 * Separador de randos: -
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/26/18.
 */
public class SetFiltroCuentas extends SvrProcess {

    private MZAcctBrowser acctBrowser = null;

    @Override
    protected void prepare() {
        this.acctBrowser = new MZAcctBrowser(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        String message = this.acctBrowser.setCuentasFiltro();

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }
}
