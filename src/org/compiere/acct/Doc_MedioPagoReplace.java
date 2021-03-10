package org.compiere.acct;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.adempiere.pdf.Document;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MBank;
import org.compiere.model.MDocType;
import org.compiere.process.DocumentEngine;
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

        setAmount(Doc.AMTTYPE_Charge, this.medioPagoReplace.getChargeAmt());

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

            setC_BPartner_ID(replaceLin.getC_BPartner_ID());

            String nroMedioPagoOLD = null;
            int OLD_emisionMedioPago_ID = 0;
            if (replaceLin.getZ_MedioPagoItem_ID() > 0){

                MZMedioPagoItem pagoItem = (MZMedioPagoItem) replaceLin.getZ_MedioPagoItem();
                nroMedioPagoOLD = pagoItem.getNroMedioPago();
                if (pagoItem.getDocumentSerie() != null){
                    if (pagoItem.getC_BankAccount_ID() > 0){
                        if (pagoItem.getC_BankAccount().getC_Bank_ID() > 0){
                            if (((MBank) pagoItem.getC_BankAccount().getC_Bank()).get_ValueAsBoolean("IncSerieConcilia")){
                                nroMedioPagoOLD = pagoItem.getDocumentSerie().trim() + nroMedioPagoOLD;
                            }
                        }
                    }
                }
                OLD_emisionMedioPago_ID = pagoItem.getZ_EmisionMedioPago_ID();
            }
            else{
                nroMedioPagoOLD = replaceLin.getNroMedioPago();
            }


            // **************** ASIENTO DE ANULACION DE MEDIO DE PAGO ANTERIOR

            // Si el tipo de medio de pago a reemplazar, se contabiliza
            MZMedioPago medioPagoOLD = (MZMedioPago) replaceLin.getZ_MedioPago();
            if (medioPagoOLD.isContabilizar()){

                // DR - Monto del medio de pago reemplazado - Cuenta del medio de pago a emitir
                int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
                if (mpEmitidos_ID <= 0){
                    p_Error = "No se obtuvo Cuenta Contable para Medios de Pago Emitidos";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }

                FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), amt, null);

                // Guardo detalles asociados a esta pata del asiento contable
                if (fl1 != null){
                    fl1.setAD_Org_ID(this.getAD_Org_ID());
                    fl1.saveEx();
                    MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                    factDet.setFact_Acct_ID(fl1.get_ID());
                    factDet.setAD_Org_ID(this.medioPagoReplace.getAD_Org_ID());
                    factDet.setZ_MedioPagoReplace_ID(this.medioPagoReplace.get_ID());
                    factDet.setZ_EmisionMedioPago_ID(OLD_emisionMedioPago_ID);
                    factDet.setZ_MedioPago_ID(replaceLin.getZ_MedioPago_ID());

                    if (replaceLin.getC_BankAccount_ID() > 0){
                        factDet.setC_BankAccount_ID(replaceLin.getC_BankAccount_ID());
                        factDet.setC_Bank_ID(replaceLin.getC_BankAccount().getC_Bank_ID());
                    }

                    if (replaceLin.getZ_MedioPagoItem_ID() > 0){
                        factDet.setZ_MedioPagoItem_ID(replaceLin.getZ_MedioPagoItem_ID());
                    }

                    factDet.setNroMedioPago(nroMedioPagoOLD);
                    factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ANULADO);
                    factDet.setCurrencyRate(Env.ONE);
                    factDet.setDueDate(replaceLin.getDueDate());
                    factDet.saveEx();
                }

                // CR : Monto del medio de pago reemplazado - Cuenta medio de pago emitido y pendiente de entrega
                int emiPendEnt_ID = getValidCombination_ID (Doc.ACCTYPE_MP_EmiPendEnt, as);
                if (emiPendEnt_ID <= 0){
                    p_Error = "Falta parametrizar Cuenta Contable para Medio de Pago Emitido y Pendiente de Entrega en moneda de este Documento.";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }
                FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(),  null, amt);

                // Guardo detalles asociados a esta pata del asiento contable
                if (fl2 != null){
                    fl2.setAD_Org_ID(this.getAD_Org_ID());
                    fl2.saveEx();
                    MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                    factDet.setFact_Acct_ID(fl2.get_ID());
                    factDet.setAD_Org_ID(this.medioPagoReplace.getAD_Org_ID());
                    factDet.setZ_MedioPagoReplace_ID(this.medioPagoReplace.get_ID());
                    factDet.setZ_EmisionMedioPago_ID(OLD_emisionMedioPago_ID);
                    factDet.setZ_MedioPago_ID(replaceLin.getZ_MedioPago_ID());

                    if (replaceLin.getC_BankAccount_ID() > 0){
                        factDet.setC_BankAccount_ID(replaceLin.getC_BankAccount_ID());
                        factDet.setC_Bank_ID(replaceLin.getC_BankAccount().getC_Bank_ID());
                    }

                    if (replaceLin.getZ_MedioPagoItem_ID() > 0){
                        factDet.setZ_MedioPagoItem_ID(replaceLin.getZ_MedioPagoItem_ID());
                    }

                    factDet.setNroMedioPago(nroMedioPagoOLD);
                    factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ANULADO);
                    factDet.setCurrencyRate(Env.ONE);
                    factDet.setDueDate(replaceLin.getDueDate());
                    factDet.saveEx();
                }
            }

            // Si hay un pago asociado a este medio de pago
            if (replaceLin.getZ_Pago_ID() > 0){

                // Si el pago esta completo hago asientos, sino no hago nada.
                MZPago pago = (MZPago) replaceLin.getZ_Pago();
                if ((pago != null) && (pago.get_ID() > 0) && pago.getDocStatus().equalsIgnoreCase(DocumentEngine.STATUS_Completed)){

                    MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) replaceLin.getZ_MedioPagoItem();

                    // DR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria del medio de pago viejo.
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
                        FactLine fl3 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), amt, null);

                        // Detalle de asiento
                        if (fl3 != null){
                            fl3.saveEx();
                            MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                            factDet.setFact_Acct_ID(fl3.get_ID());
                            factDet.setAD_Org_ID(this.medioPagoReplace.getAD_Org_ID());
                            factDet.setZ_Pago_ID(medioPagoItem.getZ_Pago_ID());
                            factDet.setZ_MedioPagoReplace_ID(this.medioPagoReplace.get_ID());
                            factDet.setZ_MedioPago_ID(replaceLin.getZ_MedioPago_ID());
                            if (replaceLin.getC_BankAccount_ID() > 0){
                                factDet.setC_BankAccount_ID(replaceLin.getC_BankAccount_ID());
                                factDet.setC_Bank_ID(replaceLin.getC_BankAccount().getC_Bank_ID());
                            }

                            factDet.setZ_MedioPagoItem_ID(medioPagoItem.get_ID());

                            String nroMedioPago = medioPagoItem.getNroMedioPago();
                            if (medioPagoItem.getDocumentSerie() != null){
                                if (medioPagoItem.getC_BankAccount_ID() > 0){
                                    if (medioPagoItem.getC_BankAccount().getC_Bank_ID() > 0){
                                        if (((MBank) medioPagoItem.getC_BankAccount().getC_Bank()).get_ValueAsBoolean("IncSerieConcilia")){
                                            nroMedioPago = medioPagoItem.getDocumentSerie().trim() + nroMedioPago;
                                        }
                                    }
                                }
                            }
                            factDet.setNroMedioPago(nroMedioPago);
                            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                            factDet.setCurrencyRate(Env.ONE);
                            factDet.setDueDate(replaceLin.getDueDate());
                            factDet.saveEx();
                        }
                    }

                    // CR - Cargo contable asociado al cabezal de este documento
                    if (this.medioPagoReplace.getC_Charge_ID() > 0){
                        if ((this.medioPagoReplace.getChargeAmt() != null) && (this.medioPagoReplace.getChargeAmt().compareTo(Env.ZERO) != 0)){
                            fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as), getC_Currency_ID(), null, getAmount(Doc.AMTTYPE_Charge));
                        }
                    }

                    // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria del medio de pago nuevo
                    List<MZMedioPagoReplaceDet> dets = replaceLin.getDetail();
                    for (MZMedioPagoReplaceDet replaceDet: dets){

                        MZMedioPagoItem NEW_medioPagoItem = (MZMedioPagoItem) replaceDet.getZ_MedioPagoItem();
                        String nroMedioPago = NEW_medioPagoItem.getNroMedioPago();
                        if (NEW_medioPagoItem.getDocumentSerie() != null){
                            nroMedioPago = NEW_medioPagoItem.getDocumentSerie().trim() + nroMedioPago;
                        }

                        // CR -  Monto del nuevo medio de pago - Cuenta contable asociada a la cuenta bancaria.
                        int bankAccountID = -1;
                        if (replaceDet.getC_BankAccount_ID() > 0){
                            bankAccountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankInTransit, replaceDet.getC_BankAccount_ID(), as, null);
                        }
                        else{
                            if (replaceDet.getZ_MedioPago_ID() > 0){
                                bankAccountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Entregados, replaceDet.getZ_MedioPago_ID(), replaceDet.getC_Currency_ID(), as, null);
                            }
                            else{
                                p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                                log.log(Level.SEVERE, p_Error);
                                fact = null;
                            }
                        }
                        if (bankAccountID > 0){
                            MAccount acctBankCr = MAccount.get(getCtx(), accountID);
                            FactLine fl3 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), null, replaceDet.getTotalAmt());

                            // Detalle de asiento
                            if (fl3 != null){
                                fl3.saveEx();
                                MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                                factDet.setFact_Acct_ID(fl3.get_ID());
                                factDet.setAD_Org_ID(this.medioPagoReplace.getAD_Org_ID());
                                factDet.setZ_Pago_ID(medioPagoItem.getZ_Pago_ID());
                                factDet.setZ_MedioPagoReplace_ID(this.medioPagoReplace.get_ID());
                                factDet.setZ_MedioPago_ID(replaceDet.getZ_MedioPago_ID());
                                if (replaceDet.getC_BankAccount_ID() > 0){
                                    factDet.setC_BankAccount_ID(replaceDet.getC_BankAccount_ID());
                                    factDet.setC_Bank_ID(replaceDet.getC_BankAccount().getC_Bank_ID());
                                }

                                factDet.setZ_MedioPagoItem_ID(NEW_medioPagoItem.get_ID());
                                factDet.setNroMedioPago(nroMedioPago);
                                factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                                factDet.setCurrencyRate(Env.ONE);
                                factDet.setDueDate(replaceLin.getDueDate());
                                factDet.saveEx();
                            }
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
