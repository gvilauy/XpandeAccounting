package org.xpande.acct.model;

import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.comercial.model.MZComercialConfig;

import java.math.BigDecimal;

/**
 * Validador de modelos para el modulo Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/25/18.
 */
public class ValidatorAccounting implements ModelValidator {

    private int adClientID = 0;

    @Override
    public void initialize(ModelValidationEngine engine, MClient client) {

        // Guardo compañia
        if (client != null){
            this.adClientID = client.get_ID();
        }

        // DB Validations
        engine.addModelChange(X_Fact_Acct.Table_Name, this);
        engine.addModelChange(I_C_Invoice.Table_Name, this);

    }

    @Override
    public int getAD_Client_ID() {
        return this.adClientID;
    }

    @Override
    public String login(int AD_Org_ID, int AD_Role_ID, int AD_User_ID) {
        return null;
    }

    @Override
    public String modelChange(PO po, int type) throws Exception {

        if (po.get_TableName().equalsIgnoreCase(I_Fact_Acct.Table_Name)){
            return modelChange((FactLine) po, type);
        }
        else if (po.get_TableName().equalsIgnoreCase(I_C_Invoice.Table_Name)){
            return modelChange((MInvoice) po, type);
        }

        return null;
    }

    @Override
    public String docValidate(PO po, int timing) {
        return null;
    }


    /***
     * Validaciones para el modelo de linea contable (Tabla Fact_Acct)
     * Xpande. Created by Gabriel Vila on 11/25/18.
     * @param model
     * @param type
     * @return
     * @throws Exception
     */
    public String modelChange(FactLine model, int type) throws Exception {

        String message = null, action = "";

        // Seteo tasa de cambio cuando sea necesario
        if ((type == ModelValidator.TYPE_AFTER_NEW) || (type == ModelValidator.TYPE_AFTER_CHANGE)){

            boolean obtenerTasa = true;

            // Si es un cambio de datos y no es la columna de fecha contable, no hago nada.
            if (type == ModelValidator.TYPE_AFTER_CHANGE){
                if (!model.is_ValueChanged(X_Fact_Acct.COLUMNNAME_DateAcct)){
                    return null;
                }
            }

            // Si ya tengo seteada una tasa de cambio, la respeto y salgo sin hacer nada.
            BigDecimal currencyRate = (BigDecimal) model.get_Value("CurrencyRate");
            if ((currencyRate != null) && (currencyRate.compareTo(Env.ZERO) > 0)){
                return null;
            }

            // Obtengo tasa para fecha contable cuando la moneda origen es distinta de la moneda del esquema contable.
            MAcctSchema as = (MAcctSchema) model.getC_AcctSchema();

            if (model.getC_Currency_ID() != as.getC_Currency_ID()){
                currencyRate = MConversionRate.getRate(model.getC_Currency_ID(), as.getC_Currency_ID(), model.getDateAcct(), 114, model.getAD_Client_ID(), 0);
            }
            else{
                // Moneda origen es igual a la moneda del esquema contable, tasa = 1.
                currencyRate = Env.ONE;
            }

            // Actualizo tasa si tengo valor
            if ((currencyRate != null) && (currencyRate.compareTo(Env.ZERO) > 0)){
                action = " update fact_acct set currencyrate =" + currencyRate + " where fact_acct_id =" + model.get_ID();
                DB.executeUpdateEx(action, model.get_TrxName());
            }


        }

        return message;
    }


    /***
     * Validaciones para el modelo de Invoices en contabilidad.
     * Xpande. Created by Gabriel Vila on 11/26/18.
     * @param model
     * @param type
     * @return
     * @throws Exception
     */
    public String modelChange(MInvoice model, int type) throws Exception {

        String mensaje = null, action = "";

        if ((type == ModelValidator.TYPE_AFTER_NEW) || (type == ModelValidator.TYPE_AFTER_CHANGE)){


            // Para comprobantes de compra, si el socio de negocio esta marcado para permitir asiento contable manual en comprobantes de compra,
            // dejo esta marca en este comprobante.
            if (!model.isSOTrx()){
                if (model.getC_BPartner_ID() > 0){

                    boolean canUpdateFlagAsiento = true;

                    // Si entro por modificación, pero no se modificó el socio de negocio, no hago nada.
                    if ((type == ModelValidator.TYPE_AFTER_CHANGE)){
                        if (!model.is_ValueChanged(X_C_Invoice.COLUMNNAME_C_BPartner_ID)){
                            canUpdateFlagAsiento = false;
                        }
                    }

                    if (canUpdateFlagAsiento){
                        MBPartner partner = (MBPartner) model.getC_BPartner();
                        if (partner.get_ValueAsBoolean("AsientoManualInvoice")){
                            action = " update c_invoice set AsientoManualInvoice ='Y' " +
                                    " where c_invoice_id =" + model.get_ID();
                            DB.executeUpdateEx(action, model.get_TrxName());
                        }
                        else {
                            if (model.get_ValueAsBoolean("AsientoManualInvoice")){
                                action = " update c_invoice set AsientoManualInvoice ='N' " +
                                        " where c_invoice_id =" + model.get_ID();
                                DB.executeUpdateEx(action, model.get_TrxName());
                            }
                        }
                    }
                }
            }
        }


        return mensaje;
    }

}
