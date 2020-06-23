package org.compiere.acct;

import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
import org.compiere.model.MElementValue;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctFactDet;
import org.xpande.acct.model.MZDifCambio;
import org.xpande.acct.model.MZDifCambioLin;
import org.xpande.acct.model.X_Z_AcctFactDet;
import org.xpande.financial.model.MZMedioPagoItem;
import org.xpande.financial.model.MZPago;
import org.xpande.financial.model.MZPagoMedioPago;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Contabilización de documentos de Diferencia de Cambio.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/11/19.
 */
public class Doc_DifCambio extends Doc {

    private MZDifCambio difCambio = null;
    private MDocType docType = null;

    /**
     *  Constructor
     */
    public Doc_DifCambio(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_DifCambio(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZDifCambio.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.difCambio = (MZDifCambio) getPO();

        MAcctSchema acctSchema = (MAcctSchema) this.difCambio.getC_AcctSchema();

        setDateDoc(this.difCambio.getDateDoc());
        setDateAcct(this.difCambio.getDateAcct());
        setC_Currency_ID(acctSchema.getC_Currency_ID());

        this.docType = (MDocType) this.difCambio.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        //	Lineas del documento.
        p_lines = loadLines();

        log.fine("Lines=" + p_lines.length);

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

        List<MZDifCambioLin> difCambioLinList = this.difCambio.getLines();

        for (MZDifCambioLin difCambioLin: difCambioLinList){
            DocLine docLine = new DocLine(difCambioLin, this);
            list.add(docLine);
        }

        //	Convert to Array
        DocLine[] dls = new DocLine[list.size()];
        list.toArray(dls);

        return dls;
    }


    @Override
    public BigDecimal getBalance() {
        return Env.ZERO;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);

        // Obtengo y valido cuentas contables para diferencia de cambio perdida y ganada
        int accountPerdidaID = as.getAcctSchemaDefault().get_ValueAsInt("DC_Perdida_Acct");
        int accountGanadaID = as.getAcctSchemaDefault().get_ValueAsInt("DC_Ganada_Acct");

        if (accountPerdidaID <= 0){
            p_Error = "No se obtuvo Cuenta Contable para Diferencia de Cambio Perdida (en cuentas por defecto del Esquema Contable).";
            log.log(Level.SEVERE, p_Error);
            fact = null;
            facts.add(fact);
            return facts;
        }

        if (accountGanadaID <= 0){
            p_Error = "No se obtuvo Cuenta Contable para Diferencia de Cambio Ganada (en cuentas por defecto del Esquema Contable).";
            log.log(Level.SEVERE, p_Error);
            fact = null;
            facts.add(fact);
            return facts;
        }

        MAccount accountDifCambioPerdida = MAccount.get(getCtx(), accountPerdidaID);
        MAccount accountDifCambioGanada = MAccount.get(getCtx(), accountGanadaID);

        // Recorro lineas del documento a contabilizar
        for (int i = 0; i < p_lines.length; i++){

            MZDifCambioLin difCambioLin = new MZDifCambioLin(getCtx(), p_lines[i].get_ID(), this.getTrxName());

            // Instancio Modelo de la cuenta
            MElementValue elementValue = new MElementValue(getCtx(), difCambioLin.getC_ElementValue_ID(), null);

            // Obtengo combinacion contable para cuenta de esta linea
            MAccount accountLinea = MAccount.get(getCtx(), this.difCambio.getAD_Client_ID(), this.difCambio.getAD_Org_ID(), as.getC_AcctSchema_ID(),
                    difCambioLin.getC_ElementValue_ID(),0, 0, 0, 0,0,
                    0,0,0,0, 0,0,0,0,0,
                    0,0, difCambioLin.get_TrxName());

            if (accountLinea != null)
            {
                accountLinea.saveEx();
            }
            else{
                p_Error = "No se pudo obtener combinación contable para cuenta : " + elementValue.getValue();
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            // Seteo cuenta para diferencia de cambio perdida o ganada segun naturaleza de la cuenta de esta linea (activo o pasivo)
            MAccount accountDifCambio = null;
            if (elementValue.getAccountType().equalsIgnoreCase("A")){
                accountDifCambio = accountDifCambioGanada;
            }
            else if (elementValue.getAccountType().equalsIgnoreCase("L")){
                accountDifCambio = accountDifCambioPerdida;
            }

            // Si la diferencia a guardar es debito
            if (difCambioLin.getAmtAcctDrTo().compareTo(Env.ZERO) > 0){

                // DR  - Cuenta de la linea
                FactLine f1 = fact.createLine(p_lines[i], accountLinea, as.getC_Currency_ID(), difCambioLin.getAmtAcctDrTo(), null);
                if (f1 != null){
                    f1.setAD_Org_ID(this.difCambio.getAD_Org_ID());
                    if (difCambioLin.getC_BPartner_ID() > 0){
                        f1.setC_BPartner_ID(difCambioLin.getC_BPartner_ID());
                    }
                }

                // CR - Cuenta diferencia de cambio ganada o perdida
                //FactLine f2 = fact.createLine(p_lines[i], accountDifCambio, as.getC_Currency_ID(), null, difCambioLin.getAmtAcctDrTo());
                FactLine f2 = fact.createLine(p_lines[i], accountDifCambioGanada, as.getC_Currency_ID(), null, difCambioLin.getAmtAcctDrTo());
                if (f2 != null){
                    f2.setAD_Org_ID(this.difCambio.getAD_Org_ID());
                    if (difCambioLin.getC_BPartner_ID() > 0){
                        f2.setC_BPartner_ID(difCambioLin.getC_BPartner_ID());
                    }
                }
            }
            // Si la diferencia a guardar es credito
            else if (difCambioLin.getAmtAcctCrTo().compareTo(Env.ZERO) > 0){

                // DR  - Cuenta diferencia de cambio perdida
                //FactLine f1 = fact.createLine(p_lines[i], accountDifCambio, as.getC_Currency_ID(), difCambioLin.getAmtAcctCrTo(), null);
                FactLine f1 = fact.createLine(p_lines[i], accountDifCambioPerdida, as.getC_Currency_ID(), difCambioLin.getAmtAcctCrTo(), null);
                if (f1 != null){
                    f1.setAD_Org_ID(this.difCambio.getAD_Org_ID());
                    if (difCambioLin.getC_BPartner_ID() > 0){
                        f1.setC_BPartner_ID(difCambioLin.getC_BPartner_ID());
                    }
                }

                // CR - Cuenta de la linea
                FactLine f2 = fact.createLine(p_lines[i], accountLinea, as.getC_Currency_ID(), null, difCambioLin.getAmtAcctCrTo());
                if (f2 != null){
                    f2.setAD_Org_ID(this.difCambio.getAD_Org_ID());
                    if (difCambioLin.getC_BPartner_ID() > 0){
                        f2.setC_BPartner_ID(difCambioLin.getC_BPartner_ID());
                    }
                }
            }
        }

        facts.add(fact);
        return facts;

    }
}
