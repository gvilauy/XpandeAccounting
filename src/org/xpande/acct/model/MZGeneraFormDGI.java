package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.comercial.model.MZComercialConfig;
import org.xpande.core.utils.CurrencyUtils;
import org.xpande.core.utils.DateUtils;
import org.xpande.financial.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

/**
 * Modelo para cabezal de proceso de generación de Formulario de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/21/18.
 */
public class MZGeneraFormDGI extends X_Z_GeneraFormDGI {

    private BufferedWriter bufferedWriterTXT = null;
    private MZAcctConfig acctConfig = null;


    public MZGeneraFormDGI(Properties ctx, int Z_GeneraFormDGI_ID, String trxName) {
        super(ctx, Z_GeneraFormDGI_ID, trxName);
    }

    public MZGeneraFormDGI(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }


    /***
     * Obtiene documentos para procesar según tipo de formulario de DGI seleccionado.
     * Xpande. Created by Gabriel Vila on 11/22/18
     * @return
     */
    public String getDocuments(){

        String message = null;

        try{

            this.acctConfig = MZAcctConfig.getDefault(getCtx(), null);

            // Elimino información anterior
            this.deleteDocuments();

            // Obtengo documentos según tipo de formulario de DGI a procesar.
            if (this.getTipoFormularioDGI().equalsIgnoreCase(X_Z_GeneraFormDGI.TIPOFORMULARIODGI_FORMULARIO2181)){

                // Obtengo documentos para Formulario 2/181
                message = this.getDocuments2181();

            }
            else if (this.getTipoFormularioDGI().equalsIgnoreCase(X_Z_GeneraFormDGI.TIPOFORMULARIODGI_FORMULARIO2183)){

                // Obtengo documentos para Formulario 2/183
                message = this.getDocuments2183();

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    /***
     * Genera archivo de para DGI según formulario seleccionado.
     * @return
     */
    public String generateFile(){

        String message = null;

        try{

            // Obtengo lineas
            List<MZGeneraFormDGILin> dgiLinList = this.getLines();
            List<MZGeneraFormDGIResg> dgiResgList = this.getLinesResg();

            // Si no tengo lineas, no hago nada
            if (dgiLinList.size() <= 0){
                if (dgiResgList.size() <= 0){
                    return "No hay lineas para procesar";
                }
            }

            // Obtengo RUT de la Organización
            MOrgInfo orgInfo = MOrgInfo.get(getCtx(), this.getAD_Org_ID(), null);
            if (orgInfo == null){
                return "No se pudo obtener información de la organización seleccionada";
            }
            String taxID = orgInfo.getTaxID();
            if ((taxID == null) || (taxID.trim().equalsIgnoreCase(""))){
                return "Falta parametrizar Número de Identificación para la Organización seleccionada";
            }

            // Obtengo literal del período según formato requerido por DGI
            MPeriod period = (MPeriod)this.getC_Period();
            if ((period == null) || (period.get_ID() <= 0)){
                Timestamp fechaPeriodo = this.getStartDate();
                if (fechaPeriodo == null) fechaPeriodo = this.getStartDate2();
                if (fechaPeriodo == null) fechaPeriodo = this.getStartDate3();
                period = MPeriod.get(getCtx(), fechaPeriodo, this.getAD_Org_ID());
            }

            MYear year = (MYear)period.getC_Year();
            String month = org.apache.commons.lang.StringUtils.leftPad(String.valueOf(period.getPeriodNo()), 2, "0");
            String literalPeriodo = String.valueOf(year.getYearAsInt()) + month;

            // Creo archivo para la generación del Formulario de DGI
            this.createFile();

            // Cargo información en archivo según tipo de formulario de DGI a procesar
            if (this.getTipoFormularioDGI().equalsIgnoreCase(X_Z_GeneraFormDGI.TIPOFORMULARIODGI_FORMULARIO2181)){

                // Genero archivo para Formulario 2/181
                message = this.generateFile2181(taxID, literalPeriodo);

            }
            else if (this.getTipoFormularioDGI().equalsIgnoreCase(X_Z_GeneraFormDGI.TIPOFORMULARIODGI_FORMULARIO2183)){

                // Genero archivo para Formulario 2/181
                message = this.generateFile2183(taxID, literalPeriodo);
            }

            if (this.bufferedWriterTXT != null){
                this.bufferedWriterTXT.flush();
                this.bufferedWriterTXT.close();
                this.bufferedWriterTXT = null;
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally{

            if (this.bufferedWriterTXT != null){
                try {
                    this.bufferedWriterTXT.flush();
                    this.bufferedWriterTXT.close();
                }
                catch (Exception e2) {
                }
            }
        }

        return message;
    }


    /***
     * Obtiene y retorna lineas de este modelo.
     * Xpande. Created by Gabriel Vila on 11/25/18.
     * @return
     */
    public List<MZGeneraFormDGILin> getLines() {

        String whereClause = X_Z_GeneraFormDGILin.COLUMNNAME_Z_GeneraFormDGI_ID + " =" + this.get_ID();

        List<MZGeneraFormDGILin> lines = new Query(getCtx(), I_Z_GeneraFormDGILin.Table_Name, whereClause, get_TrxName()).list();

        return lines;
    }

    /***
     * Obtiene y retorna lineas de resguardos de este modelo.
     * Xpande. Created by Gabriel Vila on 11/25/18.
     * @return
     */
    public List<MZGeneraFormDGIResg> getLinesResg() {

        String whereClause = X_Z_GeneraFormDGIResg.COLUMNNAME_Z_GeneraFormDGI_ID + " =" + this.get_ID();

        List<MZGeneraFormDGIResg> lines = new Query(getCtx(), I_Z_GeneraFormDGIResg.Table_Name, whereClause, get_TrxName()).list();

        return lines;
    }


    /***
     * Genera archivo para el formato 2181.
     * @param dgiLinList
     * @param taxID
     * @param literalPeriodo
     * @return
     */
    private String generateFile2181(String taxID, String literalPeriodo) {

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            sql = " select taxid, c_period_id, z_acctconfigrubrodgi_id, sum(amtdocumentmt) as amttotal " +
                    "from z_generaformdgilin " +
                    "where z_generaformdgi_id =" + this.get_ID() +
                    "group by taxid, c_period_id, z_acctconfigrubrodgi_id " +
                    "order by 1,2,3 ";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            rs = pstmt.executeQuery();

            while(rs.next()){

                String cadena = "";
                cadena += taxID + ";02181;" + literalPeriodo + ";";

                // Rut del Socio de Negocio
                String rutPartner = org.apache.commons.lang.StringUtils.leftPad(rs.getString("taxid"), 12, "0");
                cadena += rutPartner + ";";

                // Periodo del comprobante
                MPeriod periodInv = MPeriod.get(getCtx(), rs.getInt("c_period_id"));
                MYear yearInv = (MYear)periodInv.getC_Year();
                String monthInv = org.apache.commons.lang.StringUtils.leftPad(String.valueOf(periodInv.getPeriodNo()), 2, "0");
                String literalPeriodInv = String.valueOf(yearInv.getYearAsInt()) + monthInv;
                cadena += literalPeriodInv + ";";

                // Rubro DGI
                MZAcctConfigRubroDGI rubroDGI = new MZAcctConfigRubroDGI(getCtx(), rs.getInt("z_acctconfigrubrodgi_id"), null);
                cadena += rubroDGI.getValue() + ";";

                // Importe. Doce Digitos, completo con CEROS a la izquierda, sin decimales, y si es negativo el
                // simbolo de menos (-) se pone siempre primero. (Ej: -00000000359)
                BigDecimal monto = rs.getBigDecimal("amttotal").setScale(2, RoundingMode.HALF_UP);
                String montoStr ="";
                if (monto.compareTo(Env.ZERO) >= 0){
                    montoStr = String.valueOf(monto);
                    montoStr = org.apache.commons.lang.StringUtils.leftPad(montoStr, 12, "0");
                }
                else{
                    // Monto negativo, tengo que hacer trampita para poner el signo de menos delante de los ceros
                    montoStr = String.valueOf(monto.negate());
                    montoStr = "-" + org.apache.commons.lang.StringUtils.leftPad(montoStr, 11, "0");
                }

                cadena += montoStr + ";";

                // Guardo linea en TXT
                if (cadena != null){
                    this.bufferedWriterTXT.append(cadena + System.lineSeparator());
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }

        return message;
    }

    /***
     * Genera archivo para el formato 2183.
     * Xpande. Created by Gabriel Vila on 9/20/19.
     * @param dgiLinList
     * @param taxID
     * @param literalPeriodo
     * @return
     */
    private String generateFile2183(String taxID, String literalPeriodo) {

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            sql = " select taxid, c_period_id, z_acctconfigrubrodgi_id, sum(amtretencion) as amttotal " +
                    "from z_generaformdgiresg " +
                    "where z_generaformdgi_id =" + this.get_ID() +
                    "group by taxid, c_period_id, z_acctconfigrubrodgi_id " +
                    "order by 1,2,3 ";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            rs = pstmt.executeQuery();

            while(rs.next()){

                String cadena = "";
                cadena += taxID + ";02183;" + literalPeriodo + ";";

                // Rut del Socio de Negocio
                String rutPartner = org.apache.commons.lang.StringUtils.leftPad(rs.getString("taxid"), 12, "0");
                cadena += rutPartner + ";";

                // Periodo del comprobante
                MPeriod periodInv = MPeriod.get(getCtx(), rs.getInt("c_period_id"));
                MYear yearInv = (MYear)periodInv.getC_Year();
                String monthInv = org.apache.commons.lang.StringUtils.leftPad(String.valueOf(periodInv.getPeriodNo()), 2, "0");
                String literalPeriodInv = String.valueOf(yearInv.getYearAsInt()) + monthInv;
                cadena += literalPeriodInv + ";";

                // Rubro DGI
                MZAcctConfigRubroDGI rubroDGI = new MZAcctConfigRubroDGI(getCtx(), rs.getInt("z_acctconfigrubrodgi_id"), null);
                cadena += rubroDGI.getValue() + ";";

                // Importe. Doce Digitos, completo con CEROS a la izquierda, sin decimales, y si es negativo el
                // simbolo de menos (-) se pone siempre primero. (Ej: -00000000359)
                BigDecimal monto = rs.getBigDecimal("amttotal").setScale(2, RoundingMode.HALF_UP);
                String montoStr ="";
                if (monto.compareTo(Env.ZERO) >= 0){
                    montoStr = String.valueOf(monto);
                    montoStr = org.apache.commons.lang.StringUtils.leftPad(montoStr, 12, "0");
                }
                else{
                    // Monto negativo, tengo que hacer trampita para poner el signo de menos delante de los ceros
                    montoStr = String.valueOf(monto.negate());
                    montoStr = "-" + org.apache.commons.lang.StringUtils.leftPad(montoStr, 11, "0");
                }

                cadena += montoStr + ";";

                // Guardo linea en TXT
                if (cadena != null){
                    this.bufferedWriterTXT.append(cadena + System.lineSeparator());
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }

        return message;
    }

    /***
     * Crea el archivo de generación de Formulario de DGI.
     * Xpande. Created by Gabriel Vila on 11/25/18.
     */
    private void createFile(){

        String encoding = "8859_1";

        try{
            String fileName = null;
            String filePath = this.getFilePathOrName();

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");

            if (filePath.contains("/")){
                fileName = filePath + "/" + "DGI_" + this.getTipoFormularioDGI() + "_" + df.format(new Timestamp(System.currentTimeMillis())) + ".txt";
            }
            else{
                fileName = filePath + "\\" + "DGI_" + this.getTipoFormularioDGI() + "_" + df.format(new Timestamp(System.currentTimeMillis())) + ".txt";
            }

            this.bufferedWriterTXT = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), encoding));

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Elimina datos anteriores en tabla de lineas y de errores.
     * Xpande. Created by Gabriel Vila on 11/22/18
     */
    private void deleteDocuments() {

        String action = "";

        try{

            // Elimino lineas
            action = " delete from " + X_Z_GeneraFormDGILin.Table_Name +
                     " where " + X_Z_GeneraFormDGILin.COLUMNNAME_Z_GeneraFormDGI_ID + " =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

            action = " delete from " + X_Z_GeneraFormDGIResg.Table_Name +
                    " where " + X_Z_GeneraFormDGILin.COLUMNNAME_Z_GeneraFormDGI_ID + " =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

            // Elimino inconsistencias
            action = " delete from " + X_Z_GeneraFormDGIError.Table_Name +
                    " where " + X_Z_GeneraFormDGIError.COLUMNNAME_Z_GeneraFormDGI_ID + " =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Obtiene y carga documentos para la generación del Formuario de DGI 2/181.
     * Xpande. Created by Gabriel Vila on 11/22/18.
     * @return
     */
    private String getDocuments2181(){

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            if (this.getStartDate() == null){
                return "Debe indicar Fecha Desde";
            }

            if (this.getEndDate() == null){
                return "Debe indicar Fecha Desde";
            }

            if (this.getEndDate().before(this.getStartDate())){
                return "Fecha Desde debe ser menor a Fecha Hasta";
            }

            int docInternoID = 0;
            MZComercialConfig comercialConfig = MZComercialConfig.getDefault(getCtx(), null);
            if ((comercialConfig != null) && (comercialConfig.get_ID() > 0)){
                docInternoID = comercialConfig.getDefaultDocInterno_ID();
            }

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select inv.c_invoice_id, inv.c_doctypetarget_id, (coalesce(inv.documentserie,'') || inv.documentno) as documentnoref, " +
                    " inv.dateinvoiced, inv.dateacct, inv.c_bpartner_id, inv.c_currency_id, doc.docbasetype, inv.issotrx, " +
                    " invt.c_tax_id, case when invt.taxamt > 0 then invt.taxamt else invt.taxbaseamt end as taxamt , " +
                    " bp.c_taxgroup_id, bp.taxid, bp.value, bp.name, vc.account_id, vcVta.account_id as account_vta_id " +
                    " from c_invoice inv " +
                    " inner join c_doctype doc on inv.c_doctypetarget_id = doc.c_doctype_id " +
                    " inner join c_invoicetax invt on inv.c_invoice_id = invt.c_invoice_id " +
                    " inner join c_bpartner bp on inv.c_bpartner_id = bp.c_bpartner_id " +
                    " inner join c_tax tax on invt.c_tax_id = tax.c_tax_id " +
                    " inner join c_taxgroup taxgrp on bp.c_taxgroup_id = taxgrp.c_taxgroup_id " +
                    " left outer join c_tax_acct tacct on tax.c_tax_id = tacct.c_tax_id " +
                    " left outer join c_validcombination vc on tacct.t_credit_acct = vc.c_validcombination_id " +
                    " left outer join c_validcombination vcVta on tacct.t_due_acct = vcVta.c_validcombination_id " +
                    " where inv.docstatus = 'CO' " +
                    " and inv.ad_org_id =" + this.getAD_Org_ID() +
                    " and inv.c_doctypetarget_id != " + docInternoID +
                    " and inv.AsientoManualInvoice ='N' " +
                    " and inv.dateacct between ? and ? " +
                    " and taxgrp.value <>'OTRO' " +
                    " order by invt.c_tax_id, inv.dateacct";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	pstmt.setTimestamp(1, this.getStartDate());
        	pstmt.setTimestamp(2, this.getEndDate());

        	rs = pstmt.executeQuery();

        	while(rs.next()){

        	    boolean hayError = false;

        	    boolean isSOTrx = (rs.getString("issotrx").equalsIgnoreCase("Y")) ? true : false;

        	    String nroIdentificacion = rs.getString("taxid");

        	    if ((nroIdentificacion == null) || (nroIdentificacion.trim().equalsIgnoreCase(""))){

        	        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
        	        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                    dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                    dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                    dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                    dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                    dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                    dgiError.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                    dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                    dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                    dgiError.setTaxID(rs.getString("TaxID"));
                    dgiError.setErrorMsg("Socio de Negocio NO tiene NUMERO DE IDENTIFICACIÓN : " + rs.getString("value") +
                            " - " + rs.getString("name"));
        	        dgiError.saveEx();

        	        hayError = true;
                }
                else{

                    BigDecimal taxAmt = rs.getBigDecimal("taxamt");
                    if (rs.getString("DocBaseType").equalsIgnoreCase("APC")){
                        taxAmt = taxAmt.negate();
                    }



                    MZGeneraFormDGILin linea = new MZGeneraFormDGILin(getCtx(), 0, get_TrxName());
                    linea.setZ_GeneraFormDGI_ID(this.get_ID());
                    linea.setAmtDocument(taxAmt);
                    linea.setAmtDocumentMT(linea.getAmtDocument());
                    linea.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                    linea.setC_Currency_ID(rs.getInt("c_currency_id"));
                    linea.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                    linea.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                    linea.setC_Tax_ID(rs.getInt("c_tax_id"));
                    linea.setCurrencyRate(Env.ONE);
                    linea.setDateAcct(rs.getTimestamp("dateacct"));
                    linea.setDateDoc(rs.getTimestamp("dateinvoiced"));
                    linea.setDocumentNoRef(rs.getString("documentnoref"));
                    linea.setTaxID(nroIdentificacion);
                    linea.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));

                    // Periodo del comprobante
                    MPeriod periodInv = MPeriod.get(getCtx(), linea.getDateAcct(), this.getAD_Org_ID());
                    if ((periodInv == null) || (periodInv.get_ID() <= 0)){
                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                        dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                        dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                        dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                        dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                        dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                        dgiError.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                        dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                        dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                        dgiError.setTaxID(rs.getString("TaxID"));
                        dgiError.setErrorMsg("Falta Período Contable para fecha : " + linea.getDateAcct().toString());
                        dgiError.saveEx();
                        hayError = true;
                    }
                    linea.setC_Period_ID(periodInv.get_ID());

                    int accountID = rs.getInt("account_id");
                    if (isSOTrx){
                        accountID = rs.getInt("account_vta_id");
                    }

                    if (accountID > 0){
                        linea.setC_ElementValue_ID(accountID);
                    }

                    if (linea.getC_Currency_ID() != as.getC_Currency_ID()){
                        BigDecimal rate = CurrencyUtils.getCurrencyRateToAcctSchemaCurrency(getCtx(), this.getAD_Client_ID(), 0, linea.getC_Currency_ID(),
                                as.getC_Currency_ID(), 114, rs.getTimestamp("dateacct"), null);

                        if (rate == null){

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                            dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                            dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                            dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                            dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                            dgiError.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                            dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                            dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                            dgiError.setTaxID(rs.getString("TaxID"));
                            dgiError.setErrorMsg("Falta tasa de Cambio para moneda : " + linea.getC_Currency_ID() + ", fecha : " + linea.getDateAcct().toString());
                            dgiError.saveEx();
                            hayError = true;
                        }
                        else{
                            linea.setCurrencyRate(rate);
                            linea.setAmtDocumentMT(linea.getAmtDocument().multiply(linea.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP));
                        }
                    }

                    if (!hayError){

                        MZAcctConfigRubroDGI configRubroDGI = MZAcctConfigRubroDGI.getByTax(getCtx(), linea.getC_Tax_ID(), isSOTrx, null);
                        if ((configRubroDGI != null) && (configRubroDGI.get_ID() > 0)){
                            linea.setZ_AcctConfigRubroDGI_ID(configRubroDGI.get_ID());
                            linea.saveEx();
                        }
                        else{

                            MTax tax = new MTax(getCtx(), rs.getInt("c_tax_id"), null);

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                            dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                            dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                            dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                            dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                            dgiError.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                            dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                            dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                            dgiError.setTaxID(rs.getString("TaxID"));
                            dgiError.setErrorMsg("Impuesto NO TIENE Rubro de DGI Asociado : " + tax.getName() + " - Documento : " + rs.getString("documentnoref"));
                            dgiError.saveEx();
                        }
                    }
                }
        	}

        	// Si tengo parametrizada la contabilidad para que se incluyan ventas originadas en el POS (módulo Retail) en la generación
            // de formularios para DGI
        	if (this.acctConfig.isIncluirRetailDGI()){
        	    // Cargo datos de ventas del POS
                message = this.getPOSDocuments2181();
            }

        	// Incluyo información desde asientos contables manuales
            this.getAcctDocuments2181();

            // Incluyo información desde asientos contables manuales hechos en Invoice
            this.getInvAcctDocuments2181();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }

        return message;
    }

    /***
     * Obtiene y carga información de ventas originadas por el POS (módulo de Retail) para la generación del formatio 2/181 de DGI.
     * Xpande. Created by Gabriel Vila on 12/10/18.
     * @return
     */
    private String getPOSDocuments2181() {

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select a.z_sistecopazostaxtkrut_id as c_invoice_id, sdgi.c_doctype_id as c_doctypetarget_id, " +
                    " (coalesce(cfe.st_seriecfe,'') || cfe.st_numerocfe) as documentnoref, a.datetrx as dateinvoiced, " +
                    " a.datetrx as dateacct, bp.c_bpartner_id, 142::numeric(10,0) as c_currency_id, tax.c_tax_id, " +
                    " a.taxamt, 'Y'::character varying(1) as issotrx, " +
                    " bp.c_taxgroup_id, coalesce(bp.taxid, a.st_documentoreceptor) as taxid , bp.value, bp.name, vc.account_id, vcVta.account_id as account_vta_id " +
                    " from z_sistecopazostaxtkrut a " +
                    " left outer join c_bpartner bp on a.st_documentoreceptor = bp.taxid " +
                    " left outer join z_sisteco_tk_cfecab cfe on a.z_sisteco_tk_cvta_id = cfe.z_sisteco_tk_cvta_id " +
                    " left outer join z_cfe_configdocdgi dgi on cfe.st_tipocfe = dgi.codigodgi " +
                    " left outer join z_cfe_configdocsend sdgi on dgi.z_cfe_configdocdgi_id = sdgi.z_cfe_configdocdgi_id " +
                    " left outer join c_tax tax on (a.c_taxcategory_id = tax.c_taxcategory_id and tax.isdefault='Y') " +
                    " left outer join c_tax_acct tacct on tax.c_tax_id = tacct.c_tax_id " +
                    " left outer join c_validcombination vc on tacct.t_credit_acct = vc.c_validcombination_id " +
                    " left outer join c_validcombination vcVta on tacct.t_due_acct = vcVta.c_validcombination_id " +
                    " where a.ad_org_id =" + this.getAD_Org_ID() +
                    " and a.datetrx between ? and ? " +
                    " and ((cfe.st_tipocfe <> '101') or (cfe.st_tipocfe = '101' and cfe.st_tipodocumentoreceptor <> '0')) " +
                    " order by a.c_taxcategory_id, a.datetrx ";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setTimestamp(1, this.getStartDate());
            pstmt.setTimestamp(2, this.getEndDate());

        	rs = pstmt.executeQuery();

        	while(rs.next()){

                boolean isSOTrx = (rs.getString("issotrx").equalsIgnoreCase("Y")) ? true : false;

                String nroIdentificacion = rs.getString("taxid");

                if ((nroIdentificacion == null) || (nroIdentificacion.trim().equalsIgnoreCase(""))){

                    MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                    dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                    dgiError.setErrorMsg("POS - Socio de Negocio NO tiene NUMERO DE IDENTIFICACIÓN : " + rs.getString("value") +
                            " - " + rs.getString("name"));
                    dgiError.saveEx();
                }
                else{

                    if (rs.getInt("c_bpartner_id") <= 0){

                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                        dgiError.setErrorMsg("POS - No existe un Socio de Negocio definido con NUMERO DE IDENTIFICACIÓN : " + nroIdentificacion);
                        dgiError.saveEx();
                    }
                    else{
                        MZGeneraFormDGILin linea = new MZGeneraFormDGILin(getCtx(), 0, get_TrxName());
                        linea.setZ_GeneraFormDGI_ID(this.get_ID());
                        linea.setAmtDocument(rs.getBigDecimal("taxamt"));
                        linea.setAmtDocumentMT(linea.getAmtDocument());
                        linea.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                        linea.setC_Currency_ID(rs.getInt("c_currency_id"));
                        linea.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                        linea.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                        linea.setC_Tax_ID(rs.getInt("c_tax_id"));
                        linea.setCurrencyRate(Env.ONE);
                        linea.setDateAcct(rs.getTimestamp("dateacct"));
                        linea.setDateDoc(rs.getTimestamp("dateinvoiced"));
                        linea.setDocumentNoRef(rs.getString("documentnoref"));
                        linea.setTaxID(nroIdentificacion);
                        linea.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));

                        // Periodo del comprobante
                        MPeriod periodInv = MPeriod.get(getCtx(), linea.getDateAcct(), this.getAD_Org_ID());
                        if ((periodInv == null) || (periodInv.get_ID() <= 0)){
                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setErrorMsg("POS - Falta Período Contable para fecha : " + linea.getDateAcct().toString());
                            dgiError.saveEx();
                        }
                        else{
                            linea.setC_Period_ID(periodInv.get_ID());

                            int accountID = rs.getInt("account_id");
                            if (isSOTrx){
                                accountID = rs.getInt("account_vta_id");
                            }

                            if (accountID > 0){
                                linea.setC_ElementValue_ID(accountID);
                            }

                            if (linea.getC_Currency_ID() != as.getC_Currency_ID()){
                                BigDecimal rate = CurrencyUtils.getCurrencyRateToAcctSchemaCurrency(getCtx(), this.getAD_Client_ID(), 0, linea.getC_Currency_ID(),
                                        as.getC_Currency_ID(), 114, rs.getTimestamp("dateacct"), null);

                                if (rate == null){

                                    MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                    dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                    dgiError.setErrorMsg("POS - Falta tasa de Cambio para moneda : " + linea.getC_Currency_ID() + ", fecha : " + linea.getDateAcct().toString());
                                    dgiError.saveEx();
                                }
                                else{
                                    linea.setCurrencyRate(rate);
                                    linea.setAmtDocumentMT(linea.getAmtDocument().multiply(linea.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP));
                                }
                            }

                            MZAcctConfigRubroDGI configRubroDGI = MZAcctConfigRubroDGI.getByTax(getCtx(), linea.getC_Tax_ID(), isSOTrx, null);
                            if ((configRubroDGI != null) && (configRubroDGI.get_ID() > 0)){
                                linea.setZ_AcctConfigRubroDGI_ID(configRubroDGI.get_ID());
                                linea.saveEx();
                            }
                            else{

                                MTax tax = new MTax(getCtx(), rs.getInt("c_tax_id"), null);

                                MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                dgiError.setErrorMsg("POS - Impuesto NO TIENE Rubro de DGI Asociado : " + tax.getName() + " - Documento : " + rs.getString("documentnoref"));
                                dgiError.saveEx();
                            }

                        }

                    }
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }

        return message;
    }


    /***
     * Obtiene y carga documentos de asientos manuales para la generación del Formuario de DGI 2/181.
     * Xpande. Created by Gabriel Vila on 11/22/18.
     * @return
     */
    private String getAcctDocuments2181(){

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select gl.account_id, g.dateacct, g.gl_journal_id, g.c_doctype_id, g.documentno, " +
                    " g.datedoc, gl.c_bpartner_id, gl.c_currency_id, doc.docbasetype, doc.issotrx, " +
                    " gl.c_validcombination_id, gl.amtsourcedr, gl.amtsourcecr, gl.c_tax_id, " +
                    " bp.c_taxgroup_id, bp.taxid, bp.value, bp.name " +
                    " from gl_journal g " +
                    " inner join gl_journalline gl on g.gl_journal_id = gl.gl_journal_id " +
                    " inner join c_doctype doc on g.c_doctype_id = doc.c_doctype_id " +
                    " inner join c_bpartner bp on gl.c_bpartner_id = bp.c_bpartner_id " +
                    " where g.docstatus = 'CO' " +
                    " and g.ad_org_id =" + this.getAD_Org_ID() +
                    " and g.dateacct between ? and ? " +
                    " and gl.account_id in (select account_id from c_validcombination where c_validcombination_id in (" +
                    " select distinct c_validcombination_id from z_rubrodgiacct)) " +
                    " order by 1,2";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setTimestamp(1, this.getStartDate());
            pstmt.setTimestamp(2, this.getEndDate());

            rs = pstmt.executeQuery();

            while(rs.next()){

                boolean hayError = false;

                boolean isSOTrx = (rs.getString("issotrx").equalsIgnoreCase("Y")) ? true : false;

                String nroIdentificacion = rs.getString("taxid");

                if ((nroIdentificacion == null) || (nroIdentificacion.trim().equalsIgnoreCase(""))){

                    MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                    dgiError.setZ_GeneraFormDGI_ID(this.get_ID());


                    dgiError.setErrorMsg("Socio de Negocio NO tiene NUMERO DE IDENTIFICACIÓN : " + rs.getString("value") +
                            " - " + rs.getString("name"));
                    dgiError.saveEx();

                    hayError = true;
                }
                else{

                    BigDecimal taxAmt = rs.getBigDecimal("amtsourcedr");
                    if ((taxAmt == null) || (taxAmt.compareTo(Env.ZERO) == 0)){
                        taxAmt = rs.getBigDecimal("amtsourcecr");
                        if (taxAmt != null) taxAmt = taxAmt.negate();
                    }
                    if (taxAmt == null) taxAmt = Env.ZERO;

                    if (taxAmt.compareTo(Env.ZERO) == 0) continue;

                    MZGeneraFormDGILin linea = new MZGeneraFormDGILin(getCtx(), 0, get_TrxName());
                    linea.setZ_GeneraFormDGI_ID(this.get_ID());
                    linea.setAmtDocument(taxAmt);
                    linea.setAmtDocumentMT(linea.getAmtDocument());
                    linea.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                    linea.setC_Currency_ID(rs.getInt("c_currency_id"));
                    linea.setC_DocType_ID(rs.getInt("c_doctype_id"));
                    //linea.setC_Invoice_ID(rs.getInt("c_invoice_id"));

                    if (rs.getInt("c_tax_id") > 0){
                        linea.setC_Tax_ID(rs.getInt("c_tax_id"));
                    }

                    linea.setCurrencyRate(Env.ONE);
                    linea.setDateAcct(rs.getTimestamp("dateacct"));
                    linea.setDateDoc(rs.getTimestamp("datedoc"));
                    linea.setDocumentNoRef(rs.getString("documentno"));
                    linea.setTaxID(nroIdentificacion);
                    linea.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                    linea.setC_ValidCombination_ID(rs.getInt("c_validcombination_id"));

                    // Periodo del comprobante
                    MPeriod periodInv = MPeriod.get(getCtx(), linea.getDateAcct(), this.getAD_Org_ID());
                    if ((periodInv == null) || (periodInv.get_ID() <= 0)){
                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                        dgiError.setErrorMsg("Falta Período Contable para fecha : " + linea.getDateAcct().toString());
                        dgiError.saveEx();
                        hayError = true;
                    }
                    linea.setC_Period_ID(periodInv.get_ID());

                    int accountID = rs.getInt("account_id");
                    if (accountID > 0){
                        linea.setC_ElementValue_ID(accountID);
                    }

                    if (linea.getC_Currency_ID() != as.getC_Currency_ID()){
                        BigDecimal rate = CurrencyUtils.getCurrencyRateToAcctSchemaCurrency(getCtx(), this.getAD_Client_ID(), 0, linea.getC_Currency_ID(),
                                as.getC_Currency_ID(), 114, rs.getTimestamp("dateacct"), null);

                        if (rate == null){

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setErrorMsg("Falta tasa de Cambio para moneda : " + linea.getC_Currency_ID() + ", fecha : " + linea.getDateAcct().toString());
                            dgiError.saveEx();
                            hayError = true;
                        }
                        else{
                            linea.setCurrencyRate(rate);
                            linea.setAmtDocumentMT(linea.getAmtDocument().multiply(linea.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP));
                        }
                    }

                    if (!hayError){

                        MZAcctConfigRubroDGI configRubroDGI = null;

                        // Obtengo por rubro por impuesto o por cuenta en caso de no tenerlo.
                        if (linea.getC_Tax_ID() > 0){
                            configRubroDGI = MZAcctConfigRubroDGI.getByTax(getCtx(), linea.getC_Tax_ID(), isSOTrx , null);
                        }
                        else {
                            configRubroDGI = MZAcctConfigRubroDGI.getByAcct(getCtx(), linea.getC_ElementValue_ID(), isSOTrx, null);
                        }

                        if ((configRubroDGI != null) && (configRubroDGI.get_ID() > 0)){
                            linea.setZ_AcctConfigRubroDGI_ID(configRubroDGI.get_ID());
                            linea.saveEx();
                        }
                        else{

                            MElementValue ev = new MElementValue(getCtx(), linea.getC_ElementValue_ID(), null);

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setErrorMsg("Cuenta Contable NO TIENE Rubro de DGI Asociado : " + ev.getName() + " - Documento : " + rs.getString("documentno"));
                            dgiError.saveEx();
                        }
                    }
                }
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }

        return message;
    }


    /***
     * Obtiene y carga documentos de asientos manuales en Invoice para la generación del Formuario de DGI 2/181.
     * Xpande. Created by Gabriel Vila on 11/22/18.
     * @return
     */
    private String getInvAcctDocuments2181(){

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select b.account_id, a.dateacct, a.c_invoice_id, a.c_doctypetarget_id, " +
                    " (coalesce(a.documentserie, '') || a.documentno) as documentnoref, " +
                    " a.dateinvoiced, a.c_bpartner_id, a.c_currency_id, doc.docbasetype, a.issotrx, " +
                    " b.c_validcombination_id, b.amtsourcedr, b.amtsourcecr, " +
                    " bp.c_taxgroup_id, bp.taxid, bp.value, bp.name " +
                    " from c_invoice a " +
                    " inner join Z_InvoiceAstoManual b on a.c_invoice_id = b.c_invoice_id " +
                    " inner join c_doctype doc on a.c_doctypetarget_id = doc.c_doctype_id " +
                    " inner join c_bpartner bp on a.c_bpartner_id = bp.c_bpartner_id " +
                    " where a.docstatus = 'CO' " +
                    " and a.ad_org_id =" + this.getAD_Org_ID() +
                    " and a.dateacct between ? and ? " +
                    " and b.account_id in (select account_id from c_validcombination where c_validcombination_id in (" +
                    " select distinct c_validcombination_id from z_rubrodgiacct)) " +
                    " order by 1,2";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setTimestamp(1, this.getStartDate());
            pstmt.setTimestamp(2, this.getEndDate());

            rs = pstmt.executeQuery();

            while(rs.next()){

                boolean hayError = false;

                boolean isSOTrx = (rs.getString("issotrx").equalsIgnoreCase("Y")) ? true : false;

                String nroIdentificacion = rs.getString("taxid");

                if ((nroIdentificacion == null) || (nroIdentificacion.trim().equalsIgnoreCase(""))){

                    MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                    dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                    dgiError.setErrorMsg("Socio de Negocio NO tiene NUMERO DE IDENTIFICACIÓN : " + rs.getString("value") +
                            " - " + rs.getString("name"));
                    dgiError.saveEx();

                    hayError = true;
                }
                else{

                    BigDecimal taxAmt = rs.getBigDecimal("amtsourcedr");
                    if ((taxAmt == null) || (taxAmt.compareTo(Env.ZERO) == 0)){
                        taxAmt = rs.getBigDecimal("amtsourcecr");
                    }
                    if (taxAmt == null) taxAmt = Env.ZERO;

                    if (taxAmt.compareTo(Env.ZERO) == 0) continue;

                    MZGeneraFormDGILin linea = new MZGeneraFormDGILin(getCtx(), 0, get_TrxName());
                    linea.setZ_GeneraFormDGI_ID(this.get_ID());
                    linea.setAmtDocument(taxAmt);
                    linea.setAmtDocumentMT(linea.getAmtDocument());
                    linea.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                    linea.setC_Currency_ID(rs.getInt("c_currency_id"));
                    linea.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                    linea.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                    //linea.setC_Tax_ID(rs.getInt("c_tax_id"));
                    linea.setCurrencyRate(Env.ONE);
                    linea.setDateAcct(rs.getTimestamp("dateacct"));
                    linea.setDateDoc(rs.getTimestamp("dateinvoiced"));
                    linea.setDocumentNoRef(rs.getString("documentnoref"));
                    linea.setTaxID(nroIdentificacion);
                    linea.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                    linea.setC_ValidCombination_ID(rs.getInt("c_validcombination_id"));

                    // Periodo del comprobante
                    MPeriod periodInv = MPeriod.get(getCtx(), linea.getDateAcct(), this.getAD_Org_ID());
                    if ((periodInv == null) || (periodInv.get_ID() <= 0)){
                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                        dgiError.setErrorMsg("Falta Período Contable para fecha : " + linea.getDateAcct().toString());
                        dgiError.saveEx();
                        hayError = true;
                    }
                    linea.setC_Period_ID(periodInv.get_ID());

                    int accountID = rs.getInt("account_id");
                    if (accountID > 0){
                        linea.setC_ElementValue_ID(accountID);
                    }

                    if (linea.getC_Currency_ID() != as.getC_Currency_ID()){
                        BigDecimal rate = CurrencyUtils.getCurrencyRateToAcctSchemaCurrency(getCtx(), this.getAD_Client_ID(), 0, linea.getC_Currency_ID(),
                                as.getC_Currency_ID(), 114, rs.getTimestamp("dateacct"), null);

                        if (rate == null){

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setErrorMsg("Falta tasa de Cambio para moneda : " + linea.getC_Currency_ID() + ", fecha : " + linea.getDateAcct().toString());
                            dgiError.saveEx();
                            hayError = true;
                        }
                        else{
                            linea.setCurrencyRate(rate);
                            linea.setAmtDocumentMT(linea.getAmtDocument().multiply(linea.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP));
                        }
                    }

                    if (!hayError){

                        MZAcctConfigRubroDGI configRubroDGI = MZAcctConfigRubroDGI.getByAcct(getCtx(), linea.getC_ElementValue_ID(), false, null);
                        if ((configRubroDGI != null) && (configRubroDGI.get_ID() > 0)){
                            linea.setZ_AcctConfigRubroDGI_ID(configRubroDGI.get_ID());
                            linea.saveEx();
                        }
                        else{

                            MElementValue ev = new MElementValue(getCtx(), linea.getC_ElementValue_ID(), null);

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setErrorMsg("Cuenta Contable NO TIENE Rubro de DGI Asociado : " + ev.getName() + " - Documento : " + rs.getString("documentno"));
                            dgiError.saveEx();
                        }
                    }
                }
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }

        return message;
    }


    /***
     * Obtiene y carga documentos para la generación del Formuario de DGI 2/183.
     * Xpande. Created by Gabriel Vila on 9/22/19.
     * @return
     */
    private String getDocuments2183(){

        String message = null;

        try{

            if (this.getStartDate2() == null){
                return "Debe indicar Fecha Desde para OBLIGACIONES TRIBUTARIAS DE TERCEROS";
            }

            if (this.getEndDate2() == null){
                return "Debe indicar Fecha Hasta para OBLIGACIONES TRIBUTARIAS DE TERCEROS";
            }

            if (this.getEndDate2().before(this.getStartDate2())){
                return "Fecha Desde debe ser menor a Fecha Hasta para OBLIGACIONES TRIBUTARIAS DE TERCEROS";
            }

            if (this.getStartDate3() == null){
                return "Debe indicar Fecha Desde para IRIC/IRAE- EMP. SEGURIDAD, VIGILANCIA Y LIMPIEZA";
            }

            if (this.getEndDate3() == null){
                return "Debe indicar Fecha Hasta para IRIC/IRAE- EMP. SEGURIDAD, VIGILANCIA Y LIMPIEZA";
            }

            if (this.getEndDate3().before(this.getStartDate3())){
                return "Fecha Desde debe ser menor a Fecha Hasta para IRIC/IRAE- EMP. SEGURIDAD, VIGILANCIA Y LIMPIEZA";
            }

            // Carga documentos para retenciones OTT
            this.getDocuments_OTT_2183();

            // Carga documentos para retenciones de IRIC-IRAE
            this.getDocuments_IRIC_IRAE_2183();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    /***
     * Obtiene documentos para retenciones OTT.
     * Xpande. Created by Gabriel Vila on 9/22/19.
     * @return
     */
    private void getDocuments_OTT_2183(){

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MZRetencionSocio retencionSocioOTT = MZRetencionSocio.getByCodigoDGI(getCtx(), "2183165", null);

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select distinct inv.c_invoice_id, inv.c_doctypetarget_id, (coalesce(inv.documentserie,'') || inv.documentno) as documentnoref, " +
                    " inv.dateinvoiced, inv.dateacct, inv.c_bpartner_id, inv.c_currency_id, doc.docbasetype, " +
                    " bp.c_taxgroup_id, bp.taxid, bp.value, bp.name " +
                    " from c_invoice inv " +
                    " inner join c_doctype doc on inv.c_doctypetarget_id = doc.c_doctype_id " +
                    " inner join c_bpartner bp on inv.c_bpartner_id = bp.c_bpartner_id " +
                    " inner join z_retencionsociobpartner retbp on bp.c_bpartner_id = retbp.c_bpartner_id " +
                    " inner join z_retencionsocio ret on ret.z_retencionsocio_id = retbp.z_retencionsocio_id " +
                    " where inv.docstatus = 'CO' " +
                    " and inv.ad_org_id =" + this.getAD_Org_ID() +
                    " and inv.issotrx='N' " +
                    " and inv.dateinvoiced between ? and ? " +
                    " and ret.codigodgi ='2183165' " +
                    " order by inv.dateinvoiced";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setTimestamp(1, this.getStartDate2());
            pstmt.setTimestamp(2, this.getEndDate2());

            rs = pstmt.executeQuery();

            while(rs.next()){

                boolean hayError = false;

                int cInvoiceID = rs.getInt("c_invoice_id");

                // Si este comprobante tiene resguardo
                MZResguardoSocioDoc resguardoSocioDoc = MZResguardoSocio.getByInvoice(getCtx(), cInvoiceID, null);
                if (resguardoSocioDoc == null){
                    // Si este comprobante no requiere resguardo, continuo con el siguiente
                    boolean aplicaReteneciones = MZRetencionSocio.invoiceAplicanRetenciones(getCtx(), cInvoiceID, null);
                    if (!aplicaReteneciones) {
                        continue;
                    }
                    else {
                        // Inconsistencia: este documento requiere resguardo, pero no se le emitió ninguno.
                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                        dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                        dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                        dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                        dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                        dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                        dgiError.setC_Invoice_ID(cInvoiceID);
                        dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                        dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                        dgiError.setTaxID(rs.getString("TaxID"));
                        dgiError.setZ_RetencionSocio_ID(retencionSocioOTT.get_ID());
                        dgiError.setErrorMsg("Comprobante requiere Resguardo");
                        dgiError.saveEx();

                        hayError = true;
                        continue;
                    }
                }

                // Lista de retenciones asociadas a este documento
                List<MZResguardoSocioDocRet> resguardoSocioDocRetList = resguardoSocioDoc.getResguardoDocRets();
                if (resguardoSocioDocRetList.size() <= 0){
                    continue;
                }

                for (MZResguardoSocioDocRet resguardoSocioDocRet: resguardoSocioDocRetList){

                    MZRetencionSocio retencionSocio = (MZRetencionSocio) resguardoSocioDocRet.getZ_RetencionSocio();
                    if ((retencionSocio.getCodigoDGI() != null) && (!retencionSocio.getCodigoDGI().trim().equalsIgnoreCase(""))){

                        // Retención OTT
                        if (retencionSocio.getCodigoDGI().trim().equalsIgnoreCase("2183165")){

                            Timestamp fechaDoc = rs.getTimestamp("dateinvoiced");
                            String nroIdentificacion = rs.getString("taxid");

                            if ((nroIdentificacion == null) || (nroIdentificacion.trim().equalsIgnoreCase(""))){

                                MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                dgiError.setErrorMsg("Socio de Negocio NO tiene NUMERO DE IDENTIFICACIÓN : " + rs.getString("value") +
                                        " - " + rs.getString("name"));

                                dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                                dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                                dgiError.setC_Invoice_ID(cInvoiceID);
                                dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                                dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                dgiError.setTaxID(rs.getString("TaxID"));
                                dgiError.setZ_RetencionSocio_ID(retencionSocioOTT.get_ID());
                                dgiError.saveEx();

                                hayError = true;
                            }
                            else{

                                if (retencionSocio.getZ_AcctConfigRubroDGI_ID() <= 0){
                                    MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                    dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                    dgiError.setErrorMsg("Falta indicar Rubro DGI para Retención : " + retencionSocio.getName());
                                    dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                    dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                    dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                                    dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                    dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                                    dgiError.setC_Invoice_ID(cInvoiceID);
                                    dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                                    dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                    dgiError.setTaxID(rs.getString("TaxID"));
                                    dgiError.setZ_RetencionSocio_ID(retencionSocioOTT.get_ID());
                                    dgiError.saveEx();

                                    hayError = true;
                                }
                                else{
                                    MZGeneraFormDGIResg dgiResg = new MZGeneraFormDGIResg(getCtx(), 0, get_TrxName());
                                    dgiResg.setZ_GeneraFormDGI_ID(this.get_ID());
                                    dgiResg.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                    dgiResg.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                    dgiResg.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                                    dgiResg.setDateAcct(rs.getTimestamp("dateacct"));
                                    dgiResg.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                    dgiResg.setDocumentNoRef(rs.getString("documentnoref"));
                                    dgiResg.setTaxID(nroIdentificacion);
                                    dgiResg.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                    dgiResg.setZ_AcctConfigRubroDGI_ID(retencionSocio.getZ_AcctConfigRubroDGI_ID());
                                    dgiResg.setAmtBase(resguardoSocioDocRet.getAmtBase());
                                    dgiResg.setAmtRetencion(resguardoSocioDocRet.getAmtRetencion());
                                    dgiResg.setC_Currency_ID(resguardoSocioDoc.getC_Currency_ID());

                                    dgiResg.setAmtBaseMO(dgiResg.getAmtBase());
                                    dgiResg.setAmtRetencionMO(dgiResg.getAmtRetencion());

                                    if (resguardoSocioDoc.getC_Currency_ID() != as.getC_Currency_ID()){
                                        if (resguardoSocioDoc.getCurrencyRate() != null){
                                            if (resguardoSocioDoc.getCurrencyRate().compareTo(Env.ZERO) > 0){
                                                dgiResg.setAmtBaseMO(dgiResg.getAmtBase().divide(resguardoSocioDoc.getCurrencyRate(), 2, RoundingMode.HALF_UP));
                                                dgiResg.setAmtRetencionMO(dgiResg.getAmtRetencion().divide(resguardoSocioDoc.getCurrencyRate(), 2, RoundingMode.HALF_UP));
                                            }
                                        }
                                    }

                                    dgiResg.setPorcRetencion(resguardoSocioDocRet.getPorcRetencion());
                                    dgiResg.setZ_ResguardoSocio_ID(resguardoSocioDoc.getZ_ResguardoSocio_ID());
                                    dgiResg.setZ_ResguardoSocioDoc_ID(resguardoSocioDoc.get_ID());
                                    dgiResg.setZ_ResguardoSocioDocRet_ID(resguardoSocioDocRet.get_ID());
                                    dgiResg.setZ_RetencionSocio_ID(retencionSocio.get_ID());

                                    MZResguardoSocio resguardoSocio = (MZResguardoSocio) resguardoSocioDoc.getZ_ResguardoSocio();
                                    dgiResg.setDateRefResguardo(resguardoSocio.getDateDoc());

                                    // Periodo del comprobante
                                    MPeriod periodInv = MPeriod.get(getCtx(), dgiResg.getDateDoc(), this.getAD_Org_ID());
                                    if ((periodInv == null) || (periodInv.get_ID() <= 0)){
                                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                        dgiError.setErrorMsg("Falta Período Contable para fecha : " + dgiResg.getDateDoc().toString());
                                        dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                        dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                        dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                                        dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                        dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                                        dgiError.setC_Invoice_ID(cInvoiceID);
                                        dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                                        dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                        dgiError.setTaxID(rs.getString("TaxID"));
                                        dgiError.setZ_RetencionSocio_ID(retencionSocioOTT.get_ID());
                                        dgiError.saveEx();
                                        hayError = true;
                                    }
                                    dgiResg.setC_Period_ID(periodInv.get_ID());

                                    if (!hayError){
                                        dgiResg.saveEx();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }
    }

    /***
     * Obtiene documentos para retenciones OTT.
     * Xpande. Created by Gabriel Vila on 9/22/19.
     * @return
     */
    private void getDocuments_IRIC_IRAE_2183(){

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select distinct inv.c_invoice_id, inv.c_doctypetarget_id, (coalesce(inv.documentserie,'') || inv.documentno) as documentnoref, " +
                    " inv.dateinvoiced, inv.dateacct, inv.c_bpartner_id, inv.c_currency_id, doc.docbasetype, " +
                    " bp.c_taxgroup_id, bp.taxid, bp.value, bp.name " +
                    " from c_invoice inv " +
                    " inner join c_doctype doc on inv.c_doctypetarget_id = doc.c_doctype_id " +
                    " inner join c_bpartner bp on inv.c_bpartner_id = bp.c_bpartner_id " +
                    " inner join z_retencionsociobpartner retbp on bp.c_bpartner_id = retbp.c_bpartner_id " +
                    " inner join z_retencionsocio ret on ret.z_retencionsocio_id = retbp.z_retencionsocio_id " +
                    " where inv.docstatus = 'CO' " +
                    " and inv.ad_org_id =" + this.getAD_Org_ID() +
                    " and inv.issotrx='N' " +
                    " and inv.dateinvoiced between ? and ? " +
                    " and ret.codigodgi in('2183114','2183121') " +
                    " order by inv.dateinvoiced";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            pstmt.setTimestamp(1, this.getStartDate3());
            pstmt.setTimestamp(2, this.getEndDate3());

            rs = pstmt.executeQuery();

            while(rs.next()){

                boolean hayError = false;

                int cInvoiceID = rs.getInt("c_invoice_id");

                // Si este comprobante tiene resguardo
                MZResguardoSocioDoc resguardoSocioDoc = MZResguardoSocio.getByInvoice(getCtx(), cInvoiceID, null);
                if (resguardoSocioDoc == null){
                    // Si este comprobante no requiere resguardo, continuo con el siguiente
                    boolean aplicaReteneciones = MZRetencionSocio.invoiceAplicanRetenciones(getCtx(), cInvoiceID, null);
                    if (!aplicaReteneciones) {
                        continue;
                    }
                    else {
                        // Inconsistencia: este documento requiere resguardo, pero no se le emitió ninguno.
                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                        dgiError.setErrorMsg("Comprobante requiere Resguardo");
                        dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                        dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                        dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                        dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                        dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                        dgiError.setC_Invoice_ID(cInvoiceID);
                        dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                        dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                        dgiError.setTaxID(rs.getString("TaxID"));
                        dgiError.saveEx();

                        hayError = true;
                        continue;
                    }
                }

                // Lista de retenciones asociadas a este documento
                List<MZResguardoSocioDocRet> resguardoSocioDocRetList = resguardoSocioDoc.getResguardoDocRets();
                if (resguardoSocioDocRetList.size() <= 0){
                    continue;
                }

                for (MZResguardoSocioDocRet resguardoSocioDocRet: resguardoSocioDocRetList){

                    MZRetencionSocio retencionSocio = (MZRetencionSocio) resguardoSocioDocRet.getZ_RetencionSocio();
                    if ((retencionSocio.getCodigoDGI() != null) && (!retencionSocio.getCodigoDGI().trim().equalsIgnoreCase(""))){

                        // Retenciónes IRIC - IRAE
                        if ((retencionSocio.getCodigoDGI().trim().equalsIgnoreCase("2183114"))
                                || (retencionSocio.getCodigoDGI().trim().equalsIgnoreCase("2183121"))){

                            Timestamp fechaDoc = rs.getTimestamp("dateinvoiced");
                            String nroIdentificacion = rs.getString("taxid");

                            if ((nroIdentificacion == null) || (nroIdentificacion.trim().equalsIgnoreCase(""))){

                                MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                dgiError.setErrorMsg("Socio de Negocio NO tiene NUMERO DE IDENTIFICACIÓN : " + rs.getString("value") +
                                        " - " + rs.getString("name"));
                                dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                                dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                                dgiError.setC_Invoice_ID(cInvoiceID);
                                dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                                dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                dgiError.setTaxID(rs.getString("TaxID"));
                                dgiError.saveEx();

                                hayError = true;
                            }
                            else{

                                if (retencionSocio.getZ_AcctConfigRubroDGI_ID() <= 0){
                                    MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                    dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                    dgiError.setErrorMsg("Falta indicar Rubro DGI para Retención : " + retencionSocio.getName());
                                    dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                    dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                    dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                                    dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                    dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                                    dgiError.setC_Invoice_ID(cInvoiceID);
                                    dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                                    dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                    dgiError.setTaxID(rs.getString("TaxID"));
                                    dgiError.saveEx();

                                    hayError = true;
                                }
                                else{
                                    MZGeneraFormDGIResg dgiResg = new MZGeneraFormDGIResg(getCtx(), 0, get_TrxName());
                                    dgiResg.setZ_GeneraFormDGI_ID(this.get_ID());
                                    dgiResg.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                    dgiResg.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                    dgiResg.setC_Invoice_ID(rs.getInt("c_invoice_id"));
                                    dgiResg.setDateAcct(rs.getTimestamp("dateacct"));
                                    dgiResg.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                    dgiResg.setDocumentNoRef(rs.getString("documentnoref"));
                                    dgiResg.setTaxID(nroIdentificacion);
                                    dgiResg.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                    dgiResg.setZ_AcctConfigRubroDGI_ID(retencionSocio.getZ_AcctConfigRubroDGI_ID());
                                    dgiResg.setAmtBase(resguardoSocioDocRet.getAmtBase());
                                    dgiResg.setAmtRetencion(resguardoSocioDocRet.getAmtRetencion());
                                    dgiResg.setC_Currency_ID(resguardoSocioDoc.getC_Currency_ID());

                                    dgiResg.setAmtBaseMO(dgiResg.getAmtBase());
                                    dgiResg.setAmtRetencionMO(dgiResg.getAmtRetencion());

                                    if (resguardoSocioDoc.getC_Currency_ID() != as.getC_Currency_ID()){
                                        if (resguardoSocioDoc.getCurrencyRate() != null){
                                            if (resguardoSocioDoc.getCurrencyRate().compareTo(Env.ZERO) > 0){
                                                dgiResg.setAmtBaseMO(dgiResg.getAmtBase().divide(resguardoSocioDoc.getCurrencyRate(), 2, RoundingMode.HALF_UP));
                                                dgiResg.setAmtRetencionMO(dgiResg.getAmtRetencion().divide(resguardoSocioDoc.getCurrencyRate(), 2, RoundingMode.HALF_UP));
                                            }
                                        }
                                    }


                                    dgiResg.setPorcRetencion(resguardoSocioDocRet.getPorcRetencion());
                                    dgiResg.setZ_ResguardoSocio_ID(resguardoSocioDoc.getZ_ResguardoSocio_ID());
                                    dgiResg.setZ_ResguardoSocioDoc_ID(resguardoSocioDoc.get_ID());
                                    dgiResg.setZ_ResguardoSocioDocRet_ID(resguardoSocioDocRet.get_ID());
                                    dgiResg.setZ_RetencionSocio_ID(retencionSocio.get_ID());

                                    MZResguardoSocio resguardoSocio = (MZResguardoSocio) resguardoSocioDoc.getZ_ResguardoSocio();
                                    dgiResg.setDateRefResguardo(resguardoSocio.getDateDoc());

                                    // Periodo del comprobante
                                    MPeriod periodInv = MPeriod.get(getCtx(), dgiResg.getDateDoc(), this.getAD_Org_ID());
                                    if ((periodInv == null) || (periodInv.get_ID() <= 0)){
                                        MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                                        dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                                        dgiError.setErrorMsg("Falta Período Contable para fecha : " + dgiResg.getDateDoc().toString());
                                        dgiError.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                        dgiError.setC_DocType_ID(rs.getInt("c_doctypetarget_id"));
                                        dgiError.setDocumentNoRef(rs.getString("documentnoref"));
                                        dgiError.setDateDoc(rs.getTimestamp("dateinvoiced"));
                                        dgiError.setDateAcct(rs.getTimestamp("dateacct"));
                                        dgiError.setC_Invoice_ID(cInvoiceID);
                                        dgiError.setC_Currency_ID(rs.getInt("c_currency_id"));
                                        dgiError.setC_TaxGroup_ID(rs.getInt("c_taxgroup_id"));
                                        dgiError.setTaxID(rs.getString("TaxID"));
                                        dgiError.saveEx();
                                        hayError = true;
                                    }
                                    dgiResg.setC_Period_ID(periodInv.get_ID());

                                    if (!hayError){
                                        dgiResg.saveEx();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }
    }

}
