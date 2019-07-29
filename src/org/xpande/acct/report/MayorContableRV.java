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
 * Proceso para reporte RV del Mayor Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/28/19.
 */
public class MayorContableRV extends SvrProcess {

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


    @Override
    protected void prepare() {

        this.mayorProcessor = new MayorContable(getCtx(), null);

        ProcessInfoParameter[] para = getParameter();

        for (int i = 0; i < para.length; i++){

            String name = para[i].getParameterName();

            if (name != null){
                if (name.trim().equalsIgnoreCase("AD_Client_ID")){
                    this.mayorProcessor.adClientID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
                else if (name.trim().equalsIgnoreCase("AD_Org_ID")){
                    this.mayorProcessor.adOrgID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
                else if (name.trim().equalsIgnoreCase("C_AcctSchema_ID")){
                    this.mayorProcessor.cAcctSchemaID = ((BigDecimal)para[i].getParameter()).intValueExact();
                }
                else if (name.trim().equalsIgnoreCase("C_Currency_ID")){
                    if (para[i].getParameter() != null){
                        this.mayorProcessor.cCurrencyID = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
                else if (name.trim().equalsIgnoreCase("C_Currency_2_ID")){
                    if (para[i].getParameter() != null){
                        this.mayorProcessor.cCurrencyID_2 = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
                else if (name.trim().equalsIgnoreCase("TipoFiltroMonAcct")){
                    this.mayorProcessor.tipoFiltroMonAcct = (String)para[i].getParameter();
                }

                else if (name.trim().equalsIgnoreCase("TextoFiltro")){
                    if (para[i].getParameter() != null){
                        this.mayorProcessor.textoFiltroCuentas = ((String)para[i].getParameter()).trim();
                    }
                }
                else if (name.trim().equalsIgnoreCase("C_BPartner_ID")){
                    if (para[i].getParameter() != null){
                        this.mayorProcessor.cBPartnerID = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
                else if (name.trim().equalsIgnoreCase("M_Product_ID")){
                    if (para[i].getParameter() != null){
                        this.mayorProcessor.mProductID = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }
                else if (name.trim().equalsIgnoreCase("C_Activity_ID")){
                    if (para[i].getParameter() != null){
                        this.mayorProcessor.cActivityID = ((BigDecimal)para[i].getParameter()).intValueExact();
                    }
                }

                else if (name.trim().equalsIgnoreCase("DateAcct")){
                    this.mayorProcessor.startDate = (Timestamp)para[i].getParameter();
                    this.mayorProcessor.endDate = (Timestamp)para[i].getParameter_To();
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

        this.mayorProcessor.adUserID = this.getAD_User_ID();
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
