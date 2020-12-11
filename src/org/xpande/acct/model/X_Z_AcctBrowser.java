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

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for Z_AcctBrowser
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctBrowser extends PO implements I_Z_AcctBrowser, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20201209L;

    /** Standard Constructor */
    public X_Z_AcctBrowser (Properties ctx, int Z_AcctBrowser_ID, String trxName)
    {
      super (ctx, Z_AcctBrowser_ID, trxName);
      /** if (Z_AcctBrowser_ID == 0)
        {
			setAnulado (true);
// Y
			setC_AcctSchema_ID (0);
			setC_Currency_ID (0);
			setConciliado (true);
// Y
			setDepositado (true);
// Y
			setEditable (true);
// Y
			setEmitido (true);
// Y
			setEntregado (true);
// Y
			setFiltroEstadoMPago (false);
// N
			setFiltroManual (false);
// N
			setIncCtaSaldoSinMov (true);
// Y
			setIncCtaSinSaldoConMov (true);
// Y
			setIncCtaSinSaldoSinMov (true);
// Y
			setIncInfoAuditoria (false);
// N
			setIncInfoDocumento (false);
// N
			setIncInfoMedioPago (false);
// N
			setIncInfoPartner (false);
// N
			setIncInfoProd (false);
// N
			setIncInfoRetencion (false);
// N
			setIncInfoTax (false);
// N
			setIncSaldoInicial (true);
// Y
			setIncTotMensual (false);
// N
			setIsCierreDiferencial (true);
// Y
			setIsCierreIntegral (true);
// Y
			setReemplazado (true);
// Y
			setTipoAcctBrowser (null);
// MAYOR
			setTipoBalanceAcct (null);
// COMPLETO
			setTipoFiltroMonAcct (null);
// UNA_MONEDA
			setZ_AcctBrowser_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctBrowser (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctBrowser[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** AccountType AD_Reference_ID=117 */
	public static final int ACCOUNTTYPE_AD_Reference_ID=117;
	/** Asset = A */
	public static final String ACCOUNTTYPE_Asset = "A";
	/** Liability = L */
	public static final String ACCOUNTTYPE_Liability = "L";
	/** Revenue = R */
	public static final String ACCOUNTTYPE_Revenue = "R";
	/** Expense = E */
	public static final String ACCOUNTTYPE_Expense = "E";
	/** Owner's Equity = O */
	public static final String ACCOUNTTYPE_OwnerSEquity = "O";
	/** Memo = M */
	public static final String ACCOUNTTYPE_Memo = "M";
	/** Set Account Type.
		@param AccountType 
		Indicates the type of account
	  */
	public void setAccountType (String AccountType)
	{

		set_Value (COLUMNNAME_AccountType, AccountType);
	}

	/** Get Account Type.
		@return Indicates the type of account
	  */
	public String getAccountType () 
	{
		return (String)get_Value(COLUMNNAME_AccountType);
	}

	/** Set Anulado.
		@param Anulado 
		Si esta anulado o no
	  */
	public void setAnulado (boolean Anulado)
	{
		set_Value (COLUMNNAME_Anulado, Boolean.valueOf(Anulado));
	}

	/** Get Anulado.
		@return Si esta anulado o no
	  */
	public boolean isAnulado () 
	{
		Object oo = get_Value(COLUMNNAME_Anulado);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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

	public I_C_BP_Group getC_BP_Group() throws RuntimeException
    {
		return (I_C_BP_Group)MTable.get(getCtx(), I_C_BP_Group.Table_Name)
			.getPO(getC_BP_Group_ID(), get_TrxName());	}

	/** Set Business Partner Group.
		@param C_BP_Group_ID 
		Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID)
	{
		if (C_BP_Group_ID < 1) 
			set_Value (COLUMNNAME_C_BP_Group_ID, null);
		else 
			set_Value (COLUMNNAME_C_BP_Group_ID, Integer.valueOf(C_BP_Group_ID));
	}

	/** Get Business Partner Group.
		@return Business Partner Group
	  */
	public int getC_BP_Group_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BP_Group_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set C_Currency_2_ID.
		@param C_Currency_2_ID 
		Moneda secundaria para procesos
	  */
	public void setC_Currency_2_ID (int C_Currency_2_ID)
	{
		if (C_Currency_2_ID < 1) 
			set_Value (COLUMNNAME_C_Currency_2_ID, null);
		else 
			set_Value (COLUMNNAME_C_Currency_2_ID, Integer.valueOf(C_Currency_2_ID));
	}

	/** Get C_Currency_2_ID.
		@return Moneda secundaria para procesos
	  */
	public int getC_Currency_2_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Currency_2_ID);
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

	/** Set Conciliado.
		@param Conciliado 
		Conciliado si o no
	  */
	public void setConciliado (boolean Conciliado)
	{
		set_Value (COLUMNNAME_Conciliado, Boolean.valueOf(Conciliado));
	}

	/** Get Conciliado.
		@return Conciliado si o no
	  */
	public boolean isConciliado () 
	{
		Object oo = get_Value(COLUMNNAME_Conciliado);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Depositado.
		@param Depositado 
		Si esta o no depositado
	  */
	public void setDepositado (boolean Depositado)
	{
		set_Value (COLUMNNAME_Depositado, Boolean.valueOf(Depositado));
	}

	/** Get Depositado.
		@return Si esta o no depositado
	  */
	public boolean isDepositado () 
	{
		Object oo = get_Value(COLUMNNAME_Depositado);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Editable.
		@param Editable 
		SI un registro es o no editable
	  */
	public void setEditable (boolean Editable)
	{
		set_Value (COLUMNNAME_Editable, Boolean.valueOf(Editable));
	}

	/** Get Editable.
		@return SI un registro es o no editable
	  */
	public boolean isEditable () 
	{
		Object oo = get_Value(COLUMNNAME_Editable);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Emitido.
		@param Emitido 
		Documento emitido
	  */
	public void setEmitido (boolean Emitido)
	{
		set_Value (COLUMNNAME_Emitido, Boolean.valueOf(Emitido));
	}

	/** Get Emitido.
		@return Documento emitido
	  */
	public boolean isEmitido () 
	{
		Object oo = get_Value(COLUMNNAME_Emitido);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set End Date.
		@param EndDate 
		Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate)
	{
		set_Value (COLUMNNAME_EndDate, EndDate);
	}

	/** Get End Date.
		@return Last effective date (inclusive)
	  */
	public Timestamp getEndDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate);
	}

	/** Set Entregado.
		@param Entregado 
		Si esta entregado o no
	  */
	public void setEntregado (boolean Entregado)
	{
		set_Value (COLUMNNAME_Entregado, Boolean.valueOf(Entregado));
	}

	/** Get Entregado.
		@return Si esta entregado o no
	  */
	public boolean isEntregado () 
	{
		Object oo = get_Value(COLUMNNAME_Entregado);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set FiltroEstadoMPago.
		@param FiltroEstadoMPago 
		Si se desea filtrar o no información según estado de medios de pago
	  */
	public void setFiltroEstadoMPago (boolean FiltroEstadoMPago)
	{
		set_Value (COLUMNNAME_FiltroEstadoMPago, Boolean.valueOf(FiltroEstadoMPago));
	}

	/** Get FiltroEstadoMPago.
		@return Si se desea filtrar o no información según estado de medios de pago
	  */
	public boolean isFiltroEstadoMPago () 
	{
		Object oo = get_Value(COLUMNNAME_FiltroEstadoMPago);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set FiltroManual.
		@param FiltroManual 
		Si se indica o no filtros de información de manera manual
	  */
	public void setFiltroManual (boolean FiltroManual)
	{
		set_Value (COLUMNNAME_FiltroManual, Boolean.valueOf(FiltroManual));
	}

	/** Get FiltroManual.
		@return Si se indica o no filtros de información de manera manual
	  */
	public boolean isFiltroManual () 
	{
		Object oo = get_Value(COLUMNNAME_FiltroManual);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncCtaSaldoSinMov.
		@param IncCtaSaldoSinMov 
		Incluye cuentas contables con saldo sin movimientos
	  */
	public void setIncCtaSaldoSinMov (boolean IncCtaSaldoSinMov)
	{
		set_Value (COLUMNNAME_IncCtaSaldoSinMov, Boolean.valueOf(IncCtaSaldoSinMov));
	}

	/** Get IncCtaSaldoSinMov.
		@return Incluye cuentas contables con saldo sin movimientos
	  */
	public boolean isIncCtaSaldoSinMov () 
	{
		Object oo = get_Value(COLUMNNAME_IncCtaSaldoSinMov);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncCtaSinSaldoConMov.
		@param IncCtaSinSaldoConMov 
		Incluye cuentas contables sin saldo con movimientos
	  */
	public void setIncCtaSinSaldoConMov (boolean IncCtaSinSaldoConMov)
	{
		set_Value (COLUMNNAME_IncCtaSinSaldoConMov, Boolean.valueOf(IncCtaSinSaldoConMov));
	}

	/** Get IncCtaSinSaldoConMov.
		@return Incluye cuentas contables sin saldo con movimientos
	  */
	public boolean isIncCtaSinSaldoConMov () 
	{
		Object oo = get_Value(COLUMNNAME_IncCtaSinSaldoConMov);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncCtaSinSaldoSinMov.
		@param IncCtaSinSaldoSinMov 
		Incluye cuentas contables sin saldo sin movimiento
	  */
	public void setIncCtaSinSaldoSinMov (boolean IncCtaSinSaldoSinMov)
	{
		set_Value (COLUMNNAME_IncCtaSinSaldoSinMov, Boolean.valueOf(IncCtaSinSaldoSinMov));
	}

	/** Get IncCtaSinSaldoSinMov.
		@return Incluye cuentas contables sin saldo sin movimiento
	  */
	public boolean isIncCtaSinSaldoSinMov () 
	{
		Object oo = get_Value(COLUMNNAME_IncCtaSinSaldoSinMov);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoAuditoria.
		@param IncInfoAuditoria 
		Incluye información de auditoría
	  */
	public void setIncInfoAuditoria (boolean IncInfoAuditoria)
	{
		set_Value (COLUMNNAME_IncInfoAuditoria, Boolean.valueOf(IncInfoAuditoria));
	}

	/** Get IncInfoAuditoria.
		@return Incluye información de auditoría
	  */
	public boolean isIncInfoAuditoria () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoAuditoria);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoDocumento.
		@param IncInfoDocumento 
		Incluye información de documentos
	  */
	public void setIncInfoDocumento (boolean IncInfoDocumento)
	{
		set_Value (COLUMNNAME_IncInfoDocumento, Boolean.valueOf(IncInfoDocumento));
	}

	/** Get IncInfoDocumento.
		@return Incluye información de documentos
	  */
	public boolean isIncInfoDocumento () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoDocumento);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoMedioPago.
		@param IncInfoMedioPago 
		Incluye información de medios de pago
	  */
	public void setIncInfoMedioPago (boolean IncInfoMedioPago)
	{
		set_Value (COLUMNNAME_IncInfoMedioPago, Boolean.valueOf(IncInfoMedioPago));
	}

	/** Get IncInfoMedioPago.
		@return Incluye información de medios de pago
	  */
	public boolean isIncInfoMedioPago () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoMedioPago);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoPartner.
		@param IncInfoPartner 
		Incluye información de socios de negocio
	  */
	public void setIncInfoPartner (boolean IncInfoPartner)
	{
		set_Value (COLUMNNAME_IncInfoPartner, Boolean.valueOf(IncInfoPartner));
	}

	/** Get IncInfoPartner.
		@return Incluye información de socios de negocio
	  */
	public boolean isIncInfoPartner () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoPartner);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoProd.
		@param IncInfoProd 
		Incluye información de productos
	  */
	public void setIncInfoProd (boolean IncInfoProd)
	{
		set_Value (COLUMNNAME_IncInfoProd, Boolean.valueOf(IncInfoProd));
	}

	/** Get IncInfoProd.
		@return Incluye información de productos
	  */
	public boolean isIncInfoProd () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoProd);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoRetencion.
		@param IncInfoRetencion 
		Incluye información de retenciones
	  */
	public void setIncInfoRetencion (boolean IncInfoRetencion)
	{
		set_Value (COLUMNNAME_IncInfoRetencion, Boolean.valueOf(IncInfoRetencion));
	}

	/** Get IncInfoRetencion.
		@return Incluye información de retenciones
	  */
	public boolean isIncInfoRetencion () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoRetencion);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncInfoTax.
		@param IncInfoTax 
		Incluye información de impuestos
	  */
	public void setIncInfoTax (boolean IncInfoTax)
	{
		set_Value (COLUMNNAME_IncInfoTax, Boolean.valueOf(IncInfoTax));
	}

	/** Get IncInfoTax.
		@return Incluye información de impuestos
	  */
	public boolean isIncInfoTax () 
	{
		Object oo = get_Value(COLUMNNAME_IncInfoTax);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncSaldoInicial.
		@param IncSaldoInicial 
		Si se incluye o no el saldo inicial
	  */
	public void setIncSaldoInicial (boolean IncSaldoInicial)
	{
		set_Value (COLUMNNAME_IncSaldoInicial, Boolean.valueOf(IncSaldoInicial));
	}

	/** Get IncSaldoInicial.
		@return Si se incluye o no el saldo inicial
	  */
	public boolean isIncSaldoInicial () 
	{
		Object oo = get_Value(COLUMNNAME_IncSaldoInicial);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IncTotMensual.
		@param IncTotMensual 
		Si incluye o no totales mensuales en informe
	  */
	public void setIncTotMensual (boolean IncTotMensual)
	{
		set_Value (COLUMNNAME_IncTotMensual, Boolean.valueOf(IncTotMensual));
	}

	/** Get IncTotMensual.
		@return Si incluye o no totales mensuales en informe
	  */
	public boolean isIncTotMensual () 
	{
		Object oo = get_Value(COLUMNNAME_IncTotMensual);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IsCierreDiferencial.
		@param IsCierreDiferencial 
		Si se considera o no cierre de cuentas diferenciales
	  */
	public void setIsCierreDiferencial (boolean IsCierreDiferencial)
	{
		set_Value (COLUMNNAME_IsCierreDiferencial, Boolean.valueOf(IsCierreDiferencial));
	}

	/** Get IsCierreDiferencial.
		@return Si se considera o no cierre de cuentas diferenciales
	  */
	public boolean isCierreDiferencial () 
	{
		Object oo = get_Value(COLUMNNAME_IsCierreDiferencial);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set IsCierreIntegral.
		@param IsCierreIntegral 
		Si considera o no cierra y apertura de cuentas integrales
	  */
	public void setIsCierreIntegral (boolean IsCierreIntegral)
	{
		set_Value (COLUMNNAME_IsCierreIntegral, Boolean.valueOf(IsCierreIntegral));
	}

	/** Get IsCierreIntegral.
		@return Si considera o no cierra y apertura de cuentas integrales
	  */
	public boolean isCierreIntegral () 
	{
		Object oo = get_Value(COLUMNNAME_IsCierreIntegral);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	public I_M_Product_Category getM_Product_Category() throws RuntimeException
    {
		return (I_M_Product_Category)MTable.get(getCtx(), I_M_Product_Category.Table_Name)
			.getPO(getM_Product_Category_ID(), get_TrxName());	}

	/** Set Product Category.
		@param M_Product_Category_ID 
		Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID)
	{
		if (M_Product_Category_ID < 1) 
			set_Value (COLUMNNAME_M_Product_Category_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_Category_ID, Integer.valueOf(M_Product_Category_ID));
	}

	/** Get Product Category.
		@return Category of a Product
	  */
	public int getM_Product_Category_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_Category_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name 
		Alphanumeric identifier of the entity
	  */
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName () 
	{
		return (String)get_Value(COLUMNNAME_Name);
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

	/** Set ProcessButton2.
		@param ProcessButton2 
		Botón de Proceso
	  */
	public void setProcessButton2 (String ProcessButton2)
	{
		set_Value (COLUMNNAME_ProcessButton2, ProcessButton2);
	}

	/** Get ProcessButton2.
		@return Botón de Proceso
	  */
	public String getProcessButton2 () 
	{
		return (String)get_Value(COLUMNNAME_ProcessButton2);
	}

	/** Set ProcessButton3.
		@param ProcessButton3 
		Botón para proceso
	  */
	public void setProcessButton3 (String ProcessButton3)
	{
		set_Value (COLUMNNAME_ProcessButton3, ProcessButton3);
	}

	/** Get ProcessButton3.
		@return Botón para proceso
	  */
	public String getProcessButton3 () 
	{
		return (String)get_Value(COLUMNNAME_ProcessButton3);
	}

	/** Set ProcessButton4.
		@param ProcessButton4 
		Botón de Proceso
	  */
	public void setProcessButton4 (String ProcessButton4)
	{
		set_Value (COLUMNNAME_ProcessButton4, ProcessButton4);
	}

	/** Get ProcessButton4.
		@return Botón de Proceso
	  */
	public String getProcessButton4 () 
	{
		return (String)get_Value(COLUMNNAME_ProcessButton4);
	}

	/** ProductType AD_Reference_ID=270 */
	public static final int PRODUCTTYPE_AD_Reference_ID=270;
	/** Item = I */
	public static final String PRODUCTTYPE_Item = "I";
	/** Service = S */
	public static final String PRODUCTTYPE_Service = "S";
	/** Resource = R */
	public static final String PRODUCTTYPE_Resource = "R";
	/** Expense type = E */
	public static final String PRODUCTTYPE_ExpenseType = "E";
	/** Online = O */
	public static final String PRODUCTTYPE_Online = "O";
	/** Set Product Type.
		@param ProductType 
		Type of product
	  */
	public void setProductType (String ProductType)
	{

		set_Value (COLUMNNAME_ProductType, ProductType);
	}

	/** Get Product Type.
		@return Type of product
	  */
	public String getProductType () 
	{
		return (String)get_Value(COLUMNNAME_ProductType);
	}

	/** Set Reemplazado.
		@param Reemplazado 
		Reemplazado si o no
	  */
	public void setReemplazado (boolean Reemplazado)
	{
		set_Value (COLUMNNAME_Reemplazado, Boolean.valueOf(Reemplazado));
	}

	/** Get Reemplazado.
		@return Reemplazado si o no
	  */
	public boolean isReemplazado () 
	{
		Object oo = get_Value(COLUMNNAME_Reemplazado);
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

	/** Set TextoFiltro.
		@param TextoFiltro 
		Texto genérico para filtro de valores
	  */
	public void setTextoFiltro (String TextoFiltro)
	{
		set_Value (COLUMNNAME_TextoFiltro, TextoFiltro);
	}

	/** Get TextoFiltro.
		@return Texto genérico para filtro de valores
	  */
	public String getTextoFiltro () 
	{
		return (String)get_Value(COLUMNNAME_TextoFiltro);
	}

	/** Set TextoFiltro2.
		@param TextoFiltro2 
		Texto genérico para filtro de valores
	  */
	public void setTextoFiltro2 (String TextoFiltro2)
	{
		set_Value (COLUMNNAME_TextoFiltro2, TextoFiltro2);
	}

	/** Get TextoFiltro2.
		@return Texto genérico para filtro de valores
	  */
	public String getTextoFiltro2 () 
	{
		return (String)get_Value(COLUMNNAME_TextoFiltro2);
	}

	/** TipoAcctBrowser AD_Reference_ID=1000051 */
	public static final int TIPOACCTBROWSER_AD_Reference_ID=1000051;
	/** BALANCE CONTABLE = BALANCE */
	public static final String TIPOACCTBROWSER_BALANCECONTABLE = "BALANCE";
	/** MAYOR CONTABLE = MAYOR */
	public static final String TIPOACCTBROWSER_MAYORCONTABLE = "MAYOR";
	/** Set TipoAcctBrowser.
		@param TipoAcctBrowser 
		Tipo de consulta en el Navegador Contable
	  */
	public void setTipoAcctBrowser (String TipoAcctBrowser)
	{

		set_Value (COLUMNNAME_TipoAcctBrowser, TipoAcctBrowser);
	}

	/** Get TipoAcctBrowser.
		@return Tipo de consulta en el Navegador Contable
	  */
	public String getTipoAcctBrowser () 
	{
		return (String)get_Value(COLUMNNAME_TipoAcctBrowser);
	}

	/** TipoBalanceAcct AD_Reference_ID=1000039 */
	public static final int TIPOBALANCEACCT_AD_Reference_ID=1000039;
	/** BALANCE COMPLETO = COMPLETO */
	public static final String TIPOBALANCEACCT_BALANCECOMPLETO = "COMPLETO";
	/** ESTADO DE RESULTADOS = RESULTADO */
	public static final String TIPOBALANCEACCT_ESTADODERESULTADOS = "RESULTADO";
	/** ESTADO DE SITUACIÓN PATRIMONIAL = PATRIMONIAL */
	public static final String TIPOBALANCEACCT_ESTADODESITUACIÓNPATRIMONIAL = "PATRIMONIAL";
	/** Set TipoBalanceAcct.
		@param TipoBalanceAcct 
		Tipo de Balance Contable
	  */
	public void setTipoBalanceAcct (String TipoBalanceAcct)
	{

		set_Value (COLUMNNAME_TipoBalanceAcct, TipoBalanceAcct);
	}

	/** Get TipoBalanceAcct.
		@return Tipo de Balance Contable
	  */
	public String getTipoBalanceAcct () 
	{
		return (String)get_Value(COLUMNNAME_TipoBalanceAcct);
	}

	/** TipoFiltroMonAcct AD_Reference_ID=1000052 */
	public static final int TIPOFILTROMONACCT_AD_Reference_ID=1000052;
	/** DOS MONEDAS = DOS_MONEDAS */
	public static final String TIPOFILTROMONACCT_DOSMONEDAS = "DOS_MONEDAS";
	/** UNA MONEDA = UNA_MONEDA */
	public static final String TIPOFILTROMONACCT_UNAMONEDA = "UNA_MONEDA";
	/** Set TipoFiltroMonAcct.
		@param TipoFiltroMonAcct 
		Tipo filtro de moneda para reportes contables
	  */
	public void setTipoFiltroMonAcct (String TipoFiltroMonAcct)
	{

		set_Value (COLUMNNAME_TipoFiltroMonAcct, TipoFiltroMonAcct);
	}

	/** Get TipoFiltroMonAcct.
		@return Tipo filtro de moneda para reportes contables
	  */
	public String getTipoFiltroMonAcct () 
	{
		return (String)get_Value(COLUMNNAME_TipoFiltroMonAcct);
	}

	/** TipoMayorAcct AD_Reference_ID=1000053 */
	public static final int TIPOMAYORACCT_AD_Reference_ID=1000053;
	/** MAYOR ANALITICO = ANALITICO */
	public static final String TIPOMAYORACCT_MAYORANALITICO = "ANALITICO";
	/** Set TipoMayorAcct.
		@param TipoMayorAcct 
		Tipo de Mayor Contable
	  */
	public void setTipoMayorAcct (String TipoMayorAcct)
	{

		set_Value (COLUMNNAME_TipoMayorAcct, TipoMayorAcct);
	}

	/** Get TipoMayorAcct.
		@return Tipo de Mayor Contable
	  */
	public String getTipoMayorAcct () 
	{
		return (String)get_Value(COLUMNNAME_TipoMayorAcct);
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

	/** Set Z_AcctBrowser ID.
		@param Z_AcctBrowser_ID Z_AcctBrowser ID	  */
	public void setZ_AcctBrowser_ID (int Z_AcctBrowser_ID)
	{
		if (Z_AcctBrowser_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowser_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowser_ID, Integer.valueOf(Z_AcctBrowser_ID));
	}

	/** Get Z_AcctBrowser ID.
		@return Z_AcctBrowser ID	  */
	public int getZ_AcctBrowser_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctBrowser_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_DataFiltro ID.
		@param Z_DataFiltro_ID Z_DataFiltro ID	  */
	public void setZ_DataFiltro_ID (int Z_DataFiltro_ID)
	{
		if (Z_DataFiltro_ID < 1) 
			set_Value (COLUMNNAME_Z_DataFiltro_ID, null);
		else 
			set_Value (COLUMNNAME_Z_DataFiltro_ID, Integer.valueOf(Z_DataFiltro_ID));
	}

	/** Get Z_DataFiltro ID.
		@return Z_DataFiltro ID	  */
	public int getZ_DataFiltro_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_DataFiltro_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}