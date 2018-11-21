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
package org.xpande.acct.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for Z_GeneraFormDGI
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_GeneraFormDGI 
{

    /** TableName=Z_GeneraFormDGI */
    public static final String Table_Name = "Z_GeneraFormDGI";

    /** AD_Table_ID=1000246 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID(int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID(int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name EndDate */
    public static final String COLUMNNAME_EndDate = "EndDate";

	/** Set End Date.
	  * Last effective date (inclusive)
	  */
	public void setEndDate(Timestamp EndDate);

	/** Get End Date.
	  * Last effective date (inclusive)
	  */
	public Timestamp getEndDate();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive(boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name ProcessButton */
    public static final String COLUMNNAME_ProcessButton = "ProcessButton";

	/** Set ProcessButton	  */
	public void setProcessButton(String ProcessButton);

	/** Get ProcessButton	  */
	public String getProcessButton();

    /** Column name ProcessButton2 */
    public static final String COLUMNNAME_ProcessButton2 = "ProcessButton2";

	/** Set ProcessButton2.
	  * Botón de Proceso
	  */
	public void setProcessButton2(String ProcessButton2);

	/** Get ProcessButton2.
	  * Botón de Proceso
	  */
	public String getProcessButton2();

    /** Column name StartDate */
    public static final String COLUMNNAME_StartDate = "StartDate";

	/** Set Start Date.
	  * First effective day (inclusive)
	  */
	public void setStartDate(Timestamp StartDate);

	/** Get Start Date.
	  * First effective day (inclusive)
	  */
	public Timestamp getStartDate();

    /** Column name TipoFormularioDGI */
    public static final String COLUMNNAME_TipoFormularioDGI = "TipoFormularioDGI";

	/** Set TipoFormularioDGI.
	  * Tipo de Formulario de DGI
	  */
	public void setTipoFormularioDGI(String TipoFormularioDGI);

	/** Get TipoFormularioDGI.
	  * Tipo de Formulario de DGI
	  */
	public String getTipoFormularioDGI();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name UUID */
    public static final String COLUMNNAME_UUID = "UUID";

	/** Set Immutable Universally Unique Identifier.
	  * Immutable Universally Unique Identifier
	  */
	public void setUUID(String UUID);

	/** Get Immutable Universally Unique Identifier.
	  * Immutable Universally Unique Identifier
	  */
	public String getUUID();

    /** Column name Z_GeneraFormDGI_ID */
    public static final String COLUMNNAME_Z_GeneraFormDGI_ID = "Z_GeneraFormDGI_ID";

	/** Set Z_GeneraFormDGI ID	  */
	public void setZ_GeneraFormDGI_ID(int Z_GeneraFormDGI_ID);

	/** Get Z_GeneraFormDGI ID	  */
	public int getZ_GeneraFormDGI_ID();
}
