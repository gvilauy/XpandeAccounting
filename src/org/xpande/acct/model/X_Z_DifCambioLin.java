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

/** Generated Model for Z_DifCambioLin
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_DifCambioLin extends PO implements I_Z_DifCambioLin, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190711L;

    /** Standard Constructor */
    public X_Z_DifCambioLin (Properties ctx, int Z_DifCambioLin_ID, String trxName)
    {
      super (ctx, Z_DifCambioLin_ID, trxName);
      /** if (Z_DifCambioLin_ID == 0)
        {
			setAmtAcctCr (Env.ZERO);
			setAmtAcctCrDif (Env.ZERO);
			setAmtAcctCrTo (Env.ZERO);
			setAmtAcctDr (Env.ZERO);
			setAmtAcctDrDif (Env.ZERO);
			setAmtAcctDrTo (Env.ZERO);
			setAmtSourceCr (Env.ZERO);
			setAmtSourceCrDif (Env.ZERO);
			setAmtSourceDr (Env.ZERO);
			setAmtSourceDrDif (Env.ZERO);
			setC_Currency_ID (0);
			setC_ElementValue_ID (0);
			setCurrencyRate (Env.ZERO);
			setZ_DifCambio_ID (0);
			setZ_DifCambioLin_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_DifCambioLin (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_DifCambioLin[")
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

	/** Set AmtAcctCrDif.
		@param AmtAcctCrDif 
		Monto diferencia en créditos en moneda nacional
	  */
	public void setAmtAcctCrDif (BigDecimal AmtAcctCrDif)
	{
		set_Value (COLUMNNAME_AmtAcctCrDif, AmtAcctCrDif);
	}

	/** Get AmtAcctCrDif.
		@return Monto diferencia en créditos en moneda nacional
	  */
	public BigDecimal getAmtAcctCrDif () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctCrDif);
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

	/** Set AmtAcctDrDif.
		@param AmtAcctDrDif 
		Monto diferencia en débitos en moneda nacional
	  */
	public void setAmtAcctDrDif (BigDecimal AmtAcctDrDif)
	{
		set_Value (COLUMNNAME_AmtAcctDrDif, AmtAcctDrDif);
	}

	/** Get AmtAcctDrDif.
		@return Monto diferencia en débitos en moneda nacional
	  */
	public BigDecimal getAmtAcctDrDif () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtAcctDrDif);
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

	/** Set Source Credit.
		@param AmtSourceCr 
		Source Credit Amount
	  */
	public void setAmtSourceCr (BigDecimal AmtSourceCr)
	{
		set_Value (COLUMNNAME_AmtSourceCr, AmtSourceCr);
	}

	/** Get Source Credit.
		@return Source Credit Amount
	  */
	public BigDecimal getAmtSourceCr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSourceCr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtSourceCrDif.
		@param AmtSourceCrDif 
		Monto diferencia para créditos en moneda orígen
	  */
	public void setAmtSourceCrDif (BigDecimal AmtSourceCrDif)
	{
		set_Value (COLUMNNAME_AmtSourceCrDif, AmtSourceCrDif);
	}

	/** Get AmtSourceCrDif.
		@return Monto diferencia para créditos en moneda orígen
	  */
	public BigDecimal getAmtSourceCrDif () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSourceCrDif);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Source Debit.
		@param AmtSourceDr 
		Source Debit Amount
	  */
	public void setAmtSourceDr (BigDecimal AmtSourceDr)
	{
		set_Value (COLUMNNAME_AmtSourceDr, AmtSourceDr);
	}

	/** Get Source Debit.
		@return Source Debit Amount
	  */
	public BigDecimal getAmtSourceDr () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSourceDr);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set AmtSourceDrDif.
		@param AmtSourceDrDif 
		Monto diferencia para débitos en moneda origen
	  */
	public void setAmtSourceDrDif (BigDecimal AmtSourceDrDif)
	{
		set_Value (COLUMNNAME_AmtSourceDrDif, AmtSourceDrDif);
	}

	/** Get AmtSourceDrDif.
		@return Monto diferencia para débitos en moneda origen
	  */
	public BigDecimal getAmtSourceDrDif () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_AmtSourceDrDif);
		if (bd == null)
			 return Env.ZERO;
		return bd;
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

	/** Set Rate.
		@param CurrencyRate 
		Currency Conversion Rate
	  */
	public void setCurrencyRate (BigDecimal CurrencyRate)
	{
		set_Value (COLUMNNAME_CurrencyRate, CurrencyRate);
	}

	/** Get Rate.
		@return Currency Conversion Rate
	  */
	public BigDecimal getCurrencyRate () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_CurrencyRate);
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

	public I_Z_DifCambio getZ_DifCambio() throws RuntimeException
    {
		return (I_Z_DifCambio)MTable.get(getCtx(), I_Z_DifCambio.Table_Name)
			.getPO(getZ_DifCambio_ID(), get_TrxName());	}

	/** Set Z_DifCambio ID.
		@param Z_DifCambio_ID Z_DifCambio ID	  */
	public void setZ_DifCambio_ID (int Z_DifCambio_ID)
	{
		if (Z_DifCambio_ID < 1) 
			set_Value (COLUMNNAME_Z_DifCambio_ID, null);
		else 
			set_Value (COLUMNNAME_Z_DifCambio_ID, Integer.valueOf(Z_DifCambio_ID));
	}

	/** Get Z_DifCambio ID.
		@return Z_DifCambio ID	  */
	public int getZ_DifCambio_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_DifCambio_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_DifCambioLin ID.
		@param Z_DifCambioLin_ID Z_DifCambioLin ID	  */
	public void setZ_DifCambioLin_ID (int Z_DifCambioLin_ID)
	{
		if (Z_DifCambioLin_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_DifCambioLin_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_DifCambioLin_ID, Integer.valueOf(Z_DifCambioLin_ID));
	}

	/** Get Z_DifCambioLin ID.
		@return Z_DifCambioLin ID	  */
	public int getZ_DifCambioLin_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_DifCambioLin_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}