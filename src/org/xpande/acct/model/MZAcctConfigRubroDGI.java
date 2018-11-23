package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para configuración de Rubros para Formularios de DGI.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 11/23/18.
 */
public class MZAcctConfigRubroDGI extends X_Z_AcctConfigRubroDGI {

    public MZAcctConfigRubroDGI(Properties ctx, int Z_AcctConfigRubroDGI_ID, String trxName) {
        super(ctx, Z_AcctConfigRubroDGI_ID, trxName);
    }

    public MZAcctConfigRubroDGI(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }


    /***
     * Obtnego y retorno modelo segun impuesto y flag de venta recibidos.
     * Xpande. Created by Gabriel Vila on 11/23/18.
     * @param ctx
     * @param cTaxID
     * @param isSOTrx
     * @param trxName
     * @return
     */
    public static MZAcctConfigRubroDGI getByTax(Properties ctx, int cTaxID, boolean isSOTrx, String trxName){

        MZAcctConfigRubroDGI model = null;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            sql = " select a.z_acctconfigrubrodgi_id " +
                    " from z_rubrodgitax a " +
                    " inner join z_acctconfigrubrodgi b on a.z_acctconfigrubrodgi_id = b.z_acctconfigrubrodgi_id " +
                    " where a.c_tax_id =" + cTaxID +
                    " and a.isactive ='Y' " +
                    " and b.isactive ='Y' " +
                    " and b.issotrx =" + ((isSOTrx) ? "'Y'" : "'N'") +
                    " order by a.created desc ";

        	pstmt = DB.prepareStatement(sql, trxName);
        	rs = pstmt.executeQuery();

        	if (rs.next()){
                model = new MZAcctConfigRubroDGI(ctx, rs.getInt("z_acctconfigrubrodgi_id"), trxName);
        	}
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }

        return model;
    }

}
