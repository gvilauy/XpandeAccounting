package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para configuraciones contables del Core.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/11/18.
 */
public class MZAcctConfig extends X_Z_AcctConfig {

    public MZAcctConfig(Properties ctx, int Z_AcctConfig_ID, String trxName) {
        super(ctx, Z_AcctConfig_ID, trxName);
    }

    public MZAcctConfig(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /***
     * Obtiene modelo único de configuración de modulo contable.
     * Xpande. Created by Gabriel Vila on 7/11/18.
     * @param ctx
     * @param trxName
     * @return
     */
    public static MZAcctConfig getDefault(Properties ctx, String trxName){

        MZAcctConfig model = new Query(ctx, I_Z_AcctConfig.Table_Name, "", trxName).first();

        return model;
    }


    /***
     * Configuración contable de un determinado producto en el módulo de Retail.
     * Xpande. Created by Gabriel Vila on 7/12/18.
     * @param adClientID
     * @param mProductID
     * @param cTaxCategoryID
     * @param zRetailConfigID
     */
    public void setRetailProdAcct(int adClientID, int mProductID, int cTaxCategoryID, int zRetailConfigID){

        String action = "";
        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            // Esquema contable
            MClient client = new MClient(getCtx(), adClientID, null);
            MAcctSchema schema = client.getAcctSchema();

            int pExpenseAcctID = 0, pRevenueAcctID = 0;

            // Obtengo parametrización de cuentas contables, en retail, para categoría de impuesto recibida.
            sql = " select * from z_retailconfigprodacct " +
                    " where z_retailconfig_id =" + zRetailConfigID +
                    " and c_taxcategory_id =" + cTaxCategoryID;

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	if (rs.next()){
        	   pExpenseAcctID = rs.getInt("P_Expense_Acct");
        	   pRevenueAcctID = rs.getInt("P_Revenue_Acct");
        	}
        	else{
        	    return;
            }

        	// Actualizo las cuentas del producto, en caso de ya estar seteadas
            action = " update m_product_acct set P_InventoryClearing_Acct =" + pExpenseAcctID + ", " +
                   " P_Expense_Acct =" + pExpenseAcctID + ", " +
                   " P_Revenue_Acct =" + pRevenueAcctID +
                   " where ad_client_id =" + client.get_ID() +
                   " and C_AcctSchema_ID =" + schema.get_ID() +
                   " and m_product_id =" + mProductID;
        	int updated = DB.executeUpdate(action, get_TrxName());
        	if (updated == 0){
        	    action = " INSERT INTO m_product_acct(ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, " +
                        " m_product_id, c_acctschema_id, p_revenue_acct, p_expense_acct, p_inventoryclearing_acct) " +
                        " values (" + client.get_ID() + ", 0, 'Y', now(), 100, now(), 100, " + mProductID + ", " +
                        schema.get_ID() + ", " + pRevenueAcctID + ", " + pExpenseAcctID + ", " + pExpenseAcctID + ")";
        	    DB.executeUpdateEx(action, get_TrxName());
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
