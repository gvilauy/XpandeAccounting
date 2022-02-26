package org.compiere.acct;

import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.financial.model.*;
import org.xpande.financial.utils.InfoMultiCurrency;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/30/20.
 */
public class Doc_MPagoCaja extends Doc {

    private MZMPagoCaja pagoCaja = null;
    private MDocType docType = null;

    /**
     *  Constructor
     */
    public Doc_MPagoCaja(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_MPagoCaja(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZMPagoCaja.class, rs, null, trxName);
    }

    @Override
    protected String loadDocumentDetails() {

        this.pagoCaja = (MZMPagoCaja) getPO();
        setDateDoc(pagoCaja.getDateDoc());
        setDateAcct(pagoCaja.getDateDoc());
        setC_Currency_ID(pagoCaja.getC_Currency_ID());
        setC_BPartner_ID(pagoCaja.getC_BPartner_ID());

        this.docType = (MDocType) pagoCaja.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        // Seteo grupo de socio de negocio en caso de tenerlo
        MBPartner partner = (MBPartner) this.pagoCaja.getC_BPartner();
        if ((partner != null) && (partner.get_ID() > 0)){
            if (partner.getC_BP_Group_ID() > 0){
                this.cBPGroupID = partner.getC_BP_Group_ID();
            }
        }

        //	Lineas del documento.
        p_lines = loadLines();

        setAmount(Doc.AMTTYPE_Gross, pagoCaja.getTotalAmt());

        log.fine("Lines=" + p_lines.length);

        return null;
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

        // DR - CR - Lineas de Medios de Pago - Monto de cada linea.
        for (int i = 0; i < p_lines.length; i++)
        {

            BigDecimal amt = p_lines[i].getAmtSource();

            MZMPagoCajaLin pagoCajaLin = new MZMPagoCajaLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());
            MZMedioPagoItem medioPagoItem = (MZMedioPagoItem) pagoCajaLin.getZ_MedioPagoItem();
            MZMedioPago medioPago = (MZMedioPago) pagoCajaLin.getZ_MedioPago();

            // DR - Lineas de Medios de Pago - Monto de cada linea
            this.zMedioPagoID = medioPago.get_ID();
            int accountID = getValidCombination_ID(Doc.ACCTYPE_MP_Recibidos, as);
            if (accountID <= 0){
                p_Error = "No se obtuvo Cuenta Contable para Medios de Pago recibidos y moneda del documento.";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }
            FactLine fl1 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), amt, null);
            if (fl1 != null){
                fl1.setAD_Org_ID(this.pagoCaja.getAD_Org_ID());
            }

            // CR - Lineas de Medios de Pago - Monto de cada linea
            accountID = getValidCombination_ID(Doc.ACCTYPE_MP_MPagoCaja, as);
            if (accountID <= 0){
                p_Error = "No se obtuvo Cuenta Contable para Contraparte Ingreso de Cheque de linea de caja y moneda del documento.";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }
            FactLine fl2 = fact.createLine(p_lines[i], MAccount.get(getCtx(), accountID), getC_Currency_ID(), null, amt);
            if (fl2 != null){
                fl2.setAD_Org_ID(this.pagoCaja.getAD_Org_ID());
            }

            // Detalle de asiento
            if (fl1 != null){
                fl1.saveEx();
                MZAcctFactDet factDet = new MZAcctFactDet(getCtx(), 0, getTrxName());
                factDet.setFact_Acct_ID(fl1.get_ID());
                factDet.setAD_Org_ID(this.pagoCaja.getAD_Org_ID());
                factDet.setZ_MPagoCaja_ID(this.pagoCaja.get_ID());
                factDet.setZ_MedioPago_ID(pagoCajaLin.getZ_MedioPago_ID());
                if (pagoCajaLin.getC_Bank_ID() > 0){
                    factDet.setC_Bank_ID(pagoCajaLin.getC_Bank_ID());
                }
                factDet.setNroMedioPago(pagoCajaLin.getDocumentNoRef());

                if (pagoCajaLin.getZ_MedioPagoItem_ID() > 0){
                    factDet.setZ_MedioPagoItem_ID(pagoCajaLin.getZ_MedioPagoItem_ID());
                    if (medioPagoItem != null){
                        if (medioPagoItem.getNroMedioPago() != null){
                            factDet.setNroMedioPago(medioPagoItem.getNroMedioPago());
                        }
                    }
                }
                factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_EMITIDO);
                if (medioPagoItem.isEntregado()){
                    factDet.setEstadoMedioPago(X_Z_AcctFactDet.ESTADOMEDIOPAGO_ENTREGADO);
                }
                factDet.setCurrencyRate(Env.ONE);
                factDet.setDueDate(pagoCajaLin.getDueDate());
                factDet.saveEx();
            }
        }

        facts.add(fact);
        return facts;

    }

    /***
     * Carga lineas de documento.
     * Xpande. Created by Gabriel Vila on 7/30/20.
     * @return
     */
    private DocLine[] loadLines ()
    {
        ArrayList<DocLine> list = new ArrayList<DocLine>();

        List<MZMPagoCajaLin> pagoCajaLinList = this.pagoCaja.getLines();

        for (MZMPagoCajaLin pagoCajaLin: pagoCajaLinList){

            DocLine docLine = new DocLine(pagoCajaLin, this);
            docLine.setAmount(pagoCajaLin.getTotalAmt());
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }

}
