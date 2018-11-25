package org.compiere.acct;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBankAccount;
import org.compiere.model.MDocType;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.MZEmisionMedioPago;
import org.xpande.financial.model.MZPago;
import org.xpande.financial.model.MZPagoMedioPago;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;

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

        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        // Obtengo numero de medio de pago para luego guardarlo asociado a cada pata del asiento contable.
        String nroMedioPago = null;
        if (this.emisionMedioPago.getZ_MedioPagoItem_ID() > 0){
            nroMedioPago = this.emisionMedioPago.getZ_MedioPagoItem().getNroMedioPago();
        }
        else{
            nroMedioPago = this.emisionMedioPago.getReferenceNo();
        }


        // DR : Monto de la emisión - Cuenta del Socio de Negocio
        int payables_ID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), payables_ID), getC_Currency_ID(), grossAmt, null);

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

            factDet.setNroMedioPago(nroMedioPago);
            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_EMITIDO);
            factDet.setCurrencyRate(this.emisionMedioPago.getCurrencyRate());
            factDet.setDueDate(this.emisionMedioPago.getDueDate());
            factDet.saveEx();
        }

        // CR - Monto de la emisión - Cuenta del medio de pago a emitir
        int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
        FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), null, grossAmt);

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

            factDet.setNroMedioPago(nroMedioPago);
            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_EMITIDO);
            factDet.setCurrencyRate(this.emisionMedioPago.getCurrencyRate());
            factDet.setDueDate(this.emisionMedioPago.getDueDate());
            factDet.saveEx();
        }

        facts.add(fact);
        return facts;
    }

}
