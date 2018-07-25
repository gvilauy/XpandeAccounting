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

/** Generated Interface for Z_AcctBrowSumMayor
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_AcctBrowSumMayor 
{

    /** TableName=Z_AcctBrowSumMayor */
    public static final String Table_Name = "Z_AcctBrowSumMayor";

    /** AD_Table_ID=1000222 */
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

    /** Column name AmtCr1 */
    public static final String COLUMNNAME_AmtCr1 = "AmtCr1";

	/** Set AmtCr1.
	  * Monto crédito uno
	  */
	public void setAmtCr1(BigDecimal AmtCr1);

	/** Get AmtCr1.
	  * Monto crédito uno
	  */
	public BigDecimal getAmtCr1();

    /** Column name AmtCr2 */
    public static final String COLUMNNAME_AmtCr2 = "AmtCr2";

	/** Set AmtCr2.
	  * Monto crédito dos
	  */
	public void setAmtCr2(BigDecimal AmtCr2);

	/** Get AmtCr2.
	  * Monto crédito dos
	  */
	public BigDecimal getAmtCr2();

    /** Column name AmtDr1 */
    public static final String COLUMNNAME_AmtDr1 = "AmtDr1";

	/** Set AmtDr1.
	  * Monto débito uno
	  */
	public void setAmtDr1(BigDecimal AmtDr1);

	/** Get AmtDr1.
	  * Monto débito uno
	  */
	public BigDecimal getAmtDr1();

    /** Column name AmtDr2 */
    public static final String COLUMNNAME_AmtDr2 = "AmtDr2";

	/** Set AmtDr2.
	  * Monto débito dos
	  */
	public void setAmtDr2(BigDecimal AmtDr2);

	/** Get AmtDr2.
	  * Monto débito dos
	  */
	public BigDecimal getAmtDr2();

    /** Column name AmtInicial1 */
    public static final String COLUMNNAME_AmtInicial1 = "AmtInicial1";

	/** Set AmtInicial1.
	  * Monto inicial uno
	  */
	public void setAmtInicial1(BigDecimal AmtInicial1);

	/** Get AmtInicial1.
	  * Monto inicial uno
	  */
	public BigDecimal getAmtInicial1();

    /** Column name AmtInicial2 */
    public static final String COLUMNNAME_AmtInicial2 = "AmtInicial2";

	/** Set AmtInicial2.
	  * Monto inicial dos
	  */
	public void setAmtInicial2(BigDecimal AmtInicial2);

	/** Get AmtInicial2.
	  * Monto inicial dos
	  */
	public BigDecimal getAmtInicial2();

    /** Column name AmtSubtotal1 */
    public static final String COLUMNNAME_AmtSubtotal1 = "AmtSubtotal1";

	/** Set AmtSubtotal1.
	  * Monto subtotal uno
	  */
	public void setAmtSubtotal1(BigDecimal AmtSubtotal1);

	/** Get AmtSubtotal1.
	  * Monto subtotal uno
	  */
	public BigDecimal getAmtSubtotal1();

    /** Column name AmtSubtotal2 */
    public static final String COLUMNNAME_AmtSubtotal2 = "AmtSubtotal2";

	/** Set AmtSubtotal2.
	  * Monto subtotal dos
	  */
	public void setAmtSubtotal2(BigDecimal AmtSubtotal2);

	/** Get AmtSubtotal2.
	  * Monto subtotal dos
	  */
	public BigDecimal getAmtSubtotal2();

    /** Column name AmtTotal1 */
    public static final String COLUMNNAME_AmtTotal1 = "AmtTotal1";

	/** Set AmtTotal1.
	  * Monto total uno
	  */
	public void setAmtTotal1(BigDecimal AmtTotal1);

	/** Get AmtTotal1.
	  * Monto total uno
	  */
	public BigDecimal getAmtTotal1();

    /** Column name AmtTotal2 */
    public static final String COLUMNNAME_AmtTotal2 = "AmtTotal2";

	/** Set AmtTotal2.
	  * Monto total dos
	  */
	public void setAmtTotal2(BigDecimal AmtTotal2);

	/** Get AmtTotal2.
	  * Monto total dos
	  */
	public BigDecimal getAmtTotal2();

    /** Column name C_Currency_2_ID */
    public static final String COLUMNNAME_C_Currency_2_ID = "C_Currency_2_ID";

	/** Set C_Currency_2_ID.
	  * Moneda secundaria para procesos
	  */
	public void setC_Currency_2_ID(int C_Currency_2_ID);

	/** Get C_Currency_2_ID.
	  * Moneda secundaria para procesos
	  */
	public int getC_Currency_2_ID();

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID(int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public I_C_Currency getC_Currency() throws RuntimeException;

    /** Column name C_ElementValue_ID */
    public static final String COLUMNNAME_C_ElementValue_ID = "C_ElementValue_ID";

	/** Set Account Element.
	  * Account Element
	  */
	public void setC_ElementValue_ID(int C_ElementValue_ID);

	/** Get Account Element.
	  * Account Element
	  */
	public int getC_ElementValue_ID();

	public I_C_ElementValue getC_ElementValue() throws RuntimeException;

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

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue(String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

    /** Column name Z_AcctBrowser_ID */
    public static final String COLUMNNAME_Z_AcctBrowser_ID = "Z_AcctBrowser_ID";

	/** Set Z_AcctBrowser ID	  */
	public void setZ_AcctBrowser_ID(int Z_AcctBrowser_ID);

	/** Get Z_AcctBrowser ID	  */
	public int getZ_AcctBrowser_ID();

	public I_Z_AcctBrowser getZ_AcctBrowser() throws RuntimeException;

    /** Column name Z_AcctBrowSumMayor_ID */
    public static final String COLUMNNAME_Z_AcctBrowSumMayor_ID = "Z_AcctBrowSumMayor_ID";

	/** Set Z_AcctBrowSumMayor ID	  */
	public void setZ_AcctBrowSumMayor_ID(int Z_AcctBrowSumMayor_ID);

	/** Get Z_AcctBrowSumMayor ID	  */
	public int getZ_AcctBrowSumMayor_ID();
}
