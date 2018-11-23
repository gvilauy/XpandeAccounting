package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.Doc;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.comercial.model.MZInvoiceTaxManual;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/23/18.
 */
public class ValidatorAccounting implements FactsValidator {

    private int adClientID = 0;

    @Override
    public int getAD_Client_ID() {
        return this.adClientID;
    }

    @Override
    public String factsValidate(MAcctSchema schema, List<Fact> facts, PO po) {

        String message = null;
        String sql = "";

        try{

            if (po instanceof MInvoice){

                MInvoice invoice = (MInvoice) po;

                // Si es invoice de compra
                if (!invoice.isSOTrx()){

                    this.adClientID = invoice.getAD_Client_ID();

                    BigDecimal amtManualTaxNoAplicaTotal = Env.ZERO;

                    // Obtengo si tengo impuestos manuales asociados a esta invoice
                    List<MZInvoiceTaxManual> taxManualList = MZInvoiceTaxManual.getManualTaxes(po.getCtx(), invoice.get_ID(), po.get_TrxName());
                    for (MZInvoiceTaxManual taxManual: taxManualList){
                        MTax tax = (MTax) taxManual.getC_Tax();
                        if (tax.get_ValueAsString("ManualTaxAction") != null){
                            if (tax.get_ValueAsString("ManualTaxAction").trim().equalsIgnoreCase("NO APLICA")){
                                amtManualTaxNoAplicaTotal = amtManualTaxNoAplicaTotal.add(taxManual.getTaxAmt());
                            }
                        }
                    }

                    if (amtManualTaxNoAplicaTotal.compareTo(Env.ZERO) > 0){

                        MAccount account = null;

                        // Recorro patas del asiento para buscar una cuenta de mercadería.
                        for (Fact fact: facts){
                            FactLine[] factLines = fact.getLines();
                            for (int i = 0; i < factLines.length; i ++){
                                FactLine factLine = factLines[i];
                                if (factLine.getM_Product_ID() > 0){
                                    account = factLine.getAccount();
                                }
                            }

                            if (account != null){
                                MDocType docType = (MDocType) invoice.getC_DocTypeTarget();
                                if (docType.getDocBaseType().equalsIgnoreCase(Doc.DOCTYPE_APInvoice)){
                                    fact.createLine(null, account, invoice.getC_Currency_ID(), null, amtManualTaxNoAplicaTotal);

                                }
                                else if (docType.getDocBaseType().equalsIgnoreCase(Doc.DOCTYPE_APCredit)){
                                    fact.createLine(null, account, invoice.getC_Currency_ID(), amtManualTaxNoAplicaTotal, null);
                                }
                            }
                        }
                    }
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }
}
