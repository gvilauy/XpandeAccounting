package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MTax;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.core.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para cabezal de proceso de generación de Formulario de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/21/18.
 */
public class MZGeneraFormDGI extends X_Z_GeneraFormDGI {

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

            // Elimino información anterior
            this.deleteDocuments();

            // Obtengo documentos según tipo de formulario de DGI a procesar.
            if (this.getTipoFormularioDGI().equalsIgnoreCase(X_Z_GeneraFormDGI.TIPOFORMULARIODGI_FORMULARIO2181)){

                // Obtengo documentos para Formulario 2/181
                message = this.getDocuments2181();

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
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

            MAcctSchema as = (MAcctSchema) this.getC_AcctSchema();

            sql = " select inv.c_invoice_id, inv.c_doctypetarget_id, (coalesce(inv.documentserie,'') || inv.documentno) as documentnoref, " +
                    " inv.dateinvoiced, inv.dateacct, inv.c_bpartner_id, inv.c_currency_id, invt.c_tax_id, invt.taxamt, inv.issotrx, " +
                    " bp.c_taxgroup_id, bp.taxid, bp.value, bp.name " +
                    " from c_invoice inv " +
                    " inner join c_invoicetax invt on inv.c_invoice_id = invt.c_invoice_id " +
                    " inner join c_bpartner bp on inv.c_bpartner_id = bp.c_bpartner_id " +
                    " where inv.docstatus = 'CO' " +
                    " and inv.ad_org_id =" + this.getAD_Org_ID() +
                    " and inv.dateacct between ? and ? " +
                    " and invt.taxamt != 0 " +
                    " order by invt.c_tax_id, inv.dateacct";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	pstmt.setTimestamp(1, this.getStartDate());
        	pstmt.setTimestamp(2, this.getEndDate());

        	rs = pstmt.executeQuery();

        	while(rs.next()){

        	    boolean hayError = false;

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
                        boolean isSOTrx = (rs.getString("issotrx").equalsIgnoreCase("Y")) ? true : false;

                        MZAcctConfigRubroDGI configRubroDGI = MZAcctConfigRubroDGI.getByTax(getCtx(), linea.getC_Tax_ID(), isSOTrx, null);
                        if ((configRubroDGI != null) && (configRubroDGI.get_ID() > 0)){
                            linea.setZ_AcctConfigRubroDGI_ID(configRubroDGI.get_ID());
                            linea.saveEx();
                        }
                        else{

                            MTax tax = new MTax(getCtx(), rs.getInt("c_tax_id"), null);

                            MZGeneraFormDGIError dgiError = new MZGeneraFormDGIError(getCtx(), 0, get_TrxName());
                            dgiError.setZ_GeneraFormDGI_ID(this.get_ID());
                            dgiError.setErrorMsg("Impuesto NO TIENE Rubro de DGI Asociado : " + tax.getName() + " - Documento : " + rs.getString("documentnoref"));
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

}
