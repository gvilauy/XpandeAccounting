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

/** Generated Model for Z_GeneraFormDGIError
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_GeneraFormDGIError extends PO implements I_Z_GeneraFormDGIError, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20181121L;

    /** Standard Constructor */
    public X_Z_GeneraFormDGIError (Properties ctx, int Z_GeneraFormDGIError_ID, String trxName)
    {
      super (ctx, Z_GeneraFormDGIError_ID, trxName);
      /** if (Z_GeneraFormDGIError_ID == 0)
        {
			setErrorMsg (null);
			setZ_GeneraFormDGIError_ID (0);
			setZ_GeneraFormDGI_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_GeneraFormDGIError (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_GeneraFormDGIError[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Error Msg.
		@param ErrorMsg Error Msg	  */
	public void setErrorMsg (String ErrorMsg)
	{
		set_Value (COLUMNNAME_ErrorMsg, ErrorMsg);
	}

	/** Get Error Msg.
		@return Error Msg	  */
	public String getErrorMsg () 
	{
		return (String)get_Value(COLUMNNAME_ErrorMsg);
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

	/** Set Z_GeneraFormDGIError ID.
		@param Z_GeneraFormDGIError_ID Z_GeneraFormDGIError ID	  */
	public void setZ_GeneraFormDGIError_ID (int Z_GeneraFormDGIError_ID)
	{
		if (Z_GeneraFormDGIError_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_GeneraFormDGIError_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_GeneraFormDGIError_ID, Integer.valueOf(Z_GeneraFormDGIError_ID));
	}

	/** Get Z_GeneraFormDGIError ID.
		@return Z_GeneraFormDGIError ID	  */
	public int getZ_GeneraFormDGIError_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_GeneraFormDGIError_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.xpande.acct.model.I_Z_GeneraFormDGI getZ_GeneraFormDGI() throws RuntimeException
    {
		return (org.xpande.acct.model.I_Z_GeneraFormDGI)MTable.get(getCtx(), org.xpande.acct.model.I_Z_GeneraFormDGI.Table_Name)
			.getPO(getZ_GeneraFormDGI_ID(), get_TrxName());	}

	/** Set Z_GeneraFormDGI ID.
		@param Z_GeneraFormDGI_ID Z_GeneraFormDGI ID	  */
	public void setZ_GeneraFormDGI_ID (int Z_GeneraFormDGI_ID)
	{
		if (Z_GeneraFormDGI_ID < 1) 
			set_Value (COLUMNNAME_Z_GeneraFormDGI_ID, null);
		else 
			set_Value (COLUMNNAME_Z_GeneraFormDGI_ID, Integer.valueOf(Z_GeneraFormDGI_ID));
	}

	/** Get Z_GeneraFormDGI ID.
		@return Z_GeneraFormDGI ID	  */
	public int getZ_GeneraFormDGI_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_GeneraFormDGI_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}