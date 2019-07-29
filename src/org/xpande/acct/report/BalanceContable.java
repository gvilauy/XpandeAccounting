package org.xpande.acct.report;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.core.utils.CurrencyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

/**
 * Lógica del reporte de Balance Contable.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 7/19/19.
 */
public class BalanceContable {

    private final String TABLA_REPORTE = "Z_RP_BalanceContable";

    public int adClientID = 0;
    public int adOrgID = 0;
    public int adUserID = 0;
    public int cAcctSchemaID = 0;
    public int cCurrencyID = 0;
    public int cCurrencyID_2 = 0;
    public String tipoFiltroMonAcct = "";
    public String tipoBalanceAcct = "";
    public String incCtaSaldoSinMov = "N";
    public Timestamp startDate = null;
    public Timestamp endDate = null;
    public boolean mostrarSinSaldo = false;

    private Properties ctx = null;
    private String trxName = null;
    private BigDecimal currencyRate = Env.ONE;
    private MAcctSchema acctSchema = null;


    /***
     * Constructor
     */
    public BalanceContable(Properties ctx, String trxName) {

        this.ctx = ctx;
        this.trxName = trxName;
    }

    /***
     * Método que ejecuta la lógica del reporte.
     * Xpande. Created by Gabriel Vila on 7/19/19.
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

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    /***
     * Obtiene información inicial y carga la misma en la Tabla del Reporte.
     * Xpande. Created by Gabriel Vila on 7/19/19.
     */
    private void getData() {

        String action = "", sql = "";

        try{
            // Inserto en table de reporte el árbol de todas las cuentas contables según opción de Tipo Balance.
            action = " insert into " + TABLA_REPORTE + " (ad_client_id, ad_org_id, ad_user_id, c_elementvalue_id, c_currency_id, " +
                    " codigocuenta, nombrecuenta, issummary, accounttype, nrocapituloacct, nomcapituloacct, " +
                    " parent_id, node_id, seqno, nrofila, " +
                    " amttotal1, amttotal2, c_acctschema_id, tipobalanceacct, dateacct, tipofiltromonacct, incctasaldosinmov) ";

            String whereClause = "";

            // Filtro según tipo de reporte de balance
            if (this.tipoBalanceAcct.equalsIgnoreCase("PATRIMONIAL")){
                whereClause = " and f.accounttype in ('A', 'L', 'O') ";
            }
            else if (this.tipoBalanceAcct.equalsIgnoreCase("RESULTADO")){
                whereClause = " and f.accounttype in ('R', 'E') ";
            }

            sql = " select " + this.adClientID + ", " + this.adOrgID + ", " + this.adUserID + ", f.c_elementvalue_id, " + this.cCurrencyID + ", " +
                    " f.value, f.name, f.issummary, f.accounttype, " +
                    " case when f.accounttype='A' then '1' else " +
                    " case when f.accounttype='E' then '5' else " +
                    " case when f.accounttype='L' then '2' else " +
                    " case when f.accounttype='O' then '3' else " +
                    " case when f.accounttype='R' then '4' else '9' end end end end end as nrocapituloacct, " +
                    " case when f.accounttype='A' then 'ACTIVO' else " +
                    " case when f.accounttype='E' then 'GASTOS' else " +
                    " case when f.accounttype='L' then 'PASIVO' else " +
                    " case when f.accounttype='O' then 'PATRIMONIO' else " +
                    " case when f.accounttype='R' then 'GANANCIAS' else 'OTROS' end end end end end as nomcapituloacct, " +
                    " f.parent_id, f.node_id, f.seqno, f.nrofila, 0, 0, " + this.cAcctSchemaID + ", '" + this.tipoBalanceAcct + "', '" +
                    this.endDate + "', '" + this.tipoFiltroMonAcct + "', '" + this.incCtaSaldoSinMov + "' " +
                    " from ZV_ElementValueTree f " +
                    " where f.ad_client_id =" + this.adClientID +
                    " and f.c_acctschema_id =" + this.cAcctSchemaID + whereClause +
                    " order by f.nrofila ";
            DB.executeUpdateEx(action + sql, null);

            // Actualizo Moneda 2 si es que tengo
            if (this.cCurrencyID_2 > 0){
                action = " update " + TABLA_REPORTE + " set c_currency_2_id =" + this.cCurrencyID_2 +
                        " where ad_user_id =" + this.adUserID;
                DB.executeUpdateEx(action, null);
            }
        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
    }

    /***
     * Actualiza información en la Tabla del Reporte.
     * Xpande. Created by Gabriel Vila on 7/19/19.
     */
    private void updateData() {

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
     * Actualiza saldos de cuentas no totalizadoras.
     * Xpande. Created by Gabriel Vila on 7/19/19.
     */
    private void updateDataBalanceNotSummary() {

        String sql = "", action = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            // Cuentas de diferencia de cambio
            MAccount acctDifCambioGanada = MAccount.get(this.ctx, acctSchema.getAcctSchemaDefault().get_ValueAsInt("DC_Ganada_Acct"));
            MAccount acctDifCambioPerdida = MAccount.get(this.ctx, acctSchema.getAcctSchemaDefault().get_ValueAsInt("DC_Perdida_Acct"));


            // Armo condiciones según filtros
            String whereClause = " and f.ad_client_id =" + this.adClientID +
                    " and f.ad_org_id =" + this.adOrgID;

            if (this.startDate != null){
                whereClause += " and f.dateacct >='" + this.startDate + "'" ;
            }
            if (this.endDate != null){
                whereClause += " and f.dateacct <='" + this.endDate + "'" ;
            }
            if (this.cCurrencyID_2 > 0){
                whereClause += " and f.c_currency_id in (" + this.cCurrencyID + ", " + this.cCurrencyID_2 + ") ";
            }
            else {
                // Si tengo una sola moneda y la misma no es moneda nacional, filtro que traiga movimientos en moneda nacional y en moneda extranjera seleccionaca.
                if (this.cCurrencyID != acctSchema.getC_Currency_ID()){
                    whereClause += " and f.c_currency_id in (" + this.cCurrencyID + ", " + this.acctSchema.getC_Currency_ID() + ") ";
                }
            }

            sql = " select f.account_id, f.c_currency_id, sum(f.amtsourcedr - f.amtsourcecr) as saldomo, " +
                    " sum(f.amtacctdr - f.amtacctcr) as saldomn " +
                    " from fact_acct f " +
                    " inner join " + TABLA_REPORTE + " b on (f.account_id = b.c_elementvalue_id " +
                    " and b.ad_user_id =" + this.adUserID + ") " +
                    " where b.issummary ='N' " + whereClause +
                    " group by f.account_id, f.c_currency_id " +
                    " order by f.account_id, f.c_currency_id ";

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();

            int accountIDAux = -1;
            BigDecimal amtCurrency1 = Env.ZERO, amtCurrency2 = Env.ZERO;

            while(rs.next()){


                // Corte por cuenta contable
                if (rs.getInt("account_id") != accountIDAux){

                    if (accountIDAux > 0){

                        // Actualizo saldo de cuenta
                        action  = " update " + TABLA_REPORTE + " set amttotal1 =" + amtCurrency1 + ", " +
                                " amttotal2 =" + amtCurrency2 +
                                " where ad_user_id =" + this.adUserID +
                                " and c_elementvalue_id =" + accountIDAux;
                        DB.executeUpdateEx(action, null);
                    }

                    accountIDAux = rs.getInt("account_id");
                    amtCurrency1 = Env.ZERO;
                    amtCurrency2 = Env.ZERO;
                }

                BigDecimal saldoMO = rs.getBigDecimal("saldomo");
                BigDecimal saldoMN = rs.getBigDecimal("saldomn");
                int cCurrencyID = rs.getInt("c_currency_id");

                if (saldoMO == null) saldoMO = Env.ZERO;
                if (saldoMN == null) saldoMN = Env.ZERO;

                // Actulizo columna de monto por moneda del reporte
                if (this.cCurrencyID == acctSchema.getC_Currency_ID()){
                    amtCurrency1 = amtCurrency1.add(saldoMN);

                    // Traducir a segunda moneda, si moneda leída es moneda nacional y no es una cuenta de diferencia de cambio
                    if (this.cCurrencyID_2 > 0){
                        if (cCurrencyID == acctSchema.getC_Currency_ID()){
                            if ((acctDifCambioGanada != null) && (acctDifCambioPerdida != null)){
                                if ((accountIDAux != acctDifCambioGanada.getAccount_ID()) && (accountIDAux != acctDifCambioPerdida.getAccount_ID())){
                                    BigDecimal amtConverted = saldoMN.divide(this.currencyRate, 2, RoundingMode.HALF_UP);
                                    amtCurrency2 = amtCurrency2.add(amtConverted);
                                }
                            }
                        }
                    }
                }
                else{
                    if (this.cCurrencyID == cCurrencyID){
                        amtCurrency1 = amtCurrency1.add(saldoMO);
                    }
                    else{
                        // Si no tengo segunda moneda, traduzco a la primera
                        if (this.cCurrencyID_2 <= 0){
                            if ((acctDifCambioGanada != null) && (acctDifCambioPerdida != null)){
                                if ((accountIDAux != acctDifCambioGanada.getAccount_ID()) && (accountIDAux != acctDifCambioPerdida.getAccount_ID())){
                                    BigDecimal amtConverted = saldoMN.divide(this.currencyRate, 2, RoundingMode.HALF_UP);
                                    amtCurrency1 = amtCurrency1.add(amtConverted);
                                }
                            }
                        }
                    }
                }

                if (this.cCurrencyID_2 > 0){
                    if (this.cCurrencyID_2 == acctSchema.getC_Currency_ID()){
                        amtCurrency2 = amtCurrency2.add(saldoMN);

                        // Traducir a segunda moneda, si moneda leída es moneda nacional y no es una cuenta de diferencia de cambio
                        if (cCurrencyID == acctSchema.getC_Currency_ID()){
                            if ((acctDifCambioGanada != null) && (acctDifCambioPerdida != null)){
                                if ((accountIDAux != acctDifCambioGanada.getAccount_ID()) && (accountIDAux != acctDifCambioPerdida.getAccount_ID())){
                                    BigDecimal amtConverted = saldoMN.divide(this.currencyRate, 2, RoundingMode.HALF_UP);
                                    amtCurrency1 = amtCurrency1.add(amtConverted);
                                }
                            }
                        }
                    }
                    else{
                        if (this.cCurrencyID_2 == cCurrencyID){
                            amtCurrency2 = amtCurrency2.add(saldoMO);
                        }
                    }
                }
            }

            if (accountIDAux > 0){

                // Actualizo saldo de cuenta
                action  = " update " + TABLA_REPORTE + " set amttotal1 =" + amtCurrency1 + ", " +
                        " amttotal2 =" + amtCurrency2 +
                        " where ad_user_id =" + this.adUserID +
                        " and c_elementvalue_id =" + accountIDAux;
                DB.executeUpdateEx(action, null);
            }


            // Finalmente elimino cuentas sin saldo si asi lo desea el usuario
            if (!this.mostrarSinSaldo){
                action = " delete from " + TABLA_REPORTE +
                        " where amttotal1 = 0 and amttotal2 = 0 " +
                        " and ad_user_id =" + this.adUserID +
                        " and issummary='N'";
                DB.executeUpdateEx(action, null);
            }

            // Dejo en nulo importe en moneda 2 si no tengo moneda 2 seleccionada.
            if (this.cCurrencyID_2 <= 0){
                action = " update " + TABLA_REPORTE +
                        " set amttotal2 = null " +
                        " and ad_user_id =" + this.adUserID +
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
     * Actualiza saldos de cuentas totalizadoras en consulta de Balance Contable.
     * Xpande. Created by Gabriel Vila on 3/12/19.
     */
    private void updateDataBalanceSummary() {

        String sql = "", action = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            sql = " select c_elementvalue_id " +
                    " from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID +
                    " and parent_id = 0" +
                    " order by nrofila ";

            pstmt = DB.prepareStatement(sql, null);
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

        String sql = "", action = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            // Actualizo primero nivel de la cuenta totalizadora recibida
            action = " update " + TABLA_REPORTE +
                    " set nivelcuenta =" + nivel +
                    " where ad_user_id =" + this.adUserID +
                    " and c_elementvalue_id =" + cElementValueID;
            DB.executeUpdateEx(action, null);

            // Subo nivel
            nivel++;

            // Obtengo cuentas hijas de la cuenta totalizadora recibida.
            // Dentro de sus hijas pueden haber cuentas a su vez totalizadoras, por eso la recursividad.
            sql = " select c_elementvalue_id, IsSummary " +
                    " from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID +
                    " and parent_id =" + cElementValueID +
                    " order by nrofila ";

            pstmt = DB.prepareStatement(sql, null);
            rs = pstmt.executeQuery();

            while(rs.next()){

                // Si la cuenta es totalizadora, sigo la recursividad
                if (rs.getString("IsSummary").equalsIgnoreCase("Y")){

                    this.updateDataBalRecursive(rs.getInt("c_elementvalue_id"), nivel);
                }
                else{
                    // Cuenta no totalizadora, le actualizo simplemente el nivel
                    // Actualizo primero nivel de la cuenta totalizadora recibida
                    action = " update " + TABLA_REPORTE +
                            " set nivelcuenta =" + nivel +
                            " where ad_user_id =" + this.adUserID +
                            " and c_elementvalue_id =" + rs.getInt("c_elementvalue_id");
                    DB.executeUpdateEx(action, null);
                }
            }

            // Actualizo saldos de cuenta totalizadora recibida
            sql = " select sum(amttotal1) as saldo " +
                    " from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID +
                    " and parent_id =" + cElementValueID;
            BigDecimal saldo1 = DB.getSQLValueBDEx(null, sql);
            if (saldo1 == null) saldo1 = Env.ZERO;

            sql = " select sum(amttotal2) as saldo " +
                    " from " + TABLA_REPORTE +
                    " where ad_user_id =" + this.adUserID +
                    " and parent_id =" + cElementValueID;
            BigDecimal saldo2 = DB.getSQLValueBDEx(null, sql);
            if (saldo2 == null) saldo2 = Env.ZERO;

            action = " update " + TABLA_REPORTE +
                    " set amttotal1 =" + saldo1 + ", amttotal2 =" + saldo2 +
                    " where ad_user_id =" + this.adUserID +
                    " and c_elementvalue_id =" + cElementValueID;
            DB.executeUpdateEx(action, null);

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
