/******************************************************************************
 * Product: ADempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 2006-2017 ADempiere Foundation, All Rights Reserved.         *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * or (at your option) any later version.										*
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * or via info@adempiere.net or http://www.adempiere.net/license.html         *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package org.xpande.acct.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for Z_GeneraFormDGIResg
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_GeneraFormDGIResg extends PO implements I_Z_GeneraFormDGIResg, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190922L;

    /** Standard Constructor */
    public X_Z_GeneraFormDGIResg (Properties ctx, int Z_GeneraFormDGIResg_ID, String trxName)
    {
      super (ctx, Z_GeneraFormDGIResg_ID, trxName);
      /** if (Z_GeneraFormDGIResg_ID == 0)
        {
			setAmtBase (Env.ZERO);
			setAmtRetencion (Env.ZERO);
			setC_BPartner_ID (0);
			setC_DocType_ID (0);
			setC_Invoice_ID (0);
			setC_Period_ID (0);
			setC_TaxGroup_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDateDoc (new Timestamp( System.currentTimeMillis() ));
			setDocBaseType (null);
			setDocumentNoRef (null);
			setPorcRetencion (Env.ZERO);
			setReference (null);
			setTaxID (null);
			setZ_AcctConfigRubroDGI_ID (0);
			setZ_GeneraFormDGI_ID (0);
			setZ_GeneraFormDGIResg_ID (0);
			setZ_ResguardoSocioDoc_ID (0);
			setZ_ResguardoSocioDocRet_ID (0);
			setZ_ResguardoSocio_ID (0);
			setZ_RetencionSocio_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_GeneraFormDGIResg (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_Z_GeneraFormDGIResg[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set AmtBase.
		@param AmtBase 
		Monto base
	  */
	public void setAmtBase (BigDecimal AmtBase)
	{
		set_Value (COLUMNNAME_AmtBase, AmtBase);
	}

	/** Get AmtBase.
		@return Monto base
	  */
	public BigDecimal getAmtBase () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtBase);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtBaseMO.
		@param AmtBaseMO 
		Monto base en moneda origen
	  */
	public void setAmtBaseMO (BigDecimal AmtBaseMO)
	{
		set_Value (COLUMNNAME_AmtBaseMO, AmtBaseMO);
	}

	/** Get AmtBaseMO.
		@return Monto base en moneda origen
	  */
	public BigDecimal getAmtBaseMO () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtBaseMO);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtRetencion.
		@param AmtRetencion 
		Monto retención
	  */
	public void setAmtRetencion (BigDecimal AmtRetencion)
	{
		set_Value (COLUMNNAME_AmtRetencion, AmtRetencion);
	}

	/** Get AmtRetencion.
		@return Monto retención
	  */
	public BigDecimal getAmtRetencion () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtRetencion);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtRetencionMO.
		@param AmtRetencionMO 
		Monto retención en Moneda Origen que es igual a la moneda del documento
	  */
	public void setAmtRetencionMO (BigDecimal AmtRetencionMO)
	{
		set_Value (COLUMNNAME_AmtRetencionMO, AmtRetencionMO);
	}

	/** Get AmtRetencionMO.
		@return Monto retención en Moneda Origen que es igual a la moneda del documento
	  */
	public BigDecimal getAmtRetencionMO () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtRetencionMO);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (I_C_BPartner)MTable.get(getCtx(), I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Currency getC_Currency() throws RuntimeException
    {
		return (I_C_Currency)MTable.get(getCtx(), I_C_Currency.Table_Name)
			.getPO(getC_Currency_ID(), get_TrxName());	}

	/** Set Currency.
		@param C_Currency_ID 
		The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID)
	{
		if (C_Currency_ID < 1) 
			set_Value (COLUMNNAME_C_Currency_ID, null);
		else 
			set_Value (COLUMNNAME_C_Currency_ID, Integer.valueOf(C_Currency_ID));
	}

	/** Get Currency.
		@return The Currency for this record
	  */
	public int getC_Currency_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_DocType getC_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (I_C_Invoice)MTable.get(getCtx(), I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_Value (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_Value (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Period getC_Period() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_ID(), get_TrxName());	}

	/** Set Period.
		@param C_Period_ID 
		Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1) 
			set_Value (COLUMNNAME_C_Period_ID, null);
		else 
			set_Value (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	public int getC_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.eevolution.model.I_C_TaxGroup getC_TaxGroup() throws RuntimeException
    {
		return (org.eevolution.model.I_C_TaxGroup)MTable.get(getCtx(), org.eevolution.model.I_C_TaxGroup.Table_Name)
			.getPO(getC_TaxGroup_ID(), get_TrxName());	}

	/** Set Tax Group.
		@param C_TaxGroup_ID Tax Group	  */
	public void setC_TaxGroup_ID (int C_TaxGroup_ID)
	{
		if (C_TaxGroup_ID < 1) 
			set_Value (COLUMNNAME_C_TaxGroup_ID, null);
		else 
			set_Value (COLUMNNAME_C_TaxGroup_ID, Integer.valueOf(C_TaxGroup_ID));
	}

	/** Get Tax Group.
		@return Tax Group	  */
	public int getC_TaxGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_TaxGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
	}

	/** Set DateRefResguardo.
		@param DateRefResguardo 
		Fecha referencia de un Resguardo a Socio de Negocio
	  */
	public void setDateRefResguardo (Timestamp DateRefResguardo)
	{
		set_Value (COLUMNNAME_DateRefResguardo, DateRefResguardo);
	}

	/** Get DateRefResguardo.
		@return Fecha referencia de un Resguardo a Socio de Negocio
	  */
	public Timestamp getDateRefResguardo () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateRefResguardo);
	}

	/** DocBaseType AD_Reference_ID=183 */
	public static final int DOCBASETYPE_AD_Reference_ID=183;
	/** GL Journal = GLJ */
	public static final String DOCBASETYPE_GLJournal = "GLJ";
	/** GL Document = GLD */
	public static final String DOCBASETYPE_GLDocument = "GLD";
	/** AP Invoice = API */
	public static final String DOCBASETYPE_APInvoice = "API";
	/** AP Payment = APP */
	public static final String DOCBASETYPE_APPayment = "APP";
	/** AR Invoice = ARI */
	public static final String DOCBASETYPE_ARInvoice = "ARI";
	/** AR Receipt = ARR */
	public static final String DOCBASETYPE_ARReceipt = "ARR";
	/** Sales Order = SOO */
	public static final String DOCBASETYPE_SalesOrder = "SOO";
	/** AR Pro Forma Invoice = ARF */
	public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
	/** Material Delivery = MMS */
	public static final String DOCBASETYPE_MaterialDelivery = "MMS";
	/** Material Receipt = MMR */
	public static final String DOCBASETYPE_MaterialReceipt = "MMR";
	/** Material Movement = MMM */
	public static final String DOCBASETYPE_MaterialMovement = "MMM";
	/** Purchase Order = POO */
	public static final String DOCBASETYPE_PurchaseOrder = "POO";
	/** Purchase Requisition = POR */
	public static final String DOCBASETYPE_PurchaseRequisition = "POR";
	/** Material Physical Inventory = MMI */
	public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
	/** AP Credit Memo = APC */
	public static final String DOCBASETYPE_APCreditMemo = "APC";
	/** AR Credit Memo = ARC */
	public static final String DOCBASETYPE_ARCreditMemo = "ARC";
	/** Bank Statement = CMB */
	public static final String DOCBASETYPE_BankStatement = "CMB";
	/** Cash Journal = CMC */
	public static final String DOCBASETYPE_CashJournal = "CMC";
	/** Payment Allocation = CMA */
	public static final String DOCBASETYPE_PaymentAllocation = "CMA";
	/** Material Production = MMP */
	public static final String DOCBASETYPE_MaterialProduction = "MMP";
	/** Match Invoice = MXI */
	public static final String DOCBASETYPE_MatchInvoice = "MXI";
	/** Match PO = MXP */
	public static final String DOCBASETYPE_MatchPO = "MXP";
	/** Project Issue = PJI */
	public static final String DOCBASETYPE_ProjectIssue = "PJI";
	/** Maintenance Order = MOF */
	public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
	/** Manufacturing Order = MOP */
	public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
	/** Quality Order = MQO */
	public static final String DOCBASETYPE_QualityOrder = "MQO";
	/** Payroll = HRP */
	public static final String DOCBASETYPE_Payroll = "HRP";
	/** Distribution Order = DOO */
	public static final String DOCBASETYPE_DistributionOrder = "DOO";
	/** Manufacturing Cost Collector = MCC */
	public static final String DOCBASETYPE_ManufacturingCostCollector = "MCC";
	/** Warehouse Management Order = WMO */
	public static final String DOCBASETYPE_WarehouseManagementOrder = "WMO";
	/** Manufacturing Planned Order = MPO */
	public static final String DOCBASETYPE_ManufacturingPlannedOrder = "MPO";
	/** AP Payment Selection = APS */
	public static final String DOCBASETYPE_APPaymentSelection = "APS";
	/** Sales Commission = SOC */
	public static final String DOCBASETYPE_SalesCommission = "SOC";
	/** Fixed Assets Addition = FAA */
	public static final String DOCBASETYPE_FixedAssetsAddition = "FAA";
	/** Fixed Assets Disposal = FAD */
	public static final String DOCBASETYPE_FixedAssetsDisposal = "FAD";
	/** Fixed Assets Depreciation = FDP */
	public static final String DOCBASETYPE_FixedAssetsDepreciation = "FDP";
	/** PLV Precios de Proveedor = PLV */
	public static final String DOCBASETYPE_PLVPreciosDeProveedor = "PLV";
	/** Retail Confirmacion Etiquetas = RCE */
	public static final String DOCBASETYPE_RetailConfirmacionEtiquetas = "RCE";
	/** PVP Actualizacion Precios de Venta = PVP */
	public static final String DOCBASETYPE_PVPActualizacionPreciosDeVenta = "PVP";
	/** RCP Retail Comunicacion POS = RCP */
	public static final String DOCBASETYPE_RCPRetailComunicacionPOS = "RCP";
	/** RGU Emision de Resguardos = RGU */
	public static final String DOCBASETYPE_RGUEmisionDeResguardos = "RGU";
	/** RGC Emision de Contra-Resguardos = RGC */
	public static final String DOCBASETYPE_RGCEmisionDeContra_Resguardos = "RGC";
	/** OPG Generacion de Ordenes de Pago = OPG */
	public static final String DOCBASETYPE_OPGGeneracionDeOrdenesDePago = "OPG";
	/** OOP Ordenes de Pago = OOP */
	public static final String DOCBASETYPE_OOPOrdenesDePago = "OOP";
	/** EMP Emision Medio Pago = EMP */
	public static final String DOCBASETYPE_EMPEmisionMedioPago = "EMP";
	/** RMP Reemplazo Medio de Pago = RMP */
	public static final String DOCBASETYPE_RMPReemplazoMedioDePago = "RMP";
	/** NCG Generacion de Notas de Cŕedito = NCG */
	public static final String DOCBASETYPE_NCGGeneracionDeNotasDeCŕedito = "NCG";
	/** ODV Orden de Devolución a Proveedor = ODV */
	public static final String DOCBASETYPE_ODVOrdenDeDevoluciónAProveedor = "ODV";
	/** OFP Oferta Periódica Retail = OFP */
	public static final String DOCBASETYPE_OFPOfertaPeriódicaRetail = "OFP";
	/** PPD Pago Proveedor = PPD */
	public static final String DOCBASETYPE_PPDPagoProveedor = "PPD";
	/** PPR Recibo de Pagos Emitidos = PPR */
	public static final String DOCBASETYPE_PPRReciboDePagosEmitidos = "PPR";
	/** PPA Anticipo a Proveedor = PPA */
	public static final String DOCBASETYPE_PPAAnticipoAProveedor = "PPA";
	/** RDI Remito Diferencia Factura = RDI */
	public static final String DOCBASETYPE_RDIRemitoDiferenciaFactura = "RDI";
	/** RDC Remito Diferencia Cantidad = RDC */
	public static final String DOCBASETYPE_RDCRemitoDiferenciaCantidad = "RDC";
	/** CCD Cobranza a Cliente = CCD */
	public static final String DOCBASETYPE_CCDCobranzaACliente = "CCD";
	/** GEN General = GEN */
	public static final String DOCBASETYPE_GENGeneral = "GEN";
	/** CII Carga Inicial Invoices = CII */
	public static final String DOCBASETYPE_CIICargaInicialInvoices = "CII";
	/** TSP Transferencia Saldo a Pagar = TSP */
	public static final String DOCBASETYPE_TSPTransferenciaSaldoAPagar = "TSP";
	/** CIM Carga Inicial Medios de Pago = CIM */
	public static final String DOCBASETYPE_CIMCargaInicialMediosDePago = "CIM";
	/** CIJ Carga Inicial Asientos Contables = CIJ */
	public static final String DOCBASETYPE_CIJCargaInicialAsientosContables = "CIJ";
	/** AVG Generacion de Asientos de Venta POS = AVG */
	public static final String DOCBASETYPE_AVGGeneracionDeAsientosDeVentaPOS = "AVG";
	/** DFC Diferencia de Cambio = DFC */
	public static final String DOCBASETYPE_DFCDiferenciaDeCambio = "DFC";
	/** FME Formulario Movimiento Efectivo = FME */
	public static final String DOCBASETYPE_FMEFormularioMovimientoEfectivo = "FME";
	/** DPT Deposito Medio Pago Tercero = DPT */
	public static final String DOCBASETYPE_DPTDepositoMedioPagoTercero = "DPT";
	/** Set Document BaseType.
		@param DocBaseType 
		Logical type of document
	  */
	public void setDocBaseType (String DocBaseType)
	{

		set_Value (COLUMNNAME_DocBaseType, DocBaseType);
	}

	/** Get Document BaseType.
		@return Logical type of document
	  */
	public String getDocBaseType () 
	{
		return (String)get_Value(COLUMNNAME_DocBaseType);
	}

	/** Set DocumentNoRef.
		@param DocumentNoRef 
		Numero de documento referenciado
	  */
	public void setDocumentNoRef (String DocumentNoRef)
	{
		set_Value (COLUMNNAME_DocumentNoRef, DocumentNoRef);
	}

	/** Get DocumentNoRef.
		@return Numero de documento referenciado
	  */
	public String getDocumentNoRef () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNoRef);
	}

	/** Set PorcRetencion.
		@param PorcRetencion 
		Porcentaje Retención 
	  */
	public void setPorcRetencion (BigDecimal PorcRetencion)
	{
		set_Value (COLUMNNAME_PorcRetencion, PorcRetencion);
	}

	/** Get PorcRetencion.
		@return Porcentaje Retención 
	  */
	public BigDecimal getPorcRetencion () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PorcRetencion);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Reference.
		@param Reference 
		Reference for this record
	  */
	public void setReference (String Reference)
	{
		set_Value (COLUMNNAME_Reference, Reference);
	}

	/** Get Reference.
		@return Reference for this record
	  */
	public String getReference () 
	{
		return (String)get_Value(COLUMNNAME_Reference);
	}

	/** Set Tax ID.
		@param TaxID 
		Tax Identification
	  */
	public void setTaxID (String TaxID)
	{
		set_Value (COLUMNNAME_TaxID, TaxID);
	}

	/** Get Tax ID.
		@return Tax Identification
	  */
	public String getTaxID () 
	{
		return (String)get_Value(COLUMNNAME_TaxID);
	}

	/** Set Immutable Universally Unique Identifier.
		@param UUID 
		Immutable Universally Unique Identifier
	  */
	public void setUUID (String UUID)
	{
		set_Value (COLUMNNAME_UUID, UUID);
	}

	/** Get Immutable Universally Unique Identifier.
		@return Immutable Universally Unique Identifier
	  */
	public String getUUID () 
	{
		return (String)get_Value(COLUMNNAME_UUID);
	}

	public I_Z_AcctConfigRubroDGI getZ_AcctConfigRubroDGI() throws RuntimeException
    {
		return (I_Z_AcctConfigRubroDGI)MTable.get(getCtx(), I_Z_AcctConfigRubroDGI.Table_Name)
			.getPO(getZ_AcctConfigRubroDGI_ID(), get_TrxName());	}

	/** Set Z_AcctConfigRubroDGI ID.
		@param Z_AcctConfigRubroDGI_ID Z_AcctConfigRubroDGI ID	  */
	public void setZ_AcctConfigRubroDGI_ID (int Z_AcctConfigRubroDGI_ID)
	{
		if (Z_AcctConfigRubroDGI_ID < 1) 
			set_Value (COLUMNNAME_Z_AcctConfigRubroDGI_ID, null);
		else 
			set_Value (COLUMNNAME_Z_AcctConfigRubroDGI_ID, Integer.valueOf(Z_AcctConfigRubroDGI_ID));
	}

	/** Get Z_AcctConfigRubroDGI ID.
		@return Z_AcctConfigRubroDGI ID	  */
	public int getZ_AcctConfigRubroDGI_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctConfigRubroDGI_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_Z_GeneraFormDGI getZ_GeneraFormDGI() throws RuntimeException
    {
		return (I_Z_GeneraFormDGI)MTable.get(getCtx(), I_Z_GeneraFormDGI.Table_Name)
			.getPO(getZ_GeneraFormDGI_ID(), get_TrxName());	}

	/** Set Z_GeneraFormDGI ID.
		@param Z_GeneraFormDGI_ID Z_GeneraFormDGI ID	  */
	public void setZ_GeneraFormDGI_ID (int Z_GeneraFormDGI_ID)
	{
		if (Z_GeneraFormDGI_ID < 1) 
			set_Value (COLUMNNAME_Z_GeneraFormDGI_ID, null);
		else 
			set_Value (COLUMNNAME_Z_GeneraFormDGI_ID, Integer.valueOf(Z_GeneraFormDGI_ID));
	}

	/** Get Z_GeneraFormDGI ID.
		@return Z_GeneraFormDGI ID	  */
	public int getZ_GeneraFormDGI_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_GeneraFormDGI_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_GeneraFormDGIResg ID.
		@param Z_GeneraFormDGIResg_ID Z_GeneraFormDGIResg ID	  */
	public void setZ_GeneraFormDGIResg_ID (int Z_GeneraFormDGIResg_ID)
	{
		if (Z_GeneraFormDGIResg_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_GeneraFormDGIResg_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_GeneraFormDGIResg_ID, Integer.valueOf(Z_GeneraFormDGIResg_ID));
	}

	/** Get Z_GeneraFormDGIResg ID.
		@return Z_GeneraFormDGIResg ID	  */
	public int getZ_GeneraFormDGIResg_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_GeneraFormDGIResg_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_ResguardoSocioDoc ID.
		@param Z_ResguardoSocioDoc_ID Z_ResguardoSocioDoc ID	  */
	public void setZ_ResguardoSocioDoc_ID (int Z_ResguardoSocioDoc_ID)
	{
		if (Z_ResguardoSocioDoc_ID < 1) 
			set_Value (COLUMNNAME_Z_ResguardoSocioDoc_ID, null);
		else 
			set_Value (COLUMNNAME_Z_ResguardoSocioDoc_ID, Integer.valueOf(Z_ResguardoSocioDoc_ID));
	}

	/** Get Z_ResguardoSocioDoc ID.
		@return Z_ResguardoSocioDoc ID	  */
	public int getZ_ResguardoSocioDoc_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_ResguardoSocioDoc_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_ResguardoSocioDocRet ID.
		@param Z_ResguardoSocioDocRet_ID Z_ResguardoSocioDocRet ID	  */
	public void setZ_ResguardoSocioDocRet_ID (int Z_ResguardoSocioDocRet_ID)
	{
		if (Z_ResguardoSocioDocRet_ID < 1) 
			set_Value (COLUMNNAME_Z_ResguardoSocioDocRet_ID, null);
		else 
			set_Value (COLUMNNAME_Z_ResguardoSocioDocRet_ID, Integer.valueOf(Z_ResguardoSocioDocRet_ID));
	}

	/** Get Z_ResguardoSocioDocRet ID.
		@return Z_ResguardoSocioDocRet ID	  */
	public int getZ_ResguardoSocioDocRet_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_ResguardoSocioDocRet_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_ResguardoSocio ID.
		@param Z_ResguardoSocio_ID Z_ResguardoSocio ID	  */
	public void setZ_ResguardoSocio_ID (int Z_ResguardoSocio_ID)
	{
		if (Z_ResguardoSocio_ID < 1) 
			set_Value (COLUMNNAME_Z_ResguardoSocio_ID, null);
		else 
			set_Value (COLUMNNAME_Z_ResguardoSocio_ID, Integer.valueOf(Z_ResguardoSocio_ID));
	}

	/** Get Z_ResguardoSocio ID.
		@return Z_ResguardoSocio ID	  */
	public int getZ_ResguardoSocio_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_ResguardoSocio_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_RetencionSocio ID.
		@param Z_RetencionSocio_ID Z_RetencionSocio ID	  */
	public void setZ_RetencionSocio_ID (int Z_RetencionSocio_ID)
	{
		if (Z_RetencionSocio_ID < 1) 
			set_Value (COLUMNNAME_Z_RetencionSocio_ID, null);
		else 
			set_Value (COLUMNNAME_Z_RetencionSocio_ID, Integer.valueOf(Z_RetencionSocio_ID));
	}

	/** Get Z_RetencionSocio ID.
		@return Z_RetencionSocio ID	  */
	public int getZ_RetencionSocio_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_RetencionSocio_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}