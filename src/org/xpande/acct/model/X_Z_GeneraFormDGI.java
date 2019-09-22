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

/** Generated Model for Z_GeneraFormDGI
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class X_Z_GeneraFormDGI extends PO implements I_Z_GeneraFormDGI, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190922L;

    /** Standard Constructor */
    public X_Z_GeneraFormDGI (Properties ctx, int Z_GeneraFormDGI_ID, String trxName)
    {
      super (ctx, Z_GeneraFormDGI_ID, trxName);
      /** if (Z_GeneraFormDGI_ID == 0)
        {
			setC_AcctSchema_ID (0);
			setEndDate (new Timestamp( System.currentTimeMillis() ));
			setStartDate (new Timestamp( System.currentTimeMillis() ));
			setTipoFormularioDGI (null);
// -1
			setZ_GeneraFormDGI_ID (0);
        } */
    }

    /** Load Constructor */
    public X_Z_GeneraFormDGI (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_Z_GeneraFormDGI[")
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

	public I_C_Period getC_Period() throws RuntimeException
    {
		return (I_C_Period)MTable.get(getCtx(), I_C_Period.Table_Name)
			.getPO(getC_Period_ID(), get_TrxName());	}

	/** Set Period.
		@param C_Period_ID 
		Period of the Calendar
	  */
	public void setC_Period_ID (int C_Period_ID)
	{
		if (C_Period_ID < 1) 
			set_Value (COLUMNNAME_C_Period_ID, null);
		else 
			set_Value (COLUMNNAME_C_Period_ID, Integer.valueOf(C_Period_ID));
	}

	/** Get Period.
		@return Period of the Calendar
	  */
	public int getC_Period_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Period_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set End Date.
		@param EndDate 
		Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate)
	{
		set_Value (COLUMNNAME_EndDate, EndDate);
	}

	/** Get End Date.
		@return Last effective date (inclusive)
	  */
	public Timestamp getEndDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate);
	}

	/** Set EndDate2.
		@param EndDate2 
		Fecha hasta secundaria
	  */
	public void setEndDate2 (Timestamp EndDate2)
	{
		set_Value (COLUMNNAME_EndDate2, EndDate2);
	}

	/** Get EndDate2.
		@return Fecha hasta secundaria
	  */
	public Timestamp getEndDate2 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate2);
	}

	/** Set EndDate3.
		@param EndDate3 
		Fecha hasta 3
	  */
	public void setEndDate3 (Timestamp EndDate3)
	{
		set_Value (COLUMNNAME_EndDate3, EndDate3);
	}

	/** Get EndDate3.
		@return Fecha hasta 3
	  */
	public Timestamp getEndDate3 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_EndDate3);
	}

	/** Set File Path or Name.
		@param FilePathOrName 
		Path of directory or name of the local file or URL
	  */
	public void setFilePathOrName (String FilePathOrName)
	{
		set_Value (COLUMNNAME_FilePathOrName, FilePathOrName);
	}

	/** Get File Path or Name.
		@return Path of directory or name of the local file or URL
	  */
	public String getFilePathOrName () 
	{
		return (String)get_Value(COLUMNNAME_FilePathOrName);
	}

	/** Set ProcessButton.
		@param ProcessButton ProcessButton	  */
	public void setProcessButton (String ProcessButton)
	{
		set_Value (COLUMNNAME_ProcessButton, ProcessButton);
	}

	/** Get ProcessButton.
		@return ProcessButton	  */
	public String getProcessButton () 
	{
		return (String)get_Value(COLUMNNAME_ProcessButton);
	}

	/** Set ProcessButton2.
		@param ProcessButton2 
		Botón de Proceso
	  */
	public void setProcessButton2 (String ProcessButton2)
	{
		set_Value (COLUMNNAME_ProcessButton2, ProcessButton2);
	}

	/** Get ProcessButton2.
		@return Botón de Proceso
	  */
	public String getProcessButton2 () 
	{
		return (String)get_Value(COLUMNNAME_ProcessButton2);
	}

	/** Set Start Date.
		@param StartDate 
		First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate)
	{
		set_Value (COLUMNNAME_StartDate, StartDate);
	}

	/** Get Start Date.
		@return First effective day (inclusive)
	  */
	public Timestamp getStartDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate);
	}

	/** Set StartDate2.
		@param StartDate2 
		Fecha desde secundaria
	  */
	public void setStartDate2 (Timestamp StartDate2)
	{
		set_Value (COLUMNNAME_StartDate2, StartDate2);
	}

	/** Get StartDate2.
		@return Fecha desde secundaria
	  */
	public Timestamp getStartDate2 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate2);
	}

	/** Set StartDate3.
		@param StartDate3 
		Fecha de inicio 3
	  */
	public void setStartDate3 (Timestamp StartDate3)
	{
		set_Value (COLUMNNAME_StartDate3, StartDate3);
	}

	/** Get StartDate3.
		@return Fecha de inicio 3
	  */
	public Timestamp getStartDate3 () 
	{
		return (Timestamp)get_Value(COLUMNNAME_StartDate3);
	}

	/** TipoFormularioDGI AD_Reference_ID=1000045 */
	public static final int TIPOFORMULARIODGI_AD_Reference_ID=1000045;
	/** FORMULARIO 2/181 = 2181 */
	public static final String TIPOFORMULARIODGI_FORMULARIO2181 = "2181";
	/** FORMULARIO 2/183 = 2183 */
	public static final String TIPOFORMULARIODGI_FORMULARIO2183 = "2183";
	/** FORMULARIO 1/146 = 1146 */
	public static final String TIPOFORMULARIODGI_FORMULARIO1146 = "1146";
	/** FORMULARIO 1/246 = 1246 */
	public static final String TIPOFORMULARIODGI_FORMULARIO1246 = "1246";
	/** Set TipoFormularioDGI.
		@param TipoFormularioDGI 
		Tipo de Formulario de DGI
	  */
	public void setTipoFormularioDGI (String TipoFormularioDGI)
	{

		set_Value (COLUMNNAME_TipoFormularioDGI, TipoFormularioDGI);
	}

	/** Get TipoFormularioDGI.
		@return Tipo de Formulario de DGI
	  */
	public String getTipoFormularioDGI () 
	{
		return (String)get_Value(COLUMNNAME_TipoFormularioDGI);
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

	/** Set Z_GeneraFormDGI ID.
		@param Z_GeneraFormDGI_ID Z_GeneraFormDGI ID	  */
	public void setZ_GeneraFormDGI_ID (int Z_GeneraFormDGI_ID)
	{
		if (Z_GeneraFormDGI_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_Z_GeneraFormDGI_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_Z_GeneraFormDGI_ID, Integer.valueOf(Z_GeneraFormDGI_ID));
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