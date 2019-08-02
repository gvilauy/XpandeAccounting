package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 10/8/18.
 */
public class Doc_EmisionMedioPago extends Doc {

    private MZEmisionMedioPago emisionMedioPago = null;
    private MDocType docType = null;

    /***
     * Constructor
     */
    public Doc_EmisionMedioPago(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_EmisionMedioPago(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZEmisionMedioPago.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.emisionMedioPago = (MZEmisionMedioPago) getPO();
        setDateDoc(emisionMedioPago.getDateDoc());
        setDateAcct(emisionMedioPago.getDateDoc());
        setC_Currency_ID(emisionMedioPago.getC_Currency_ID());
        setC_BPartner_ID(emisionMedioPago.getC_BPartner_ID());
        setAmount(Doc.AMTTYPE_Gross, this.emisionMedioPago.getTotalAmt());

        this.docType = (MDocType) emisionMedioPago.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        setAmount(Doc.AMTTYPE_Charge, this.emisionMedioPago.getChargeAmt());

        return null;
    }

    @Override
    public BigDecimal getBalance() {

        // En una emisión de pago hay un solo monto, ya que no hay lineas.
        // Por lo tanto siempre esta balanceado.

        BigDecimal retValue = Env.ZERO;
        return retValue;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        int cChargeID = this.emisionMedioPago.getC_Charge_ID();
        BigDecimal amtCharge = this.emisionMedioPago.getChargeAmt();
        if (amtCharge == null) amtCharge = Env.ZERO;

        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        // Obtengo modelo de banco asaociado a cuenta bancaria de este medio de pago
        MBank bank = null;
        if (this.emisionMedioPago.getC_BankAccount_ID() > 0){
            bank = (MBank) this.emisionMedioPago.getC_BankAccount().getC_Bank();
        }

        // Obtengo numero de medio de pago para luego guardarlo asociado a cada pata del asiento contable.
        String nroMedioPago = null;
        if (this.emisionMedioPago.getZ_MedioPagoItem_ID() > 0){

            MZMedioPagoItem pagoItem = (MZMedioPagoItem) this.emisionMedioPago.getZ_MedioPagoItem();
            nroMedioPago = pagoItem.getNroMedioPago();
            if (pagoItem.getDocumentSerie() != null){
                if ((bank != null) && (bank.get_ID() > 0)){
                    if (bank.get_ValueAsBoolean("IncSerieConcilia")){
                        nroMedioPago = pagoItem.getDocumentSerie().trim() + nroMedioPago;
                    }
                }
            }
        }
        else{
            nroMedioPago = this.emisionMedioPago.getReferenceNo();
        }

        // DR : Monto de la emisión - Cuenta Medios de Pago Pendientes de Entrega (cuenta puente)
        int emiPendEnt_ID = getValidCombination_ID (Doc.ACCTYPE_MP_EmiPendEnt, as);
        if (emiPendEnt_ID <= 0){
            p_Error = "Falta parametrizar Cuenta Contable para Medio de Pago Emitido y Pendiente de Entrega en moneda de este Documento.";
            log.log(Level.SEVERE, p_Error);
            fact = null;
            facts.add(fact);
            return facts;
        }

        FactLine fl1 = null;

        if (!emisionMedioPago.isExtornarAcct()){
            fl1 = fact.createLine(null, MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), grossAmt.add(amtCharge), null);
        }
        else{
            fl1 = fact.createLine(null, MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), null, grossAmt.add(amtCharge));
        }
        // Guardo detalles asociados a esta pata del asiento contable
        if (fl1 != null){
            fl1.saveEx();
            MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
            factDet.setFact_Acct_ID(fl1.get_ID());
            factDet.setAD_Org_ID(this.emisionMedioPago.getAD_Org_ID());
            factDet.setZ_EmisionMedioPago_ID(this.emisionMedioPago.get_ID());
            factDet.setZ_MedioPago_ID(this.emisionMedioPago.getZ_MedioPago_ID());

            if (this.emisionMedioPago.getC_BankAccount_ID() > 0){
                factDet.setC_BankAccount_ID(this.emisionMedioPago.getC_BankAccount_ID());
                factDet.setC_Bank_ID(this.emisionMedioPago.getC_BankAccount().getC_Bank_ID());
            }

            if (this.emisionMedioPago.getZ_MedioPagoItem_ID() > 0){
                factDet.setZ_MedioPagoItem_ID(this.emisionMedioPago.getZ_MedioPagoItem_ID());
            }

            factDet.setNroMedioPago(nroMedioPago);
            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_EMITIDO);
            factDet.setCurrencyRate(this.emisionMedioPago.getCurrencyRate());
            factDet.setDueDate(this.emisionMedioPago.getDueDate());
            factDet.saveEx();
        }

        // CR - Monto de la emisión - Cuenta del medio de pago a emitir
        int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
        if (mpEmitidos_ID <= 0){
            p_Error = "Falta parametrizar Cuenta Contable para Medio de Pago Emitido en moneda de este Documento.";
            log.log(Level.SEVERE, p_Error);
            fact = null;
            facts.add(fact);
            return facts;
        }

        FactLine fl2 = null;

        if (!emisionMedioPago.isExtornarAcct()){
            fl2 = fact.createLine(null, MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), null, grossAmt);
        }
        else{
            fl2 = fact.createLine(null, MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), grossAmt, null);
        }

        // Guardo detalles asociados a esta pata del asiento contable
        if (fl2 != null){
            fl2.saveEx();
            MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
            factDet.setFact_Acct_ID(fl2.get_ID());
            factDet.setAD_Org_ID(this.emisionMedioPago.getAD_Org_ID());
            factDet.setZ_EmisionMedioPago_ID(this.emisionMedioPago.get_ID());
            factDet.setZ_MedioPago_ID(this.emisionMedioPago.getZ_MedioPago_ID());

            if (this.emisionMedioPago.getC_BankAccount_ID() > 0){
                factDet.setC_BankAccount_ID(this.emisionMedioPago.getC_BankAccount_ID());
                factDet.setC_Bank_ID(this.emisionMedioPago.getC_BankAccount().getC_Bank_ID());
            }

            if (this.emisionMedioPago.getZ_MedioPagoItem_ID() > 0){
                factDet.setZ_MedioPagoItem_ID(this.emisionMedioPago.getZ_MedioPagoItem_ID());
            }

            factDet.setNroMedioPago(nroMedioPago);
            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_EMITIDO);
            factDet.setCurrencyRate(this.emisionMedioPago.getCurrencyRate());
            factDet.setDueDate(this.emisionMedioPago.getDueDate());
            factDet.saveEx();
        }


        // CR - Monto de cargos contables si tengo
        if ((cChargeID > 0) && (amtCharge.compareTo(Env.ZERO) != 0)){

            FactLine flCharge = null;
            if (!emisionMedioPago.isExtornarAcct()){
                flCharge = fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as), getC_Currency_ID(), null, amtCharge);
            }
            else{
                flCharge = fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as), getC_Currency_ID(), amtCharge, null);
            }

            if (flCharge != null){
                flCharge.setAD_Org_ID(this.emisionMedioPago.getAD_Org_ID());
            }
        }


        facts.add(fact);
        return facts;
    }

}
