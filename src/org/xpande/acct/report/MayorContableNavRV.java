package org.xpande.acct.report;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctBrowser;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/26/20.
 */
public class MayorContableNavRV extends SvrProcess {

    private MayorContable mayorProcessor = null;

    private ProcessInfoParameter paramTituloReporte = null;
    private ProcessInfoParameter paramCompania = null;
    private ProcessInfoParameter paramOrganizacion = null;
    private ProcessInfoParameter paramMonedaReporte = null;
    private ProcessInfoParameter paramMoneda1 = null;
    private ProcessInfoParameter paramMoneda2 = null;
    private ProcessInfoParameter paramFechaInicio = null;
    private ProcessInfoParameter paramFechaFin = null;
    private ProcessInfoParameter paramUsuario = null;
    private ProcessInfoParameter paramSaldoInicial = null;
    private ProcessInfoParameter paramVarianteJasper = null;


    @Override
    protected void prepare() {

        this.mayorProcessor = new MayorContable(getCtx(), null);

        ProcessInfoParameter[] para = getParameter();

        for (int i = 0; i < para.length; i++){

            String name = para[i].getParameterName();

            if (name != null){

                if (name.trim().equalsIgnoreCase("Z_AcctBrowser_ID")){
                    this.mayorProcessor.zAcctBrowserID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
            }
        }

        MZAcctBrowser acctBrowser = new MZAcctBrowser(getCtx(), this.getRecord_ID(), null);
        this.mayorProcessor.zAcctBrowserID = acctBrowser.get_ID();

        this.mayorProcessor.adUserID = this.getAD_User_ID();
        this.mayorProcessor.adClientID = acctBrowser.getAD_Client_ID();
        this.mayorProcessor.adOrgID = acctBrowser.getAD_Org_ID();
        this.mayorProcessor.cAcctSchemaID = acctBrowser.getC_AcctSchema_ID();
        this.mayorProcessor.cCurrencyID = acctBrowser.getC_Currency_ID();
        this.mayorProcessor.cCurrencyID_2 = acctBrowser.getC_Currency_2_ID();
        this.mayorProcessor.tipoFiltroMonAcct = acctBrowser.getTipoFiltroMonAcct();
        this.mayorProcessor.textoFiltroCuentas = acctBrowser.getTextoFiltro();
        this.mayorProcessor.consideraSaldoInicial = acctBrowser.isIncSaldoInicial();
        this.mayorProcessor.isCierreDiferencial = acctBrowser.isCierreDiferencial();
        this.mayorProcessor.isCierreIntegral = acctBrowser.isCierreIntegral();
        this.mayorProcessor.startDate = acctBrowser.getStartDate();
        this.mayorProcessor.endDate = acctBrowser.getEndDate();
    }


    @Override
    protected String doIt() throws Exception {

        String message = this.mayorProcessor.executeReport();

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }
}
