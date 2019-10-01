package org.compiere.acct;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctConfig;
import org.xpande.retail.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 10/1/19.
 */
public class Doc_AstoVtaRecMP extends Doc {

    private MZAstoVtaRecMP astoVtaRecMP = null;
    private MDocType docType = null;

    /**
     *  Constructor
     */
    public Doc_AstoVtaRecMP(MAcctSchema[] ass, Class<?> clazz, ResultSet rs, String defaultDocumentType, String trxName) {
        super(ass, clazz, rs, defaultDocumentType, trxName);
    }

    /**
     *  Constructor
     */
    public Doc_AstoVtaRecMP(MAcctSchema[] ass, ResultSet rs, String trxName)
    {
        super (ass, MZAstoVtaRecMP.class, rs, null, trxName);
    }


    @Override
    protected String loadDocumentDetails() {

        this.astoVtaRecMP = (MZAstoVtaRecMP) getPO();
        setDateDoc(this.astoVtaRecMP.getDateDoc());
        setDateAcct(this.astoVtaRecMP.getDateAcct());

        this.docType = (MDocType) this.astoVtaRecMP.getC_DocType();
        setDocumentType(docType.getDocBaseType());

        return null;

    }

    @Override
    public BigDecimal getBalance() {
        return Env.ZERO;
    }

