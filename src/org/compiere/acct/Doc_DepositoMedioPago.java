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
 * Xpande. Created by Gabriel Vila on 9/6/19.
 */
public class Doc_DepositoMedioPago extends Doc {

    private MZDepositoMedioPago depositoMedioPago = null;
    private MDocType docType = null;

    /***
     * Constructor
     * @param ass
     * @param clazz
     * @param rs
     * @param defaultDocumentType
     * @param trxName
     */
    public Doc_DepositoMedioPago(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /***
     * Constructor
     * @param ass
     * @param rs
     * @param trxName
     */
    public Doc_DepositoMedioPago(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZDepositoMedioPago.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.depositoMedioPago = (MZDepositoMedioPago) getPO();
        setDateDoc(this.depositoMedioPago.getDateDoc());
        setDateAcct(this.depositoMedioPago.getDateAcct());
        setAmount(Doc.AMTTYPE_Gross, this.depositoMedioPago.getTotalAmt());

        this.docType = (MDocType) this.depositoMedioPago.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        return null;
    }

    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 8/28/18.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZDepositoMPagoLin> depositoMPagoLinList = this.depositoMedioPago.getLines();

        for (MZDepositoMPagoLin depositoMPagoLin: depositoMPagoLinList){
            DocLine docLine = new DocLine(depositoMPagoLin, this);
            docLine.setAmount(depositoMPagoLin.getTotalAmt());
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

        // DR - Total del documento - Cuenta contable de la cuenta bancaria o caja destino del deposito
        int accountID = -1;
        if (this.depositoMedioPago.getC_BankAccount_ID() > 0){
            accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankAsset, this.depositoMedioPago.getC_BankAccount_ID(), as, null);;
            if (accountID <= 0){
                MBankAccount bankAccount = (MBankAccount) this.depositoMedioPago.getC_BankAccount();
                p_Error = "No se obtuvo Cuenta Contable (BankAsset) asociada a la Cuenta Bancaria : " + bankAccount.getName();
                log.log(Level.SEVERE, p_Error);
                facts.add(null);
                return facts;
            }
        }
        else if (this.depositoMedioPago.getC_CashBook_ID() > 0){
            accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_CashExpense, this.depositoMedioPago.getC_CashBook_ID(), as, null);;
            if (accountID <= 0){
                MCashBook cashBook = (MCashBook) this.depositoMedioPago.getC_CashBook();
                p_Error = "No se obtuvo Cuenta Contable (CashExpense) asociada a la caja : " + cashBook.getName();
                log.log(Level.SEVERE, p_Error);
                facts.add(null);
                return facts;
            }
        }

        MAccount acctBankCr = MAccount.get(getCtx(), accountID);
        FactLine fl1 = fact.createLine (null, acctBankCr, getC_Currency_ID(), grossAmt, null);
        if (fl1 != null){
            fl1.setAD_Org_ID(this.depositoMedioPago.getAD_Org_ID());
        }

        // CR - Monto de Lineas del documento - Cuenta contable del medio de pago de la linea
        for (int i = 0; i < p_lines.length; i++){

            BigDecimal amt = p_lines[i].getAmtSource();

            MZDepositoMPagoLin depositoMPagoLin = new MZDepositoMPagoLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());
            MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) depositoMPagoLin.getZ_MedioPagoItem();

            int accountMpID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Recibidos, depositoMPagoLin.getZ_MedioPago_ID(), getC_Currency_ID(), as, null);
            if (accountMpID <= 0){
                MZMedioPago medioPago = (MZMedioPago) depositoMPagoLin.getZ_MedioPago();
                p_Error = "No se obtuvo Cuenta Contable (MP_Recibidos) asociada al medio de pago : " + medioPago.getName();
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            FactLine fl2 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountMpID), getC_Currency_ID(), null, amt);
            if (fl2 != null){
                fl2.setAD_Org_ID(this.depositoMedioPago.getAD_Org_ID());
            }

            // Detalle de asiento
            if (fl2 != null){
                fl2.saveEx();
                MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                factDet.setFact_Acct_ID(fl2.get_ID());
                factDet.setAD_Org_ID(this.depositoMedioPago.getAD_Org_ID());
                factDet.setZ_DepositoMedioPago_ID(this.depositoMedioPago.get_ID());
                factDet.setZ_MedioPago_ID(depositoMPagoLin.getZ_MedioPago_ID());
                if (depositoMPagoLin.getC_Bank_ID() > 0){
                    factDet.setC_Bank_ID(depositoMPagoLin.getC_Bank_ID());
                }

                factDet.setNroMedioPago(depositoMPagoLin.getDocumentNoRef());

                if (depositoMPagoLin.getZ_MedioPagoItem_ID() > 0){
                    factDet.setZ_MedioPagoItem_ID(depositoMPagoLin.getZ_MedioPagoItem_ID());
                    if (medioPagoItem != null){
                        if (medioPagoItem.getNroMedioPago() != null){
                            factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                        }
                    }
                }
                factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_DEPOSITADO);
                factDet.setDueDate(depositoMPagoLin.getDueDate());
                factDet.saveEx();
            }
        }

        facts.add(fact);
        return facts;
    }

}
