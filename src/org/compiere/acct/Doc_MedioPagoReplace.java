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
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 2/7/19.
 */
public class Doc_MedioPagoReplace extends Doc {

    private MZMedioPagoReplace medioPagoReplace = null;
    private MDocType docType = null;

    /**
     *  Constructor
     */
    public Doc_MedioPagoReplace(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_MedioPagoReplace(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZMedioPagoReplace.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.medioPagoReplace = (MZMedioPagoReplace) getPO();
        setDateDoc(this.medioPagoReplace.getDateDoc());
        setDateAcct(this.medioPagoReplace.getDateDoc());
        setC_Currency_ID(this.medioPagoReplace.getC_Currency_ID());

        this.docType = (MDocType) this.medioPagoReplace.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        log.fine("Lines=" + p_lines.length);

        return null;
    }

    @Override
    public BigDecimal getBalance() {

        // En este documento muevo importes balanceados.
        // Por lo tanto siempre esta balanceado.

        BigDecimal retValue = Env.ZERO;
        return retValue;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        // Recorro medios de pago a reemplazar (los viejos)
        for (int i = 0; i < p_lines.length; i++){

            BigDecimal amt = p_lines[i].getAmtSource();

            MZMedioPagoReplaceLin replaceLin = new MZMedioPagoReplaceLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            // Si hay un pago asociado a este medio de pago
            if (replaceLin.getZ_Pago_ID() > 0){

                if (replaceLin.getZ_MedioPagoItem_ID() > 0){

                    // Doy vuelta asiento de este medio de pago que se hizo en el documento de pago.
                    MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) replaceLin.getZ_MedioPagoItem();

                    // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta del medio de pago a emitir
                    int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
                    FactLine fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), null, amt);

                    // Detalle de asiento
                    if (fl1 != null){
                        fl1.saveEx();
                        MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                        factDet.setFact_Acct_ID(fl1.get_ID());
                        factDet.setAD_Org_ID(this.medioPagoReplace.getAD_Org_ID());
                        factDet.setZ_Pago_ID(medioPagoItem.getZ_Pago_ID());
                        factDet.setZ_MedioPagoReplace_ID(this.medioPagoReplace.get_ID());
                        factDet.setZ_MedioPago_ID(replaceLin.getZ_MedioPago_ID());
                        if (replaceLin.getC_BankAccount_ID() > 0){
                            factDet.setC_BankAccount_ID(replaceLin.getC_BankAccount_ID());
                            factDet.setC_Bank_ID(replaceLin.getC_BankAccount().getC_Bank_ID());
                        }

                        factDet.setZ_MedioPagoItem_ID(medioPagoItem.get_ID());
                        factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                        factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                        factDet.setCurrencyRate(Env.ONE);
                        factDet.setDueDate(replaceLin.getDueDate());
                        factDet.saveEx();
                    }

                    // DR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria.
                    int accountID = -1;
                    if (replaceLin.getC_BankAccount_ID() > 0){
                        accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankInTransit, replaceLin.getC_BankAccount_ID(), as, null);
                    }
                    else{
                        if (replaceLin.getZ_MedioPago_ID() > 0){
                            accountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Entregados, replaceLin.getZ_MedioPago_ID(), replaceLin.getC_Currency_ID(), as, null);
                        }
                        else{
                            p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                        }
                    }
                    if (accountID > 0){
                        MAccount acctBankCr = MAccount.get(getCtx(), accountID);
                        FactLine fl2 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), amt, null);

                        // Detalle de asiento
                        if (fl2 != null){
                            fl2.saveEx();
                            MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                            factDet.setFact_Acct_ID(fl2.get_ID());
                            factDet.setAD_Org_ID(this.medioPagoReplace.getAD_Org_ID());
                            factDet.setZ_Pago_ID(medioPagoItem.getZ_Pago_ID());
                            factDet.setZ_MedioPagoReplace_ID(this.medioPagoReplace.get_ID());
                            factDet.setZ_MedioPago_ID(replaceLin.getZ_MedioPago_ID());
                            if (replaceLin.getC_BankAccount_ID() > 0){
                                factDet.setC_BankAccount_ID(replaceLin.getC_BankAccount_ID());
                                factDet.setC_Bank_ID(replaceLin.getC_BankAccount().getC_Bank_ID());
                            }

                            factDet.setZ_MedioPagoItem_ID(medioPagoItem.get_ID());
                            factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                            factDet.setCurrencyRate(Env.ONE);
                            factDet.setDueDate(replaceLin.getDueDate());
                            factDet.saveEx();
                        }
                    }
                }
            }
        }

        facts.add(fact);
        return facts;
    }


    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 8/28/18.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZMedioPagoReplaceLin> replaceLinList = this.medioPagoReplace.getLines();

        for (MZMedioPagoReplaceLin replaceLin: replaceLinList){

            DocLine docLine = new DocLine(replaceLin, this);
            docLine.setAmount(replaceLin.getTotalAmt());
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }


}
