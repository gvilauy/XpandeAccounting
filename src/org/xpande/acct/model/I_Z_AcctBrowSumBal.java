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

/** Generated Interface for Z_AcctBrowSumBal
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_AcctBrowSumBal 
{

    /** TableName=Z_AcctBrowSumBal */
    public static final String Table_Name = "Z_AcctBrowSumBal";

    /** AD_Table_ID=1000271 */
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

    /** Column name AmtActivo1 */
    public static final String COLUMNNAME_AmtActivo1 = "AmtActivo1";

	/** Set AmtActivo1.
	  * Monto Activo
	  */
	public void setAmtActivo1(BigDecimal AmtActivo1);

	/** Get AmtActivo1.
	  * Monto Activo
	  */
	public BigDecimal getAmtActivo1();

    /** Column name AmtActivo2 */
    public static final String COLUMNNAME_AmtActivo2 = "AmtActivo2";

	/** Set AmtActivo2.
	  * Monto Activo
	  */
	public void setAmtActivo2(BigDecimal AmtActivo2);

	/** Get AmtActivo2.
	  * Monto Activo
	  */
	public BigDecimal getAmtActivo2();

    /** Column name AmtControl1 */
    public static final String COLUMNNAME_AmtControl1 = "AmtControl1";

	/** Set AmtControl1.
	  * Monto de Control
	  */
	public void setAmtControl1(BigDecimal AmtControl1);

	/** Get AmtControl1.
	  * Monto de Control
	  */
	public BigDecimal getAmtControl1();

    /** Column name AmtControl2 */
    public static final String COLUMNNAME_AmtControl2 = "AmtControl2";

	/** Set AmtControl2.
	  * Monto de Control
	  */
	public void setAmtControl2(BigDecimal AmtControl2);

	/** Get AmtControl2.
	  * Monto de Control
	  */
	public BigDecimal getAmtControl2();

    /** Column name AmtGastos1 */
    public static final String COLUMNNAME_AmtGastos1 = "AmtGastos1";

	/** Set AmtGastos1.
	  * Monto Gastos
	  */
	public void setAmtGastos1(BigDecimal AmtGastos1);

	/** Get AmtGastos1.
	  * Monto Gastos
	  */
	public BigDecimal getAmtGastos1();

    /** Column name AmtGastos2 */
    public static final String COLUMNNAME_AmtGastos2 = "AmtGastos2";

	/** Set AmtGastos2.
	  * Monto Gastos
	  */
	public void setAmtGastos2(BigDecimal AmtGastos2);

	/** Get AmtGastos2.
	  * Monto Gastos
	  */
	public BigDecimal getAmtGastos2();

    /** Column name AmtIngresos1 */
    public static final String COLUMNNAME_AmtIngresos1 = "AmtIngresos1";

	/** Set AmtIngresos1.
	  * Monto Ingresos
	  */
	public void setAmtIngresos1(BigDecimal AmtIngresos1);

	/** Get AmtIngresos1.
	  * Monto Ingresos
	  */
	public BigDecimal getAmtIngresos1();

    /** Column name AmtIngresos2 */
    public static final String COLUMNNAME_AmtIngresos2 = "AmtIngresos2";

	/** Set AmtIngresos2.
	  * Monto Ingresos
	  */
	public void setAmtIngresos2(BigDecimal AmtIngresos2);

	/** Get AmtIngresos2.
	  * Monto Ingresos
	  */
	public BigDecimal getAmtIngresos2();

    /** Column name AmtPasivo1 */
    public static final String COLUMNNAME_AmtPasivo1 = "AmtPasivo1";

	/** Set AmtPasivo1.
	  * Monto Pasivo
	  */
	public void setAmtPasivo1(BigDecimal AmtPasivo1);

	/** Get AmtPasivo1.
	  * Monto Pasivo
	  */
	public BigDecimal getAmtPasivo1();

    /** Column name AmtPasivo2 */
    public static final String COLUMNNAME_AmtPasivo2 = "AmtPasivo2";

	/** Set AmtPasivo2.
	  * Monto Pasivo
	  */
	public void setAmtPasivo2(BigDecimal AmtPasivo2);

	/** Get AmtPasivo2.
	  * Monto Pasivo
	  */
	public BigDecimal getAmtPasivo2();

    /** Column name AmtPatrimonio1 */
    public static final String COLUMNNAME_AmtPatrimonio1 = "AmtPatrimonio1";

	/** Set AmtPatrimonio1.
	  * Monto Patrimonio
	  */
	public void setAmtPatrimonio1(BigDecimal AmtPatrimonio1);

	/** Get AmtPatrimonio1.
	  * Monto Patrimonio
	  */
	public BigDecimal getAmtPatrimonio1();

    /** Column name AmtPatrimonio2 */
    public static final String COLUMNNAME_AmtPatrimonio2 = "AmtPatrimonio2";

	/** Set AmtPatrimonio2.
	  * Monto Patrimonio
	  */
	public void setAmtPatrimonio2(BigDecimal AmtPatrimonio2);

	/** Get AmtPatrimonio2.
	  * Monto Patrimonio
	  */
	public BigDecimal getAmtPatrimonio2();

    /** Column name AmtResultado1 */
    public static final String COLUMNNAME_AmtResultado1 = "AmtResultado1";

	/** Set AmtResultado1.
	  * Monto Resultado
	  */
	public void setAmtResultado1(BigDecimal AmtResultado1);

	/** Get AmtResultado1.
	  * Monto Resultado
	  */
	public BigDecimal getAmtResultado1();

    /** Column name AmtResultado2 */
    public static final String COLUMNNAME_AmtResultado2 = "AmtResultado2";

	/** Set AmtResultado2.
	  * Monto Resultado
	  */
	public void setAmtResultado2(BigDecimal AmtResultado2);

	/** Get AmtResultado2.
	  * Monto Resultado
	  */
	public BigDecimal getAmtResultado2();

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

    /** Column name Z_AcctBrowser_ID */
    public static final String COLUMNNAME_Z_AcctBrowser_ID = "Z_AcctBrowser_ID";

	/** Set Z_AcctBrowser ID	  */
	public void setZ_AcctBrowser_ID(int Z_AcctBrowser_ID);

	/** Get Z_AcctBrowser ID	  */
	public int getZ_AcctBrowser_ID();

	public I_Z_AcctBrowser getZ_AcctBrowser() throws RuntimeException;

    /** Column name Z_AcctBrowSumBal_ID */
    public static final String COLUMNNAME_Z_AcctBrowSumBal_ID = "Z_AcctBrowSumBal_ID";

	/** Set Z_AcctBrowSumBal ID	  */
	public void setZ_AcctBrowSumBal_ID(int Z_AcctBrowSumBal_ID);

	/** Get Z_AcctBrowSumBal ID	  */
	public int getZ_AcctBrowSumBal_ID();
}
