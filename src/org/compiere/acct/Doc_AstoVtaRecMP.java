package org.compiere.acct;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.model.MZAcctConfig;
import org.xpande.financial.model.MZMPagoIdentProd;
import org.xpande.financial.model.MZMedioPagoIdent;
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

                    int accountID = 0, cBpartnerID = 0, mProductID = 0;

                    // Busco Identificador segun codigo del medio de pago
                    int zMedioPagoID = 0, zMedioPagoIdentID = 0, zMPagoIdentProdID = 0;

                    sql = " select z_mediopago_id, z_mediopagoident_id, z_mpagoidentprod_id " +
                            " from z_sistecotipotarjeta " +
                            " where z_sistecotipotarjeta_id =" + astoVtaRecMPLinST.getZ_SistecoTipoTarjeta_ID();
                    pstmt = DB.prepareStatement(sql, null);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        zMedioPagoID = rs.getInt("z_mediopago_id");
                        zMedioPagoIdentID = rs.getInt("z_mediopagoident_id");
                        zMPagoIdentProdID = rs.getInt("z_mpagoidentprod_id");
                    }
                    DB.close(rs, pstmt);

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + as.get_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        // Obtengo cuenta segun medio de pago
                        sql = " select z_mediopago_id " +
                                " from z_sistecomediopago " +
                                " where z_sistecomediopago_id =" + astoVtaRecMPLinST.getZ_SistecoMedioPago_ID();
                        zMedioPagoID = DB.getSQLValueEx(null, sql);

                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "Falta indicar cuenta contable para Tipo de Tarjeta con ID: " + astoVtaRecMPLinST.getZ_SistecoTipoTarjeta_ID();                                log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }

                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, astoVtaRecMPLinST.getTotalAmt(), null);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                        if (cBpartnerID > 0){
                            fl1.setC_BPartner_ID(cBpartnerID);
                        }
                        if (mProductID > 0){
                            fl1.setM_Product_ID(mProductID);
                        }
                    }



                    // CR = Cuenta del medio de pago a ser reemplazado
                    accountID = 0;
                    cBpartnerID = 0;
                    mProductID = 0;
                    zMedioPagoID = 0;
                    zMedioPagoIdentID = 0;
                    zMPagoIdentProdID = 0;

                    // Busco Identificador segun codigo del medio de pago
                    String codMPagoAux = astoVtaRecMPLinST.getST_TipoTarjetaCredito();
                    if (codMPagoAux == null) codMPagoAux = astoVtaRecMPLinST.getST_CodigoMedioPago();
                    sql = " select z_mediopago_id, z_mediopagoident_id, z_mpagoidentprod_id " +
                            " from z_sistecotipotarjeta " +
                            " where value ='" + codMPagoAux + "'";
                    pstmt = DB.prepareStatement(sql, null);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        zMedioPagoID = rs.getInt("z_mediopago_id");
                        zMedioPagoIdentID = rs.getInt("z_mediopagoident_id");
                        zMPagoIdentProdID = rs.getInt("z_mpagoidentprod_id");
                    }
                    DB.close(rs, pstmt);

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + as.get_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        // Obtengo cuenta segun medio de pago
                        sql = " select z_mediopago_id " +
                                " from z_sistecomediopago " +
                                " where value ='" + astoVtaRecMPLinST.getST_CodigoMedioPago() + "'";
                        zMedioPagoID = DB.getSQLValueEx(null, sql);

                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "No se indica Cuenta Contable para Medio de Pago : " + astoVtaRecMPLinST.getST_CodigoMedioPago() +
                                    " - " + astoVtaRecMPLinST.getST_NombreMedioPago();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }

                    FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, astoVtaRecMPLinST.getTotalAmt());
                    if (fl2 != null){
                        fl2.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                        if (cBpartnerID > 0){
                            fl2.setC_BPartner_ID(cBpartnerID);
                        }
                        if (mProductID > 0){
                            fl2.setM_Product_ID(mProductID);
                        }
                    }
                }
            }

            // POS Scanntech
            else if (posVendor.getValue().equalsIgnoreCase("SCANNTECH")){

                List<MZAstoVtaRecMPLinSC> astoVtaRecMPLinSCList = this.astoVtaRecMP.getLinesScanntech();
                for (MZAstoVtaRecMPLinSC astoVtaRecMPLinSC: astoVtaRecMPLinSCList){

                    int cCurrencyID = as.getC_Currency_ID();
                    if (!astoVtaRecMPLinSC.getSC_CodigoMoneda().equalsIgnoreCase("858")){
                        cCurrencyID = 100;
                    }

                    int accountID = 0, cBpartnerID = 0, mProductID = 0;

                    // Busco Identificador segun codigo del medio de pago
                    int zMedioPagoID = 0, zMedioPagoIdentID = 0, zMPagoIdentProdID = 0;

                    sql = " select z_mediopago_id, z_mediopagoident_id, z_mpagoidentprod_id " +
                            " from z_stechcreditos " +
                            " where z_stechcreditos_id =" + astoVtaRecMPLinSC.getZ_StechCreditos_ID();
                    pstmt = DB.prepareStatement(sql, null);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        zMedioPagoID = rs.getInt("z_mediopago_id");
                        zMedioPagoIdentID = rs.getInt("z_mediopagoident_id");
                        zMPagoIdentProdID = rs.getInt("z_mpagoidentprod_id");
                    }
                    DB.close(rs, pstmt);

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + as.get_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        // Obtengo cuenta segun medio de pago
                        sql = " select z_mediopago_id " +
                                " from z_stechmediopago " +
                                " where z_stechmediopago_id =" + astoVtaRecMPLinSC.getZ_StechMedioPago_ID();
                        zMedioPagoID = DB.getSQLValueEx(null, sql);

                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "Falta indicar cuenta contable para Tipo de Tarjeta con ID: " + astoVtaRecMPLinSC.getZ_StechCreditos_ID();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }

                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, astoVtaRecMPLinSC.getSC_Importe(), null);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                        if (cBpartnerID > 0){
                            fl1.setC_BPartner_ID(cBpartnerID);
                        }
                        if (mProductID > 0){
                            fl1.setM_Product_ID(mProductID);
                        }
                    }



                    // CR = Cuenta del medio de pago a ser reemplazado
                    accountID = 0;
                    cBpartnerID = 0;
                    mProductID = 0;
                    zMedioPagoID = 0;
                    zMedioPagoIdentID = 0;
                    zMPagoIdentProdID = 0;

                    // Busco Identificador segun codigo del medio de pago
                    String codMPagoAux = astoVtaRecMPLinSC.getCodMedioPagoPOS();
                    sql = " select z_mediopago_id, z_mediopagoident_id, z_mpagoidentprod_id " +
                            " from z_stechcreditos " +
                            " where value ='" + astoVtaRecMPLinSC.getCodMedioPagoPOS() + "'";
                    pstmt = DB.prepareStatement(sql, null);
                    rs = pstmt.executeQuery();

                    if (rs.next()){
                        zMedioPagoID = rs.getInt("z_mediopago_id");
                        zMedioPagoIdentID = rs.getInt("z_mediopagoident_id");
                        zMPagoIdentProdID = rs.getInt("z_mpagoidentprod_id");
                    }
                    DB.close(rs, pstmt);

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + as.get_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        // Obtengo cuenta segun medio de pago
                        sql = " select z_mediopago_id " +
                                " from z_stechmediopago " +
                                " where value ='" + astoVtaRecMPLinSC.getCodMedioPagoPOS() + "'";
                        zMedioPagoID = DB.getSQLValueEx(null, sql);

                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "No se indica Cuenta Contable para Medio de Pago : " + astoVtaRecMPLinSC.getCodMedioPagoPOS() +
                                    " - " + astoVtaRecMPLinSC.getNomMedioPagoPOS();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }

                    FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, astoVtaRecMPLinSC.getSC_Importe());
                    if (fl2 != null){
                        fl2.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                        if (cBpartnerID > 0){
                            fl2.setC_BPartner_ID(cBpartnerID);
                        }
                        if (mProductID > 0){
                            fl2.setM_Product_ID(mProductID);
                        }
                    }
                }
            }
            // POS Geocom
            else if (posVendor.getValue().equalsIgnoreCase("GEOCOM")){
                List<MZAstoVtaRecMPLinGeo> astoVtaRecMPLinGeoList = this.astoVtaRecMP.getLinesGeocom();
                for (MZAstoVtaRecMPLinGeo astoVtaRecMPLin: astoVtaRecMPLinGeoList){

                    int cCurrencyID = as.getC_Currency_ID();
                    if ((!astoVtaRecMPLin.getISO_Code().equalsIgnoreCase("858")) &&
                            (!astoVtaRecMPLin.getISO_Code().equalsIgnoreCase("UYU"))) {
                        cCurrencyID = 100;
                    }
                    int accountID = 0, cBpartnerID = 0, mProductID = 0;

                    // Busco Identificador segun codigo del medio de pago
                    int zMedioPagoID = 0, zMedioPagoIdentID = 0, zMPagoIdentProdID = 0;

                    if (astoVtaRecMPLin.getZ_MPagoIdentPos_ID() > 0){
                        sql = " select z_mediopagoident_id from z_mpagoidentpos where z_mpagoidentpos_id =" + astoVtaRecMPLin.getZ_MPagoIdentPos_ID();
                        zMedioPagoIdentID = DB.getSQLValueEx(null, sql);
                        if (zMedioPagoIdentID > 0){
                            sql = " select max(z_mpagoidentprod_id) as z_mpagoidentprod_id from z_mpagoidentprod where z_mediopagoident_id =" + zMedioPagoIdentID;
                            zMPagoIdentProdID = DB.getSQLValueEx(null, sql);
                        }
                    }
                    sql = " select z_mediopago_id from z_mediopagopos where z_mediopagopos_id =" + astoVtaRecMPLin.getZ_MedioPagoPos_ID();
                    zMedioPagoID = DB.getSQLValueEx(null, sql);

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + as.get_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "Falta indicar cuenta contable para Medio de Pago y/o Tarjeta";
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }

                    FactLine fl1 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, astoVtaRecMPLin.getTotalAmt(), null);
                    if (fl1 != null){
                        fl1.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                        if (cBpartnerID > 0){
                            fl1.setC_BPartner_ID(cBpartnerID);
                        }
                        if (mProductID > 0){
                            fl1.setM_Product_ID(mProductID);
                        }
                    }

                    // CR = Cuenta del medio de pago a ser reemplazado
                    accountID = 0;
                    cBpartnerID = 0;
                    mProductID = 0;
                    zMedioPagoID = 0;
                    zMedioPagoIdentID = 0;
                    zMPagoIdentProdID = 0;

                    // Busco Identificador segun codigo del medio de pago
                    String codMPagoAux = astoVtaRecMPLin.getCodMedioPagoPOS();

                    if ((astoVtaRecMPLin.getnrotarjeta() != null) && (!astoVtaRecMPLin.getnrotarjeta().equalsIgnoreCase(""))){
                        sql = " select z_mediopagoident_id from z_mpagoidentpos  where codmediopagopos='" + codMPagoAux +
                                "' and z_posvendor_id = 1000000";
                        zMedioPagoIdentID = DB.getSQLValueEx(null, sql);
                        if (zMedioPagoIdentID > 0){
                            sql = " select max(z_mpagoidentprod_id) as z_mpagoidentprod_id from z_mpagoidentprod where z_mediopagoident_id =" + zMedioPagoIdentID;
                            zMPagoIdentProdID = DB.getSQLValueEx(null, sql);
                            sql = " select z_mediopago_id from z_mediopagoident where z_mediopagoident_id =" + zMedioPagoIdentID;
                            zMedioPagoID = DB.getSQLValueEx(null, sql);
                        }
                        else{
                            sql = " select z_mediopago_id from z_mediopagopos where codmediopagopos='" + codMPagoAux +
                                    "' and z_posvendor_id = 1000000 ";
                            zMedioPagoID = DB.getSQLValueEx(null, sql);
                        }
                    }
                    else{
                        sql = " select z_mediopago_id from z_mediopagopos where codmediopagopos='" + codMPagoAux +
                                "' and z_posvendor_id = 1000000 ";
                        zMedioPagoID = DB.getSQLValueEx(null, sql);
                    }

                    if ((zMedioPagoIdentID > 0) || (zMedioPagoID > 0)){
                        if (zMedioPagoIdentID > 0){
                            MZMedioPagoIdent pagoIdent = new MZMedioPagoIdent(getCtx(), zMedioPagoIdentID, null);
                            cBpartnerID = pagoIdent.getC_BPartner_ID();
                            if (zMPagoIdentProdID > 0){
                                MZMPagoIdentProd pagoIdentProd = new MZMPagoIdentProd(getCtx(), zMPagoIdentProdID, null);
                                mProductID = pagoIdentProd.getM_Product_ID();
                            }
                            // Cuenta contable del identificador si es que tengo
                            sql = " select mp_recibidos_acct " +
                                    " from z_mpagoident_acct " +
                                    " where z_mediopagoident_id =" + zMedioPagoIdentID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            accountID = DB.getSQLValueEx(null, sql);
                        }
                        if (accountID <= 0){
                            if (zMedioPagoID > 0){
                                // Cuenta contable directo del medio de pago
                                sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                        " from z_mediopago_acct " +
                                        " where z_mediopago_id =" + zMedioPagoID +
                                        " and c_acctschema_id =" + as.get_ID() +
                                        " and c_currency_id =" + cCurrencyID;
                                pstmt = DB.prepareStatement(sql, null);
                                rs = pstmt.executeQuery();

                                if (rs.next()){
                                    accountID = rs.getInt("mp_recibidos_acct");
                                    if (rs.getInt("c_bpartner_id") > 0){
                                        cBpartnerID = rs.getInt("c_bpartner_id");
                                    }
                                    if (rs.getInt("m_product_id") > 0){
                                        mProductID = rs.getInt("m_product_id");
                                    }
                                }
                                DB.close(rs, pstmt);
                            }
                        }
                    }
                    else {
                        if (zMedioPagoID > 0){
                            // Cuenta contable directo del medio de pago
                            sql = " select mp_recibidos_acct, c_bpartner_id, m_product_id " +
                                    " from z_mediopago_acct " +
                                    " where z_mediopago_id =" + zMedioPagoID +
                                    " and c_acctschema_id =" + as.get_ID() +
                                    " and c_currency_id =" + cCurrencyID;
                            pstmt = DB.prepareStatement(sql, null);
                            rs = pstmt.executeQuery();

                            if (rs.next()){
                                accountID = rs.getInt("mp_recibidos_acct");
                                if (rs.getInt("c_bpartner_id") > 0){
                                    cBpartnerID = rs.getInt("c_bpartner_id");
                                }
                                if (rs.getInt("m_product_id") > 0){
                                    mProductID = rs.getInt("m_product_id");
                                }
                            }
                            DB.close(rs, pstmt);
                        }
                    }

                    // Si no obtuve cuenta, aviso y salgo
                    if (accountID <= 0){
                        if (accountID <= 0){
                            p_Error = "No se indica Cuenta Contable para Medio de Pago : " + astoVtaRecMPLin.getCodMedioPagoPOS() +
                                    " - " + astoVtaRecMPLin.getNomMedioPagoPOS();
                            log.log(Level.SEVERE, p_Error);
                            fact = null;
                            facts.add(fact);
                            return facts;
                        }
                    }

                    FactLine fl2 = fact.createLine(null, MAccount.get(getCtx(), accountID), cCurrencyID, null, astoVtaRecMPLin.getTotalAmt());
                    if (fl2 != null){
                        fl2.setAD_Org_ID(this.astoVtaRecMP.getAD_Org_ID());
                        if (cBpartnerID > 0){
                            fl2.setC_BPartner_ID(cBpartnerID);
                        }
                        if (mProductID > 0){
                            fl2.setM_Product_ID(mProductID);
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
