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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for Z_AcctBrowSumBal
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctBrowSumBal extends PO implements I_Z_AcctBrowSumBal, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190312L;

    /** Standard Constructor */
    public X_Z_AcctBrowSumBal (Properties ctx, int Z_AcctBrowSumBal_ID, String trxName)
    {
      super (ctx, Z_AcctBrowSumBal_ID, trxName);
      /** if (Z_AcctBrowSumBal_ID == 0)
        {
			setUUID (null);
			setZ_AcctBrowser_ID (0);
			setZ_AcctBrowSumBal_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctBrowSumBal (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctBrowSumBal[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set AmtActivo1.
		@param AmtActivo1 
		Monto Activo
	  */
	public void setAmtActivo1 (BigDecimal AmtActivo1)
	{
		set_Value (COLUMNNAME_AmtActivo1, AmtActivo1);
	}

	/** Get AmtActivo1.
		@return Monto Activo
	  */
	public BigDecimal getAmtActivo1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtActivo1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtActivo2.
		@param AmtActivo2 
		Monto Activo
	  */
	public void setAmtActivo2 (BigDecimal AmtActivo2)
	{
		set_Value (COLUMNNAME_AmtActivo2, AmtActivo2);
	}

	/** Get AmtActivo2.
		@return Monto Activo
	  */
	public BigDecimal getAmtActivo2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtActivo2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtControl1.
		@param AmtControl1 
		Monto de Control
	  */
	public void setAmtControl1 (BigDecimal AmtControl1)
	{
		set_Value (COLUMNNAME_AmtControl1, AmtControl1);
	}

	/** Get AmtControl1.
		@return Monto de Control
	  */
	public BigDecimal getAmtControl1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtControl1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtControl2.
		@param AmtControl2 
		Monto de Control
	  */
	public void setAmtControl2 (BigDecimal AmtControl2)
	{
		set_Value (COLUMNNAME_AmtControl2, AmtControl2);
	}

	/** Get AmtControl2.
		@return Monto de Control
	  */
	public BigDecimal getAmtControl2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtControl2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtGastos1.
		@param AmtGastos1 
		Monto Gastos
	  */
	public void setAmtGastos1 (BigDecimal AmtGastos1)
	{
		set_Value (COLUMNNAME_AmtGastos1, AmtGastos1);
	}

	/** Get AmtGastos1.
		@return Monto Gastos
	  */
	public BigDecimal getAmtGastos1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtGastos1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtGastos2.
		@param AmtGastos2 
		Monto Gastos
	  */
	public void setAmtGastos2 (BigDecimal AmtGastos2)
	{
		set_Value (COLUMNNAME_AmtGastos2, AmtGastos2);
	}

	/** Get AmtGastos2.
		@return Monto Gastos
	  */
	public BigDecimal getAmtGastos2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtGastos2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtIngresos1.
		@param AmtIngresos1 
		Monto Ingresos
	  */
	public void setAmtIngresos1 (BigDecimal AmtIngresos1)
	{
		set_Value (COLUMNNAME_AmtIngresos1, AmtIngresos1);
	}

	/** Get AmtIngresos1.
		@return Monto Ingresos
	  */
	public BigDecimal getAmtIngresos1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtIngresos1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtIngresos2.
		@param AmtIngresos2 
		Monto Ingresos
	  */
	public void setAmtIngresos2 (BigDecimal AmtIngresos2)
	{
		set_Value (COLUMNNAME_AmtIngresos2, AmtIngresos2);
	}

	/** Get AmtIngresos2.
		@return Monto Ingresos
	  */
	public BigDecimal getAmtIngresos2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtIngresos2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtPasivo1.
		@param AmtPasivo1 
		Monto Pasivo
	  */
	public void setAmtPasivo1 (BigDecimal AmtPasivo1)
	{
		set_Value (COLUMNNAME_AmtPasivo1, AmtPasivo1);
	}

	/** Get AmtPasivo1.
		@return Monto Pasivo
	  */
	public BigDecimal getAmtPasivo1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtPasivo1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtPasivo2.
		@param AmtPasivo2 
		Monto Pasivo
	  */
	public void setAmtPasivo2 (BigDecimal AmtPasivo2)
	{
		set_Value (COLUMNNAME_AmtPasivo2, AmtPasivo2);
	}

	/** Get AmtPasivo2.
		@return Monto Pasivo
	  */
	public BigDecimal getAmtPasivo2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtPasivo2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtPatrimonio1.
		@param AmtPatrimonio1 
		Monto Patrimonio
	  */
	public void setAmtPatrimonio1 (BigDecimal AmtPatrimonio1)
	{
		set_Value (COLUMNNAME_AmtPatrimonio1, AmtPatrimonio1);
	}

	/** Get AmtPatrimonio1.
		@return Monto Patrimonio
	  */
	public BigDecimal getAmtPatrimonio1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtPatrimonio1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtPatrimonio2.
		@param AmtPatrimonio2 
		Monto Patrimonio
	  */
	public void setAmtPatrimonio2 (BigDecimal AmtPatrimonio2)
	{
		set_Value (COLUMNNAME_AmtPatrimonio2, AmtPatrimonio2);
	}

	/** Get AmtPatrimonio2.
		@return Monto Patrimonio
	  */
	public BigDecimal getAmtPatrimonio2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtPatrimonio2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtResultado1.
		@param AmtResultado1 
		Monto Resultado
	  */
	public void setAmtResultado1 (BigDecimal AmtResultado1)
	{
		set_Value (COLUMNNAME_AmtResultado1, AmtResultado1);
	}

	/** Get AmtResultado1.
		@return Monto Resultado
	  */
	public BigDecimal getAmtResultado1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtResultado1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtResultado2.
		@param AmtResultado2 
		Monto Resultado
	  */
	public void setAmtResultado2 (BigDecimal AmtResultado2)
	{
		set_Value (COLUMNNAME_AmtResultado2, AmtResultado2);
	}

	/** Get AmtResultado2.
		@return Monto Resultado
	  */
	public BigDecimal getAmtResultado2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtResultado2);
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

	public I_Z_AcctBrowser getZ_AcctBrowser() throws RuntimeException
    {
		return (I_Z_AcctBrowser)MTable.get(getCtx(), I_Z_AcctBrowser.Table_Name)
			.getPO(getZ_AcctBrowser_ID(), get_TrxName());	}

	/** Set Z_AcctBrowser ID.
		@param Z_AcctBrowser_ID Z_AcctBrowser ID	  */
	public void setZ_AcctBrowser_ID (int Z_AcctBrowser_ID)
	{
		if (Z_AcctBrowser_ID < 1) 
			set_Value (COLUMNNAME_Z_AcctBrowser_ID, null);
		else 
			set_Value (COLUMNNAME_Z_AcctBrowser_ID, Integer.valueOf(Z_AcctBrowser_ID));
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

	/** Set Z_AcctBrowSumBal ID.
		@param Z_AcctBrowSumBal_ID Z_AcctBrowSumBal ID	  */
	public void setZ_AcctBrowSumBal_ID (int Z_AcctBrowSumBal_ID)
	{
		if (Z_AcctBrowSumBal_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowSumBal_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowSumBal_ID, Integer.valueOf(Z_AcctBrowSumBal_ID));
	}

	/** Get Z_AcctBrowSumBal ID.
		@return Z_AcctBrowSumBal ID	  */
	public int getZ_AcctBrowSumBal_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctBrowSumBal_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}