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

/** Generated Model for Z_RubroDGITax
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_RubroDGITax extends PO implements I_Z_RubroDGITax, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20181123L;

    /** Standard Constructor */
    public X_Z_RubroDGITax (Properties ctx, int Z_RubroDGITax_ID, String trxName)
    {
      super (ctx, Z_RubroDGITax_ID, trxName);
      /** if (Z_RubroDGITax_ID == 0)
        {
			setC_Tax_ID (0);
			setZ_AcctConfigRubroDGI_ID (0);
			setZ_RubroDGITax_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_RubroDGITax (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_RubroDGITax[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_Tax getC_Tax() throws RuntimeException
    {
		return (I_C_Tax)MTable.get(getCtx(), I_C_Tax.Table_Name)
			.getPO(getC_Tax_ID(), get_TrxName());	}

	/** Set Tax.
		@param C_Tax_ID 
		Tax identifier
	  */
	public void setC_Tax_ID (int C_Tax_ID)
	{
		if (C_Tax_ID < 1) 
			set_Value (COLUMNNAME_C_Tax_ID, null);
		else 
			set_Value (COLUMNNAME_C_Tax_ID, Integer.valueOf(C_Tax_ID));
	}

	/** Get Tax.
		@return Tax identifier
	  */
	public int getC_Tax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Tax_ID);
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

	public org.xpande.acct.model.I_Z_AcctConfigRubroDGI getZ_AcctConfigRubroDGI() throws RuntimeException
    {
		return (org.xpande.acct.model.I_Z_AcctConfigRubroDGI)MTable.get(getCtx(), org.xpande.acct.model.I_Z_AcctConfigRubroDGI.Table_Name)
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

	/** Set Z_RubroDGITax ID.
		@param Z_RubroDGITax_ID Z_RubroDGITax ID	  */
	public void setZ_RubroDGITax_ID (int Z_RubroDGITax_ID)
	{
		if (Z_RubroDGITax_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_RubroDGITax_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_RubroDGITax_ID, Integer.valueOf(Z_RubroDGITax_ID));
	}

	/** Get Z_RubroDGITax ID.
		@return Z_RubroDGITax ID	  */
	public int getZ_RubroDGITax_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_RubroDGITax_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}