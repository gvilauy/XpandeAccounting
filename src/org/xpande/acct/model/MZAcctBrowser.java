package org.xpande.acct.model;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MClient;
import org.compiere.model.MSequence;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.core.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
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

            this.deleteDataMayor();

            this.setCuentasFiltro();

            this.getDataMayor();
            this.updateDataMayor();

            this.getDataSumMayor();
            this.updateDataSumMayor();

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

            // Secuencia de tabla de detalle de consulta de mayor contable
            MSequence sequence = MSequence.get(getCtx(), I_Z_AcctBrowserMayor.Table_Name, get_TrxName());

            // Inserto en table de detalle de consulta de mayor directamente leyendo desde Fact_acct
            action = " insert into z_acctbrowsermayor (z_acctbrowsermayor_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, " +
                    " z_acctbrowser_id, ad_table_id, record_id, c_elementvalue_id, c_currency_id, amtsourcedr, amtsourcecr, amtacctdr, amtacctcr, " +
                    " c_period_id, dateacct, datedoc, description, c_bpartner_id, m_product_id, c_tax_id, qty, taxid, ad_user_id, " +
                    " c_doctype_id, documentnoref) ";

            // Falta : c_doctype_id, documentnoref, nromediopago, z_mediopago_id, z_retencionsocio_id
            sql = " select nextid(" + sequence.get_ID() + ",'N'), f.ad_client_id, f.ad_org_id, f.isactive, f.created, f.createdby, f.updated, f.updatedby," +
                    this.get_ID() + ", f.ad_table_id, f.record_id, f.account_id, f.c_currency_id, f.amtsourcedr, f.amtsourcecr, f.amtacctdr, f.amtacctcr, " +
                    " f.c_period_id, f.dateacct, f.datetrx, f.description, f.c_bpartner_id, f.m_product_id, f.c_tax_id, f.qty, bp.taxid, f.createdby, " +
                    " f.c_doctype_id, f.documentnoref " +
                    " from fact_acct f " +
                    " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                    " where f.ad_client_id =" + this.getAD_Client_ID() +
                    " and f.ad_org_id =" + this.getAD_Org_ID() +
                    " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() + whereClause +
                    //" and (f.amtsourcedr != 0 and f.amtsourcecr != 0) " + whereClause +
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


    /***
     * Actualiza información en tablas de consulta del Mayor Contable
     * Xpande. Created by Gabriel Vila on 7/20/18.
     */
    private void updateDataMayor() {

        String action = "";

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
                    action = " update z_acctbrowsermayor set " +
                            " amtdr2 = round((amtsourcedr * z_currencyrate(c_currency_id, " + this.getC_Currency_2_ID() + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " amtcr2 = round((amtsourcecr * z_currencyrate(c_currency_id, " + this.getC_Currency_2_ID() + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " c_currency_2_id =" + this.getC_Currency_2_ID() +
                            " where z_acctbrowser_id =" + this.get_ID() +
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
     * Obtiene y carga resultado sumarizado de consulta de Mayor Contable.
     * Xpande. Created by Gabriel Vila on 7/24/18.
     */
    private void getDataSumMayor() {

        String action = "", sql = "";

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

        String action = "";

        String sql = "";
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

            BigDecimal amtSchemaCurrency = Env.ZERO, amtNotSchemaCurrency = Env.ZERO;

            // Si la moneda uno es igual a la moneda del esquema contable
            if (schema.getC_Currency_ID() == cCurrencyID){
                sql = " select sum(f.amtacctdr - f.amtacctcr) " +
                        " from fact_acct f " +
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
                        " where f.ad_client_id =" + this.getAD_Client_ID() +
                        " and f.ad_org_id =" + this.getAD_Org_ID() +
                        " and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
                        " and f.c_currency_id =" + cCurrencyID +
                        " and f.account_id =" + cElementValueID + whereClause;

                amtNotSchemaCurrency = DB.getSQLValueBDEx(get_TrxName(), sql);
                if (amtNotSchemaCurrency == null) amtNotSchemaCurrency = Env.ZERO;

                sql = " select sum(round(((f.amtsourcedr - f.amtsourcecr) * currencyrate(c_currency_id," + cCurrencyID + ", dateacct, 114, ad_client_id, ad_org_id)),2)) " +
                        " from fact_acct f " +
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

        return true;
    }

    /***
     * Interpreta texto de filtro de cuentas contables e inserta el resultado en la tabla de filtro de cuentas.
     * Xpande. Created by Gabriel Vila on 7/26/18.
     * @return
     */
    public String setCuentasFiltro(){

        String message = null;

        try{

            if ((this.getTextoFiltro() == null) || (this.getTextoFiltro().trim().equalsIgnoreCase(""))){
                return message;
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

        return message;
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

}
