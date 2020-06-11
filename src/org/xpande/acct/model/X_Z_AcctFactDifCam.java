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

/** Generated Model for Z_AcctFactDifCam
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctFactDifCam extends PO implements I_Z_AcctFactDifCam, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20200611L;

    /** Standard Constructor */
    public X_Z_AcctFactDifCam (Properties ctx, int Z_AcctFactDifCam_ID, String trxName)
    {
      super (ctx, Z_AcctFactDifCam_ID, trxName);
      /** if (Z_AcctFactDifCam_ID == 0)
        {
			setCurrencyRate (Env.ZERO);
			setDateTrx (new Timestamp( System.currentTimeMillis() ));
			setFact_Acct_ID (0);
			setZ_AcctFactDifCam_ID (0);
			setZ_DifCambio_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctFactDifCam (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctFactDifCam[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Transaction Date.
		@param DateTrx 
		Transaction Date
	  */
	public void setDateTrx (Timestamp DateTrx)
	{
		set_Value (COLUMNNAME_DateTrx, DateTrx);
	}

	/** Get Transaction Date.
		@return Transaction Date
	  */
	public Timestamp getDateTrx () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateTrx);
	}

	public I_Fact_Acct getFact_Acct() throws RuntimeException
    {
		return (I_Fact_Acct)MTable.get(getCtx(), I_Fact_Acct.Table_Name)
			.getPO(getFact_Acct_ID(), get_TrxName());	}

	/** Set Accounting Fact.
		@param Fact_Acct_ID Accounting Fact	  */
	public void setFact_Acct_ID (int Fact_Acct_ID)
	{
		if (Fact_Acct_ID < 1) 
			set_Value (COLUMNNAME_Fact_Acct_ID, null);
		else 
			set_Value (COLUMNNAME_Fact_Acct_ID, Integer.valueOf(Fact_Acct_ID));
	}

	/** Get Accounting Fact.
		@return Accounting Fact	  */
	public int getFact_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Fact_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	/** Set Z_AcctFactDifCam ID.
		@param Z_AcctFactDifCam_ID Z_AcctFactDifCam ID	  */
	public void setZ_AcctFactDifCam_ID (int Z_AcctFactDifCam_ID)
	{
		if (Z_AcctFactDifCam_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctFactDifCam_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctFactDifCam_ID, Integer.valueOf(Z_AcctFactDifCam_ID));
	}

	/** Get Z_AcctFactDifCam ID.
		@return Z_AcctFactDifCam ID	  */
	public int getZ_AcctFactDifCam_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctFactDifCam_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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
}