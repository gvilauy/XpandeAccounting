package org.xpande.acct.process;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MJournal;
import org.compiere.model.MJournalLine;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.MZGeneraOrdenPago;
import org.xpande.financial.model.X_Z_MedioPagoFolio;

import java.math.BigDecimal;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/16/18.
 */
public class CopiarAsiento extends SvrProcess {

    private MJournal journalOrigen = null;
    private MJournal journalDestino = null;
    private String documentNo = null;

    @Override
    protected void prepare() {

        ProcessInfoParameter[] para = getParameter();

        for (int i = 0; i < para.length; i++){

            String name = para[i].getParameterName();

            if (name != null){
                if (para[i].getParameter() != null){
                    if (name.trim().equalsIgnoreCase("DocumentNoRef")){
                        this.documentNo = para[i].getParameter().toString().trim();
                    }
                }
            }
        }

        if (this.documentNo != null){
            this.journalOrigen = AccountUtils.getJournalByDocumentNo(this.getCtx(), this.documentNo, null);
        }

        this.journalDestino = new MJournal(this.getCtx(), this.getRecord_ID(), this.get_TrxName());
    }

    @Override
    protected String doIt() throws Exception {

        try{

            if ((this.journalOrigen == null) || (this.journalOrigen.get_ID() <= 0)){
                return "@Error@ " + "No existe Asiento Contable Manual con el Número :" + this.documentNo;
            }

            // Onbtengo lineas del asiento origen y las recorro.
            MJournalLine[] lines = this.journalOrigen.getLines(true);
            for (int i = 0; i < lines.length; i++){
                MJournalLine journalLineOrigen = lines[i];

                MJournalLine journalLineDest = new MJournalLine(this.journalDestino);
                journalLineDest.setAD_Org_ID(this.journalDestino.getAD_Org_ID());
                journalLineDest.setAccount_ID(journalLineOrigen.getAccount_ID());
                journalLineDest.setC_Currency_ID(journalLineOrigen.getC_Currency_ID());
                journalLineDest.setDescription(journalLineOrigen.getDescription());
                journalLineDest.setAmtAcct(Env.ZERO, Env.ZERO);
                journalLineDest.setC_BPartner_ID(journalLineOrigen.getC_BPartner_ID());
                journalLineDest.setAmtAcctCr(Env.ZERO);
                journalLineDest.setAmtAcctDr(Env.ZERO);
                journalLineDest.setAmtSourceCr(Env.ZERO);
                journalLineDest.setAmtSourceDr(Env.ZERO);
                journalLineDest.setC_UOM_ID(journalLineOrigen.getC_UOM_ID());
                journalLineDest.setQty(journalLineOrigen.getQty());
                journalLineDest.setC_ValidCombination_ID(journalLineOrigen.getC_ValidCombination_ID());
                journalLineDest.setM_Product_ID(journalLineOrigen.getM_Product_ID());

                journalLineDest.saveEx();
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return "OK";
    }
}
