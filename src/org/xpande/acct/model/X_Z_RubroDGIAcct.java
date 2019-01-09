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
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for Z_RubroDGIAcct
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_RubroDGIAcct extends PO implements I_Z_RubroDGIAcct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190108L;

    /** Standard Constructor */
    public X_Z_RubroDGIAcct (Properties ctx, int Z_RubroDGIAcct_ID, String trxName)
    {
      super (ctx, Z_RubroDGIAcct_ID, trxName);
      /** if (Z_RubroDGIAcct_ID == 0)
        {
			setAccount_ID (0);
			setC_AcctSchema_ID (0);
			setC_ValidCombination_ID (0);
			setZ_AcctConfigRubroDGI_ID (0);
			setZ_RubroDGIAcct_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_RubroDGIAcct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_RubroDGIAcct[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_ElementValue getAccount() throws RuntimeException
    {
		return (I_C_ElementValue)MTable.get(getCtx(), I_C_ElementValue.Table_Name)
			.getPO(getAccount_ID(), get_TrxName());	}

	/** Set Account.
		@param Account_ID 
		Account used
	  */
	public void setAccount_ID (int Account_ID)
	{
		if (Account_ID < 1) 
			set_Value (COLUMNNAME_Account_ID, null);
		else 
			set_Value (COLUMNNAME_Account_ID, Integer.valueOf(Account_ID));
	}

	/** Get Account.
		@return Account used
	  */
	public int getAccount_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Account_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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

	public I_C_ValidCombination getC_ValidCombination() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getC_ValidCombination_ID(), get_TrxName());	}

	/** Set Combination.
		@param C_ValidCombination_ID 
		Valid Account Combination
	  */
	public void setC_ValidCombination_ID (int C_ValidCombination_ID)
	{
		if (C_ValidCombination_ID < 1) 
			set_Value (COLUMNNAME_C_ValidCombination_ID, null);
		else 
			set_Value (COLUMNNAME_C_ValidCombination_ID, Integer.valueOf(C_ValidCombination_ID));
	}

	/** Get Combination.
		@return Valid Account Combination
	  */
	public int getC_ValidCombination_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_ValidCombination_ID);
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

	/** Set Z_RubroDGIAcct ID.
		@param Z_RubroDGIAcct_ID Z_RubroDGIAcct ID	  */
	public void setZ_RubroDGIAcct_ID (int Z_RubroDGIAcct_ID)
	{
		if (Z_RubroDGIAcct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_RubroDGIAcct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_RubroDGIAcct_ID, Integer.valueOf(Z_RubroDGIAcct_ID));
	}

	/** Get Z_RubroDGIAcct ID.
		@return Z_RubroDGIAcct ID	  */
	public int getZ_RubroDGIAcct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_RubroDGIAcct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}