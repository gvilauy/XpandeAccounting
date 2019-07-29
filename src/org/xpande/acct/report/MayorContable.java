package org.xpande.acct.report;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MSequence;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.I_Z_AcctBrowserMayor;
import org.xpande.acct.model.MZAcctBrowFiltroCta;
import org.xpande.core.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Lógica del reporte de Mayor Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/28/19.
 */
public class MayorContable {

    private final String TABLA_REPORTE = "Z_RP_MayorContable";
    private final String TABLA_FILTRO_CTA = "Z_RP_FiltroCta";

    public int adClientID = 0;
    public int adOrgID = 0;
    public int adUserID = 0;
    public int cAcctSchemaID = 0;
    public int cCurrencyID = 0;
    public int cCurrencyID_2 = 0;
    public String tipoFiltroMonAcct = "";
    public Timestamp startDate = null;
    public Timestamp endDate = null;
    public String textoFiltroCuentas = "";
    public int cBPartnerID = 0;
    public int mProductID = 0;
    public int cActivityID = 0;

    private Properties ctx = null;
    private String trxName = null;
    private BigDecimal currencyRate = Env.ONE;
    private MAcctSchema acctSchema = null;


    /***
     * Constructor
     * @param ctx
     * @param trxName
     */
    public MayorContable(Properties ctx, String trxName) {
        this.ctx = ctx;
        this.trxName = trxName;
    }