    @Override
    public ArrayList<Fact> createFacts(MAcctSchema as) {

        ArrayList<Fact> facts = new ArrayList<Fact>();
        Fact fact = new Fact(this, as, Fact.POST_Actual);


        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            MZPosVendor posVendor = (MZPosVendor) this.astoVtaRecMP.getZ_PosVendor();

            MZRetailConfig retailConfig = MZRetailConfig.getDefault(getCtx(), null);
            if ((retailConfig == null) || (retailConfig.get_ID() <= 0)){
                p_Error = "Falta parametrización en Configuracion de Retail";
                log.log(Level.SEVERE, p_Error);
                fact = null;
                facts.add(fact);
                return facts;
            }

            // Obtengo lineas de detalle de medios de pago modificadas cuando tengo Pos Sisteco
            if (posVendor.getValue().equalsIgnoreCase("SISTECO")){

                List<MZAstoVtaRecMPLinST> astoVtaRecMPLinSTList = this.astoVtaRecMP.getLinesSisteco();
                for (MZAstoVtaRecMPLinST astoVtaRecMPLinST: astoVtaRecMPLinSTList){

                    int cCurrencyID = as.getC_Currency_ID();
                    if (astoVtaRecMPLinST.getST_CodigoMoneda().equalsIgnoreCase("DOLARES")){
                        cCurrencyID = 100;
                    }

                    // Si esta linea tiene modificada la tarjeta, hago asiento
                    if (astoVtaRecMPLinST.getZ_SistecoTipoTarjeta_ID() > 0){

                        // DR - Cuenta contable asociada a la tarjeta de sisteco. Importe de este linea de detalle.
                        sql = " select c_receivable_acct, c_bpartner_id, m_product_id " +
                                " from z_sistecotarjeta_acct " +
                                " where c_acctschema_id =" + as.get_ID() +
                                " and z_sistecotipotarjeta_id =" + astoVtaRecMPLinST.getZ_SistecoTipoTarjeta_ID() +
                                " and c_currency_id =" + cCurrencyID;
                        int accountID = DB.getSQLValueEx(null, sql);
                        if (accountID <= 0){
                            p_Error = "Falta indicar cuenta contable para Tipo de Tarjeta con ID: " + astoVtaRecMPLinST.getZ_SistecoTipoTarjeta_ID();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                        FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, astoVtaRecMPLinST.getTotalAmt(), null);
                        if (fl1 != null){
                            fl1.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                            if (rs.getInt("c_bpartner_id") > 0){
                                fl1.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                            }
                            if (rs.getInt("m_product_id") > 0){
                                fl1.setM_Product_ID(rs.getInt("m_product_id"));
                            }
                        }

                        // CR - Cuenta contable asociada al tipo de linea de sisteco. Importe de este linea de detalle.
                        accountID = -1;
                        sql = " select a.c_receivable_acct, c_bpartner_id, m_product_id " +
                                " from z_sistecolinea_acct a " +
                                " inner join z_sistecotipolineapazos b on a.z_sistecotipolineapazos_id = b.z_sistecotipolineapazos_id " +
                                " where b.value ='" + astoVtaRecMPLinST.getST_TipoLinea() + "' " +
                                " and a.c_acctschema_id =" + as.get_ID() +
                                " and c_currency_id =" + cCurrencyID;

                        accountID = DB.getSQLValueEx(null, sql);
                        if (accountID <= 0){
                            p_Error = "Falta indicar cuenta contable para Tipo de Linea : " + astoVtaRecMPLinST.getST_TipoLinea();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                        FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, astoVtaRecMPLinST.getTotalAmt());
                        if (fl2 != null){
                            fl2.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                            if (rs.getInt("c_bpartner_id") > 0){
                                fl1.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                            }
                            if (rs.getInt("m_product_id") > 0){
                                fl1.setM_Product_ID(rs.getInt("m_product_id"));
                            }
                        }

                    }
                    else{
                        // Si no tiene modificada la tarjeta, pero tiene modificado el medio de pago, hago asiento
                        if (astoVtaRecMPLinST.getZ_SistecoMedioPago_ID() > 0){

                            // DR - Cuenta contable asociada a la tarjeta de sisteco. Importe de este linea de detalle.
                            sql = " select c_receivable_acct, c_bpartner_id, m_product_id " +
                                    " from z_sistecompago_acct " +
                                    " where c_acctschema_id =" + as.get_ID() +
                                    " and z_sistecomediopago_id =" + astoVtaRecMPLinST.getZ_SistecoMedioPago_ID() +
                                    " and c_currency_id =" + cCurrencyID;

                            int accountID = DB.getSQLValueEx(null, sql);
                            if (accountID <= 0){
                                p_Error = "Falta indicar cuenta contable para Medio de Pago con ID: " + astoVtaRecMPLinST.getZ_SistecoTipoTarjeta_ID();
                                log.log(Level.SEVERE, p_Error);
                                fact = null;
                                facts.add(fact);
                                return facts;
                            }
                            FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, astoVtaRecMPLinST.getTotalAmt(), null);
                            if (fl1 != null){
                                fl1.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                                if (rs.getInt("c_bpartner_id") > 0){
                                    fl1.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    fl1.setM_Product_ID(rs.getInt("m_product_id"));
                                }
                            }

                            // CR - Cuenta contable asociada al tipo de linea de sisteco. Importe de este linea de detalle.
                            accountID = -1;
                            sql = " select a.c_receivable_acct, c_bpartner_id, m_product_id " +
                                    " from z_sistecolinea_acct a " +
                                    " inner join z_sistecotipolineapazos b on a.z_sistecotipolineapazos_id = b.z_sistecotipolineapazos_id " +
                                    " where b.value ='" + astoVtaRecMPLinST.getST_TipoLinea() + "' " +
                                    " and a.c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;

                            accountID = DB.getSQLValueEx(null, sql);
                            if (accountID <= 0){
                                p_Error = "Falta indicar cuenta contable para Tipo de Linea : " + astoVtaRecMPLinST.getST_TipoLinea();
                                log.log(Level.SEVERE, p_Error);
                                fact = null;
                                facts.add(fact);
                                return facts;
                            }
                            FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, astoVtaRecMPLinST.getTotalAmt());
                            if (fl2 != null){
                                fl2.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                                if (rs.getInt("c_bpartner_id") > 0){
                                    fl1.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    fl1.setM_Product_ID(rs.getInt("m_product_id"));
                                }
                            }
                        }
                    }
                }

            }

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
            rs = null; pstmt = null;
        }

        facts.add(fact);
        return facts;

    }
}
