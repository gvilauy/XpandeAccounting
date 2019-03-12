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
	private static final long serialVersionUID = 20190312L;

    /** Standard Constructor */
    public X_Z_AcctBrowser (Properties ctx, int Z_AcctBrowser_ID, String trxName)
    {
      super (ctx, Z_AcctBrowser_ID, trxName);
      /** if (Z_AcctBrowser_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setC_Currency_ID (0);
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
			setIncTotMensual (false);
// N
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

	/** TipoAcctBrowser AD_Reference_ID=1000039 */
	public static final int TIPOACCTBROWSER_AD_Reference_ID=1000039;
	/** MAYOR CONTABLE = MAYOR */
	public static final String TIPOACCTBROWSER_MAYORCONTABLE = "MAYOR";
	/** BALANCE CONTABLE = BALANCE */
	public static final String TIPOACCTBROWSER_BALANCECONTABLE = "BALANCE";
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

	/** TipoBalanceAcct AD_Reference_ID=1000050 */
	public static final int TIPOBALANCEACCT_AD_Reference_ID=1000050;
	/** BALANCE COMPLETO = COMPLETO */
	public static final String TIPOBALANCEACCT_BALANCECOMPLETO = "COMPLETO";
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

	/** TipoFiltroMonAcct AD_Reference_ID=1000040 */
	public static final int TIPOFILTROMONACCT_AD_Reference_ID=1000040;
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

	/** TipoMayorAcct AD_Reference_ID=1000041 */
	public static final int TIPOMAYORACCT_AD_Reference_ID=1000041;
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
}