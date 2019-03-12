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

/** Generated Model for Z_AcctBrowserBal
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_AcctBrowserBal extends PO implements I_Z_AcctBrowserBal, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190312L;

    /** Standard Constructor */
    public X_Z_AcctBrowserBal (Properties ctx, int Z_AcctBrowserBal_ID, String trxName)
    {
      super (ctx, Z_AcctBrowserBal_ID, trxName);
      /** if (Z_AcctBrowserBal_ID == 0)
        {
			setC_Currency_ID (0);
			setC_ElementValue_ID (0);
			setCodigoCuenta (null);
			setIsSummary (false);
// N
			setNombreCuenta (null);
			setZ_AcctBrowserBal_ID (0);
			setZ_AcctBrowser_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_AcctBrowserBal (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_AcctBrowserBal[")
        .append(get_ID()).append("]");
      return sb.toString();
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

	/** Set Summary Level.
		@param IsSummary 
		This is a summary entity
	  */
	public void setIsSummary (boolean IsSummary)
	{
		set_Value (COLUMNNAME_IsSummary, Boolean.valueOf(IsSummary));
	}

	/** Get Summary Level.
		@return This is a summary entity
	  */
	public boolean isSummary () 
	{
		Object oo = get_Value(COLUMNNAME_IsSummary);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set NivelCuenta.
		@param NivelCuenta 
		Nivel de cuenta contable para balance
	  */
	public void setNivelCuenta (int NivelCuenta)
	{
		set_Value (COLUMNNAME_NivelCuenta, Integer.valueOf(NivelCuenta));
	}

	/** Get NivelCuenta.
		@return Nivel de cuenta contable para balance
	  */
	public int getNivelCuenta () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_NivelCuenta);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Node.
		@param Node_ID Node	  */
	public void setNode_ID (int Node_ID)
	{
		if (Node_ID < 0) 
			set_Value (COLUMNNAME_Node_ID, null);
		else 
			set_Value (COLUMNNAME_Node_ID, Integer.valueOf(Node_ID));
	}

	/** Get Node.
		@return Node	  */
	public int getNode_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Node_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set NombreCuenta.
		@param NombreCuenta 
		Nombre de Cuenta Contable
	  */
	public void setNombreCuenta (String NombreCuenta)
	{
		set_Value (COLUMNNAME_NombreCuenta, NombreCuenta);
	}

	/** Get NombreCuenta.
		@return Nombre de Cuenta Contable
	  */
	public String getNombreCuenta () 
	{
		return (String)get_Value(COLUMNNAME_NombreCuenta);
	}

	/** Set NroFila.
		@param NroFila 
		Número de Fila
	  */
	public void setNroFila (int NroFila)
	{
		set_Value (COLUMNNAME_NroFila, Integer.valueOf(NroFila));
	}

	/** Get NroFila.
		@return Número de Fila
	  */
	public int getNroFila () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_NroFila);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Parent.
		@param Parent_ID 
		Parent of Entity
	  */
	public void setParent_ID (int Parent_ID)
	{
		if (Parent_ID < 1) 
			set_Value (COLUMNNAME_Parent_ID, null);
		else 
			set_Value (COLUMNNAME_Parent_ID, Integer.valueOf(Parent_ID));
	}

	/** Get Parent.
		@return Parent of Entity
	  */
	public int getParent_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Parent_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Sequence.
		@param SeqNo 
		Method of ordering records; lowest number comes first
	  */
	public void setSeqNo (int SeqNo)
	{
		set_Value (COLUMNNAME_SeqNo, Integer.valueOf(SeqNo));
	}

	/** Get Sequence.
		@return Method of ordering records; lowest number comes first
	  */
	public int getSeqNo () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_SeqNo);
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

	/** Set Z_AcctBrowserBal ID.
		@param Z_AcctBrowserBal_ID Z_AcctBrowserBal ID	  */
	public void setZ_AcctBrowserBal_ID (int Z_AcctBrowserBal_ID)
	{
		if (Z_AcctBrowserBal_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowserBal_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_AcctBrowserBal_ID, Integer.valueOf(Z_AcctBrowserBal_ID));
	}

	/** Get Z_AcctBrowserBal ID.
		@return Z_AcctBrowserBal ID	  */
	public int getZ_AcctBrowserBal_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctBrowserBal_ID);
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

	public I_Z_AcctBrowSumBal getZ_AcctBrowSumBal() throws RuntimeException
    {
		return (I_Z_AcctBrowSumBal)MTable.get(getCtx(), I_Z_AcctBrowSumBal.Table_Name)
			.getPO(getZ_AcctBrowSumBal_ID(), get_TrxName());	}

	/** Set Z_AcctBrowSumBal ID.
		@param Z_AcctBrowSumBal_ID Z_AcctBrowSumBal ID	  */
	public void setZ_AcctBrowSumBal_ID (int Z_AcctBrowSumBal_ID)
	{
		if (Z_AcctBrowSumBal_ID < 1) 
			set_Value (COLUMNNAME_Z_AcctBrowSumBal_ID, null);
		else 
			set_Value (COLUMNNAME_Z_AcctBrowSumBal_ID, Integer.valueOf(Z_AcctBrowSumBal_ID));
	}

	/** Get Z_AcctBrowSumBal ID.
		@return Z_AcctBrowSumBal ID	  */
	public int getZ_AcctBrowSumBal_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_AcctBrowSumBal_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}