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


    public String getDocuments(){

        String message = null;

        try{

            // Obtengo documentos según tipo de formulario de DGI a procesar.
            if (this.getTipoFormularioDGI().equalsIgnoreCase(X_Z_GeneraFormDGI.TIPOFORMULARIODGI_FORMULARIO2181)){

                message = this.getDocuments2181();

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    private String getDocuments2181(){

        String message = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            sql = "";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	while(rs.next()){

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
