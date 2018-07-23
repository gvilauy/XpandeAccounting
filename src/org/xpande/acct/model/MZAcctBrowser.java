package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MSequence;
import org.compiere.util.DB;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Modelo para cabezales de Navegador Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/20/18.
 */
public class MZAcctBrowser extends X_Z_AcctBrowser {

    public MZAcctBrowser(Properties ctx, int Z_AcctBrowser_ID, String trxName) {
        super(ctx, Z_AcctBrowser_ID, trxName);
    }

    public MZAcctBrowser(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }


    /***
     * Ejecuta consulta segun tipo de consulta seleccionado por el usuario.
     * Xpande. Created by Gabriel Vila on 7/20/18.
     * @return
     */
    public String executeBrowser(){

        String message = null;

        try{

            if (this.getTipoAcctBrowser().equalsIgnoreCase(X_Z_AcctBrowser.TIPOACCTBROWSER_MAYORCONTABLE)){
                message = this.executeMayor();
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    /***
     * Ejecuta consulta de Mayor Contable.
     * Xpande. Created by Gabriel Vila on 7/20/18.
     * @return
     */
    private String executeMayor(){

        String message = null;

        try{

            //message = this.validateFiltros();

            this.deleteDataMayor();
            this.getDataMayor();
            this.updateDataMayor();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    /***
     * Elimina datos anteriores de la tabla para consulta de Mayor Contable.
     * Xpande. Created by Gabriel Vila on 7/20/18.
     */
    private void deleteDataMayor() {

        try{
            String action = " delete from Z_AcctBrowserMayor where z_acctbrowser_id =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Obtiene y carga información para la consulta de Mayor Contable.
     * Xpande. Created by Gabriel Vila on 7/20/18.
     */
    private void getDataMayor() {

        String action = "", sql = "";

        try{

            // Armo condiciones según filtros
            String whereClause = "";
            if (this.getStartDate() != null){
                whereClause += " and f.dateacct >='" + this.getStartDate() + "'" ;
            }
            if (this.getEndDate() != null){
                whereClause += " and f.dateacct <='" + this.getEndDate() + "'" ;
            }

            // Secuencia de tabla de detalle de consulta de mayor contable
            MSequence sequence = MSequence.get(getCtx(), I_Z_AcctBrowserMayor.Table_Name, get_TrxName());

            // Inserto en table de detalle de consulta de mayor directamente leyendo desde Fact_acct
            action = " insert into z_acctbrowsermayor (z_acctbrowsermayor_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, " +
                    " z_acctbrowser_id, ad_table_id, record_id, c_elementvalue_id, c_currency_id, amtsourcedr, amtsourcecr, amtacctdr, amtacctcr, " +
                    " c_period_id, dateacct, datedoc, description, c_bpartner_id, m_product_id, c_tax_id, qty, taxid, ad_user_id) ";

            // Falta : c_doctype_id, documentnoref, nromediopago, z_mediopago_id, z_retencionsocio_id
            sql = " select nextid(" + sequence.get_ID() + ",'N'), f.ad_client_id, f.ad_org_id, f.isactive, f.created, f.createdby, f.updated, f.updatedby," +
                    this.get_ID() + ", f.ad_table_id, f.record_id, f.account_id, f.c_currency_id, f.amtsourcedr, f.amtsourcecr, f.amtacctdr, f.amtacctcr, " +
                    " f.c_period_id, f.dateacct, f.datetrx, f.description, f.c_bpartner_id, f.m_product_id, f.c_tax_id, f.qty, bp.taxid, f.createdby " +
                    " from fact_acct f " +
                    " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                    " where f.ad_client_id =" + this.getAD_Client_ID() +
                    " and f.ad_org_id =" + this.getAD_Org_ID() +
                    " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() + whereClause +
                    " order by f.account_id, f.dateacct ";
            DB.executeUpdateEx(action + sql, get_TrxName());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Actualiza información en tablas de consulta del Mayor Contable
     * Xpande. Created by Gabriel Vila on 7/20/18.
     */
    private void updateDataMayor() {

        try{

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

}
