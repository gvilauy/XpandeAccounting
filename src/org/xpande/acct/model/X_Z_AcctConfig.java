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

/** Generated Model for Z_AcctConfig
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctConfig extends PO implements I_Z_AcctConfig, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20180711L;

    /** Standard Constructor */
    public X_Z_AcctConfig (Properties ctx, int Z_AcctConfig_ID, String trxName)
    {
      super (ctx, Z_AcctConfig_ID, trxName);
      /** if (Z_AcctConfig_ID == 0)
        {
			setValue (null);
			setZ_AcctConfig_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctConfig (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctConfig[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Charge_Rounding_ID.
		@param Charge_Rounding_ID 
		ID de Cargo para ajustes por redondeo en módulo contable del Core
	  */
	public void setCharge_Rounding_ID (int Charge_Rounding_ID)
	{
		if (Charge_Rounding_ID < 1) 
			set_Value (COLUMNNAME_Charge_Rounding_ID, null);
		else 
			set_Value (COLUMNNAME_Charge_Rounding_ID, Integer.valueOf(Charge_Rounding_ID));
	}

	/** Get Charge_Rounding_ID.
		@return ID de Cargo para ajustes por redondeo en módulo contable del Core
	  */
	public int getCharge_Rounding_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Charge_Rounding_ID);
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

	/** Set Z_AcctConfig ID.
		@param Z_AcctConfig_ID Z_AcctConfig ID	  */
	public void setZ_AcctConfig_ID (int Z_AcctConfig_ID)
	{
		if (Z_AcctConfig_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctConfig_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctConfig_ID, Integer.valueOf(Z_AcctConfig_ID));
	}

	/** Get Z_AcctConfig ID.
		@return Z_AcctConfig ID	  */
	public int getZ_AcctConfig_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctConfig_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}