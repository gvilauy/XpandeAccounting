package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MSequence;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.core.model.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
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
            else if (this.getTipoAcctBrowser().equalsIgnoreCase(X_Z_AcctBrowser.TIPOACCTBROWSER_BALANCECONTABLE)){
                message = this.executeBalance();
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

        try{

            this.deleteDataMayor();

            this.setCuentasFiltro();

            this.getDataMayor(null);
            this.updateDataMayor();
            this.updateAcumuladoMayor();

            this.getDataSumMayor();
            this.updateDataSumMayor();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return null;
    }


    /***
     * Ejecuta consulta de Balance Contable.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     * @return
     */
    private String executeBalance(){

        String message = null;

        try{

            this.deleteDataBalance();

            this.setCuentasFiltro();

            this.getDataBalance();

            this.updateDataBalance();

            //this.getDataSumBalance();
            //this.updateDataSumBalance();

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

            action = " delete from Z_AcctBrowSumMayor where z_acctbrowser_id =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Elimina datos anteriores de la tabla para consulta de Balance.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     */
    private void deleteDataBalance() {

        try{
            String action = " delete from Z_AcctBrowserBal where z_acctbrowser_id =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

            action = " delete from Z_AcctBrowSumBal where z_acctbrowser_id =" + this.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Obtiene y carga información para la consulta de Mayor Contable.
     * Xpande. Created by Gabriel Vila on 7/20/18.
     * @param acctBrowserBal : si es recibida, obtiene datos solamente para esta cuenta de balance.
     */
    private void getDataMayor(MZAcctBrowserBal acctBrowserBal) {

        String action, sql;

        try{

            // Guardo ID de cuenta de balance si es recibida, para luego insertar en la tabla de mayor.
            int zAcctBrowserBalID = 0;
            if (acctBrowserBal != null){
                zAcctBrowserBalID = acctBrowserBal.get_ID();
            }

            StringBuilder whereClause = new StringBuilder("");

            // Armo condiciones según filtros
            if (this.getStartDate() != null){
                whereClause.append(" and f.dateacct >='" + this.getStartDate() + "' ");
            }
            if (this.getEndDate() != null){
                whereClause.append(" and f.dateacct <='" + this.getEndDate() + "' ");
            }

            // Filtro cuentas siempre y cuando no reciba cuenta de balance
            if (acctBrowserBal == null){
                // Si tengo cuentas contables para filtrar, agrego condición
                sql = " select count(*) from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.get_ID();
                int contadorCta = DB.getSQLValueEx(get_TrxName(), sql);
                if (contadorCta > 0) {
                    whereClause.append(" and f.account_id in (select distinct(c_elementvalue_id) " +
                            " from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.get_ID() + ") ");
                }
            }
            else{
                // Solo considero cuenta contable recibida en cuenta de balance
                whereClause.append(" and f.account_id =" + acctBrowserBal.getC_ElementValue_ID());
            }

            whereClause.append(this.getFiltrosMayor());

            // Considerar cierre de cuentas diferenciales
            if ((!this.isCierreDiferencial()) && (!this.isCierreIntegral())){
                whereClause.append(" and doc.docbasetype not in ('CJD','CJI') ");
            }
            else{
                if (!this.isCierreDiferencial()){
                    whereClause.append(" and doc.docbasetype <>'CJD' ");
                }
                else{
                    if (!this.isCierreIntegral()){
                        whereClause.append(" and doc.docbasetype <>'CJI' ");
                    }
                }
            }

            // Secuencia de tabla de detalle de consulta de mayor contable
            MSequence sequence = MSequence.get(getCtx(), I_Z_AcctBrowserMayor.Table_Name, get_TrxName());

            // Inserto en table de detalle de consulta de mayor directamente leyendo desde Fact_acct
            action = " insert into z_acctbrowsermayor (z_acctbrowsermayor_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, " +
                    " z_acctbrowser_id, ad_table_id, record_id, c_elementvalue_id, c_currency_id, amtsourcedr, amtsourcecr, amtacctdr, amtacctcr, " +
                    " c_period_id, dateacct, datedoc, description, c_bpartner_id, m_product_id, c_tax_id, qty, taxid, ad_user_id, " +
                    " c_doctype_id, documentnoref, currencyrate, duedate, estadomediopago, nromediopago, z_mediopago_id, z_retencionsocio_id, " +
                    " c_activity_id, Z_AcctBrowserBal_ID) ";

            // Falta : currencyrate, duedate, estadomediopago, nromediopago, z_mediopago_id, z_retencionsocio_id
            sql = " select nextid(" + sequence.get_ID() + ",'N'), f.ad_client_id, f.ad_org_id, f.isactive, f.created, f.createdby, f.updated, f.updatedby," +
                    this.get_ID() + ", f.ad_table_id, f.record_id, f.account_id, f.c_currency_id, f.amtsourcedr, f.amtsourcecr, f.amtacctdr, f.amtacctcr, " +
                    " f.c_period_id, f.dateacct, f.datetrx, f.description, f.c_bpartner_id, f.m_product_id, f.c_tax_id, f.qty, bp.taxid, f.createdby, " +
                    " f.c_doctype_id, f.documentnoref, " +
                    " f.currencyrate, f.duedate, det.estadomediopago, det.nromediopago, det.z_mediopago_id, det.z_retencionsocio_id, f.c_activity_id, " +
                    ((zAcctBrowserBalID > 0) ? Integer.toString(zAcctBrowserBalID) : "null") +
                    " from fact_acct f " +
                    " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                    " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                    " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                    " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                    " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
                    " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                    " where f.ad_client_id =" + this.getAD_Client_ID() +
                    " and f.ad_org_id =" + this.getAD_Org_ID() +
                    " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() + whereClause.toString() +
                    " order by f.account_id, f.dateacct ";
            DB.executeUpdateEx(action + sql, get_TrxName());

            // Elimino registros sin importe en DR y CR (ejemplo Factura Bonificacion)
            action = " delete from z_acctbrowsermayor " +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " and amtsourcedr = 0 and amtsourcecr = 0 ";
            DB.executeUpdateEx(action, get_TrxName());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    public StringBuilder getFiltrosMayor() {

        StringBuilder whereClause = new StringBuilder("");

        try{


            if (this.getAccountType() != null){
                whereClause.append(" and ev.AccountType ='" + this.getAccountType() + "' ");
            }

            if (this.getC_BP_Group_ID() > 0){
                whereClause.append(" and bp.c_bp_group_id =" + this.getC_BP_Group_ID());
            }

            if (this.getM_Product_Category_ID() > 0){
                whereClause.append(" and prod.m_product_category_id =" + this.getM_Product_Category_ID());
            }

            if (this.getProductType() != null){
                whereClause.append(" and prod.ProductType ='" + this.getProductType() + "' ");
            }

            whereClause.append(this.getFiltrosRetail());
            whereClause.append(this.getFiltroEstadoMPago());


            // Si tengo socios de negocio para filtrar, agrego condición
            String sql = " select count(*) from Z_AcctBrowFiltroBP where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorBP = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorBP > 0) {
                whereClause.append(" and f.c_bpartner_id in (select distinct(c_bpartner_id) " +
                        " from Z_AcctBrowFiltroBP where Z_AcctBrowser_ID =" + this.get_ID() + ") ");
            }

            // Si tengo productos para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroProd where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorProd = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorProd > 0) {
                whereClause.append(" and f.m_product_id in (select distinct(m_product_id) " +
                        " from Z_AcctBrowFiltroProd where Z_AcctBrowser_ID =" + this.get_ID() + ") ");
            }

            // Si tengo impuestos para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroTax where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorTax = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorTax > 0) {
                whereClause.append(" and f.c_tax_id in (select distinct(c_tax_id) " +
                        " from Z_AcctBrowFiltroTax where Z_AcctBrowser_ID =" + this.get_ID() + ") ");
            }

            // Si tengo documento para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroDoc where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorDoc = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorDoc > 0) {
                whereClause.append(" and f.c_doctype_id in (select distinct(c_doctype_id) " +
                        " from Z_AcctBrowFiltroDoc where Z_AcctBrowser_ID =" + this.get_ID() + ") ");
            }

            // Si tengo retenciones para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroRet where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorRet = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorRet > 0) {
                whereClause.append(" and f.fact_acct_id in (select fact_acct_id from z_acctfactdet " +
                        " where z_retencionsocio_id in (select distinct(z_retencionsocio_id) " +
                        " from Z_AcctBrowFiltroRet where Z_AcctBrowser_ID =" + this.get_ID() + ")) ");
            }

            // Si tengo medios de pago para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroMPago where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorMPago = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorMPago > 0) {
                whereClause.append(" and f.fact_acct_id in (select fact_acct_id from z_acctfactdet " +
                        " where z_mediopago_id in (select distinct(z_mediopago_id) " +
                        " from Z_AcctBrowFiltroMPago where Z_AcctBrowser_ID =" + this.get_ID() + ")) ");
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        return whereClause;
    }

    private String getFiltroEstadoMPago() {

        String whereClause = "";

        try{

            if (this.isFiltroEstadoMPago()){

                if (this.isEmitido()){
                    whereClause += " and (mpi.emitido ='Y' ";
                }
                else {
                    whereClause += " and (mpi.emitido ='N' ";
                }

                if (this.isEntregado()){
                    whereClause += " and mpi.entregado ='Y' ";
                }
                else {
                    whereClause += " and mpi.entregado ='N' ";
                }

                if (this.isDepositado()){
                    whereClause += " and mpi.depositado ='Y' ";
                }
                else {
                    whereClause += " and mpi.depositado ='N' ";
                }

                if (this.isConciliado()){
                    whereClause += " and mpi.conciliado ='Y' ";
                }
                else {
                    whereClause += " and mpi.conciliado ='N' ";
                }

                if (this.isReemplazado()){
                    whereClause += " and mpi.reemplazado ='Y' ";
                }
                else {
                    whereClause += " and mpi.reemplazado ='N' ";
                }

                if (this.isAnulado()){
                    whereClause += " and mpi.anulado ='Y') ";
                }
                else {
                    whereClause += " and mpi.anulado ='N') ";
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return whereClause;
    }

    private String getFiltrosRetail() {

        String whereClause = "";

        try{

            int zProductoSeccionID = this.get_ValueAsInt("Z_ProductoSeccion_ID");
            int zProductoRubroID = this.get_ValueAsInt("Z_ProductoRubro_ID");
            int zProductoFamiliaID = this.get_ValueAsInt("Z_ProductoFamilia_ID");
            int zProductoSubfamiliaID = this.get_ValueAsInt("Z_ProductoSubfamilia_ID");

            if (zProductoSeccionID > 0){
                whereClause += " and prod.z_productoseccion_id =" + zProductoSeccionID;
            }

            if (zProductoRubroID > 0){
                whereClause += " and prod.z_productorubro_id =" + zProductoRubroID;
            }

            if (zProductoFamiliaID > 0){
                whereClause += " and prod.z_productofamilia_id =" + zProductoFamiliaID;
            }

            if (zProductoSubfamiliaID > 0){
                whereClause += " and prod.z_productosubfamilia_id =" + zProductoSubfamiliaID;
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return whereClause;
    }


    /***
     * Obtiene y carga información para la consulta de Balance.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     */
    private void getDataBalance() {

        String action, sql;

        try{

            // Secuencia de tabla de detalle de consulta de balance contable
            MSequence sequence = MSequence.get(getCtx(), I_Z_AcctBrowserBal.Table_Name, get_TrxName());

            // Inserto en table de detalle de consulta de balance directamente leyendo desde Fact_acct
            action = " insert into z_acctbrowserbal (z_acctbrowserbal_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, " +
                    " z_acctbrowser_id, c_elementvalue_id, c_currency_id, codigocuenta, nombrecuenta, issummary, parent_id, node_id, seqno, nrofila, " +
                    " amttotal1, amttotal2) ";

            // Falta : currencyrate, duedate, estadomediopago, nromediopago, z_mediopago_id, z_retencionsocio_id
            sql = " select nextid(" + sequence.get_ID() + ",'N'), f.ad_client_id, " + this.getAD_Org_ID() + ", 'Y' as isactive, now() as created, " +
                    this.getCreatedBy() + ", now() as updated, " + this.getUpdatedBy() + "," +
                    this.get_ID() + ", f.c_elementvalue_id, " + this.getC_Currency_ID() + ", f.value, f.name, f.issummary, " +
                    " f.parent_id, f.node_id, f.seqno, f.nrofila, 0, 0 " +
                    " from ZV_ElementValueTree f " +
                    " where f.ad_client_id =" + this.getAD_Client_ID() +
                    " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() + //whereClause +
                    " order by f.nrofila ";
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

        String action;

        try{

            MAcctSchema schema = (MAcctSchema) this.getC_AcctSchema();

            // Actualizo importes en moneda uno de consulta
            // Si la moneda uno es igual a la moneda del esquema contable
            if (schema.getC_Currency_ID() == this.getC_Currency_ID()){
                action = " update z_acctbrowsermayor set amtdr1 = amtacctdr, amtcr1 = amtacctcr, c_currency_1_id =" + this.getC_Currency_ID() +
                         " where z_acctbrowser_id =" + this.get_ID();
                DB.executeUpdateEx(action, get_TrxName());
            }
            else{
                // Moneda uno de la consulta es distinta a la moneda del esquema contable
                // Actualizo importes para asientos con misma moneda de asiento que la uno de la consulta
                action = " update z_acctbrowsermayor set " +
                        " amtdr1 = amtsourcedr, " +
                        " amtcr1 = amtsourcecr, " +
                        " c_currency_1_id =" + this.getC_Currency_ID() +
                        " where z_acctbrowser_id =" + this.get_ID() +
                        " and c_currency_id =" + this.getC_Currency_ID();
                DB.executeUpdateEx(action, get_TrxName());

                // Actualizo importes para asientos con distinta moneda de asiento, considerando tipo de cambio del día del asiento
                action = " update z_acctbrowsermayor set " +
                        " amtdr1 = (amtsourcedr * currencyrate(c_currency_id, " + this.getC_Currency_ID() + ", dateacct, 114, ad_client_id, ad_org_id)), " +
                        " amtcr1 = (amtsourcecr * currencyrate(c_currency_id, " + this.getC_Currency_ID() + ", dateacct, 114, ad_client_id, ad_org_id)), " +
                        " c_currency_1_id =" + this.getC_Currency_ID() +
                        " where z_acctbrowser_id =" + this.get_ID() +
                        " and c_currency_id !=" + this.getC_Currency_ID();
                DB.executeUpdateEx(action, get_TrxName());
            }

            // Actualizo importes en moneda dos de consulta
            if (this.getC_Currency_2_ID() > 0){
                // Si la moneda dos es igual a la moneda del esquema contable
                if (schema.getC_Currency_ID() == this.getC_Currency_2_ID()){
                    action = " update z_acctbrowsermayor set amtdr2 = amtacctdr, amtcr2 = amtacctcr, c_currency_2_id =" + this.getC_Currency_2_ID() +
                            " where z_acctbrowser_id =" + this.get_ID();
                    DB.executeUpdateEx(action, get_TrxName());
                }
                else{
                    // Moneda dos de la consulta es distinta a la moneda del esquema contable
                    // Actualizo importes para asientos con misma moneda de asiento que la dos de la consulta
                    action = " update z_acctbrowsermayor set " +
                            " amtdr2 = amtsourcedr, " +
                            " amtcr2 = amtsourcecr, " +
                            " c_currency_2_id =" + this.getC_Currency_2_ID() +
                            " where z_acctbrowser_id =" + this.get_ID() +
                            " and c_currency_id =" + this.getC_Currency_2_ID();
                    DB.executeUpdateEx(action, get_TrxName());

                    // Actualizo importes para asientos con distinta moneda de asiento, considerando tipo de cambio del día del asiento
                    // No considerar asientos contables hecho en el proceso de diferencia de cambio para esta traduccion.
                    action = " update z_acctbrowsermayor set " +
                            " amtdr2 = round((amtsourcedr * z_currencyrate(c_currency_id, " + this.getC_Currency_2_ID() + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " amtcr2 = round((amtsourcecr * z_currencyrate(c_currency_id, " + this.getC_Currency_2_ID() + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " c_currency_2_id =" + this.getC_Currency_2_ID() +
                            " where z_acctbrowser_id =" + this.get_ID() +
                            " and ad_table_id !=" + I_Z_DifCambio.Table_ID +
                            " and c_currency_id !=" + this.getC_Currency_2_ID();
                    DB.executeUpdateEx(action, get_TrxName());
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    /***
     * Actualiza información en tablas de consulta de Balance Contable
     * Xpande. Created by Gabriel Vila on 3/12/19.
     */
    private void updateDataBalance(){

        try{

            // Actualizo saldos de cuentas no totalizadoras
            this.updateDataBalanceNotSummary();

            // Actualizo saldos de cuentas totalizadoras
            this.updateDataBalanceSummary();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }


    /***
     * Actualiza saldos de cuentas no totalizadoras en consulta de Balance Contable.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     */
    private void updateDataBalanceNotSummary() {

        String sql, action;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            // Armo condiciones según filtros
            String whereClause = " and f.ad_client_id =" + this.getAD_Client_ID() +
                                 " and f.ad_org_id =" + this.getAD_Org_ID();

            if (this.getStartDate() != null){
                whereClause += " and f.dateacct >='" + this.getStartDate() + "'" ;
            }
            if (this.getEndDate() != null){
                whereClause += " and f.dateacct <='" + this.getEndDate() + "'" ;
            }

            // Si tengo cuentas contables para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorCta = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorCta > 0) {
                whereClause += " and f.account_id in (select distinct(c_elementvalue_id) " +
                        " from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.get_ID() + ") ";
            }

            // Si tengo socios de negocio para filtrar, agrego condición
            sql = " select count(*) from Z_AcctBrowFiltroBP where Z_AcctBrowser_ID =" + this.get_ID();
            int contadorBP = DB.getSQLValueEx(get_TrxName(), sql);
            if (contadorBP > 0) {
                whereClause += " and f.c_bpartner_id in (select distinct(c_bpartner_id) " +
                        " from Z_AcctBrowFiltroBP where Z_AcctBrowser_ID =" + this.get_ID() + ") ";
            }

            sql = " select f.account_id, sum(amtacctdr - amtacctcr) as saldo " +
                    " from fact_acct f " +
                    " inner join z_acctbrowserbal b on f.account_id = b.c_elementvalue_id " +
                    " where b.z_acctbrowser_id =" + this.get_ID() + whereClause +
                    " and b.issummary ='N' " +
                    " group by f.account_id ";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	while(rs.next()){

                BigDecimal saldo = rs.getBigDecimal("saldo");
                if (saldo == null){
                    saldo = Env.ZERO;
                }

                // Actualizo saldo de cuenta
                action  = " update z_acctbrowserbal set amttotal1 =" + saldo +
                        " where z_acctbrowser_id =" + this.get_ID() +
                        " and c_elementvalue_id =" + rs.getInt("account_id");
                DB.executeUpdateEx(action, get_TrxName());
        	}

        	// Finalmente elimino cuentas sin saldo si asi lo desea el usuario
            if (!this.isIncCtaSaldoSinMov()){
                action = " delete from z_acctbrowserbal " +
                        " where amttotal1 = 0 " +
                        " and z_acctbrowser_id =" + this.get_ID() +
                        " and issummary ='N' ";
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


    /***
     * Actualiza saldos de cuentas totalizadoras en consulta de Balance Contable.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     */
    private void updateDataBalanceSummary() {

        String sql;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            sql = " select c_elementvalue_id " +
                    " from z_acctbrowserbal " +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " and parent_id = 0" +
                    " order by nrofila ";

            pstmt = DB.prepareStatement(sql, get_TrxName());
            rs = pstmt.executeQuery();

            while(rs.next()){

                this.updateDataBalRecursive(rs.getInt("c_elementvalue_id"), 1);
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
     * Actualiza cuentas totalizadoras de manera recursiva.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     * @param cElementValueID
     * @param nivel
     */
    private void updateDataBalRecursive(int cElementValueID, int nivel){

        String sql, action;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            // Actualizo primero nivel de la cuenta totalizadora recibida
            action = " update z_acctbrowserbal " +
                    " set nivelcuenta =" + nivel +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " and c_elementvalue_id =" + cElementValueID;
            DB.executeUpdateEx(action, get_TrxName());

            // Subo nivel
            nivel++;

            // Obtengo cuentas hijas de la cuenta totalizadora recibida.
            // Dentro de sus hijas pueden haber cuentas a su vez totalizadoras, por eso la recursividad.
            sql = " select c_elementvalue_id, IsSummary " +
                    " from z_acctbrowserbal " +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " and parent_id =" + cElementValueID +
                    " order by nrofila ";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	while(rs.next()){

        	    // Si la cuenta es totalizadora, sigo la recursividad
                if (rs.getString("IsSummary").equalsIgnoreCase("Y")){

                    this.updateDataBalRecursive(rs.getInt("c_elementvalue_id"), nivel);
                }
                else{
                    // Cuenta no totalizadora, le actualizo simplemente el nivel
                    // Actualizo primero nivel de la cuenta totalizadora recibida
                    action = " update z_acctbrowserbal " +
                            " set nivelcuenta =" + nivel +
                            " where z_acctbrowser_id =" + this.get_ID() +
                            " and c_elementvalue_id =" + rs.getInt("c_elementvalue_id");
                    DB.executeUpdateEx(action, get_TrxName());
                }
        	}

        	// Actualizo saldos de cuenta totalizadora recibida
            sql = " select sum(amttotal1) as saldo " +
                    " from z_acctbrowserbal " +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " and parent_id =" + cElementValueID;
            BigDecimal saldo1 = DB.getSQLValueBDEx(get_TrxName(), sql);
            if (saldo1 == null) saldo1 = Env.ZERO;

            action = " update z_acctbrowserbal " +
                    " set amttotal1 =" + saldo1 +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " and c_elementvalue_id =" + cElementValueID;
            DB.executeUpdateEx(action, get_TrxName());

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
     * Obtiene y carga resultado sumarizado de consulta de Mayor Contable.
     * Xpande. Created by Gabriel Vila on 7/24/18.
     */
    private void getDataSumMayor() {

        String action, sql;

        try{
            // Secuencia de tabla sumarizada de consulta de mayor contable
            MSequence sequence = MSequence.get(getCtx(), I_Z_AcctBrowSumMayor.Table_Name, get_TrxName());


            // Insert sumarización
            action = " insert into z_acctbrowsummayor(z_acctbrowsummayor_id, ad_client_id, ad_org_id, created, createdby, isactive, updated, updatedby, " +
                    "z_acctbrowser_id, c_currency_id, c_currency_2_id, c_elementvalue_id, value, amtdr1, amtcr1, amtdr2, amtcr2) ";

            sql = " select nextid(" + sequence.get_ID() + ", 'N'), " + this.getAD_Client_ID() + ", " + this.getAD_Org_ID() + ", " +
                    " now(), 100, 'Y', now(), 100, " + this.get_ID() + ", " + this.getC_Currency_ID() + ", " + this.getC_Currency_2_ID() + ", " +
                    " a.c_elementvalue_id, ev.value, sum(a.amtdr1), sum(a.amtcr1), sum(a.amtdr2), sum(a.amtcr2) " +
                    " from z_acctbrowsermayor a " +
                    " inner join c_elementvalue ev on a.c_elementvalue_id = ev.c_elementvalue_id " +
                    " where a.z_acctbrowser_id =" + this.get_ID() +
                    " group by a.c_elementvalue_id, ev.value " +
                    " order by ev.value ";
            DB.executeUpdateEx(action + sql, get_TrxName());

            // Actualizo detalle con ID del sumarizador para hacer join
            action = " update z_acctbrowsermayor set z_acctbrowsummayor_id = a.z_acctbrowsummayor_id " +
                    " from z_acctbrowsummayor a " +
                    " where z_acctbrowsermayor.z_acctbrowser_id =" + this.get_ID() +
                    " and a.z_acctbrowser_id =" + this.get_ID() +
                    " and z_acctbrowsermayor.c_elementvalue_id = a.c_elementvalue_id ";
            DB.executeUpdateEx(action, get_TrxName());

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    /***
     * Actualiza información en tablas sumarizada de consulta del Mayor Contable
     * Xpande. Created by Gabriel Vila on 7/20/18.
     */
    private void updateDataSumMayor() {

        String action;

        String sql;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            sql = " select * from z_acctbrowsummayor where z_acctbrowser_id =" + this.get_ID();

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	while(rs.next()){

        	    BigDecimal saldoInicial1 = Env.ZERO, saldoInicial2 = Env.ZERO;
        	    int cElementValueID = rs.getInt("c_elementvalue_id");

                // Obtengo saldo inicial en moneda uno de consulta para esta cuenta contable
                saldoInicial1 = this.getSaldoInicial(cElementValueID, this.getC_Currency_ID());
                if (saldoInicial1 == null) saldoInicial1 = Env.ZERO;

                // Si corresponde obtengo saldo inicial en moneda dos de consulta
                if (this.getC_Currency_2_ID() > 0){
                    saldoInicial2 = this.getSaldoInicial(cElementValueID, this.getC_Currency_2_ID());
                    if (saldoInicial2 == null) saldoInicial2 = Env.ZERO;
                }

                action = " update z_acctbrowsummayor set AmtInicial1 =" + saldoInicial1 + ", " +
                        " AmtInicial2 =" + saldoInicial2 + ", " +
                        " AmtSubtotal1 = (AmtDr1 - AmtCr1), " +
                        " AmtTotal1 = (" + saldoInicial1 + " + AmtDr1 - AmtCr1), " +
                        " AmtSubtotal2 = (AmtDr2 - AmtCr2), " +
                        " AmtTotal2 = (" + saldoInicial2 + " + AmtDr2 - AmtCr2) " +
                        " where z_acctbrowser_id =" + this.get_ID() +
                        " and c_elementvalue_id =" + cElementValueID;
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


    /***
     * Obtiene y retorna saldo inicial para una determinada cuenta expresado en determinada moneda.
     * Xpande. Created by Gabriel Vila on 7/24/18.
     * @param cElementValueID
     * @param cCurrencyID
     * @return
     */
    private BigDecimal getSaldoInicial(int cElementValueID, int cCurrencyID) {

        BigDecimal amt = Env.ZERO;
        String sql = "";

        try{

            MAcctSchema schema = (MAcctSchema) this.getC_AcctSchema();

            // Armo condiciones según filtros
            String whereClause = "";
            if (this.getStartDate() != null){
                whereClause += " and f.dateacct <'" + this.getStartDate() + "'" ;
            }

            whereClause += this.getFiltrosMayor().toString();

            // Considerar cierre de cuentas diferenciales
            if ((!this.isCierreDiferencial()) && (!this.isCierreIntegral())){
                whereClause += " and doc.docbasetype not in ('CJD','CJI') ";
            }
            else{
                if (!this.isCierreDiferencial()){
                    whereClause += " and doc.docbasetype <>'CJD' ";
                }
                else{
                    if (!this.isCierreIntegral()){
                        whereClause += " and doc.docbasetype <>'CJI' ";
                    }
                }
            }

            BigDecimal amtSchemaCurrency = Env.ZERO, amtNotSchemaCurrency = Env.ZERO;

            // Si la moneda uno es igual a la moneda del esquema contable
            if (schema.getC_Currency_ID() == cCurrencyID){
                sql = " select sum(f.amtacctdr - f.amtacctcr) " +
                        " from fact_acct f " +
                        " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                        " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                        " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                        " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
                        " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                        " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                        " where f.ad_client_id =" + this.getAD_Client_ID() +
                        " and f.ad_org_id =" + this.getAD_Org_ID() +
                        " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
                        " and f.account_id =" + cElementValueID + whereClause;

                amtSchemaCurrency = DB.getSQLValueBDEx(get_TrxName(), sql);
                if (amtSchemaCurrency == null) amtSchemaCurrency = Env.ZERO;
            }
            else{

                sql = " select sum(f.amtsourcedr - f.amtsourcecr) " +
                        " from fact_acct f " +
                        " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                        " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                        " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                        " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
                        " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                        " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                        " where f.ad_client_id =" + this.getAD_Client_ID() +
                        " and f.ad_org_id =" + this.getAD_Org_ID() +
                        " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
                        " and f.c_currency_id =" + cCurrencyID +
                        " and f.account_id =" + cElementValueID + whereClause;

                amtNotSchemaCurrency = DB.getSQLValueBDEx(get_TrxName(), sql);
                if (amtNotSchemaCurrency == null) amtNotSchemaCurrency = Env.ZERO;

                sql = " select sum(round(((f.amtsourcedr - f.amtsourcecr) * currencyrate(f.c_currency_id," + cCurrencyID + ", f.dateacct, 114, f.ad_client_id, f.ad_org_id)),2)) " +
                        " from fact_acct f " +
                        " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                        " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                        " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                        " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
                        " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                        " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                        " where f.ad_client_id =" + this.getAD_Client_ID() +
                        " and f.ad_org_id =" + this.getAD_Org_ID() +
                        " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
                        " and f.c_currency_id !=" + cCurrencyID +
                        " and f.account_id =" + cElementValueID + whereClause;

                BigDecimal amtAux = DB.getSQLValueBDEx(get_TrxName(), sql);
                if (amtAux == null) amtAux = Env.ZERO;

                amtNotSchemaCurrency = amtNotSchemaCurrency.add(amtAux);
            }

            amt = amtSchemaCurrency.add(amtNotSchemaCurrency);
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return amt;
    }

    @Override
    protected boolean beforeSave(boolean newRecord) {

        // Valido rango de fechas
        if (this.getStartDate() != null){
            if (this.getEndDate() != null){
                if (this.getEndDate().before(this.getStartDate())){
                    log.saveError("ATENCIÓN", "Fecha Hasta debe ser Mayor que Fecha Desde");
                    return false;
                }
            }
        }

        // Valido segunda moneda cuando se requiere
        if (this.getTipoFiltroMonAcct().equalsIgnoreCase(X_Z_AcctBrowser.TIPOFILTROMONACCT_DOSMONEDAS)){
            if (this.getC_Currency_2_ID() <= 0){
                log.saveError("ATENCIÓN", "Debe Indicar Segunda Moneda de Consulta a considerar");
                return false;
            }
            if (this.getC_Currency_2_ID() == this.getC_Currency_ID()){
                log.saveError("ATENCIÓN", "Debe Indicar Segunda Moneda de Consulta diferente a Primer Moneda");
                return false;
            }
        }

        // Si tengo tipo de filtro de informe, cargo datos del mismo.
        if (this.getZ_DataFiltro_ID() > 0) {
            // Cargo filtros del cabezal
            MZDataFiltro dataFiltro = new MZDataFiltro(getCtx(), this.getZ_DataFiltro_ID(), get_TrxName());
            if (dataFiltro.getAccountType() != null) {
                this.setAccountType(dataFiltro.getAccountType());
            }
            if (dataFiltro.getC_BP_Group_ID() > 0) {
                this.setC_BP_Group_ID(dataFiltro.getC_BP_Group_ID());
            }
            if (dataFiltro.getM_Product_Category_ID() > 0) {
                this.setM_Product_Category_ID(dataFiltro.getM_Product_Category_ID());
            }
            if (dataFiltro.getProductType() != null) {
                this.setProductType(dataFiltro.getProductType());
            }

            this.setFiltroEstadoMPago(dataFiltro.isFiltroEstadoMPago());
            this.setEmitido(dataFiltro.isEmitido());
            this.setEntregado(dataFiltro.isEntregado());
            this.setDepositado(dataFiltro.isDepositado());
            this.setConciliado(dataFiltro.isConciliado());
            this.setReemplazado(dataFiltro.isReemplazado());
            this.setAnulado(dataFiltro.isAnulado());

            // Atributos de Retail que me quedaron enganchados
            if (dataFiltro.get_ValueAsInt("Z_ProductoSeccion_ID") > 0){
                this.set_Value("Z_ProductoSeccion_ID", dataFiltro.get_ValueAsInt("Z_ProductoSeccion_ID"));
            }
            if (dataFiltro.get_ValueAsInt("Z_ProductoRubro_ID") > 0){
                this.set_Value("Z_ProductoRubro_ID", dataFiltro.get_ValueAsInt("Z_ProductoRubro_ID"));
            }
            if (dataFiltro.get_ValueAsInt("Z_ProductoFamilia_ID") > 0){
                this.set_Value("Z_ProductoFamilia_ID", dataFiltro.get_ValueAsInt("Z_ProductoFamilia_ID"));
            }
            if (dataFiltro.get_ValueAsInt("Z_ProductoSubfamilia_ID") > 0){
                this.set_Value("Z_ProductoSubfamilia_ID", dataFiltro.get_ValueAsInt("Z_ProductoSubfamilia_ID"));
            }
        }

        return true;
    }

    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (!success) return false;

        // Si tengo tipo de filtro de informe, cargo datos del mismo.
        if (this.getZ_DataFiltro_ID() > 0){

            MZDataFiltro dataFiltro = new MZDataFiltro(getCtx(), this.getZ_DataFiltro_ID(), get_TrxName());

            // Cuentas contables
            List<MZDataFiltroAcct> acctList = dataFiltro.getFiltrosAcct();
            for (MZDataFiltroAcct filtroAcct: acctList){
                MZAcctBrowFiltroCta filtroCta = new MZAcctBrowFiltroCta(getCtx(), 0, get_TrxName());
                filtroCta.setC_ElementValue_ID(filtroAcct.getC_ElementValue_ID());
                filtroCta.setZ_AcctBrowser_ID(this.get_ID());
                filtroCta.saveEx();
            }

            // Socios de negocio
            List<MZDataFiltroBP> bpList = dataFiltro.getFiltrosBP();
            for (MZDataFiltroBP filtroBP: bpList){
                MZAcctBrowFiltroBP browFiltroBP = new MZAcctBrowFiltroBP(getCtx(), 0, get_TrxName());
                browFiltroBP.setC_BPartner_ID(filtroBP.getC_BPartner_ID());
                browFiltroBP.setZ_AcctBrowser_ID(this.get_ID());
                browFiltroBP.saveEx();
            }

            // Productos
            List<MZDataFiltroProd> prodList = dataFiltro.getFiltrosProd();
            for (MZDataFiltroProd filtroProd: prodList){
                MZAcctBrowFiltroProd browFiltroProd = new MZAcctBrowFiltroProd(getCtx(), 0, get_TrxName());
                browFiltroProd.setM_Product_ID(filtroProd.getM_Product_ID());
                browFiltroProd.setZ_AcctBrowser_ID(this.get_ID());
                browFiltroProd.saveEx();
            }

            // Impuestos
            List<MZDataFiltroTax> taxList = dataFiltro.getFiltrosTax();
            for (MZDataFiltroTax filtroTax: taxList){
                MZAcctBrowFiltroTax browFiltroTax = new MZAcctBrowFiltroTax(getCtx(), 0, get_TrxName());
                browFiltroTax.setC_Tax_ID(filtroTax.getC_Tax_ID());
                browFiltroTax.setZ_AcctBrowser_ID(this.get_ID());
                browFiltroTax.saveEx();
            }

            // Retenciones
            List<MZDataFiltroRet> retList = dataFiltro.getFiltrosRet();
            for (MZDataFiltroRet filtroRet: retList){
                MZAcctBrowFiltroRet browFiltroRet = new MZAcctBrowFiltroRet(getCtx(), 0, get_TrxName());
                browFiltroRet.setZ_RetencionSocio_ID(filtroRet.getZ_RetencionSocio_ID());
                browFiltroRet.setZ_AcctBrowser_ID(this.get_ID());
                browFiltroRet.saveEx();
            }

            // Documentos
            List<MZDataFiltroDoc> docList = dataFiltro.getFiltrosDoc();
            for (MZDataFiltroDoc filtroDoc: docList){
                MZAcctBrowFiltroDoc browFiltroDoc = new MZAcctBrowFiltroDoc(getCtx(), 0, get_TrxName());
                browFiltroDoc.setC_DocType_ID(filtroDoc.getC_DocType_ID());
                browFiltroDoc.setZ_AcctBrowser_ID(this.get_ID());
                browFiltroDoc.saveEx();
            }

            // Medios de Pago
            List<MZDataFiltroMPago> mpagoList = dataFiltro.getFiltrosMPago();
            for (MZDataFiltroMPago filtroMPago: mpagoList){
                MZAcctBrowFiltroMPago browFiltroMPago = new MZAcctBrowFiltroMPago(getCtx(), 0, get_TrxName());
                browFiltroMPago.setZ_MedioPago_ID(filtroMPago.getZ_MedioPago_ID());
                browFiltroMPago.setZ_AcctBrowser_ID(this.get_ID());
                browFiltroMPago.saveEx();
            }
        }

        return true;
    }

    /***
     * Interpreta texto de filtro de cuentas contables e inserta el resultado en la tabla de filtro de cuentas.
     * Xpande. Created by Gabriel Vila on 7/26/18.
     * @return
     */
    public String setCuentasFiltro(){

        try{

            if ((this.getTextoFiltro() == null) || (this.getTextoFiltro().trim().equalsIgnoreCase(""))){
                return null;
            }

            // Split por separadores de cuentas
            String ctas[] = this.getTextoFiltro().trim().split(";");
            for (int i = 0; i < ctas.length; i++){
                String tokenCta = ctas[i];

                // Split por rango de cuenta en este token
                String[] rangos = tokenCta.trim().split("-");
                if (rangos.length >= 2){
                    this.insertCtasFiltro(rangos[0], rangos[1]);
                }
                else{
                    this.insertCtasFiltro(rangos[0], null);
                }
            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return null;
    }

    /***
     * Inserta cuentas en tabla de filtros, segun texto de filtro ingresado.
     * Xpande. Created by Gabriel Vila on 7/26/18.
     * @param valueFrom
     * @param valueTo
     */
    private void insertCtasFiltro(String valueFrom, String valueTo){

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            String whereClause = "";
            if (valueTo != null){
                whereClause = " and (value >='" + valueFrom.trim() + "' and value <='" + valueTo.trim() + "') ";
            }
            else{
                whereClause = " and value ='" + valueFrom.trim() + "' ";
            }

            sql = " select c_elementvalue_id " +
                    " from c_elementvalue " +
                    " where issummary ='N' and isactive ='Y' " + whereClause +
                    " and c_elementvalue_id not in " +
                    " (select c_elementvalue_id from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.get_ID() + ") " +
                    " order by value ";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	while(rs.next()){
                MZAcctBrowFiltroCta filtroCta = new MZAcctBrowFiltroCta(getCtx(), 0, get_TrxName());
                filtroCta.setZ_AcctBrowser_ID(this.get_ID());
                filtroCta.setC_ElementValue_ID(rs.getInt("c_elementvalue_id"));
                filtroCta.saveEx();
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
     * Actualiza montos acumulados para detalle de mayor contable.
     * Xpande. Created by Gabriel Vila on 4/9/19.
     */
    private void updateAcumuladoMayor(){

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String action = "";

        try{
            sql = " select Z_AcctBrowserMayor_ID, ad_org_id, c_elementvalue_id, coalesce(amtdr1,0) as amtdr1, " +
                    " coalesce(amtcr1,0) as amtcr1, coalesce(amtdr2,0) as amtdr2, coalesce(amtcr2,0) as amtcr2 " +
                    " from Z_AcctBrowserMayor " +
                    " where z_acctbrowser_id =" + this.get_ID() +
                    " order by c_elementvalue_id, dateacct ";

        	pstmt = DB.prepareStatement(sql, get_TrxName());
        	rs = pstmt.executeQuery();

        	int adOrgIDAux = 0, cElementValueIDAux = 0;
        	BigDecimal amtAcumulado1 = Env.ZERO, amtAcumulado2 = Env.ZERO;

        	while(rs.next()){

        	    // Corte por organizacion y cuenta contable
        	    if ((rs.getInt("c_elementvalue_id") != cElementValueIDAux)
                        || (rs.getInt("ad_org_id") != adOrgIDAux)){

        	        cElementValueIDAux = rs.getInt("c_elementvalue_id");
        	        adOrgIDAux = rs.getInt("ad_org_id");

        	        amtAcumulado1 = Env.ZERO;
        	        amtAcumulado2 = Env.ZERO;
                }

        	    amtAcumulado1 = amtAcumulado1.add(rs.getBigDecimal("amtdr1")).subtract(rs.getBigDecimal("amtcr1"));
                amtAcumulado2 = amtAcumulado2.add(rs.getBigDecimal("amtdr2")).subtract(rs.getBigDecimal("amtcr2"));

                action = " update Z_AcctBrowserMayor " +
                         " set amtacumulado1 =" + amtAcumulado1 + ", amtacumulado2 =" + amtAcumulado2 +
                         " where Z_AcctBrowserMayor_ID =" + rs.getInt("Z_AcctBrowserMayor_ID");
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


    /***
     * Obtiene y carga detalle de mayor para una determinada cuenta en una consulta de Balance.
     * Xpande. Created by Gabriel Vila on 4/9/19.
     * @param acctBrowserBal
     * @return
     */
    public String getDetalleBalance(MZAcctBrowserBal acctBrowserBal) {

        String message = null;
        String action = "";

        try{

            // Elimino detalle anterior para esta cuenta de balance
            action = " delete from " + X_Z_AcctBrowserMayor.Table_Name +
                     " where " + X_Z_AcctBrowserMayor.COLUMNNAME_Z_AcctBrowserBal_ID + " =" + acctBrowserBal.get_ID();
            DB.executeUpdateEx(action, get_TrxName());

            // Obtengo datos del mayor para la cuenta de balance recibida
            this.getDataMayor(acctBrowserBal);

            this.updateDataMayor();
            this.updateAcumuladoMayor();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }

}
