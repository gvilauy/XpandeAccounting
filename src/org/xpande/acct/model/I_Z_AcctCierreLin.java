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

/** Generated Interface for Z_AcctCierreLin
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_AcctCierreLin 
{

    /** TableName=Z_AcctCierreLin */
    public static final String Table_Name = "Z_AcctCierreLin";

    /** AD_Table_ID=1000352 */
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
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AmtAcctCr */
    public static final String COLUMNNAME_AmtAcctCr = "AmtAcctCr";

	/** Set Accounted Credit.
	  * Accounted Credit Amount
	  */
	public void setAmtAcctCr (BigDecimal AmtAcctCr);

	/** Get Accounted Credit.
	  * Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr();

    /** Column name AmtAcctCrTo */
    public static final String COLUMNNAME_AmtAcctCrTo = "AmtAcctCrTo";

	/** Set AmtAcctCrTo.
	  * Monto crédito final en moneda nacional
	  */
	public void setAmtAcctCrTo (BigDecimal AmtAcctCrTo);

	/** Get AmtAcctCrTo.
	  * Monto crédito final en moneda nacional
	  */
	public BigDecimal getAmtAcctCrTo();

    /** Column name AmtAcctDr */
    public static final String COLUMNNAME_AmtAcctDr = "AmtAcctDr";

	/** Set Accounted Debit.
	  * Accounted Debit Amount
	  */
	public void setAmtAcctDr (BigDecimal AmtAcctDr);

	/** Get Accounted Debit.
	  * Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr();

    /** Column name AmtAcctDrTo */
    public static final String COLUMNNAME_AmtAcctDrTo = "AmtAcctDrTo";

	/** Set AmtAcctDrTo.
	  * Monto débito final en moneda nacional
	  */
	public void setAmtAcctDrTo (BigDecimal AmtAcctDrTo);

	/** Get AmtAcctDrTo.
	  * Monto débito final en moneda nacional
	  */
	public BigDecimal getAmtAcctDrTo();

    /** Column name C_ElementValue_ID */
    public static final String COLUMNNAME_C_ElementValue_ID = "C_ElementValue_ID";

	/** Set Account Element.
	  * Account Element
	  */
	public void setC_ElementValue_ID (int C_ElementValue_ID);

	/** Get Account Element.
	  * Account Element
	  */
	public int getC_ElementValue_ID();

	public I_C_ElementValue getC_ElementValue() throws RuntimeException;

    /** Column name CodigoCuenta */
    public static final String COLUMNNAME_CodigoCuenta = "CodigoCuenta";

	/** Set CodigoCuenta.
	  * Codigo de cuenta contable
	  */
	public void setCodigoCuenta (String CodigoCuenta);

	/** Get CodigoCuenta.
	  * Codigo de cuenta contable
	  */
	public String getCodigoCuenta();

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

    /** Column name DifferenceAmt */
    public static final String COLUMNNAME_DifferenceAmt = "DifferenceAmt";

	/** Set Difference.
	  * Difference Amount
	  */
	public void setDifferenceAmt (BigDecimal DifferenceAmt);

	/** Get Difference.
	  * Difference Amount
	  */
	public BigDecimal getDifferenceAmt();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

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
	public void setUUID (String UUID);

	/** Get Immutable Universally Unique Identifier.
	  * Immutable Universally Unique Identifier
	  */
	public String getUUID();

    /** Column name Z_AcctCierre_ID */
    public static final String COLUMNNAME_Z_AcctCierre_ID = "Z_AcctCierre_ID";

	/** Set Z_AcctCierre ID	  */
	public void setZ_AcctCierre_ID (int Z_AcctCierre_ID);

	/** Get Z_AcctCierre ID	  */
	public int getZ_AcctCierre_ID();

	public I_Z_AcctCierre getZ_AcctCierre() throws RuntimeException;

    /** Column name Z_AcctCierreLin_ID */
    public static final String COLUMNNAME_Z_AcctCierreLin_ID = "Z_AcctCierreLin_ID";

	/** Set Z_AcctCierreLin ID	  */
	public void setZ_AcctCierreLin_ID (int Z_AcctCierreLin_ID);

	/** Get Z_AcctCierreLin ID	  */
	public int getZ_AcctCierreLin_ID();
}
