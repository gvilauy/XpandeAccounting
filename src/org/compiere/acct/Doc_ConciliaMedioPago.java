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
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 4/20/21.
 */
public class Doc_ConciliaMedioPago extends Doc {

    private MZConciliaMedioPago conciliaMedioPago = null;
    private MDocType docType = null;

    /***
     * Constructor
     * @param ass
     * @param clazz
     * @param rs
     * @param defaultDocumentType
     * @param trxName
     */
    public Doc_ConciliaMedioPago(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /***
     * Constructor
     * @param ass
     * @param rs
     * @param trxName
     */
    public Doc_ConciliaMedioPago(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZConciliaMedioPago.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.conciliaMedioPago = (MZConciliaMedioPago) getPO();
        setDateDoc(this.conciliaMedioPago.getDateDoc());
        setDateAcct(this.conciliaMedioPago.getDateAcct());
        setAmount(Doc.AMTTYPE_Gross, this.conciliaMedioPago.getTotalAmt());

        this.docType = (MDocType) this.conciliaMedioPago.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        return null;
    }

    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 4/20/21.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZConciliaMPagoLin> conciliaMPagoLinList = this.conciliaMedioPago.getLines();

        for (MZConciliaMPagoLin conciliaMPagoLin: conciliaMPagoLinList){
            DocLine docLine = new DocLine(conciliaMPagoLin, this);
            docLine.setAmount(conciliaMPagoLin.getTotalAmt());
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }

    @Override
    public BigDecimal getBalance() {

        BigDecimal retValue = Env.ZERO;
        StringBuffer sb = new StringBuffer (" [");

        //  Total
        retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
        sb.append(getAmount(Doc.AMTTYPE_Gross));

        //  Lineas
        for (int i = 0; i < p_lines.length; i++)
        {
            retValue = retValue.subtract(p_lines[i].getAmtSource());
            sb.append("-").append(p_lines[i].getAmtSource());
        }

        sb.append("]");

        log.fine(toString() + " Balance=" + retValue + sb.toString());

        return retValue;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        // DR - Total del documento - Cuenta contable de la cuenta bancaria (InTransit)
        int accountDRID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankInTransit, this.conciliaMedioPago.getC_BankAccount_ID(), as, null);;
        if (accountDRID <= 0){
            MBankAccount bankAccount = (MBankAccount) this.conciliaMedioPago.getC_BankAccount();
            p_Error = "No se obtuvo Cuenta Contable (BankInTransit) asociada a la Cuenta Bancaria : " + bankAccount.getName();
            log.log(Level.SEVERE, p_Error);
            facts.add(null);
            return facts;
        }

        MAccount acctBankCr = MAccount.get(getCtx(), accountDRID);
        FactLine fl1 = fact.createLine (null, acctBankCr, getC_Currency_ID(), grossAmt, null);
        if (fl1 != null){
            fl1.setAD_Org_ID(this.conciliaMedioPago.getAD_Org_ID());
        }

        // CR - Monto de Lineas del documento - Cuenta contable de la cuenta bancaria (Asset)
        for (int i = 0; i < p_lines.length; i++){

            BigDecimal amt = p_lines[i].getAmtSource();

            MZConciliaMPagoLin conciliaMPagoLin = new MZConciliaMPagoLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());
            MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) conciliaMPagoLin.getZ_MedioPagoItem();

            int accountCRID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankAsset, this.conciliaMedioPago.getC_BankAccount_ID(), as, null);;
            if (accountCRID <= 0){
                MBankAccount bankAccount = (MBankAccount) this.conciliaMedioPago.getC_BankAccount();
                p_Error = "No se obtuvo Cuenta Contable (BankAsset) asociada a la Cuenta Bancaria : " + bankAccount.getName();
                log.log(Level.SEVERE, p_Error);
                facts.add(null);
                return facts;
            }

            FactLine fl2 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountCRID), getC_Currency_ID(), null, amt);
            if (fl2 != null){
                fl2.setAD_Org_ID(this.conciliaMedioPago.getAD_Org_ID());
            }

            // Detalle de asiento
            if (fl2 != null){
                fl2.saveEx();

                MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                factDet.setFact_Acct_ID(fl2.get_ID());
                factDet.setAD_Org_ID(this.conciliaMedioPago.getAD_Org_ID());
                factDet.setZ_ConciliaMedioPago_ID(this.conciliaMedioPago.get_ID());
                factDet.setZ_MedioPago_ID(conciliaMPagoLin.getZ_MedioPago_ID());
                factDet.setC_BankAccount_ID(this.conciliaMedioPago.getC_BankAccount_ID());
                factDet.setNroMedioPago(conciliaMPagoLin.getDocumentNoRef());
                if (conciliaMPagoLin.getZ_MedioPagoItem_ID() > 0){
                    factDet.setZ_MedioPagoItem_ID(conciliaMPagoLin.getZ_MedioPagoItem_ID());
                    if (medioPagoItem != null){
                        if (medioPagoItem.getNroMedioPago() != null){
                            factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                        }
                    }
                }
                factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_CONCILIADO);
                factDet.setDueDate(conciliaMPagoLin.getDueDate());
                factDet.saveEx();
            }
        }

        facts.add(fact);
        return facts;
    }
}
