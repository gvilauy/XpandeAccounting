package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.comercial.utils.AcctUtils;
import org.xpande.financial.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Contabilización de documentos: Pagos y Cobranzas.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 8/30/18.
 */
public class Doc_Pago extends Doc {

    private MZPago pago = null;
    private MDocType docType = null;
    private BigDecimal amtMediosPago = Env.ZERO;
    List<MZPagoResgRecibido> resgRecibidos = null;

    /**
     *  Constructor
     */
    public Doc_Pago(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_Pago(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZPago.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.pago = (MZPago) getPO();
        setDateDoc(pago.getDateDoc());
        setDateAcct(pago.getDateDoc());
        setC_Currency_ID(pago.getC_Currency_ID());
        setC_BPartner_ID(pago.getC_BPartner_ID());


        this.docType = (MDocType) pago.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        // Para cobros, lineas de resguardos recibidos
        if (this.pago.isSOTrx()){
            this.resgRecibidos = this.pago.getResguardosRecibidos();
        }

        // Si es un pago
        if (!this.pago.isSOTrx()){

            // Si no es un anticipo
            if (!this.pago.isAnticipo()){
                // Total del documento es igual al total de medios de pago, ya que para Pagos no se consideraran Resguardos en este asiento.
                setAmount(Doc.AMTTYPE_Gross, this.amtMediosPago);
            }
            else{
                // Total del documento es igual al total digitado por el usuario en el cabezal del anticipo
                setAmount(Doc.AMTTYPE_Gross, this.pago.getPayAmt());
            }

        }
        // Si es un cobro
        else{
            // Total del documento
            setAmount(Doc.AMTTYPE_Gross, pago.getPayAmt());
        }

        log.fine("Lines=" + p_lines.length);

        return null;
    }


    @Override
    public BigDecimal getBalance() {

        BigDecimal retValue = Env.ZERO;
        StringBuffer sb = new StringBuffer (" [");

        // Para anticipos de proveedores, es un solo monto, y siempre cierra.
        if (!this.pago.isSOTrx()){
            if (this.pago.isAnticipo()){
                return  retValue;
            }
        }


        //  Total
        retValue = retValue.add(getAmount(Doc.AMTTYPE_Gross));
        sb.append(getAmount(Doc.AMTTYPE_Gross));

        //  - Header Charge
        retValue = retValue.subtract(getAmount(Doc.AMTTYPE_Charge));
        sb.append("-").append(getAmount(Doc.AMTTYPE_Charge));

        //  Lineas
        for (int i = 0; i < p_lines.length; i++)
        {
            retValue = retValue.subtract(p_lines[i].getAmtSource());
            sb.append("-").append(p_lines[i].getAmtSource());
        }

        //  Resguardos recibidos en caso de cobros
        if (this.pago.isSOTrx()){
            for (MZPagoResgRecibido resgRecibido: this.resgRecibidos){
                retValue = retValue.subtract(resgRecibido.getAmtAllocationMT());
                sb.append("-").append(resgRecibido.getAmtAllocationMT());
            }
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

        // Contabilización de un PAGO
        if (!this.pago.isSOTrx()){

            // Si es un anticipo a proveedor
            if (this.pago.isAnticipo()){

                // DR : Monto total del Pago - Cuenta Anticipo del Socio de Negocio
                int acctAnticipoID = getValidCombination_ID (Doc.ACCTTYPE_V_Prepayment, as);
                if (acctAnticipoID <= 0){
                    p_Error = "Falta parametrizar Cuenta Contable para Anticipo a Proveedor en moneda de este Documento.";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }

                FactLine fl1 = null;
                if (!pago.isExtornarAcct()){
                    fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), grossAmt, null);
                }
                else{
                    fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), null, grossAmt);
                }

                if (fl1 != null){
                    fl1.setAD_Org_ID(this.pago.getAD_Org_ID());
                }

                // CR : Monto total del Pago - Cuenta Acreedores del Socio de Negocio
                int acctAcreedID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
                if (acctAcreedID <= 0){
                    p_Error = "Falta parametrizar Cuenta Contable para CxP del Proveedor en moneda de este Documento.";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }

                FactLine fl2 = null;
                if (!pago.isExtornarAcct()){
                    fl2 = fact.createLine(null, MAccount.get(getCtx(), acctAcreedID), getC_Currency_ID(),  null, grossAmt);
                }
                else{
                    fl2 = fact.createLine(null, MAccount.get(getCtx(), acctAcreedID), getC_Currency_ID(), grossAmt, null);
                }

                if (fl2 != null){
                    fl2.setAD_Org_ID(this.pago.getAD_Org_ID());
                }
            }
            else{
                // Es un recibo de proveedor

                BigDecimal montoAnticipos = this.pago.getAmtAnticipo();
                if (montoAnticipos == null) montoAnticipos = Env.ZERO;
                if (this.pago.isReciboAnticipo()) montoAnticipos = Env.ZERO;

                // DR : Monto total del recibo - Cuenta Acreedores del Socio de Negocio
                int acctAcreedID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
                if (acctAcreedID <= 0){
                    p_Error = "Falta parametrizar Cuenta Contable para CxP del Proveedor en moneda de este Documento.";
                    log.log(Level.SEVERE, p_Error);
                    fact = null;
                    facts.add(fact);
                    return facts;
                }
                FactLine fl2 = null;
                if (!pago.isExtornarAcct()){
                    fl2 = fact.createLine(null, MAccount.get(getCtx(), acctAcreedID), getC_Currency_ID(), grossAmt.add(montoAnticipos), null);
                }
                else{
                    fl2 = fact.createLine(null, MAccount.get(getCtx(), acctAcreedID), getC_Currency_ID(), null, grossAmt.add(montoAnticipos));
                }
                if (fl2 != null){
                    fl2.setAD_Org_ID(this.pago.getAD_Org_ID());
                }

                HashMap<Integer, Integer> hashRepItemsCharge = new HashMap<Integer, Integer>();
                String sql = "";

                // Lineas de Medios de Pago - Monto de cada linea
                for (int i = 0; i < p_lines.length; i++)
                {
                    BigDecimal amt = p_lines[i].getAmtSource();

                    MZPagoMedioPago pagoMedioPago = new MZPagoMedioPago(getCtx(), p_lines[i].get_ID(), this.getTrxName());
                    MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) pagoMedioPago.getZ_MedioPagoItem();
                    String nroMedioPago = "";
                    MBank bank = null;

                    // Seteo numero de medio de pago
                    if (medioPagoItem != null){
                        if (medioPagoItem.getC_BankAccount_ID() > 0){
                            bank = (MBank) medioPagoItem.getC_BankAccount().getC_Bank();
                        }
                        if (medioPagoItem.getNroMedioPago() != null){
                            nroMedioPago = medioPagoItem.getNroMedioPago().trim();
                            if (medioPagoItem.getDocumentSerie() != null){
                                if ((bank != null) && (bank.get_ID() > 0)){
                                    if (bank.get_ValueAsBoolean("IncSerieConcilia")){
                                        nroMedioPago = medioPagoItem.getDocumentSerie().trim() + medioPagoItem.getNroMedioPago().trim();
                                    }
                                }
                            }
                        }
                    }

                    // DR - Lineas de Medios de Pago - Monto de cada linea - Cuenta del medio de pago a emitir
                    int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
                    if (mpEmitidos_ID <= 0){
                        p_Error = "Falta parametrizar Cuenta Contable para Medio de Pago Emitido en moneda de este Documento.";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }

                    FactLine fl1 = null;
                    if (!pago.isExtornarAcct()){
                        fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), amt, null);
                    }
                    else{
                        fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), null, amt);
                    }

                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.pago.getAD_Org_ID());
                    }

                    // Detalle de asiento
                    if (fl1 != null){
                        fl1.saveEx();
                        MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                        factDet.setFact_Acct_ID(fl1.get_ID());
                        factDet.setAD_Org_ID(this.pago.getAD_Org_ID());
                        factDet.setZ_Pago_ID(this.pago.get_ID());
                        factDet.setZ_MedioPago_ID(pagoMedioPago.getZ_MedioPago_ID());
                        if (pagoMedioPago.getC_BankAccount_ID() > 0){
                            factDet.setC_BankAccount_ID(pagoMedioPago.getC_BankAccount_ID());
                            factDet.setC_Bank_ID(pagoMedioPago.getC_BankAccount().getC_Bank_ID());
                        }
                        else{
                            if (pagoMedioPago.getC_Bank_ID() > 0){
                                factDet.setC_Bank_ID(pagoMedioPago.getC_Bank_ID());
                            }
                        }

                        factDet.setNroMedioPago(pagoMedioPago.getDocumentNoRef());

                        if (pagoMedioPago.getZ_MedioPagoItem_ID() > 0){
                            factDet.setZ_MedioPagoItem_ID(pagoMedioPago.getZ_MedioPagoItem_ID());
                            if (nroMedioPago != null){
                                factDet.setNroMedioPago(nroMedioPago);
                            }
                        }

                        factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                        factDet.setCurrencyRate(pagoMedioPago.getMultiplyRate());
                        factDet.setDueDate(pagoMedioPago.getDueDate());
                        factDet.saveEx();
                    }

                    // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta del medio de pago a emitir y pendiente de entrega
                    int emiPendEnt_ID = getValidCombination_ID (Doc.ACCTYPE_MP_EmiPendEnt, as);
                    if (emiPendEnt_ID <= 0){
                        p_Error = "Falta parametrizar Cuenta Contable para Medio de Pago Emitido y Pendiente de Entrega en moneda de este Documento.";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }
                    FactLine fl11 = null;
                    if (!pago.isExtornarAcct()){
                        fl11 = fact.createLine(p_lines[i], MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), null, amt);
                    }
                    else{
                        fl11 = fact.createLine(p_lines[i], MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), amt,  null);
                    }
                    if (fl11 != null){
                        fl11.setAD_Org_ID(this.pago.getAD_Org_ID());
                    }

                    // Detalle de asiento
                    if (fl11 != null){
                        fl11.saveEx();
                        MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                        factDet.setFact_Acct_ID(fl11.get_ID());
                        factDet.setAD_Org_ID(this.pago.getAD_Org_ID());
                        factDet.setZ_Pago_ID(this.pago.get_ID());
                        factDet.setZ_MedioPago_ID(pagoMedioPago.getZ_MedioPago_ID());
                        if (pagoMedioPago.getC_BankAccount_ID() > 0){
                            factDet.setC_BankAccount_ID(pagoMedioPago.getC_BankAccount_ID());
                            factDet.setC_Bank_ID(pagoMedioPago.getC_BankAccount().getC_Bank_ID());
                        }
                        else{
                            if (pagoMedioPago.getC_Bank_ID() > 0){
                                factDet.setC_Bank_ID(pagoMedioPago.getC_Bank_ID());
                            }
                        }

                        factDet.setNroMedioPago(pagoMedioPago.getDocumentNoRef());

                        if (pagoMedioPago.getZ_MedioPagoItem_ID() > 0){
                            factDet.setZ_MedioPagoItem_ID(pagoMedioPago.getZ_MedioPagoItem_ID());
                            if (nroMedioPago != null){
                                factDet.setNroMedioPago(nroMedioPago);
                            }
                        }

                        factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                        factDet.setCurrencyRate(pagoMedioPago.getMultiplyRate());
                        factDet.setDueDate(pagoMedioPago.getDueDate());
                        factDet.saveEx();
                    }

                    // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria.
                    int accountID = -1;
                    if (pagoMedioPago.getC_BankAccount_ID() > 0){
                        accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankInTransit, pagoMedioPago.getC_BankAccount_ID(), as, null);
                        if (accountID <= 0){
                            MBankAccount bankAccount = (MBankAccount) pagoMedioPago.getC_BankAccount();
                            p_Error = "No se obtuvo Cuenta Contable (BankInTransit) asociada a la Cuenta Bancaria : " + bankAccount.getName();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }
                    else if (pagoMedioPago.getC_CashBook_ID() > 0){
                        this.setC_CashBook_ID(pagoMedioPago.getC_CashBook_ID());
                        accountID = getValidCombination_ID(Doc.ACCTTYPE_CashExpense, as);
                        if (accountID <= 0){
                            MCashBook cashBook = (MCashBook) pagoMedioPago.getC_CashBook();
                            p_Error = "No se obtuvo Cuenta Contable (CashExpense) asociada a la caja : " + cashBook.getName();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }
                    else{
                        if (pagoMedioPago.getZ_MedioPago_ID() > 0){
                            accountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Entregados, pagoMedioPago.getZ_MedioPago_ID(), pagoMedioPago.getC_Currency_ID(), as, null);
                            if (accountID <= 0){
                                MZMedioPago medioPago = (MZMedioPago) pagoMedioPago.getZ_MedioPago();
                                p_Error = "No se obtuvo Cuenta Contable (MP_Entregados) asociada al medio de pago : " + medioPago.getName();
                                log.log(Level.SEVERE, p_Error);
                                fact = null;
                                facts.add(fact);
                                return facts;
                            }
                        }
                        else{
                            p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }
                    if (accountID > 0){
                        MAccount acctBankCr = MAccount.get(getCtx(), accountID);

                        FactLine fl22 = null;
                        if (!pago.isExtornarAcct()){
                            fl22 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), null, amt);
                        }
                        else{
                            fl22 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), amt, null);
                        }

                        if (fl22 != null){
                            fl22.setAD_Org_ID(this.pago.getAD_Org_ID());
                        }

                        // Detalle de asiento
                        if (fl22 != null){
                            fl22.saveEx();
                            MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                            factDet.setFact_Acct_ID(fl22.get_ID());
                            factDet.setAD_Org_ID(this.pago.getAD_Org_ID());
                            factDet.setZ_Pago_ID(this.pago.get_ID());
                            factDet.setZ_MedioPago_ID(pagoMedioPago.getZ_MedioPago_ID());
                            if (pagoMedioPago.getC_BankAccount_ID() > 0){
                                factDet.setC_BankAccount_ID(pagoMedioPago.getC_BankAccount_ID());
                                factDet.setC_Bank_ID(pagoMedioPago.getC_BankAccount().getC_Bank_ID());
                            }
                            else{
                                if (pagoMedioPago.getC_Bank_ID() > 0){
                                    factDet.setC_Bank_ID(pagoMedioPago.getC_Bank_ID());
                                }
                            }

                            factDet.setNroMedioPago(pagoMedioPago.getDocumentNoRef());

                            if (pagoMedioPago.getZ_MedioPagoItem_ID() > 0){
                                factDet.setZ_MedioPagoItem_ID(pagoMedioPago.getZ_MedioPagoItem_ID());
                                if (medioPagoItem != null){
                                    if (nroMedioPago != null){
                                        factDet.setNroMedioPago(nroMedioPago);
                                    }
                                }
                            }

                            factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                            factDet.setCurrencyRate(pagoMedioPago.getMultiplyRate());
                            factDet.setDueDate(pagoMedioPago.getDueDate());
                            factDet.saveEx();
                        }

                    }
                    else{
                        p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }

                    // CR - Cargos contables en caso que este medio de pago tenga un cargo asociado en un reemplazo (como nuevo medio de pago)
                    // Si este item de medio de pago surge de un reemplazo de medio de pago que ademas tiene cargo contable
                    sql = " select z_mediopagoreplace_id from zv_financial_repitemcharge where z_mediopagoitem_id =" + medioPagoItem.get_ID();
                    int medioPagoReplaceID = DB.getSQLValueEx(null, sql);
                    if (medioPagoReplaceID > 0){
                        // Si ya no procese este cargo de este documento de reemplazo
                        if (!hashRepItemsCharge.containsKey(medioPagoReplaceID)){

                            MZMedioPagoReplace medioPagoReplace = new MZMedioPagoReplace(getCtx(), medioPagoReplaceID, null);
                            if (medioPagoReplace.getC_Charge_ID() > 0){
                                if ((medioPagoReplace.getChargeAmt() != null) && (medioPagoReplace.getChargeAmt().compareTo(Env.ZERO) != 0)){
                                    this.pago.setC_Charge_ID(medioPagoReplace.getC_Charge_ID());
                                    this.pago.setChargeAmt(medioPagoReplace.getChargeAmt());
                                    setAmount(Doc.AMTTYPE_Charge, medioPagoReplace.getChargeAmt());
                                    fact.createLine(null, getAccount(Doc.ACCTTYPE_Charge, as), getC_Currency_ID(), null, getAmount(Doc.AMTTYPE_Charge));
                                }
                            }
                            hashRepItemsCharge.put(medioPagoReplaceID, medioPagoReplaceID);
                        }
                    }
                }


                // Si tengo importe de anticipos afectados en este recibo, hago el asiento correspondiente
                if ((!this.pago.isReciboAnticipo()) && (this.pago.getAmtAnticipo() != null) && (this.pago.getAmtAnticipo().compareTo(Env.ZERO) != 0)){

                    // CR : Monto total anticipos - Cuenta Anticipo del Socio de Negocio
                    int acctAnticipoID = getValidCombination_ID (Doc.ACCTTYPE_V_Prepayment, as);
                    if (acctAnticipoID <= 0){
                        p_Error = "Falta parametrizar Cuenta Contable para Anticipo a Proveedor en moneda de este Documento.";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }
                    FactLine fl1 = null;
                    if (!pago.isExtornarAcct()){
                        fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), null, this.pago.getAmtAnticipo());
                    }
                    else{
                        fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(),  this.pago.getAmtAnticipo(), null);
                    }

                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.pago.getAD_Org_ID());
                    }
                }
            }
        }
        else{
            // Contabilización de un COBRO

            // CR - Deudores comerciales por el total del cobro
            int receivables_ID = getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);

            FactLine fl3 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(),  null, grossAmt);

            if (fl3 != null){
                fl3.setAD_Org_ID(this.pago.getAD_Org_ID());
                fl3.saveEx();
            }

            // DR - Lineas de Medios de Pago - Monto de cada linea.
            // Cuenta contable asociada a la cuenta bancaria si hay, y sino tengo cuenta bancaria, entonces cuenta del medio de pago.
            for (int i = 0; i < p_lines.length; i++)
            {
                BigDecimal amt = p_lines[i].getAmtSource();

                MZPagoMedioPago pagoMedioPago = new MZPagoMedioPago(getCtx(), p_lines[i].get_ID(), this.getTrxName());
                MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) pagoMedioPago.getZ_MedioPagoItem();

                int accountID = -1;
                if (pagoMedioPago.getC_BankAccount_ID() > 0){
                    accountID = AccountUtils.getBankValidCombinationID(getCtx(), Doc.ACCTTYPE_BankAsset, pagoMedioPago.getC_BankAccount_ID(), as, null);
                    if (accountID <= 0){
                        MBankAccount bankAccount = (MBankAccount) pagoMedioPago.getC_BankAccount();
                        p_Error = "No se obtuvo Cuenta Contable (BankAsset) asociada a la Cuenta Bancaria : " + bankAccount.getName();
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }
                }
                else{
                    if (pagoMedioPago.getZ_MedioPago_ID() > 0){
                        accountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Recibidos, pagoMedioPago.getZ_MedioPago_ID(), pagoMedioPago.getC_Currency_ID(), as, null);
                        if (accountID <= 0){
                            MZMedioPago medioPago = (MZMedioPago) pagoMedioPago.getZ_MedioPago();
                            p_Error = "No se obtuvo Cuenta Contable (MP_Recibidos) asociada al medio de pago : " + medioPago.getName();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }
                    else{
                        p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }
                }

                // DR - Lineas de Medios de Pago - Monto de cada linea
                FactLine fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
                if (fl1 != null){
                    fl1.setAD_Org_ID(this.pago.getAD_Org_ID());
                }

                // Detalle de asiento
                if (fl1 != null){
                    fl1.saveEx();
                    MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                    factDet.setFact_Acct_ID(fl1.get_ID());
                    factDet.setAD_Org_ID(this.pago.getAD_Org_ID());
                    factDet.setZ_Pago_ID(this.pago.get_ID());
                    factDet.setZ_MedioPago_ID(pagoMedioPago.getZ_MedioPago_ID());
                    if (pagoMedioPago.getC_BankAccount_ID() > 0){
                        factDet.setC_BankAccount_ID(pagoMedioPago.getC_BankAccount_ID());
                        factDet.setC_Bank_ID(pagoMedioPago.getC_BankAccount().getC_Bank_ID());
                    }
                    else{
                        if (pagoMedioPago.getC_Bank_ID() > 0){
                            factDet.setC_Bank_ID(pagoMedioPago.getC_Bank_ID());
                        }
                    }

                    factDet.setNroMedioPago(pagoMedioPago.getDocumentNoRef());

                    if (pagoMedioPago.getZ_MedioPagoItem_ID() > 0){
                        factDet.setZ_MedioPagoItem_ID(pagoMedioPago.getZ_MedioPagoItem_ID());
                        if (medioPagoItem != null){
                            if (medioPagoItem.getNroMedioPago() != null){
                                factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                            }
                        }
                    }

                    factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                    factDet.setCurrencyRate(pagoMedioPago.getMultiplyRate());
                    factDet.setDueDate(pagoMedioPago.getDueDate());
                    factDet.saveEx();
                }
            }


            // DR - Lineas de Resguardos Recibidos - Monto de cada linea.
            // Cuenta contable asociada a la retención de cada linea
            if (this.resgRecibidos != null){
                for (MZPagoResgRecibido resgRecibido: this.resgRecibidos){

                    BigDecimal amt = resgRecibido.getAmtAllocationMT();

                    int accountID = AccountUtils.getRetencionValidCombinationID(getCtx(), Doc.ACCTYPE_RT_RetencionRecibida, resgRecibido.getZ_RetencionSocio_ID(),
                            resgRecibido.getC_Currency_ID(), as, null);

                    if (accountID <= 0){
                        MZRetencionSocio retencionSocio = (MZRetencionSocio) resgRecibido.getZ_RetencionSocio();
                        p_Error = "No se indica Cuenta Bancaria para Retención : " + retencionSocio.getName();
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }

                    // DR - Lineas de Resguardos Recibidos - Monto de cada linea
                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.pago.getAD_Org_ID());
                    }

                    // Detalle de asiento
                    if (fl1 != null){
                        fl1.saveEx();
                        MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                        factDet.setFact_Acct_ID(fl1.get_ID());
                        factDet.setAD_Org_ID(this.pago.getAD_Org_ID());
                        factDet.setZ_Pago_ID(this.pago.get_ID());
                        factDet.setCurrencyRate(resgRecibido.getMultiplyRate());
                        factDet.setZ_RetencionSocio_ID(resgRecibido.getZ_RetencionSocio_ID());
                        factDet.saveEx();
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

        List<MZPagoMedioPago> medioPagoList = this.pago.getMediosPago();

        this.amtMediosPago = Env.ZERO;

        for (MZPagoMedioPago medioPago: medioPagoList){

            DocLine docLine = new DocLine(medioPago, this);
            docLine.setAmount(medioPago.getTotalAmtMT());
            list.add(docLine);

            this.amtMediosPago = this.amtMediosPago.add(medioPago.getTotalAmtMT());
        }

        // Medios de pago de anticipos cuando no tengo importe de medios de pago en este documento, y tengo diferencia.
        if ((this.pago.getTotalMediosPago() == null) || (this.pago.getTotalMediosPago().compareTo(Env.ZERO) == 0)){

            if ((this.pago.getPayAmt() != null) && (this.pago.getPayAmt().compareTo(Env.ZERO) != 0)){
                List<MZPagoMedioPago> medioPagoAnticipoList = this.pago.getMediosPagoAnticipos();

                this.amtMediosPago = Env.ZERO;

                for (MZPagoMedioPago medioPagoAnticipo: medioPagoAnticipoList){

                    DocLine docLine = new DocLine(medioPagoAnticipo, this);
                    docLine.setAmount(medioPagoAnticipo.getTotalAmtMT());
                    list.add(docLine);

                    this.amtMediosPago = this.amtMediosPago.add(medioPagoAnticipo.getTotalAmtMT());
                }
            }
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }


}
