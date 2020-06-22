package org.xpande.acct.report;

import org.compiere.model.MClient;
import org.compiere.model.MCurrency;
import org.compiere.model.MOrg;
import org.compiere.model.MUser;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Proceso para reporte RV del Balance Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/28/19.
 */
public class BalanceContableRV extends SvrProcess {

    private BalanceContable balanceProcessor = null;

    private ProcessInfoParameter paramTituloReporte = null;
    private ProcessInfoParameter paramCompania = null;
    private ProcessInfoParameter paramOrganizacion = null;
    private ProcessInfoParameter paramMonedaReporte = null;
    private ProcessInfoParameter paramMoneda1 = null;
    private ProcessInfoParameter paramMoneda2 = null;
    private ProcessInfoParameter paramFechaInicio = null;
    private ProcessInfoParameter paramFechaFin = null;
    private ProcessInfoParameter paramUsuario = null;


    @Override
    protected void prepare() {

        this.balanceProcessor = new BalanceContable(getCtx(), null);

        ProcessInfoParameter[] para = getParameter();

        for (int i = 0; i < para.length; i++){

            String name = para[i].getParameterName();

            if (name != null){
                if (name.trim().equalsIgnoreCase("AD_Client_ID")){
                    this.balanceProcessor.adClientID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
                else if (name.trim().equalsIgnoreCase("AD_Org_ID")){
                    this.balanceProcessor.adOrgID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
                else if (name.trim().equalsIgnoreCase("C_AcctSchema_ID")){
                    this.balanceProcessor.cAcctSchemaID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
                else if (name.trim().equalsIgnoreCase("C_Currency_ID")){
                    if (para[i].getParameter() != null){
                        this.balanceProcessor.cCurrencyID = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
                else if (name.trim().equalsIgnoreCase("C_Currency_2_ID")){
                    if (para[i].getParameter() != null){
                        this.balanceProcessor.cCurrencyID_2 = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
                else if (name.trim().equalsIgnoreCase("TipoFiltroMonAcct")){
                    this.balanceProcessor.tipoFiltroMonAcct = (String)para[i].getParameter();
                }
                else if (name.trim().equalsIgnoreCase("TipoBalanceAcct")){
                    this.balanceProcessor.tipoBalanceAcct = (String)para[i].getParameter();
                }
                else if (name.trim().equalsIgnoreCase("DateAcct")){
                    this.balanceProcessor.startDate = (Timestamp)para[i].getParameter();
                    this.balanceProcessor.endDate = (Timestamp)para[i].getParameter_To();
                }
                else if (name.trim().equalsIgnoreCase("IncCtaSaldoSinMov")) {
                    this.balanceProcessor.mostrarSinSaldo = (((String) para[i].getParameter()).trim().equalsIgnoreCase("Y")) ? true : false;
                    this.balanceProcessor.incCtaSaldoSinMov = (String) ((String) para[i].getParameter()).trim();
                }
                else if (name.trim().equalsIgnoreCase("IsCierreDiferencial")) {
                    this.balanceProcessor.isCierreDiferencial = (((String) para[i].getParameter()).trim().equalsIgnoreCase("Y")) ? true : false;
                    this.balanceProcessor.incCierreDiferencial = (String) ((String) para[i].getParameter()).trim();
                }
                else if (name.trim().equalsIgnoreCase("IsCierreIntegral")) {
                    this.balanceProcessor.isCierreIntegral = (((String) para[i].getParameter()).trim().equalsIgnoreCase("Y")) ? true : false;
                    this.balanceProcessor.incCierreIntegral = (String) ((String) para[i].getParameter()).trim();
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
            }
        }

        this.balanceProcessor.adUserID = this.getAD_User_ID();
    }

    @Override
    protected String doIt() throws Exception {

        String message = this.balanceProcessor.executeReport();

        if (message != null){
            return "@Error@ " + message;
        }

        return "OK";
    }
}
