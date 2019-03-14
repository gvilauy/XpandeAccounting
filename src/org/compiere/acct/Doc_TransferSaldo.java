package org.compiere.acct;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
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
 * Contabilización de documentos de Transferencias de Saldos para pagos y cobros.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 3/14/19.
 */
public class Doc_TransferSaldo extends Doc {

    private MZTransferSaldo transferSaldo = null;
    private MDocType docType = null;

    /**
     * Contructor
     * @param ass
     * @param clazz
     * @param rs
     * @param defaultDocumentType
     * @param trxName
     */
    public Doc_TransferSaldo(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     * Constructor
     * @param ass
     * @param rs
     * @param trxName
     */
    public Doc_TransferSaldo(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZTransferSaldo.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.transferSaldo = (MZTransferSaldo) getPO();
        setDateDoc(this.transferSaldo.getDateDoc());
        setDateAcct(this.transferSaldo.getDateDoc());
        setC_Currency_ID(this.transferSaldo.getC_Currency_ID());
        setC_BPartner_ID(this.transferSaldo.getC_BPartner_ID());
        setAmount(Doc.AMTTYPE_Gross, this.transferSaldo.getGrandTotal());

        this.docType = (MDocType) this.transferSaldo.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        return null;
    }

    @Override
    public BigDecimal getBalance() {

        // Siempre esta balanceado porque es un único importe para debito y credito.
        return Env.ZERO;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        // Contabilización de una transferencia de saldo para pago.
        if (!this.transferSaldo.isSOTrx()){

            // CR - Cuenta contable para el socio de negocio destino de la transferencia de saldo
            setC_BPartner_ID(this.transferSaldo.getC_BPartner_ID());
            int accountID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);

            if (accountID <= 0){
                p_Error = "Falta parametrizar cuenta para socio de negocio.";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, grossAmt);

            // DR - Cuenta contable para el socio de negocio referenciado
            setC_BPartner_ID(this.transferSaldo.getC_BPartnerRelation_ID());
            accountID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);

            if (accountID <= 0){
                p_Error = "Falta parametrizar cuenta para socio de negocio referenciado.";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), grossAmt, null);

        }
        else{
            // Contabilización de una transferencia de saldo para cobro.

            // DR - Cuenta contable para el socio de negocio destino de la transferencia de saldo
            setC_BPartner_ID(this.transferSaldo.getC_BPartner_ID());
            int accountID = getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);

            if (accountID <= 0){
                p_Error = "Falta parametrizar cuenta para socio de negocio.";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), grossAmt, null);

            // CR - Cuenta contable para el socio de negocio referenciado
            setC_BPartner_ID(this.transferSaldo.getC_BPartnerRelation_ID());
            accountID = getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);

            if (accountID <= 0){
                p_Error = "Falta parametrizar cuenta para socio de negocio referenciado.";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, grossAmt);

        }

        facts.add(fact);
        return facts;
    }

}
