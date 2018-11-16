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

/** Generated Model for Z_MedioPago_Acct
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_MedioPago_Acct extends PO implements I_Z_MedioPago_Acct, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20181116L;

    /** Standard Constructor */
    public X_Z_MedioPago_Acct (Properties ctx, int Z_MedioPago_Acct_ID, String trxName)
    {
      super (ctx, Z_MedioPago_Acct_ID, trxName);
      /** if (Z_MedioPago_Acct_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setC_Currency_ID (0);
			setZ_MedioPago_Acct_ID (0);
			setZ_MedioPago_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_MedioPago_Acct (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_MedioPago_Acct[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	public I_C_ValidCombination getMP_Entregados_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getMP_Entregados_Acct(), get_TrxName());	}

	/** Set MP_Entregados_Acct.
		@param MP_Entregados_Acct 
		Cuenta contable para medio de pago entregado
	  */
	public void setMP_Entregados_Acct (int MP_Entregados_Acct)
	{
		set_Value (COLUMNNAME_MP_Entregados_Acct, Integer.valueOf(MP_Entregados_Acct));
	}

	/** Get MP_Entregados_Acct.
		@return Cuenta contable para medio de pago entregado
	  */
	public int getMP_Entregados_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MP_Entregados_Acct);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_ValidCombination getMP_Recibidos_A() throws RuntimeException
    {
		return (I_C_ValidCombination)MTable.get(getCtx(), I_C_ValidCombination.Table_Name)
			.getPO(getMP_Recibidos_Acct(), get_TrxName());	}

	/** Set MP_Recibidos_Acct.
		@param MP_Recibidos_Acct 
		Cuenta contable para medio de pago recibido
	  */
	public void setMP_Recibidos_Acct (int MP_Recibidos_Acct)
	{
		set_Value (COLUMNNAME_MP_Recibidos_Acct, Integer.valueOf(MP_Recibidos_Acct));
	}

	/** Get MP_Recibidos_Acct.
		@return Cuenta contable para medio de pago recibido
	  */
	public int getMP_Recibidos_Acct () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_MP_Recibidos_Acct);
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

	/** Set Z_MedioPago_Acct ID.
		@param Z_MedioPago_Acct_ID Z_MedioPago_Acct ID	  */
	public void setZ_MedioPago_Acct_ID (int Z_MedioPago_Acct_ID)
	{
		if (Z_MedioPago_Acct_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_MedioPago_Acct_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_MedioPago_Acct_ID, Integer.valueOf(Z_MedioPago_Acct_ID));
	}

	/** Get Z_MedioPago_Acct ID.
		@return Z_MedioPago_Acct ID	  */
	public int getZ_MedioPago_Acct_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_MedioPago_Acct_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Z_MedioPago ID.
		@param Z_MedioPago_ID Z_MedioPago ID	  */
	public void setZ_MedioPago_ID (int Z_MedioPago_ID)
	{
		if (Z_MedioPago_ID < 1) 
			set_Value (COLUMNNAME_Z_MedioPago_ID, null);
		else 
			set_Value (COLUMNNAME_Z_MedioPago_ID, Integer.valueOf(Z_MedioPago_ID));
	}

	/** Get Z_MedioPago ID.
		@return Z_MedioPago ID	  */
	public int getZ_MedioPago_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_MedioPago_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}