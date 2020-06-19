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

/** Generated Model for Z_AcctCierre
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctCierre extends PO implements I_Z_AcctCierre, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200619L;

    /** Standard Constructor */
    public X_Z_AcctCierre (Properties ctx, int Z_AcctCierre_ID, String trxName)
    {
      super (ctx, Z_AcctCierre_ID, trxName);
      /** if (Z_AcctCierre_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setC_DocType_ID (0);
			setDateAcct (new Timestamp( System.currentTimeMillis() ));
			setDateDoc (new Timestamp( System.currentTimeMillis() ));
// @#Date@
			setDocAction (null);
// CO
			setDocBaseType (null);
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setIsApproved (false);
// N
			setIsBPartner (false);
// N
			setPosted (false);
// N
			setProcessed (false);
// N
			setProcessing (false);
// N
			setZ_AcctCierre_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctCierre (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctCierre[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_AcctSchema getC_AcctSchema() throws RuntimeException
    {
		return (I_C_AcctSchema)MTable.get(getCtx(), I_C_AcctSchema.Table_Name)
			.getPO(getC_AcctSchema_ID(), get_TrxName());	}

	/** Set Accounting Schema.
		@param C_AcctSchema_ID 
		Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID)
	{
		if (C_AcctSchema_ID < 1) 
			set_Value (COLUMNNAME_C_AcctSchema_ID, null);
		else 
			set_Value (COLUMNNAME_C_AcctSchema_ID, Integer.valueOf(C_AcctSchema_ID));
	}

	/** Get Accounting Schema.
		@return Rules for accounting
	  */
	public int getC_AcctSchema_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_AcctSchema_ID);
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

	/** Set Description.
		@param Description 
		Optional short description of the record
	  */
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription () 
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
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
	/** AVR Asiento de Reclasificación de Medios de Pago = AVR */
	public static final String DOCBASETYPE_AVRAsientoDeReclasificaciónDeMediosDePago = "AVR";
	/** CEX Carga Extractos Bancarios = CEX */
	public static final String DOCBASETYPE_CEXCargaExtractosBancarios = "CEX";
	/** CIP Carga Inicial de Pagoa / Cobros = CIP */
	public static final String DOCBASETYPE_CIPCargaInicialDePagoaCobros = "CIP";
	/** CSC Carga Scanntech Comprobantes = CSC */
	public static final String DOCBASETYPE_CSCCargaScanntechComprobantes = "CSC";
	/** CJD Cierre de Cuentas Diferenciales = CJD */
	public static final String DOCBASETYPE_CJDCierreDeCuentasDiferenciales = "CJD";
	/** CJI Cierre de Cuentas Integrales = CJI */
	public static final String DOCBASETYPE_CJICierreDeCuentasIntegrales = "CJI";
	/** AJI Asiento de Apertura de Ejercicio = AJI */
	public static final String DOCBASETYPE_AJIAsientoDeAperturaDeEjercicio = "AJI";
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

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_Value (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set Approved.
		@param IsApproved 
		Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved)
	{
		set_Value (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved () 
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IsBPartner.
		@param IsBPartner 
		Si es o no un socio de negocio
	  */
	public void setIsBPartner (boolean IsBPartner)
	{
		set_Value (COLUMNNAME_IsBPartner, Boolean.valueOf(IsBPartner));
	}

	/** Get IsBPartner.
		@return Si es o no un socio de negocio
	  */
	public boolean isBPartner () 
	{
		Object oo = get_Value(COLUMNNAME_IsBPartner);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Posted.
		@param Posted 
		Posting status
	  */
	public void setPosted (boolean Posted)
	{
		set_Value (COLUMNNAME_Posted, Boolean.valueOf(Posted));
	}

	/** Get Posted.
		@return Posting status
	  */
	public boolean isPosted () 
	{
		Object oo = get_Value(COLUMNNAME_Posted);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set ProcessButton.
		@param ProcessButton ProcessButton	  */
	public void setProcessButton (String ProcessButton)
	{
		set_Value (COLUMNNAME_ProcessButton, ProcessButton);
	}

	/** Get ProcessButton.
		@return ProcessButton	  */
	public String getProcessButton () 
	{
		return (String)get_Value(COLUMNNAME_ProcessButton);
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Processed On.
		@param ProcessedOn 
		The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Start Date.
		@param StartDate 
		First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate)
	{
		set_Value (COLUMNNAME_StartDate, StartDate);
	}

	/** Get Start Date.
		@return First effective day (inclusive)
	  */
	public Timestamp getStartDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate);
	}

	/** Set TotalAcctCr.
		@param TotalAcctCr 
		Total créditos en moneda del esquema contable
	  */
	public void setTotalAcctCr (BigDecimal TotalAcctCr)
	{
		set_Value (COLUMNNAME_TotalAcctCr, TotalAcctCr);
	}

	/** Get TotalAcctCr.
		@return Total créditos en moneda del esquema contable
	  */
	public BigDecimal getTotalAcctCr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAcctCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set TotalAcctDr.
		@param TotalAcctDr 
		Total débitos en moneda del esquema contable
	  */
	public void setTotalAcctDr (BigDecimal TotalAcctDr)
	{
		set_Value (COLUMNNAME_TotalAcctDr, TotalAcctDr);
	}

	/** Get TotalAcctDr.
		@return Total débitos en moneda del esquema contable
	  */
	public BigDecimal getTotalAcctDr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAcctDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Total Amount.
		@param TotalAmt 
		Total Amount
	  */
	public void setTotalAmt (BigDecimal TotalAmt)
	{
		set_Value (COLUMNNAME_TotalAmt, TotalAmt);
	}

	/** Get Total Amount.
		@return Total Amount
	  */
	public BigDecimal getTotalAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TotalAmt);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	public I_Z_AcctApertura getZ_AcctApertura() throws RuntimeException
    {
		return (I_Z_AcctApertura)MTable.get(getCtx(), I_Z_AcctApertura.Table_Name)
			.getPO(getZ_AcctApertura_ID(), get_TrxName());	}

	/** Set Z_AcctApertura ID.
		@param Z_AcctApertura_ID Z_AcctApertura ID	  */
	public void setZ_AcctApertura_ID (int Z_AcctApertura_ID)
	{
		if (Z_AcctApertura_ID < 1) 
			set_Value (COLUMNNAME_Z_AcctApertura_ID, null);
		else 
			set_Value (COLUMNNAME_Z_AcctApertura_ID, Integer.valueOf(Z_AcctApertura_ID));
	}

	/** Get Z_AcctApertura ID.
		@return Z_AcctApertura ID	  */
	public int getZ_AcctApertura_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctApertura_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_AcctCierre ID.
		@param Z_AcctCierre_ID Z_AcctCierre ID	  */
	public void setZ_AcctCierre_ID (int Z_AcctCierre_ID)
	{
		if (Z_AcctCierre_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctCierre_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctCierre_ID, Integer.valueOf(Z_AcctCierre_ID));
	}

	/** Get Z_AcctCierre ID.
		@return Z_AcctCierre ID	  */
	public int getZ_AcctCierre_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctCierre_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}