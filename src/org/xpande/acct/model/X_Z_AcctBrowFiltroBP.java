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

/** Generated Model for Z_AcctBrowFiltroBP
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctBrowFiltroBP extends PO implements I_Z_AcctBrowFiltroBP, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20201206L;

    /** Standard Constructor */
    public X_Z_AcctBrowFiltroBP (Properties ctx, int Z_AcctBrowFiltroBP_ID, String trxName)
    {
      super (ctx, Z_AcctBrowFiltroBP_ID, trxName);
      /** if (Z_AcctBrowFiltroBP_ID == 0)
        {
			setC_BPartner_ID (0);
			setZ_AcctBrowFiltroBP_ID (0);
			setZ_AcctBrowser_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctBrowFiltroBP (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctBrowFiltroBP[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public I_C_BPartner getC_BPartner() throws RuntimeException
    {
		return (I_C_BPartner)MTable.get(getCtx(), I_C_BPartner.Table_Name)
			.getPO(getC_BPartner_ID(), get_TrxName());	}

	/** Set Business Partner .
		@param C_BPartner_ID 
		Identifies a Business Partner
	  */
	public void setC_BPartner_ID (int C_BPartner_ID)
	{
		if (C_BPartner_ID < 1) 
			set_Value (COLUMNNAME_C_BPartner_ID, null);
		else 
			set_Value (COLUMNNAME_C_BPartner_ID, Integer.valueOf(C_BPartner_ID));
	}

	/** Get Business Partner .
		@return Identifies a Business Partner
	  */
	public int getC_BPartner_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_BPartner_ID);
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

	/** Set Z_AcctBrowFiltroBP ID.
		@param Z_AcctBrowFiltroBP_ID Z_AcctBrowFiltroBP ID	  */
	public void setZ_AcctBrowFiltroBP_ID (int Z_AcctBrowFiltroBP_ID)
	{
		if (Z_AcctBrowFiltroBP_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowFiltroBP_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowFiltroBP_ID, Integer.valueOf(Z_AcctBrowFiltroBP_ID));
	}

	/** Get Z_AcctBrowFiltroBP ID.
		@return Z_AcctBrowFiltroBP ID	  */
	public int getZ_AcctBrowFiltroBP_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctBrowFiltroBP_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
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
}