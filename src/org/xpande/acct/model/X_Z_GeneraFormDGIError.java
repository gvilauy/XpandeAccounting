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
import java.sql.Timestamp;
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
	private static final long serialVersionUID = 20190923L;

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

	public I_C_DocType getC_DocType() throws RuntimeException
    {
		return (I_C_DocType)MTable.get(getCtx(), I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (I_C_Invoice)MTable.get(getCtx(), I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_Value (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_Value (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.eevolution.model.I_C_TaxGroup getC_TaxGroup() throws RuntimeException
    {
		return (org.eevolution.model.I_C_TaxGroup)MTable.get(getCtx(), org.eevolution.model.I_C_TaxGroup.Table_Name)
			.getPO(getC_TaxGroup_ID(), get_TrxName());	}

	/** Set Tax Group.
		@param C_TaxGroup_ID Tax Group	  */
	public void setC_TaxGroup_ID (int C_TaxGroup_ID)
	{
		if (C_TaxGroup_ID < 1) 
			set_Value (COLUMNNAME_C_TaxGroup_ID, null);
		else 
			set_Value (COLUMNNAME_C_TaxGroup_ID, Integer.valueOf(C_TaxGroup_ID));
	}

	/** Get Tax Group.
		@return Tax Group	  */
	public int getC_TaxGroup_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_TaxGroup_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_Value (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Document Date.
		@param DateDoc 
		Date of the Document
	  */
	public void setDateDoc (Timestamp DateDoc)
	{
		set_Value (COLUMNNAME_DateDoc, DateDoc);
	}

	/** Get Document Date.
		@return Date of the Document
	  */
	public Timestamp getDateDoc () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateDoc);
	}

	/** Set DocumentNoRef.
		@param DocumentNoRef 
		Numero de documento referenciado
	  */
	public void setDocumentNoRef (String DocumentNoRef)
	{
		set_Value (COLUMNNAME_DocumentNoRef, DocumentNoRef);
	}

	/** Get DocumentNoRef.
		@return Numero de documento referenciado
	  */
	public String getDocumentNoRef () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNoRef);
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

	/** Set Tax ID.
		@param TaxID 
		Tax Identification
	  */
	public void setTaxID (String TaxID)
	{
		set_Value (COLUMNNAME_TaxID, TaxID);
	}

	/** Get Tax ID.
		@return Tax Identification
	  */
	public String getTaxID () 
	{
		return (String)get_Value(COLUMNNAME_TaxID);
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

	public I_Z_GeneraFormDGI getZ_GeneraFormDGI() throws RuntimeException
    {
		return (I_Z_GeneraFormDGI)MTable.get(getCtx(), I_Z_GeneraFormDGI.Table_Name)
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

	/** Set Z_RetencionSocio ID.
		@param Z_RetencionSocio_ID Z_RetencionSocio ID	  */
	public void setZ_RetencionSocio_ID (int Z_RetencionSocio_ID)
	{
		if (Z_RetencionSocio_ID < 1) 
			set_Value (COLUMNNAME_Z_RetencionSocio_ID, null);
		else 
			set_Value (COLUMNNAME_Z_RetencionSocio_ID, Integer.valueOf(Z_RetencionSocio_ID));
	}

	/** Get Z_RetencionSocio ID.
		@return Z_RetencionSocio ID	  */
	public int getZ_RetencionSocio_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_Z_RetencionSocio_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}