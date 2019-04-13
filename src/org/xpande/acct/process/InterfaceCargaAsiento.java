package org.xpande.acct.process;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZLoadJournal;

/**
 * Proceso para interface de carga masiva de asientos contables.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 4/11/19.
 */
public class InterfaceCargaAsiento extends SvrProcess {

    MZLoadJournal loadJournal = null;

    @Override
    protected void prepare() {
        this.loadJournal = new MZLoadJournal(getCtx(), this.getRecord_ID(), get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        try{

            if ((this.loadJournal.getFileName() == null) || (this.loadJournal.getFileName().trim().equalsIgnoreCase(""))){
                return "@Error@ Debe indicar archivo a procesar ";
            }

            this.loadJournal.executeInterface();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return "OK";
    }

}
