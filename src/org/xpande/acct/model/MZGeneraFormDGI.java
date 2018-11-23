package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.DB;

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
            sql = " select inv.c_invoice_id, inv.c_doctypetarget_id, (coalesce(inv.documentserie,'') || inv.documentno) as documentnoref, " +
                    "inv.dateinvoiced, inv.dateacct, inv.c_bpartner_id, inv.c_currency_id, invt.c_tax_id, invt.taxamt " +
                    "from c_invoice inv " +
                    "inner join c_invoicetax invt on inv.c_invoice_id = invt.c_invoice_id " +
                    "where inv.docstatus = 'CO' " +
                    "and inv.ad_org_id = 1000001 " +
                    "and inv.dateacct between '2018-11-01 00:00:00' and '2018-11-30 00:00:00' " +
                    "order by invt.c_tax_id, inv.dateacct";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	while(rs.next()){

        	    String nroIdentificacion = rs.getString("taxid");

        	    /*
        	    String validMsg = this.validateLine(nroIdentificacion);

        	    if (validMsg != null){

                }
                */

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