    /***
     * Método que ejecuta la lógica del reporte.
     * Xpande. Created by Gabriel Vila on 7/28/19.
     * @return
     */
    public String executeReport(){

        String message = null;

        try{

            int cCurrencyFromID = this.cCurrencyID_2;
            if (cCurrencyID_2 <= 0) cCurrencyFromID = 100;

            // Instancio esquema contable
            this.acctSchema = new MAcctSchema(this.ctx, this.cAcctSchemaID, null);

            // Obtengo tasa de cambio a la fecha hasta del reporte por ahora siempre para USD
            if (this.cCurrencyID == this.acctSchema.getC_Currency_ID()){
                this.currencyRate = CurrencyUtils.getCurrencyRate(this.ctx, this.adClientID, 0, cCurrencyFromID, this.cCurrencyID, 114, this.endDate, null);
            }
            else if (this.cCurrencyID_2 == this.acctSchema.getC_Currency_ID()){
                this.currencyRate = CurrencyUtils.getCurrencyRate(this.ctx, this.adClientID, 0, this.cCurrencyID, cCurrencyFromID,  114, this.endDate, null);
            }

            if ((this.currencyRate == null) || (this.currencyRate.compareTo(Env.ZERO) == 0)){
                return "No se pudo obtener Tasa de Cambio para la fecha : " + this.endDate;
            }

            this.deleteData();
            this.getData();
            this.updateData();
            this.updateDataSum();

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

        return message;
    }


    /***
     * Elimina información anterior para este usuario de la tabla del reporte.
     * Xpande. Created by Gabriel Vila on 7/19/19.
     */
    private void deleteData() {

        try{

            String action = " delete from " + TABLA_REPORTE + " where ad_user_id =" + this.adUserID;
            DB.executeUpdateEx(action, null);

            action = " delete from " + TABLA_FILTRO_CTA + " where ad_user_id =" + this.adUserID;
            DB.executeUpdateEx(action, null);

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    private void getData(){

        String action = "", sql = "";

        try{

            // Armo condiciones según filtros
            String whereClause = "";

            if (this.startDate != null){
                whereClause += " and f.dateacct >='" + this.startDate + "'" ;
            }
            if (this.endDate != null){
                whereClause += " and f.dateacct <='" + this.endDate + "'" ;
            }

            // Cuentas Contables
            // Si tengo cuentas contables para filtrar, agrego condición
            sql = " select count(*) from " + TABLA_FILTRO_CTA + " where ad_user_id =" + this.adUserID;
            int contadorCta = DB.getSQLValueEx(null, sql);
            if (contadorCta > 0) {
                whereClause += " and f.account_id in (select distinct(c_elementvalue_id) " +
                        " from " + TABLA_FILTRO_CTA + " where ad_user_id =" + this.adUserID + ") ";
            }

            // Socio de Negocio
            if (this.cBPartnerID > 0){
                whereClause += " and f.c_bpartner_id =" + this.cBPartnerID;
            }

            // Producto
            if (this.mProductID > 0){
                whereClause += " and f.m_product_id =" + this.cBPartnerID;
            }

            // Centro de Costos
            if (this.cActivityID > 0){
                whereClause += " and f.c_activity_id =" + this.cBPartnerID;
            }

            // Inserto en tabla de reporte
            action = " insert into " + TABLA_REPORTE + " (ad_client_id, ad_org_id, created, createdby " +
                    " ad_table_id, record_id, c_elementvalue_id, c_currency_id, amtsourcedr, amtsourcecr, amtacctdr, amtacctcr, " +
                    " c_period_id, dateacct, datedoc, description, c_bpartner_id, m_product_id, c_tax_id, qty, taxid, ad_user_id, " +
                    " c_doctype_id, documentnoref, currencyrate, duedate, estadomediopago, nromediopago, z_mediopago_id, z_retencionsocio_id, " +
                    " c_activity_id) ";

            sql = " select f.ad_client_id, f.ad_org_id, f.created, f.createdby, f.ad_table_id, f.record_id, f.account_id, f.c_currency_id, " +
                    " f.amtsourcedr, f.amtsourcecr, f.amtacctdr, f.amtacctcr, " +
                    " f.c_period_id, f.dateacct, f.datetrx, f.description, f.c_bpartner_id, f.m_product_id, f.c_tax_id, f.qty, bp.taxid, f.createdby, " +
                    " f.c_doctype_id, f.documentnoref, " +
                    " f.currencyrate, f.duedate, det.estadomediopago, det.nromediopago, det.z_mediopago_id, det.z_retencionsocio_id, f.c_activity_id) " +
                    " from fact_acct f " +
                    " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                    " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                    " where f.ad_client_id =" + this.adClientID +
                    " and f.ad_org_id =" + this.adOrgID +
                    " and f.c_acctschema_id =" + this.cAcctSchemaID + whereClause +
                    " order by f.account_id, f.dateacct ";
            DB.executeUpdateEx(action + sql, null);

            // Elimino registros sin importe en DR y CR (ejemplo Factura Bonificacion)
            action = " delete from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID +
                    " and amtsourcedr = 0 and amtsourcecr = 0 ";
            DB.executeUpdateEx(action, null);

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

    }

    private void updateData(){

        String action = "";

        try{

            // Actualizo importes en moneda uno de consulta
            // Si la moneda uno es igual a la moneda del esquema contable
            if (this.cCurrencyID == acctSchema.getC_Currency_ID()){
                action = " update " + TABLA_REPORTE + " set amtdr1 = amtacctdr, amtcr1 = amtacctcr, c_currency_1_id =" + this.cCurrencyID +
                        " where ad_user_id =" + this.adUserID;
                DB.executeUpdateEx(action, null);
            }
            else{
                // Moneda uno de la consulta es distinta a la moneda del esquema contable
                // Actualizo importes para asientos con misma moneda de asiento que la uno de la consulta
                action = " update " + TABLA_REPORTE + " set " +
                        " amtdr1 = amtsourcedr, " +
                        " amtcr1 = amtsourcecr, " +
                        " c_currency_1_id =" + this.cCurrencyID +
                        " where ad_user_id =" + this.adUserID +
                        " and c_currency_id =" + this.cCurrencyID;
                DB.executeUpdateEx(action, null);

                // Actualizo importes para asientos con distinta moneda de asiento, considerando tipo de cambio del día del asiento
                action = " update " + TABLA_REPORTE + " set " +
                        " amtdr1 = (amtsourcedr * currencyrate(c_currency_id, " + this.cCurrencyID + ", dateacct, 114, ad_client_id, ad_org_id)), " +
                        " amtcr1 = (amtsourcecr * currencyrate(c_currency_id, " + this.cCurrencyID + ", dateacct, 114, ad_client_id, ad_org_id)), " +
                        " c_currency_1_id =" + cCurrencyID +
                        " where ad_user_id =" + this.adUserID +
                        " and c_currency_id !=" + this.cCurrencyID;
                DB.executeUpdateEx(action, null);
            }

            // Actualizo importes en moneda dos de consulta
            if (this.cCurrencyID_2 > 0){
                // Si la moneda dos es igual a la moneda del esquema contable
                if (this.cCurrencyID_2 == acctSchema.getC_Currency_ID()){
                    action = " update " + TABLA_REPORTE + " set amtdr2 = amtacctdr, amtcr2 = amtacctcr, c_currency_2_id =" + this.cCurrencyID_2 +
                            " where ad_user_id =" + this.adUserID;
                    DB.executeUpdateEx(action, null);
                }
                else{
                    // Moneda dos de la consulta es distinta a la moneda del esquema contable
                    // Actualizo importes para asientos con misma moneda de asiento que la dos de la consulta
                    action = " update " + TABLA_REPORTE + " set " +
                            " amtdr2 = amtsourcedr, " +
                            " amtcr2 = amtsourcecr, " +
                            " c_currency_2_id =" + cCurrencyID_2 +
                            " where ad_user_id =" + this.adUserID +
                            " and c_currency_id =" + this.cCurrencyID_2;
                    DB.executeUpdateEx(action, null);

                    // Actualizo importes para asientos con distinta moneda de asiento, considerando tipo de cambio del día del asiento
                    action = " update " + TABLA_REPORTE + " set " +
                            " amtdr2 = round((amtsourcedr * z_currencyrate(c_currency_id, " + this.cCurrencyID_2 + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " amtcr2 = round((amtsourcecr * z_currencyrate(c_currency_id, " + this.cCurrencyID_2 + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " c_currency_2_id =" + this.cCurrencyID_2 +
                            " where ad_user_id =" + this.adUserID +
                            " and c_currency_id !=" + this.cCurrencyID_2;
                    DB.executeUpdateEx(action, null);
                }
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }

    }

    /***
     * Interpreta texto de filtro de cuentas contables e inserta el resultado en la tabla de filtro de cuentas.
     * Xpande. Created by Gabriel Vila on 7/28/19.
     * @return
     */
    public String setCuentasFiltro(){

        String message = null;

        try{

            if ((this.textoFiltroCuentas == null) || (this.textoFiltroCuentas.trim().equalsIgnoreCase(""))){
                return message;
            }

            // Split por separadores de cuentas
            String ctas[] = this.textoFiltroCuentas.trim().split(";");
            if (ctas.length > 0){
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
            else{
                // Busco rango de cuentas
                // Split por rango de cuenta en este token
                String[] rangos = this.textoFiltroCuentas.trim().split("-");
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
     * Xpande. Created by Gabriel Vila on 7/28/19.
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
                    " (select c_elementvalue_id from " + TABLA_FILTRO_CTA +
                    " where ad_user_id =" + this.adUserID + ") " +
                    " order by value ";

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();

            while(rs.next()){

                String action = " insert into " + TABLA_FILTRO_CTA + " (ad_client_id, ad_org_id, ad_user_id, c_elementvalue_id) " +
                        " values (" + this.adClientID + ", " + this.adOrgID + ", " + this.adUserID + ", " +
                        rs.getInt("c_elementvalue_id") + ")";
                DB.executeUpdateEx(action, null);
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
     * Actualiza información del totales por cuenta y saldo incial de la misma.
     * Xpande. Created by Gabriel Vila on 7/28/19.
     */
    private void updateDataSum() {

        String action = "";

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            sql = " select distinct c_elementvalue_id " +
                    " from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID;

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();

            while(rs.next()){

                BigDecimal saldoInicial1 = Env.ZERO, saldoInicial2 = Env.ZERO;
                int cElementValueID = rs.getInt("c_elementvalue_id");

                // Obtengo saldo inicial en moneda uno de consulta para esta cuenta contable
                saldoInicial1 = this.getSaldoInicial(cElementValueID, this.cCurrencyID);
                if (saldoInicial1 == null) saldoInicial1 = Env.ZERO;

                // Si corresponde obtengo saldo inicial en moneda dos de consulta
                if (this.cCurrencyID_2 > 0){
                    saldoInicial2 = this.getSaldoInicial(cElementValueID, this.cCurrencyID_2);
                    if (saldoInicial2 == null) saldoInicial2 = Env.ZERO;
                }

                action = " update " + TABLA_REPORTE + " set AmtInicial1 =" + saldoInicial1 + ", " +
                        " AmtInicial2 =" + saldoInicial2 + ", " +
                        " AmtSubtotal1 = (AmtDr1 - AmtCr1), " +
                        " AmtTotal1 = (" + saldoInicial1 + " + AmtDr1 - AmtCr1), " +
                        " AmtSubtotal2 = (AmtDr2 - AmtCr2), " +
                        " AmtTotal2 = (" + saldoInicial2 + " + AmtDr2 - AmtCr2) " +
                        " where ad_user_id =" + this.adUserID +
                        " and c_elementvalue_id =" + cElementValueID;
                DB.executeUpdateEx(action, null);
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

            // Armo condiciones según filtros
            String whereClause = "";
            if (this.startDate != null){
                whereClause += " and f.dateacct <'" + this.startDate + "'" ;
            }

            // Socio de Negocio
            if (this.cBPartnerID > 0){
                whereClause += " and f.c_bpartner_id =" + this.cBPartnerID;
            }

            // Producto
            if (this.mProductID > 0){
                whereClause += " and f.m_product_id =" + this.cBPartnerID;
            }

            // Centro de Costos
            if (this.cActivityID > 0){
                whereClause += " and f.c_activity_id =" + this.cBPartnerID;
            }

            BigDecimal amtSchemaCurrency = Env.ZERO, amtNotSchemaCurrency = Env.ZERO;

            // Si la moneda uno es igual a la moneda del esquema contable
            if (cCurrencyID == acctSchema.getC_Currency_ID()){
                sql = " select sum(f.amtacctdr - f.amtacctcr) " +
                        " from fact_acct f " +
                        " where f.ad_client_id =" + this.adClientID +
                        " and f.ad_org_id =" + this.adOrgID +
                        " and f.c_acctschema_id =" + this.cAcctSchemaID +
                        " and f.account_id =" + cElementValueID + whereClause;

                amtSchemaCurrency = DB.getSQLValueBDEx(null, sql);
                if (amtSchemaCurrency == null) amtSchemaCurrency = Env.ZERO;
            }
            else{

                sql = " select sum(f.amtsourcedr - f.amtsourcecr) " +
                        " from fact_acct f " +
                        " where f.ad_client_id =" + this.adClientID +
                        " and f.ad_org_id =" + this.adOrgID +
                        " and f.c_acctschema_id =" + this.cAcctSchemaID +
                        " and f.c_currency_id =" + cCurrencyID +
                        " and f.account_id =" + cElementValueID + whereClause;

                amtNotSchemaCurrency = DB.getSQLValueBDEx(null, sql);
                if (amtNotSchemaCurrency == null) amtNotSchemaCurrency = Env.ZERO;

                sql = " select sum(round(((f.amtsourcedr - f.amtsourcecr) * currencyrate(c_currency_id," + cCurrencyID + ", dateacct, 114, ad_client_id, ad_org_id)),2)) " +
                        " from fact_acct f " +
                        " where f.ad_client_id =" + this.adClientID +
                        " and f.ad_org_id =" + this.adOrgID +
                        " and f.c_acctschema_id =" + this.cAcctSchemaID +
                        " and f.c_currency_id !=" + cCurrencyID +
                        " and f.account_id =" + cElementValueID + whereClause;

                BigDecimal amtAux = DB.getSQLValueBDEx(null, sql);
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

}
