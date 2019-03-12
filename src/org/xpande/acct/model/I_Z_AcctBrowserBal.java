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

/** Generated Interface for Z_AcctBrowserBal
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_AcctBrowserBal 
{

    /** TableName=Z_AcctBrowserBal */
    public static final String Table_Name = "Z_AcctBrowserBal";

    /** AD_Table_ID=1000270 */
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

    /** Column name CodigoCuenta */
    public static final String COLUMNNAME_CodigoCuenta = "CodigoCuenta";

	/** Set CodigoCuenta.
	  * Codigo de cuenta contable
	  */
	public void setCodigoCuenta(String CodigoCuenta);

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

    /** Column name IsSummary */
    public static final String COLUMNNAME_IsSummary = "IsSummary";

	/** Set Summary Level.
	  * This is a summary entity
	  */
	public void setIsSummary(boolean IsSummary);

	/** Get Summary Level.
	  * This is a summary entity
	  */
	public boolean isSummary();

    /** Column name NivelCuenta */
    public static final String COLUMNNAME_NivelCuenta = "NivelCuenta";

	/** Set NivelCuenta.
	  * Nivel de cuenta contable para balance
	  */
	public void setNivelCuenta(int NivelCuenta);

	/** Get NivelCuenta.
	  * Nivel de cuenta contable para balance
	  */
	public int getNivelCuenta();

    /** Column name Node_ID */
    public static final String COLUMNNAME_Node_ID = "Node_ID";

	/** Set Node	  */
	public void setNode_ID(int Node_ID);

	/** Get Node	  */
	public int getNode_ID();

    /** Column name NombreCuenta */
    public static final String COLUMNNAME_NombreCuenta = "NombreCuenta";

	/** Set NombreCuenta.
	  * Nombre de Cuenta Contable
	  */
	public void setNombreCuenta(String NombreCuenta);

	/** Get NombreCuenta.
	  * Nombre de Cuenta Contable
	  */
	public String getNombreCuenta();

    /** Column name NroFila */
    public static final String COLUMNNAME_NroFila = "NroFila";

	/** Set NroFila.
	  * Número de Fila
	  */
	public void setNroFila(int NroFila);

	/** Get NroFila.
	  * Número de Fila
	  */
	public int getNroFila();

    /** Column name Parent_ID */
    public static final String COLUMNNAME_Parent_ID = "Parent_ID";

	/** Set Parent.
	  * Parent of Entity
	  */
	public void setParent_ID(int Parent_ID);

	/** Get Parent.
	  * Parent of Entity
	  */
	public int getParent_ID();

    /** Column name SeqNo */
    public static final String COLUMNNAME_SeqNo = "SeqNo";

	/** Set Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public void setSeqNo(int SeqNo);

	/** Get Sequence.
	  * Method of ordering records;
 lowest number comes first
	  */
	public int getSeqNo();

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

    /** Column name Z_AcctBrowserBal_ID */
    public static final String COLUMNNAME_Z_AcctBrowserBal_ID = "Z_AcctBrowserBal_ID";

	/** Set Z_AcctBrowserBal ID	  */
	public void setZ_AcctBrowserBal_ID(int Z_AcctBrowserBal_ID);

	/** Get Z_AcctBrowserBal ID	  */
	public int getZ_AcctBrowserBal_ID();

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

	public I_Z_AcctBrowSumBal getZ_AcctBrowSumBal() throws RuntimeException;
}
