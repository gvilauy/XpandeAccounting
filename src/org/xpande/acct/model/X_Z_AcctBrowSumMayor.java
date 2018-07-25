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

/** Generated Model for Z_AcctBrowSumMayor
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctBrowSumMayor extends PO implements I_Z_AcctBrowSumMayor, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180724L;

    /** Standard Constructor */
    public X_Z_AcctBrowSumMayor (Properties ctx, int Z_AcctBrowSumMayor_ID, String trxName)
    {
      super (ctx, Z_AcctBrowSumMayor_ID, trxName);
      /** if (Z_AcctBrowSumMayor_ID == 0)
        {
			setC_Currency_ID (0);
			setC_ElementValue_ID (0);
			setZ_AcctBrowser_ID (0);
			setZ_AcctBrowSumMayor_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctBrowSumMayor (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctBrowSumMayor[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set AmtCr1.
		@param AmtCr1 
		Monto crédito uno
	  */
	public void setAmtCr1 (BigDecimal AmtCr1)
	{
		set_Value (COLUMNNAME_AmtCr1, AmtCr1);
	}

	/** Get AmtCr1.
		@return Monto crédito uno
	  */
	public BigDecimal getAmtCr1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtCr1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtCr2.
		@param AmtCr2 
		Monto crédito dos
	  */
	public void setAmtCr2 (BigDecimal AmtCr2)
	{
		set_Value (COLUMNNAME_AmtCr2, AmtCr2);
	}

	/** Get AmtCr2.
		@return Monto crédito dos
	  */
	public BigDecimal getAmtCr2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtCr2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtDr1.
		@param AmtDr1 
		Monto débito uno
	  */
	public void setAmtDr1 (BigDecimal AmtDr1)
	{
		set_Value (COLUMNNAME_AmtDr1, AmtDr1);
	}

	/** Get AmtDr1.
		@return Monto débito uno
	  */
	public BigDecimal getAmtDr1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtDr1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtDr2.
		@param AmtDr2 
		Monto débito dos
	  */
	public void setAmtDr2 (BigDecimal AmtDr2)
	{
		set_Value (COLUMNNAME_AmtDr2, AmtDr2);
	}

	/** Get AmtDr2.
		@return Monto débito dos
	  */
	public BigDecimal getAmtDr2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtDr2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtInicial1.
		@param AmtInicial1 
		Monto inicial uno
	  */
	public void setAmtInicial1 (BigDecimal AmtInicial1)
	{
		set_Value (COLUMNNAME_AmtInicial1, AmtInicial1);
	}

	/** Get AmtInicial1.
		@return Monto inicial uno
	  */
	public BigDecimal getAmtInicial1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtInicial1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtInicial2.
		@param AmtInicial2 
		Monto inicial dos
	  */
	public void setAmtInicial2 (BigDecimal AmtInicial2)
	{
		set_Value (COLUMNNAME_AmtInicial2, AmtInicial2);
	}

	/** Get AmtInicial2.
		@return Monto inicial dos
	  */
	public BigDecimal getAmtInicial2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtInicial2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtSubtotal1.
		@param AmtSubtotal1 
		Monto subtotal uno
	  */
	public void setAmtSubtotal1 (BigDecimal AmtSubtotal1)
	{
		set_Value (COLUMNNAME_AmtSubtotal1, AmtSubtotal1);
	}

	/** Get AmtSubtotal1.
		@return Monto subtotal uno
	  */
	public BigDecimal getAmtSubtotal1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSubtotal1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtSubtotal2.
		@param AmtSubtotal2 
		Monto subtotal dos
	  */
	public void setAmtSubtotal2 (BigDecimal AmtSubtotal2)
	{
		set_Value (COLUMNNAME_AmtSubtotal2, AmtSubtotal2);
	}

	/** Get AmtSubtotal2.
		@return Monto subtotal dos
	  */
	public BigDecimal getAmtSubtotal2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSubtotal2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtTotal1.
		@param AmtTotal1 
		Monto total uno
	  */
	public void setAmtTotal1 (BigDecimal AmtTotal1)
	{
		set_Value (COLUMNNAME_AmtTotal1, AmtTotal1);
	}

	/** Get AmtTotal1.
		@return Monto total uno
	  */
	public BigDecimal getAmtTotal1 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtTotal1);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtTotal2.
		@param AmtTotal2 
		Monto total dos
	  */
	public void setAmtTotal2 (BigDecimal AmtTotal2)
	{
		set_Value (COLUMNNAME_AmtTotal2, AmtTotal2);
	}

	/** Get AmtTotal2.
		@return Monto total dos
	  */
	public BigDecimal getAmtTotal2 () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtTotal2);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	public I_C_ElementValue getC_ElementValue() throws RuntimeException
    {
		return (I_C_ElementValue)MTable.get(getCtx(), I_C_ElementValue.Table_Name)
			.getPO(getC_ElementValue_ID(), get_TrxName());	}

	/** Set Account Element.
		@param C_ElementValue_ID 
		Account Element
	  */
	public void setC_ElementValue_ID (int C_ElementValue_ID)
	{
		if (C_ElementValue_ID < 1) 
			set_Value (COLUMNNAME_C_ElementValue_ID, null);
		else 
			set_Value (COLUMNNAME_C_ElementValue_ID, Integer.valueOf(C_ElementValue_ID));
	}

	/** Get Account Element.
		@return Account Element
	  */
	public int getC_ElementValue_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ElementValue_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
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

	/** Set Z_AcctBrowSumMayor ID.
		@param Z_AcctBrowSumMayor_ID Z_AcctBrowSumMayor ID	  */
	public void setZ_AcctBrowSumMayor_ID (int Z_AcctBrowSumMayor_ID)
	{
		if (Z_AcctBrowSumMayor_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowSumMayor_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowSumMayor_ID, Integer.valueOf(Z_AcctBrowSumMayor_ID));
	}

	/** Get Z_AcctBrowSumMayor ID.
		@return Z_AcctBrowSumMayor ID	  */
	public int getZ_AcctBrowSumMayor_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctBrowSumMayor_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}