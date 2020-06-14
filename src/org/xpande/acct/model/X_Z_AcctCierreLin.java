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

/** Generated Model for Z_AcctCierreLin
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctCierreLin extends PO implements I_Z_AcctCierreLin, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200613L;

    /** Standard Constructor */
    public X_Z_AcctCierreLin (Properties ctx, int Z_AcctCierreLin_ID, String trxName)
    {
      super (ctx, Z_AcctCierreLin_ID, trxName);
      /** if (Z_AcctCierreLin_ID == 0)
        {
			setAmtAcctCr (Env.ZERO);
			setAmtAcctCrTo (Env.ZERO);
			setAmtAcctDr (Env.ZERO);
			setAmtAcctDrTo (Env.ZERO);
			setC_ElementValue_ID (0);
			setCodigoCuenta (null);
			setDifferenceAmt (Env.ZERO);
			setZ_AcctCierre_ID (0);
			setZ_AcctCierreLin_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctCierreLin (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctCierreLin[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Accounted Credit.
		@param AmtAcctCr 
		Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr)
	{
		set_Value (COLUMNNAME_AmtAcctCr, AmtAcctCr);
	}

	/** Get Accounted Credit.
		@return Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtAcctCrTo.
		@param AmtAcctCrTo 
		Monto crédito final en moneda nacional
	  */
	public void setAmtAcctCrTo (BigDecimal AmtAcctCrTo)
	{
		set_Value (COLUMNNAME_AmtAcctCrTo, AmtAcctCrTo);
	}

	/** Get AmtAcctCrTo.
		@return Monto crédito final en moneda nacional
	  */
	public BigDecimal getAmtAcctCrTo () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctCrTo);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Accounted Debit.
		@param AmtAcctDr 
		Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr)
	{
		set_Value (COLUMNNAME_AmtAcctDr, AmtAcctDr);
	}

	/** Get Accounted Debit.
		@return Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtAcctDrTo.
		@param AmtAcctDrTo 
		Monto débito final en moneda nacional
	  */
	public void setAmtAcctDrTo (BigDecimal AmtAcctDrTo)
	{
		set_Value (COLUMNNAME_AmtAcctDrTo, AmtAcctDrTo);
	}

	/** Get AmtAcctDrTo.
		@return Monto débito final en moneda nacional
	  */
	public BigDecimal getAmtAcctDrTo () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctDrTo);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set CodigoCuenta.
		@param CodigoCuenta 
		Codigo de cuenta contable
	  */
	public void setCodigoCuenta (String CodigoCuenta)
	{
		set_Value (COLUMNNAME_CodigoCuenta, CodigoCuenta);
	}

	/** Get CodigoCuenta.
		@return Codigo de cuenta contable
	  */
	public String getCodigoCuenta () 
	{
		return (String)get_Value(COLUMNNAME_CodigoCuenta);
	}

	/** Set Difference.
		@param DifferenceAmt 
		Difference Amount
	  */
	public void setDifferenceAmt (BigDecimal DifferenceAmt)
	{
		set_Value (COLUMNNAME_DifferenceAmt, DifferenceAmt);
	}

	/** Get Difference.
		@return Difference Amount
	  */
	public BigDecimal getDifferenceAmt () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_DifferenceAmt);
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

	public I_Z_AcctCierre getZ_AcctCierre() throws RuntimeException
    {
		return (I_Z_AcctCierre)MTable.get(getCtx(), I_Z_AcctCierre.Table_Name)
			.getPO(getZ_AcctCierre_ID(), get_TrxName());	}

	/** Set Z_AcctCierre ID.
		@param Z_AcctCierre_ID Z_AcctCierre ID	  */
	public void setZ_AcctCierre_ID (int Z_AcctCierre_ID)
	{
		if (Z_AcctCierre_ID < 1) 
			set_Value (COLUMNNAME_Z_AcctCierre_ID, null);
		else 
			set_Value (COLUMNNAME_Z_AcctCierre_ID, Integer.valueOf(Z_AcctCierre_ID));
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

	/** Set Z_AcctCierreLin ID.
		@param Z_AcctCierreLin_ID Z_AcctCierreLin ID	  */
	public void setZ_AcctCierreLin_ID (int Z_AcctCierreLin_ID)
	{
		if (Z_AcctCierreLin_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctCierreLin_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctCierreLin_ID, Integer.valueOf(Z_AcctCierreLin_ID));
	}

	/** Get Z_AcctCierreLin ID.
		@return Z_AcctCierreLin ID	  */
	public int getZ_AcctCierreLin_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctCierreLin_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}