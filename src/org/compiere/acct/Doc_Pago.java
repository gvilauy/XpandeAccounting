package org.compiere.acct;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.TimeUtil;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.utils.InfoMultiCurrency;
import org.xpande.financial.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
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

        // Seteo grupo de socio de negocio en caso de tenerlo
        MBPartner partner = (MBPartner) this.pago.getC_BPartner();
        if ((partner != null) && (partner.get_ID() > 0)){
            if (partner.getC_BP_Group_ID() > 0){
                this.cBPGroupID = partner.getC_BP_Group_ID();
            }
        }

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

        // Contabilizo según comportamiento del documento

        // Si es un documento de cuenta por pagar
        if (this.docType.getDocBaseType().equalsIgnoreCase("PPD")){
            fact = this.createFacts_PPD(as);
        }
        // Si es un documento de cuenta por cobrar
        else if (this.docType.getDocBaseType().equalsIgnoreCase("CCD")){
            fact = this.createFacts_CCD(as);
        }
        // Si es un documento de anticipo a proveedor
        else if (this.docType.getDocBaseType().equalsIgnoreCase("PPA")){
            if (this.pago.isAnticipoDirecto()){
                fact = this.createFacts_PPD(as);
                //fact = this.createFacts_PPA_Directo(as);
            }
            else{
                fact = this.createFacts_PPA(as);
            }
        }
        // Si es un documento de anticipo a cliente
        else if (this.docType.getDocBaseType().equalsIgnoreCase("CCA")){
            //fact = this.createFacts_CCA(as);
        }

        facts.add(fact);
        return facts;
    }

    /***
     * Genera asientos contables para tipo de documento base: PPD
     * Documentos de cuentas por pagar.
     * Xpande. Created by Gabriel Vila on 12/19/20.
     * @return
     */
    private Fact createFacts_PPD(MAcctSchema as) {

        Fact fact = new Fact(this, as, Fact.POST_Actual);
        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        try{
            Timestamp fechaHoy = TimeUtil.trunc(new Timestamp(System.currentTimeMillis()), TimeUtil.TRUNC_DAY);

            BigDecimal amtMediosPago = this.pago.getTotalMediosPago();
            BigDecimal amtDocumentos = this.pago.getPayAmt();
            if (amtMediosPago == null) amtMediosPago = Env.ZERO;
            if (amtDocumentos == null) amtDocumentos = Env.ZERO;


            BigDecimal montoAnticipos = this.pago.getAmtAnticipo();
            if (montoAnticipos == null) montoAnticipos = Env.ZERO;
            if (this.pago.isReciboAnticipo()) montoAnticipos = Env.ZERO;

            HashMap<Integer, InfoMultiCurrency> hashPartnerCR = new HashMap<Integer, InfoMultiCurrency>();
            HashMap<Integer, InfoMultiCurrency> hashAnticipo = new HashMap<Integer, InfoMultiCurrency>();

            MZPago pago = (MZPago) getPO();
            List<MZPagoLin> pagoLinList = pago.getSelectedLines();
            for (MZPagoLin pagoLin: pagoLinList){
                // Sumarizo por moneda para contabilizacion CR por cuenta de Socio de Negocio y Moneda,
                // No considero documentos de anticipos aca, ya que luego se hace un asiento al CR para los mismos.
                boolean consideraLinea = true;
                if (pagoLin.getRef_Pago_ID() > 0){
                    if (montoAnticipos.compareTo(Env.ZERO) != 0){
                        consideraLinea = false;
                        if (!hashAnticipo.containsKey(pagoLin.getC_Currency_ID())){
                            hashAnticipo.put(pagoLin.getC_Currency_ID(), new InfoMultiCurrency());
                            hashAnticipo.get(pagoLin.getC_Currency_ID()).cuurencyID = pagoLin.getC_Currency_ID();
                        }
                        hashAnticipo.get(pagoLin.getC_Currency_ID()).amtSource = hashAnticipo.get(pagoLin.getC_Currency_ID()).amtSource.add(pagoLin.getAmtAllocation().negate());
                        hashAnticipo.get(pagoLin.getC_Currency_ID()).amtAcct = hashAnticipo.get(pagoLin.getC_Currency_ID()).amtAcct.add(pagoLin.getAmtAllocationMT().negate());
                    }
                }
                if (consideraLinea){
                    if (!hashPartnerCR.containsKey(pagoLin.getC_Currency_ID())){
                        hashPartnerCR.put(pagoLin.getC_Currency_ID(), new InfoMultiCurrency());
                        hashPartnerCR.get(pagoLin.getC_Currency_ID()).cuurencyID = pagoLin.getC_Currency_ID();
                    }
                    hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtSource = hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtSource.add(pagoLin.getAmtAllocation());
                    hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtAcct = hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtAcct.add(pagoLin.getAmtAllocationMT());
                }
            }

            // Resto resguardos en la moneda correspondiente
            List<MZPagoResguardo> pagoResguardoList = pago.getResguardos();
            for (MZPagoResguardo pagoResguardo: pagoResguardoList){
                if (hashPartnerCR.containsKey(pagoResguardo.getC_Currency_ID())){
                    hashPartnerCR.get(pagoResguardo.getC_Currency_ID()).amtSource = hashPartnerCR.get(pagoResguardo.getC_Currency_ID()).amtSource.subtract(pagoResguardo.getAmtAllocation());
                    hashPartnerCR.get(pagoResguardo.getC_Currency_ID()).amtAcct = hashPartnerCR.get(pagoResguardo.getC_Currency_ID()).amtAcct.subtract(pagoResguardo.getAmtAllocationMT());
                }
                else{
                    // Debo restar el monto en moneda extranjera del resguardo si es que tiene
                    MZResguardoSocio resguardoSocio = (MZResguardoSocio) pagoResguardo.getZ_ResguardoSocio();
                    BigDecimal montoME = resguardoSocio.getTotalAmtME();
                    if (montoME == null) montoME = Env.ZERO;
                    if (montoME.compareTo(Env.ZERO) > 0){
                        if (montoME.compareTo(resguardoSocio.getTotalAmt()) != 0){
                            hashPartnerCR.get(100).amtSource = hashPartnerCR.get(100).amtSource.subtract(montoME);
                            hashPartnerCR.get(100).amtAcct = hashPartnerCR.get(100).amtAcct.subtract(pagoResguardo.getAmtAllocationMT());
                        }
                    }
                }
            }

            // Cuando solo tengo Anticipos como documentos, debo agregar importe al hash.
            if (hashPartnerCR.size() <= 0){
                if ((this.pago.getPayAmt() != null) && (this.pago.getPayAmt().compareTo(Env.ZERO) != 0)){
                    hashPartnerCR.put(this.pago.getC_Currency_ID(), new InfoMultiCurrency());
                    hashPartnerCR.get(this.pago.getC_Currency_ID()).cuurencyID = this.pago.getC_Currency_ID();
                    hashPartnerCR.get(this.pago.getC_Currency_ID()).amtSource = hashPartnerCR.get(this.pago.getC_Currency_ID()).amtSource.add(this.pago.getPayAmt());
                    hashPartnerCR.get(this.pago.getC_Currency_ID()).amtAcct = hashPartnerCR.get(this.pago.getC_Currency_ID()).amtAcct.add(this.pago.getPayAmt());
                }
            }

            // DR : Cuenta Acreedores del Socio de Negocio según moneda
            for (HashMap.Entry<Integer, InfoMultiCurrency> entry : hashPartnerCR.entrySet()){

                // No considero montos en CERO
                if (entry.getValue().amtSource.compareTo(Env.ZERO) == 0){
                    continue;
                }

                if (entry.getValue().cuurencyID != pago.getC_Currency_ID()){
                    this.setIsMultiCurrency(true);
                }

                this.setC_Currency_ID(entry.getValue().cuurencyID);

                int acctPartnerID = getValidCombination_ID (Doc.ACCTTYPE_V_Liability, as);
                if (acctPartnerID <= 0){
                    MCurrency currency = new MCurrency(getCtx(), this.getC_Currency_ID(), null);
                    p_Error = "Falta parametrizar Cuenta Contable para CxP del Proveedor en moneda: " + currency.getISO_Code();
                    log.log(Level.SEVERE, p_Error);
                    return null;
                }

                FactLine fl2;
                if (!pago.isExtornarAcct()){
                    if (getC_Currency_ID() == as.getC_Currency_ID()){
                        fl2 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), entry.getValue().amtSource, null);
                    }
                    else {
                        if (entry.getValue().amtSource.compareTo(Env.ZERO) >= 0){
                            fl2 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), entry.getValue().amtSource, null);
                            if (fl2 != null){
                                if (this.isMultiCurrency()){
                                    fl2.setAmtAcctDr(entry.getValue().amtAcct);
                                }
                                else{
                                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                                        BigDecimal amtAcct = entry.getValue().amtSource.multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP);
                                        fl2.setAmtAcctDr(amtAcct);
                                    }
                                }
                            }
                        }
                        else{
                            fl2 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), null, entry.getValue().amtSource.negate());
                            if (fl2 != null){
                                if (this.isMultiCurrency()){
                                    fl2.setAmtAcctCr(entry.getValue().amtAcct.negate());
                                }
                                else{
                                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                                        BigDecimal amtAcct = entry.getValue().amtSource.negate().multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP);
                                        fl2.setAmtAcctCr(amtAcct);
                                    }
                                }
                            }
                        }
                    }
                }
                else{
                    if (getC_Currency_ID() == as.getC_Currency_ID()){
                        fl2 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), null, entry.getValue().amtSource.add(montoAnticipos));
                    }
                    else{
                        if (entry.getValue().amtSource.compareTo(Env.ZERO) >= 0){
                            fl2 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), null, entry.getValue().amtSource);
                            if (fl2 != null){
                                if (this.isMultiCurrency()){
                                    fl2.setAmtAcctCr(entry.getValue().amtAcct);
                                }
                                else{
                                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                                        BigDecimal amtAcct = entry.getValue().amtSource.multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP);
                                        fl2.setAmtAcctCr(amtAcct);
                                    }
                                }
                            }
                        }
                        else{
                            fl2 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), entry.getValue().amtSource.negate(), null);
                            if (fl2 != null){
                                if (this.isMultiCurrency()){
                                    fl2.setAmtAcctDr(entry.getValue().amtAcct.negate());
                                }
                                else{
                                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                                        BigDecimal amtAcct = entry.getValue().amtSource.negate().multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP);
                                        fl2.setAmtAcctDr(amtAcct);
                                    }
                                }
                            }
                        }
                    }
                }
                if (fl2 != null){
                    fl2.setAD_Org_ID(this.pago.getAD_Org_ID());
                }

                // Si no tengo medios de pago y no tengo monto de anticipos, se trata de una afectacion y por lo tanto tengo que dar vuelta el asiento.
                if ((amtMediosPago.compareTo(Env.ZERO) == 0) && (montoAnticipos.compareTo(Env.ZERO) == 0)){
                    FactLine fl3;
                    if ((fl2.getAmtSourceDr() != null) && (fl2.getAmtSourceDr().compareTo(Env.ZERO) != 0)){
                        fl3 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), null, fl2.getAmtSourceCr());
                    }
                    else{
                        fl3 = fact.createLine(null, MAccount.get(getCtx(), acctPartnerID), getC_Currency_ID(), fl2.getAmtSourceCr(), null);
                    }
                    if (fl3 != null){
                        fl3.setAD_Org_ID(fl2.getAD_Org_ID());
                        fl3.setAmtAcctDr(fl2.getAmtAcctCr());
                        fl3.setAmtAcctCr(fl2.getAmtAcctDr());
                    }
                }
            }
            this.setC_Currency_ID(pago.getC_Currency_ID());

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


                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID()){
                    amt = pagoMedioPago.getTotalAmt();
                    this.setIsMultiCurrency(true);
                }
                this.setC_Currency_ID(pagoMedioPago.getC_Currency_ID());

                // Si el medio de pago esta configurado como no emisible, entonces no muevo cuentas de Emision.
                MZMedioPago medioPagoAux = (MZMedioPago) medioPagoItem.getZ_MedioPago();
                if (medioPagoAux.isTieneEmision()){

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

                    // Si este medio de pago esta marcado para contabilizarse
                    if (medioPagoAux.isContabilizar()){
                        // DR - Lineas de Medios de Pago - Monto de cada linea - Cuenta del medio de pago a emitir
                        int mpEmitidos_ID = getValidCombination_ID (Doc.ACCTYPE_MP_Emitidos, as);
                        if (mpEmitidos_ID <= 0){
                            p_Error = "Falta parametrizar Cuenta Contable para Medio de Pago Emitido en moneda de este Documento.";
                            log.log(Level.SEVERE, p_Error);
                            return null;
                        }

                        FactLine fl1 = null;
                        if (!pago.isExtornarAcct()){
                            if (amt.compareTo(Env.ZERO) >= 0){
                                fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), amt, null);
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl1 != null){
                                        fl1.setAmtAcctDr(pagoMedioPago.getTotalAmtMT());
                                    }
                                }
                            }
                            else{
                                fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), null, amt.negate());
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl1 != null){
                                        fl1.setAmtAcctCr(pagoMedioPago.getTotalAmtMT().negate());
                                    }
                                }
                            }
                        }
                        else{
                            if (amt.compareTo(Env.ZERO) >= 0){
                                fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), null, amt);
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl1 != null){
                                        fl1.setAmtAcctCr(pagoMedioPago.getTotalAmtMT());
                                    }
                                }
                            }
                            else{
                                fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), mpEmitidos_ID), getC_Currency_ID(), amt.negate(), null);
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl1 != null){
                                        fl1.setAmtAcctDr(pagoMedioPago.getTotalAmtMT().negate());
                                    }
                                }
                            }
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
                            return null;
                        }
                        FactLine fl11 = null;
                        if (!pago.isExtornarAcct()){
                            if (amt.compareTo(Env.ZERO) >= 0){
                                fl11 = fact.createLine(p_lines[i], MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), null, amt);
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl11 != null){
                                        fl11.setAmtAcctCr(pagoMedioPago.getTotalAmtMT());
                                    }
                                }

                            }
                            else{
                                fl11 = fact.createLine(p_lines[i], MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), amt.negate(), null);
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl11 != null){
                                        fl11.setAmtAcctDr(pagoMedioPago.getTotalAmtMT().negate());
                                    }
                                }
                            }
                        }
                        else{
                            if (amt.compareTo(Env.ZERO) >= 0){
                                fl11 = fact.createLine(p_lines[i], MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), amt,  null);
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl11 != null){
                                        fl11.setAmtAcctDr(pagoMedioPago.getTotalAmtMT());
                                    }
                                }
                            }
                            else{
                                fl11 = fact.createLine(p_lines[i], MAccount.get(getCtx(), emiPendEnt_ID), getC_Currency_ID(), null, amt.negate());
                                if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                        this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                    if (fl11 != null){
                                        fl11.setAmtAcctCr(pagoMedioPago.getTotalAmtMT().negate());
                                    }
                                }
                            }
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
                    }
                }

                // CR - Lineas de Medios de Pago - Monto de cada linea - Cuenta contable asociada a la cuenta bancaria.
                int accountID = -1;
                if (pagoMedioPago.getC_BankAccount_ID() > 0){

                    // Por defecto muevo la cuenta BankAsset de la cuenta bancaria (la cuenta del banco)
                    int accountBankType = Doc.ACCTTYPE_BankAsset;

                    // Si el medio de pago maneja folio y la fecha de vencimiento es posterior a hoy, entonces muevo
                    // la cuenta BanKInTransit de la cuenta bancaria. Esto es para cheques diferidos por ejemplo.
                    if ((medioPagoAux.isTieneFolio()) && (pagoMedioPago.getDueDate().after(fechaHoy))){
                        accountBankType = Doc.ACCTTYPE_BankInTransit;
                    }
                    accountID = AccountUtils.getBankValidCombinationID(getCtx(), accountBankType, pagoMedioPago.getC_BankAccount_ID(), as, null);
                    if (accountID <= 0){
                        MBankAccount bankAccount = (MBankAccount) pagoMedioPago.getC_BankAccount();
                        p_Error = "No se obtuvo Cuenta Contable (BankInTransit) asociada a la Cuenta Bancaria : " + bankAccount.getName();
                        log.log(Level.SEVERE, p_Error);
                        return null;
                    }
                }
                else if (pagoMedioPago.getC_CashBook_ID() > 0){
                    this.setC_CashBook_ID(pagoMedioPago.getC_CashBook_ID());
                    accountID = getValidCombination_ID(Doc.ACCTTYPE_CashExpense, as);
                    if (accountID <= 0){
                        MCashBook cashBook = (MCashBook) pagoMedioPago.getC_CashBook();
                        p_Error = "No se obtuvo Cuenta Contable (CashExpense) asociada a la caja : " + cashBook.getName();
                        log.log(Level.SEVERE, p_Error);
                        return null;
                    }
                }
                else{
                    if (pagoMedioPago.getZ_MedioPago_ID() > 0){
                        accountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Entregados, pagoMedioPago.getZ_MedioPago_ID(), pagoMedioPago.getC_Currency_ID(), as, null);
                        if (accountID <= 0){
                            MZMedioPago medioPago = (MZMedioPago) pagoMedioPago.getZ_MedioPago();
                            p_Error = "No se obtuvo Cuenta Contable (MP_Entregados) asociada al medio de pago : " + medioPago.getName();
                            log.log(Level.SEVERE, p_Error);
                            return null;
                        }
                    }
                    else{
                        p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                        log.log(Level.SEVERE, p_Error);
                        return null;
                    }
                }
                if (accountID > 0){
                    MAccount acctBankCr = MAccount.get(getCtx(), accountID);

                    FactLine fl22 = null;
                    if (!pago.isExtornarAcct()){
                        if (amt.compareTo(Env.ZERO) >= 0){
                            fl22 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), null, amt);
                            if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                    this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                if (fl22 != null){
                                    fl22.setAmtAcctCr(pagoMedioPago.getTotalAmtMT());
                                }
                            }

                        }
                        else{
                            fl22 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), amt.negate(), null);
                            if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                    this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                if (fl22 != null){
                                    fl22.setAmtAcctDr(pagoMedioPago.getTotalAmtMT().negate());
                                }
                            }

                        }
                    }
                    else{
                        if (amt.compareTo(Env.ZERO) >= 0){
                            fl22 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), amt, null);
                            if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                    this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                if (fl22 != null){
                                    fl22.setAmtAcctDr(pagoMedioPago.getTotalAmtMT());
                                }
                            }

                        }
                        else {
                            fl22 = fact.createLine (p_lines[i], acctBankCr, getC_Currency_ID(), null, amt.negate());
                            if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID() &&
                                    this.pago.getC_Currency_ID() == as.getC_Currency_ID()){
                                if (fl22 != null){
                                    fl22.setAmtAcctCr(pagoMedioPago.getTotalAmtMT().negate());
                                }
                            }

                        }
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
                    return null;
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

                // DR : Cuenta Acreedores del Socio de Negocio según moneda
                for (HashMap.Entry<Integer, InfoMultiCurrency> entry : hashAnticipo.entrySet()) {

                    // No considero montos en CERO
                    if (entry.getValue().amtSource.compareTo(Env.ZERO) == 0) {
                        continue;
                    }

                    if (entry.getValue().cuurencyID != pago.getC_Currency_ID()) {
                        this.setIsMultiCurrency(true);
                    }

                    this.setC_Currency_ID(entry.getValue().cuurencyID);

                    // CR : Monto total anticipos - Cuenta Anticipo del Socio de Negocio
                    int acctAnticipoID = getValidCombination_ID (Doc.ACCTTYPE_V_Prepayment, as);
                    if (acctAnticipoID <= 0){
                        p_Error = "Falta parametrizar Cuenta Contable para Anticipo a Proveedor en moneda de este Documento.";
                        log.log(Level.SEVERE, p_Error);
                        return null;
                    }
                    FactLine fl1 = null;
                    if (!pago.isExtornarAcct()){
                        if (getC_Currency_ID() == as.getC_Currency_ID()){
                            fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), null, entry.getValue().amtSource);
                        }
                        else{
                            fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(), null, entry.getValue().amtSource);
                            if (fl1 != null){
                                if (this.isMultiCurrency()){
                                    fl1.setAmtAcctCr(entry.getValue().amtAcct);
                                }
                                else{
                                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                                        BigDecimal amtAcct = entry.getValue().amtSource.multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP);
                                        fl1.setAmtAcctCr(amtAcct);
                                    }
                                }
                            }

                        }
                    }
                    else{
                        if (getC_Currency_ID() == as.getC_Currency_ID()){
                            fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(),  entry.getValue().amtSource, null);
                        }
                        else{
                            fl1 = fact.createLine(null, MAccount.get(getCtx(), acctAnticipoID), getC_Currency_ID(),  entry.getValue().amtSource, null);
                            if (fl1 != null){
                                if (this.isMultiCurrency()){
                                    fl1.setAmtAcctDr(entry.getValue().amtAcct);
                                }
                                else{
                                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                                        BigDecimal amtAcct = entry.getValue().amtSource.multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP);
                                        fl1.setAmtAcctDr(amtAcct);
                                    }
                                }
                            }
                        }
                    }
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.pago.getAD_Org_ID());
                    }
                }
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return fact;
    }

    /***
     * Genera asientos contables para tipo de documento base: CCD
     * Documentos de cuentas por cobrar.
     * Xpande. Created by Gabriel Vila on 12/19/20.
     * @return
     */
    private Fact createFacts_CCD(MAcctSchema as) {

        Fact fact = new Fact(this, as, Fact.POST_Actual);
        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        try{

            BigDecimal amtMediosPago = this.pago.getTotalMediosPago();
            if (amtMediosPago == null) amtMediosPago = Env.ZERO;

            HashMap<Integer, InfoMultiCurrency> hashPartnerCR = new HashMap<Integer, InfoMultiCurrency>();
            MZPago pago = (MZPago) getPO();
            List<MZPagoLin> pagoLinList = pago.getSelectedLines();
            for (MZPagoLin pagoLin: pagoLinList){
                // Sumarizo por moneda para contabilizacion CR por cuenta de Socio de Negocio y Moneda,
                if (!hashPartnerCR.containsKey(pagoLin.getC_Currency_ID())){
                    hashPartnerCR.put(pagoLin.getC_Currency_ID(), new InfoMultiCurrency());
                    hashPartnerCR.get(pagoLin.getC_Currency_ID()).cuurencyID = pagoLin.getC_Currency_ID();
                }
                hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtSource = hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtSource.add(pagoLin.getAmtAllocation());
                hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtAcct = hashPartnerCR.get(pagoLin.getC_Currency_ID()).amtAcct.add(pagoLin.getAmtAllocationMT());
            }

            // Cuando solo tengo Anticipos como documentos, debo agregar importe al hash.
            if (hashPartnerCR.size() <= 0){
                if ((this.pago.getPayAmt() != null) && (this.pago.getPayAmt().compareTo(Env.ZERO) != 0)){
                    hashPartnerCR.put(this.pago.getC_Currency_ID(), new InfoMultiCurrency());
                    hashPartnerCR.get(this.pago.getC_Currency_ID()).cuurencyID = this.pago.getC_Currency_ID();
                    hashPartnerCR.get(this.pago.getC_Currency_ID()).amtSource = hashPartnerCR.get(this.pago.getC_Currency_ID()).amtSource.add(this.pago.getPayAmt());
                    hashPartnerCR.get(this.pago.getC_Currency_ID()).amtAcct = hashPartnerCR.get(this.pago.getC_Currency_ID()).amtAcct.add(this.pago.getPayAmt());
                }
            }

            // CR : Cuenta Acreedores del Socio de Negocio según moneda
            for (HashMap.Entry<Integer, InfoMultiCurrency> entry : hashPartnerCR.entrySet()) {

                // No considero montos en CERO
                if (entry.getValue().amtSource.compareTo(Env.ZERO) == 0) {
                    continue;
                }

                if (entry.getValue().cuurencyID != pago.getC_Currency_ID()) {
                    this.setIsMultiCurrency(true);
                }

                this.setC_Currency_ID(entry.getValue().cuurencyID);


                // CR - Deudores comerciales por el total del cobro
                int receivables_ID = getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);
                if (receivables_ID <= 0){
                    MCurrency currency = new MCurrency(getCtx(), this.getC_Currency_ID(), null);
                    p_Error = "Falta parametrizar Cuenta Contable para CXC del Cliente en moneda: " + currency.getISO_Code();
                    log.log(Level.SEVERE, p_Error);
                    return null;
                }

                FactLine fl3 = null;

                if (grossAmt.compareTo(Env.ZERO) >= 0){
                    fl3 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(),  null, entry.getValue().amtSource);
                }
                else{
                    fl3 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(),  entry.getValue().amtSource.negate(), null);
                }

                if (fl3 != null){
                    fl3.setAD_Org_ID(this.pago.getAD_Org_ID());

                    if (entry.getValue().cuurencyID != as.getC_Currency_ID()){
                        // Si tengo tasa de cambio ingresada, tomo esa.
                        MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), getTrxName());
                        if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                            if (grossAmt.compareTo(Env.ZERO) >= 0){
                                fl3.setAmtAcctCr(entry.getValue().amtSource.multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP));
                            }
                            else {
                                fl3.setAmtAcctDr(entry.getValue().amtSource.negate().multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP));
                            }
                        }
                        else{
                            if (this.isMultiCurrency()){
                                p_Error = "Debe indicar Tasa de Cambio para moneda Nacional. Debe ingredarlo en la pestaña: Monedas";
                                log.log(Level.SEVERE, p_Error);
                                return null;
                            }
                        }
                    }
                    fl3.saveEx();
                }

                // Si no tengo medios de pago, se trata de una afectacion y por lo tanto tengo que dar vuelta el asiento.
                if (amtMediosPago.compareTo(Env.ZERO) == 0){
                    FactLine fl4;
                    if ((fl3.getAmtSourceDr() != null) && (fl3.getAmtSourceDr().compareTo(Env.ZERO) != 0)){
                        fl4 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(), null, fl3.getAmtSourceCr());
                    }
                    else{
                        fl4 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(), fl3.getAmtSourceCr(), null);
                    }
                    if (fl4 != null){
                        fl4.setAD_Org_ID(fl3.getAD_Org_ID());
                    }
                }
            }

            this.setC_Currency_ID(this.pago.getC_Currency_ID());

            // DR - Lineas de Medios de Pago - Monto de cada linea.
            // Cuenta contable asociada a la cuenta bancaria si hay, y sino tengo cuenta bancaria, entonces cuenta del medio de pago.
            for (int i = 0; i < p_lines.length; i++)
            {

                int mProductID = 0;

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
                        return null;
                    }
                }
                else{

                    if (pagoMedioPago.getZ_MedioPagoIdent_ID() > 0){
                        accountID = AccountUtils.getMedioPagoIdentValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Recibidos, pagoMedioPago.getZ_MedioPagoIdent_ID(), pagoMedioPago.getC_Currency_ID(), as, null);

                        // Obtengo producto del identificador si es que tiene uno asociado.
                        MZMedioPagoIdent medioPagoIdent = (MZMedioPagoIdent) pagoMedioPago.getZ_MedioPagoIdent();
                        mProductID = medioPagoIdent.getLastProductID();

                    }

                    if (accountID <= 0) {
                        if (pagoMedioPago.getZ_MedioPago_ID() > 0){
                            accountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Recibidos, pagoMedioPago.getZ_MedioPago_ID(), pagoMedioPago.getC_Currency_ID(), as, null);
                            if (accountID <= 0){
                                MZMedioPago medioPago = (MZMedioPago) pagoMedioPago.getZ_MedioPago();
                                p_Error = "No se obtuvo Cuenta Contable (MP_Recibidos) asociada al medio de pago : " + medioPago.getName();
                                log.log(Level.SEVERE, p_Error);
                                return null;
                            }
                        }
                        else{
                            p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                            log.log(Level.SEVERE, p_Error);
                            return null;
                        }
                    }
                }

                // DR - Lineas de Medios de Pago - Monto de cada linea
                FactLine fl1 = null;

                if (amt.compareTo(Env.ZERO) >= 0){
                    fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
                }
                else{
                    fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, amt.negate());
                }

                if (fl1 != null){
                    fl1.setAD_Org_ID(this.pago.getAD_Org_ID());

                    if (mProductID > 0){
                        fl1.setM_Product_ID(mProductID);
                    }

                    if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID()){
                        if (pagoMedioPago.getC_Currency_ID() == as.getC_Currency_ID()){
                            if (amt.compareTo(Env.ZERO) >= 0){
                                fl1.setAmtAcctDr(pagoMedioPago.getTotalAmt());
                            }
                            else {
                                fl1.setAmtAcctCr(pagoMedioPago.getTotalAmt().negate());
                            }
                        }
                    }
                    fl1.saveEx();
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
                    if (pagoMedioPago.getZ_MedioPagoIdent_ID() > 0){
                        factDet.setZ_MedioPagoIdent_ID(pagoMedioPago.getZ_MedioPagoIdent_ID());
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
                        return null;
                    }

                    // DR - Lineas de Resguardos Recibidos - Monto de cada linea
                    FactLine fl1 = null;

                    if (amt.compareTo(Env.ZERO) >= 0){
                        fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
                    }
                    else{
                        fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, amt.negate());
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
                        factDet.setCurrencyRate(resgRecibido.getMultiplyRate());
                        factDet.setZ_RetencionSocio_ID(resgRecibido.getZ_RetencionSocio_ID());
                        factDet.saveEx();
                    }
                }
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return fact;
    }

    /***
     * Genera asientos contables para tipo de documento base: CCD
     * Documentos de cuentas por cobrar.
     * Xpande. Created by Gabriel Vila on 12/19/20.
     * @return
     */
    private Fact createFacts_CCD_OLD(MAcctSchema as) {

        Fact fact = new Fact(this, as, Fact.POST_Actual);
        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        try{

            BigDecimal amtMediosPago = this.pago.getTotalMediosPago();
            if (amtMediosPago == null) amtMediosPago = Env.ZERO;

            // CR - Deudores comerciales por el total del cobro
            int receivables_ID = getValidCombination_ID(Doc.ACCTTYPE_C_Receivable, as);

            FactLine fl3 = null;

            if (grossAmt.compareTo(Env.ZERO) >= 0){
                fl3 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(),  null, grossAmt);
            }
            else{
                fl3 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(),  grossAmt.negate(), null);
            }

            if (fl3 != null){
                fl3.setAD_Org_ID(this.pago.getAD_Org_ID());

                if (this.pago.getC_Currency_ID() != as.getC_Currency_ID()){
                    // Si tengo tasa de cambio ingresada, tomo esa.
                    MZPagoMoneda pagoMoneda = MZPagoMoneda.getByCurrencyPago(getCtx(), this.pago.get_ID(), as.getC_Currency_ID(), null);
                    if ((pagoMoneda != null) && (pagoMoneda.get_ID() > 0)){
                        if (grossAmt.compareTo(Env.ZERO) >= 0){
                            fl3.setAmtAcctCr(grossAmt.multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP));
                        }
                        else {
                            fl3.setAmtAcctDr(grossAmt.negate().multiply(pagoMoneda.getMultiplyRate()).setScale(2, RoundingMode.HALF_UP));
                        }
                    }
                    else{
                        p_Error = "Debe indicar Tasa de Cambio para moneda Nacional. Debe ingredarlo en la pestaña: Monedas";
                        log.log(Level.SEVERE, p_Error);
                        return null;
                    }
                }
                fl3.saveEx();
            }

            // Si no tengo medios de pago, se trata de una afectacion y por lo tanto tengo que dar vuelta el asiento.
            if (amtMediosPago.compareTo(Env.ZERO) == 0){
                FactLine fl4;
                if ((fl3.getAmtSourceDr() != null) && (fl3.getAmtSourceDr().compareTo(Env.ZERO) != 0)){
                    fl4 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(), null, fl3.getAmtSourceCr());
                }
                else{
                    fl4 = fact.createLine(null, MAccount.get(getCtx(), receivables_ID), getC_Currency_ID(), fl3.getAmtSourceCr(), null);
                }
                if (fl4 != null){
                    fl4.setAD_Org_ID(fl3.getAD_Org_ID());
                }
            }

            // DR - Lineas de Medios de Pago - Monto de cada linea.
            // Cuenta contable asociada a la cuenta bancaria si hay, y sino tengo cuenta bancaria, entonces cuenta del medio de pago.
            for (int i = 0; i < p_lines.length; i++)
            {

                int mProductID = 0;

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
                        return null;
                    }
                }
                else{

                    if (pagoMedioPago.getZ_MedioPagoIdent_ID() > 0){
                        accountID = AccountUtils.getMedioPagoIdentValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Recibidos, pagoMedioPago.getZ_MedioPagoIdent_ID(), pagoMedioPago.getC_Currency_ID(), as, null);

                        // Obtengo producto del identificador si es que tiene uno asociado.
                        MZMedioPagoIdent medioPagoIdent = (MZMedioPagoIdent) pagoMedioPago.getZ_MedioPagoIdent();
                        mProductID = medioPagoIdent.getLastProductID();

                    }

                    if (accountID <= 0) {
                        if (pagoMedioPago.getZ_MedioPago_ID() > 0){
                            accountID = AccountUtils.getMedioPagoValidCombinationID(getCtx(), Doc.ACCTYPE_MP_Recibidos, pagoMedioPago.getZ_MedioPago_ID(), pagoMedioPago.getC_Currency_ID(), as, null);
                            if (accountID <= 0){
                                MZMedioPago medioPago = (MZMedioPago) pagoMedioPago.getZ_MedioPago();
                                p_Error = "No se obtuvo Cuenta Contable (MP_Recibidos) asociada al medio de pago : " + medioPago.getName();
                                log.log(Level.SEVERE, p_Error);
                                return null;
                            }
                        }
                        else{
                            p_Error = "No se indica Cuenta Bancaria y tampoco se indica Medio de Pago";
                            log.log(Level.SEVERE, p_Error);
                            return null;
                        }
                    }
                }

                // DR - Lineas de Medios de Pago - Monto de cada linea
                FactLine fl1 = null;

                if (amt.compareTo(Env.ZERO) >= 0){
                    fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
                }
                else{
                    fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, amt.negate());
                }

                if (fl1 != null){
                    fl1.setAD_Org_ID(this.pago.getAD_Org_ID());

                    if (mProductID > 0){
                        fl1.setM_Product_ID(mProductID);
                    }

                    if (pagoMedioPago.getC_Currency_ID() != this.pago.getC_Currency_ID()){
                        if (pagoMedioPago.getC_Currency_ID() == as.getC_Currency_ID()){
                            if (amt.compareTo(Env.ZERO) >= 0){
                                fl1.setAmtAcctDr(pagoMedioPago.getTotalAmt());
                            }
                            else {
                                fl1.setAmtAcctCr(pagoMedioPago.getTotalAmt().negate());
                            }
                        }
                    }
                    fl1.saveEx();
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
                    if (pagoMedioPago.getZ_MedioPagoIdent_ID() > 0){
                        factDet.setZ_MedioPagoIdent_ID(pagoMedioPago.getZ_MedioPagoIdent_ID());
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
                        return null;
                    }

                    // DR - Lineas de Resguardos Recibidos - Monto de cada linea
                    FactLine fl1 = null;

                    if (amt.compareTo(Env.ZERO) >= 0){
                        fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
                    }
                    else{
                        fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, amt.negate());
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
                        factDet.setCurrencyRate(resgRecibido.getMultiplyRate());
                        factDet.setZ_RetencionSocio_ID(resgRecibido.getZ_RetencionSocio_ID());
                        factDet.saveEx();
                    }
                }
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return fact;
    }

    /***
     * Genera asientos contables para tipo de documento base: PPA
     * Documentos de Anticipos a Proveedores sin medios de pago.
     * Estos anticipos requieren una orden de pago en la cual se ingresan los medios de pago.
     * Xpande. Created by Gabriel Vila on 12/19/20.
     * @return
     */
    private Fact createFacts_PPA(MAcctSchema as) {

        Fact fact = new Fact(this, as, Fact.POST_Actual);
        BigDecimal grossAmt = getAmount(Doc.AMTTYPE_Gross);

        try{
            // DR : Monto total del Pago - Cuenta Anticipo del Socio de Negocio
            int acctAnticipoID = getValidCombination_ID (Doc.ACCTTYPE_V_Prepayment, as);
            if (acctAnticipoID <= 0){
                p_Error = "Falta parametrizar Cuenta Contable para Anticipo a Proveedor en moneda de este Documento.";
                log.log(Level.SEVERE, p_Error);
                return null;
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
                return null;
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
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return fact;
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
