package org.xpande.acct.report;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.I_Z_DifCambio;
import org.xpande.acct.model.MZAcctBrowser;
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
    public boolean consideraSaldoInicial = true;
    public String incSaldoInicial = "Y";

    public boolean isCierreDiferencial = true;
    public boolean isCierreIntegral = true;
    public String incCierreDiferencial = "Y";
    public String incCierreIntegral = "Y";

    private Properties ctx = null;
    private String trxName = null;
    private BigDecimal currencyRate = Env.ONE;
    private MAcctSchema acctSchema = null;

    public int zAcctBrowserID = 0;

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

        return null;
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
            // Si viene desde el navegador contable, tomo los filtros de cuenta desde ahí
            if (this.zAcctBrowserID > 0){
                sql = " select count(*) from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.zAcctBrowserID;
                int contadorCta = DB.getSQLValueEx(null, sql);
                if (contadorCta > 0) {
                    whereClause += " and f.account_id in (select distinct(c_elementvalue_id) " +
                            " from Z_AcctBrowFiltroCta where Z_AcctBrowser_ID =" + this.zAcctBrowserID + ") ";
                }
            }
            else{
                // Si tengo cuentas contables para filtrar, agrego condición
                if ((this.textoFiltroCuentas != null) && (!this.textoFiltroCuentas.trim().equalsIgnoreCase(""))){
                    this.setCuentasFiltro();
                    whereClause += " and f.account_id in (select distinct(c_elementvalue_id) " +
                            " from " + TABLA_FILTRO_CTA + " where ad_user_id =" + this.adUserID + ") ";
                }
            }

            // Si vengo desde el Navegador Contable aplco los filtros del navegador
            if (this.zAcctBrowserID > 0){
                MZAcctBrowser acctBrowser = new MZAcctBrowser(this.ctx, this.zAcctBrowserID, null);
                whereClause += acctBrowser.getFiltrosMayor().toString();
            }
            else{
                if (this.cBPartnerID > 0){
                    whereClause += " and f.c_bpartner_id =" + this.cBPartnerID;
                }
                if (this.mProductID > 0){
                    whereClause += " and f.m_product_id =" + this.cBPartnerID;
                }
                if (this.cActivityID > 0){
                    whereClause += " and f.c_activity_id =" + this.cBPartnerID;
                }
            }

            // Considerar cierre de cuentas diferenciales
            if ((!this.isCierreDiferencial) && (!this.isCierreIntegral)){
                whereClause += " and doc.docbasetype not in ('CJD','CJI') ";
            }
            else{
                if (!this.isCierreDiferencial){
                    whereClause += " and doc.docbasetype <>'CJD' ";
                }
                else{
                    if (!this.isCierreIntegral){
                        whereClause += " and doc.docbasetype <>'CJI' ";
                    }
                }
            }

            // Inserto en tabla de reporte
            action = " insert into " + TABLA_REPORTE + " (ad_client_id, ad_org_id, ad_user_id, fact_acct_id, created, createdby, " +
                    " ad_table_id, record_id, c_elementvalue_id, c_currency_id, amtsourcedr, amtsourcecr, amtacctdr, amtacctcr, " +
                    " c_period_id, dateacct, datedoc, description, c_bpartner_id, m_product_id, m_product_category_id, producttype, " +
                    " c_tax_id, qty, taxid, c_bp_group_id, accounttype, " +
                    " c_doctype_id, documentnoref, currencyrate, duedate, estadomediopago, nromediopago, z_mediopago_id, z_retencionsocio_id, " +
                    " codigoretencion, " +
                    " c_activity_id, codigocuenta, nombrecuenta, C_AcctSchema_ID, TextoFiltro, TipoFiltroMonAcct, " +
                    " C_Currency_1_ID, C_Currency_2_ID, IncSaldoInicial, IsCierreDiferencial, IsCierreIntegral) ";

            sql = " select f.ad_client_id, f.ad_org_id, " + this.adUserID + ", f.fact_acct_id, f.created, f.createdby, f.ad_table_id, " +
                    " f.record_id, f.account_id, f.c_currency_id, " +
                    " f.amtsourcedr, f.amtsourcecr, f.amtacctdr, f.amtacctcr, " +
                    " f.c_period_id, f.dateacct, f.datetrx, f.description, f.c_bpartner_id, f.m_product_id, " +
                    " prod.m_product_category_id, prod.producttype, " +
                    " coalesce(det.c_tax_id, f.c_tax_id) as c_tax_id, f.qty, bp.taxid, bp.c_bp_group_id, ev.accounttype, " +
                    " f.c_doctype_id, f.documentnoref, " +
                    " f.currencyrate, f.duedate, det.estadomediopago, det.nromediopago, det.z_mediopago_id, det.z_retencionsocio_id, " +
                    " ret.codigodgi, " +
                    " f.c_activity_id, ev.value, ev.name, " + this.cAcctSchemaID + ",'" + this.textoFiltroCuentas + "','" +
                    this.tipoFiltroMonAcct + "', " + this.cCurrencyID + ", " + this.cCurrencyID_2 + ",'" +
                    this.incSaldoInicial + "', '" + this.incCierreDiferencial + "', '" + this.incCierreIntegral + "' " +
                    " from fact_acct f " +
                    " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                    " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                    " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                    " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                    " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                    " left outer join z_retencionsocio ret on det.z_retencionsocio_id = ret.z_retencionsocio_id " +
                    " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
                    " where f.ad_client_id =" + this.adClientID +
                    " and f.ad_org_id =" + this.adOrgID +
                    " and f.c_acctschema_id =" + this.cAcctSchemaID + whereClause;
                    //" order by f.account_id, f.dateacct ";
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

                    // Actualizo importes para asientos con distinta moneda de asiento, considerando tipo de cambio del día del asiento.
                    // No considerar asientos contables hecho en el proceso de diferencia de cambio para esta traduccion.
                    action = " update " + TABLA_REPORTE + " set " +
                            " amtdr2 = round((amtsourcedr * z_currencyrate(c_currency_id, " + this.cCurrencyID_2 + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " amtcr2 = round((amtsourcecr * z_currencyrate(c_currency_id, " + this.cCurrencyID_2 + ", dateacct, 114, ad_client_id, ad_org_id)), 2), " +
                            " c_currency_2_id =" + this.cCurrencyID_2 +
                            " where ad_user_id =" + this.adUserID +
                            " and ad_table_id !=" + I_Z_DifCambio.Table_ID +
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

        try{

            if ((this.textoFiltroCuentas == null) || (this.textoFiltroCuentas.trim().equalsIgnoreCase(""))){
                return null;
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

        return null;
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
            sql = " select c_elementvalue_id, codigocuenta, dateacct, fact_acct_id, c_doctype_id, c_tax_id, " +
                    " coalesce(amtdr1,0) as amtdr1, coalesce(amtcr1,0) as amtcr1, " +
                    " coalesce(amtdr2,0) as amtdr2, coalesce(amtcr2,0) as amtcr2 " +
                    " from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID +
                    " order by codigocuenta, dateacct, fact_acct_id ";

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();

            int cElementValueID = 0;
            BigDecimal saldoInicial1 = Env.ZERO, saldoInicial2 = Env.ZERO;
            BigDecimal amtAcumulado1 = Env.ZERO, amtAcumulado2 = Env.ZERO;

            while(rs.next()){

                if (cElementValueID != rs.getInt("c_elementvalue_id")){

                    cElementValueID = rs.getInt("c_elementvalue_id");

                    if (this.consideraSaldoInicial){

                        saldoInicial1 = Env.ZERO;
                        saldoInicial2 = Env.ZERO;

                        // Obtengo saldo inicial en moneda uno de consulta para esta cuenta contable
                        saldoInicial1 = this.getSaldoInicial(cElementValueID, this.cCurrencyID);
                        if (saldoInicial1 == null) saldoInicial1 = Env.ZERO;

                        // Si corresponde obtengo saldo inicial en moneda dos de consulta
                        if (this.cCurrencyID_2 > 0){
                            saldoInicial2 = this.getSaldoInicial(cElementValueID, this.cCurrencyID_2);
                            if (saldoInicial2 == null) saldoInicial2 = Env.ZERO;
                        }

                        // Actualizo saldos iniciales en ambas monedas para esta cuenta en tabla del reporte
                        action = " update " + TABLA_REPORTE + " set AmtInicial1 =" + saldoInicial1 + ", " +
                                " AmtInicial2 =" + saldoInicial2 +
                                " where ad_user_id =" + this.adUserID +
                                " and c_elementvalue_id =" + cElementValueID;
                        DB.executeUpdateEx(action, null);

                        // Saldo acumulado comienza con saldo inicial
                        amtAcumulado1 = saldoInicial1;
                        amtAcumulado2 = saldoInicial2;
                    }
                    else{
                        // Saldo acumulado comienza en cero al no considerarse saldo inicial
                        amtAcumulado1 = Env.ZERO;
                        amtAcumulado2 = Env.ZERO;
                    }
                }

                // Obtengo saldo acumulado en ambas monedas
                if (rs.getBigDecimal("amtdr1").compareTo(Env.ZERO) != 0){
                    amtAcumulado1 = amtAcumulado1.add(rs.getBigDecimal("amtdr1"));
                }
                if (rs.getBigDecimal("amtcr1").compareTo(Env.ZERO) != 0){
                    amtAcumulado1 = amtAcumulado1.subtract(rs.getBigDecimal("amtcr1"));
                }
                if (rs.getBigDecimal("amtdr2").compareTo(Env.ZERO) != 0){
                    amtAcumulado2 = amtAcumulado2.add(rs.getBigDecimal("amtdr2"));
                }
                if (rs.getBigDecimal("amtcr2").compareTo(Env.ZERO) != 0){
                    amtAcumulado2 = amtAcumulado2.subtract(rs.getBigDecimal("amtcr2"));
                }

                // Obetngo Rubro de DGI para impuesto si es que tengo uno.
                // Como un impuesto puede estar en un rubro de compra y uno de venta al mismo tiempo,
                // debo considerar si el documento que originó este asiento contable es de venta o de compra.
                int cDocTypeID = rs.getInt("c_doctype_id");
                int cTaxID = rs.getInt("c_tax_id");
                if (cTaxID > 0){
                    if (cDocTypeID > 0){
                        sql = " select issotrx from c_doctype where c_doctype_id =" + cDocTypeID;
                        String isSOTrx = DB.getSQLValueStringEx(null, sql);
                        if (isSOTrx != null){
                            // Actualizo info de rubro impositiva para este asiento
                            this.updateRubroIVA(rs.getInt("fact_acct_id"), cTaxID, isSOTrx);
                        }
                    }
                }

                // Actualizo montos acumulados en tabla del reporte
                action = " update " + TABLA_REPORTE + " set AmtAcumulado1 =" + amtAcumulado1 + ", " +
                        " AmtAcumulado2 =" + amtAcumulado2 + ", " +
                        " AmtSubtotal1 = (AmtDr1 - AmtCr1), " +
                        " AmtTotal1 = (AmtInicial1 + AmtDr1 - AmtCr1), " +
                        " AmtSubtotal2 = (AmtDr2 - AmtCr2), " +
                        " AmtTotal2 = (AmtInicial2 + AmtDr2 - AmtCr2) " +
                        " where ad_user_id =" + this.adUserID +
                        " and fact_acct_id =" + rs.getInt("fact_acct_id");
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

    private void updateRubroIVA(int factAcctID, int cTaxID, String isSOTrx) {

        String sql, action;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            sql = " select b.value, b.name " +
                    "from z_rubrodgitax a " +
                    "inner join z_acctconfigrubrodgi b on a.z_acctconfigrubrodgi_id = b.z_acctconfigrubrodgi_id " +
                    "where a.c_tax_id =" + cTaxID +
                    "and b.issotrx ='" + isSOTrx + "' ";

        	pstmt = DB.prepareStatement(sql, this.trxName);
        	rs = pstmt.executeQuery();

        	if (rs.next()){

                action = " update " + TABLA_REPORTE + " set CodigoRubroIVA ='" + rs.getString("value") + "', " +
                        " NomRubroIVA ='" + rs.getString("name") + "' " +
                        " where ad_user_id =" + this.adUserID +
                        " and fact_acct_id =" + factAcctID;
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

            // Si vengo desde el Navegador Contable aplco los filtros del navegador
            if (this.zAcctBrowserID > 0){
                MZAcctBrowser acctBrowser = new MZAcctBrowser(this.ctx, this.zAcctBrowserID, null);
                whereClause += acctBrowser.getFiltrosMayor().toString();
            }
            else{
                if (this.cBPartnerID > 0){
                    whereClause += " and f.c_bpartner_id =" + this.cBPartnerID;
                }
                if (this.mProductID > 0){
                    whereClause += " and f.m_product_id =" + this.cBPartnerID;
                }
                if (this.cActivityID > 0){
                    whereClause += " and f.c_activity_id =" + this.cBPartnerID;
                }
            }

            // Considerar cierre de cuentas diferenciales
            if ((!this.isCierreDiferencial) && (!this.isCierreIntegral)){
                whereClause += " and doc.docbasetype not in ('CJD','CJI') ";
            }
            else{
                if (!this.isCierreDiferencial){
                    whereClause += " and doc.docbasetype <>'CJD' ";
                }
                else{
                    if (!this.isCierreIntegral){
                        whereClause += " and doc.docbasetype <>'CJI' ";
                    }
                }
            }

            BigDecimal amtSchemaCurrency = Env.ZERO, amtNotSchemaCurrency = Env.ZERO;

            // Si la moneda uno es igual a la moneda del esquema contable
            if (cCurrencyID == acctSchema.getC_Currency_ID()){
                sql = " select sum(f.amtacctdr - f.amtacctcr) " +
                        " from fact_acct f " +
                        " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                        " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                        " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                        " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                        " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                        " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
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
                        " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                        " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                        " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                        " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                        " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                        " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
                        " where f.ad_client_id =" + this.adClientID +
                        " and f.ad_org_id =" + this.adOrgID +
                        " and f.c_acctschema_id =" + this.cAcctSchemaID +
                        " and f.c_currency_id =" + cCurrencyID +
                        " and f.account_id =" + cElementValueID + whereClause;

                amtNotSchemaCurrency = DB.getSQLValueBDEx(null, sql);
                if (amtNotSchemaCurrency == null) amtNotSchemaCurrency = Env.ZERO;

                sql = " select sum(round(((f.amtsourcedr - f.amtsourcecr) / currencyrate(" + cCurrencyID + ", f.c_currency_id, f.dateacct, 114, f.ad_client_id, f.ad_org_id)),2)) " +
                        " from fact_acct f " +
                        " inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
                        " left outer join c_bpartner bp on f.c_bpartner_id = bp.c_bpartner_id " +
                        " left outer join m_product prod on f.m_product_id = prod.m_product_id " +
                        " left outer join z_acctfactdet det on f.fact_acct_id = det.fact_acct_id " +
                        " left outer join z_mediopagoitem mpi on det.z_mediopagoitem_id = mpi.z_mediopagoitem_id " +
                        " left outer join c_doctype doc on f.c_doctype_id = doc.c_doctype_id " +
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
