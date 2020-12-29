package org.xpande.acct.report;

import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MOrg;
import org.compiere.model.MUser;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.xpande.acct.model.MZAcctBrowser;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/26/20.
 */
public class MayorContableNavRP extends SvrProcess {

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
                else if (name.trim().equalsIgnoreCase("RP_Titulo")){
                    paramTituloReporte = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_Compania")){
                    paramCompania = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_Organizacion")){
                    paramOrganizacion = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_Moneda")){
                    paramMonedaReporte = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_Moneda1")){
                    paramMoneda1 = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_Moneda2")){
                    paramMoneda2 = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_Usuario")){
                    paramUsuario = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_StartDate")){
                    paramFechaInicio = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_EndDate")){
                    paramFechaFin = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_SaldoInicial")){
                    paramSaldoInicial = para[i];
                }
                else if (name.trim().equalsIgnoreCase("RP_VarianteJasper")){
                    paramVarianteJasper = para[i];
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
        //this.mayorProcessor.incSaldoInicial = (acctBrowser.isIncSaldoInicial()) ? "Y" : "N";
        this.mayorProcessor.isCierreDiferencial = acctBrowser.isCierreDiferencial();
        //this.mayorProcessor.incCierreDiferencial = (acctBrowser.isCierreDiferencial()) ? "Y" : "N";
        this.mayorProcessor.isCierreIntegral = acctBrowser.isCierreIntegral();
        //this.mayorProcessor.incCierreIntegral = (acctBrowser.isCierreIntegral()) ? "Y" : "N";
        this.mayorProcessor.startDate = acctBrowser.getStartDate();
        this.mayorProcessor.endDate = acctBrowser.getEndDate();

        // Seteo parametros fijos del reporte Jasper
        this.setParametrosRP();

    }

    /***
     * Setea parametros del reporte.
     * Xpande. Created by Gabriel Vila on 7/19/19.
     */
    private void setParametrosRP(){

        if (paramTituloReporte != null){
            paramTituloReporte.setParameter("Mayor Contable");
        }

        if (paramCompania != null){
            MClient client = new MClient(getCtx(), this.mayorProcessor.adClientID, null);
            paramCompania.setParameter(client.getDescription());
        }

        if (paramOrganizacion != null){
            MOrg org = new MOrg(getCtx(), this.mayorProcessor.adOrgID, null);
            paramOrganizacion.setParameter(org.getName());
        }

        if (paramUsuario != null){
            MUser user = new MUser(getCtx(), this.mayorProcessor.adUserID, null);
            paramUsuario.setParameter(user.getName());
        }

        if (paramFechaInicio != null){
            paramFechaInicio.setParameter(this.mayorProcessor.startDate);
        }

        if (paramFechaFin != null){
            paramFechaFin.setParameter(this.mayorProcessor.endDate);
        }

        if (paramSaldoInicial != null){
            paramSaldoInicial.setParameter(this.mayorProcessor.incSaldoInicial);
        }


        MCurrency currency1 = new MCurrency(getCtx(), this.mayorProcessor.cCurrencyID, null);
        MCurrency currency2 = null;

        if (paramMonedaReporte != null){

            String textoMonedas = "Moneda : " + currency1.getISO_Code();
            if (this.mayorProcessor.cCurrencyID_2 > 0){
                currency2 = new MCurrency(getCtx(), this.mayorProcessor.cCurrencyID_2, null);
                textoMonedas += " y " + currency2.getISO_Code();
            }
            paramMonedaReporte.setParameter(textoMonedas);
        }

        if (paramMoneda1 != null){
            paramMoneda1.setParameter(currency1.getISO_Code());
        }

        if (paramMoneda2 != null){
            if (currency2 != null){
                paramMoneda2.setParameter(currency2.getISO_Code());
            }
        }

        if (paramVarianteJasper != null){
            if (this.mayorProcessor.cCurrencyID_2 > 0){
                paramVarianteJasper.setParameter("DOS_MONEDAS_SIMPLE");
            }
        }
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
