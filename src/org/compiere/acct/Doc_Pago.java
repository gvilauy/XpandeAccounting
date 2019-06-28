package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.comercial.utils.AcctUtils;
import org.xpande.financial.model.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
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
                FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), grossAmt, null);
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
                FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), acctAcreedID), getC_Currency_ID(),  null, grossAmt);
                if (fl2 != null){
                    fl2.setAD_Org_ID(this.pago.getAD_Org_ID());
                }
            }
            else{
                // Es un recibo de proveedor
                // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria.
                for (int i = 0; i < p_lines.length; i++)
                {
                    BigDecimal amt = p_lines[i].getAmtSource();

                    MZPagoMedioPago pagoMedioPago = new MZPagoMedioPago(getCtx(), p_lines[i].get_ID(), this.getTrxName());
                    MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) pagoMedioPago.getZ_MedioPagoItem();

                    // DR - Lineas de Medios de Pago - Monto de cada linea - Cuenta del medio de pago a emitir
                    int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
                    FactLine fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), amt, null);

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
                                    if (medioPagoItem.getDocumentSerie() != null){
                                        factDet.setNroMedioPago(medioPagoItem.getDocumentSerie().trim() + medioPagoItem.getNroMedioPago());
                                    }
                                    else{
                                        factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                                    }
                                }
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
                        FactLine fl2 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), null, amt);

                        if (fl2 != null){
                            fl2.setAD_Org_ID(this.pago.getAD_Org_ID());
                        }

                        // Detalle de asiento
                        if (fl2 != null){
                            fl2.saveEx();
                            MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                            factDet.setFact_Acct_ID(fl2.get_ID());
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
                                        if (medioPagoItem.getDocumentSerie() != null){
                                            factDet.setNroMedioPago(medioPagoItem.getDocumentSerie().trim() + medioPagoItem.getNroMedioPago());
                                        }
                                        else{
                                            factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                                        }
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
                }
                // Si tengo importe de anticipos afectados en este recibo, hago el asiento correspondiente
                if ((this.pago.getAmtAnticipo() != null) && (this.pago.getAmtAnticipo().compareTo(Env.ZERO) != 0)){

                    // DR : Monto total anticipos - Cuenta Acreedores del Socio de Negocio
                    int acctAcreedID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
                    if (acctAcreedID <= 0){
                        p_Error = "Falta parametrizar Cuenta Contable para CxP del Proveedor en moneda de este Documento.";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }
                    FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), acctAcreedID), getC_Currency_ID(), this.pago.getAmtAnticipo(), null);
                    if (fl2 != null){
                        fl2.setAD_Org_ID(this.pago.getAD_Org_ID());
                    }

                    // Doy vuelta el asiento que se hizo originalmente por los anticipos afectados ahora en este recibo
                    // CR : Monto total anticipos - Cuenta Anticipo del Socio de Negocio
                    int acctAnticipoID = getValidCombination_ID (Doc.ACCTTYPE_V_Prepayment, as);
                    if (acctAnticipoID <= 0){
                        p_Error = "Falta parametrizar Cuenta Contable para Anticipo a Proveedor en moneda de este Documento.";
                        log.log(Level.SEVERE, p_Error);
                        fact = null;
                        facts.add(fact);
                        return facts;
                    }
                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), null, this.pago.getAmtAnticipo());
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
