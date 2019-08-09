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

/** Generated Interface for Z_DifCambioDet
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_DifCambioDet 
{

    /** TableName=Z_DifCambioDet */
    public static final String Table_Name = "Z_DifCambioDet";

    /** AD_Table_ID=1000296 */
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

    /** Column name AmtAcctCr */
    public static final String COLUMNNAME_AmtAcctCr = "AmtAcctCr";

	/** Set Accounted Credit.
	  * Accounted Credit Amount
	  */
	public void setAmtAcctCr(BigDecimal AmtAcctCr);

	/** Get Accounted Credit.
	  * Accounted Credit Amount
	  */
	public BigDecimal getAmtAcctCr();

    /** Column name AmtAcctCrDif */
    public static final String COLUMNNAME_AmtAcctCrDif = "AmtAcctCrDif";

	/** Set AmtAcctCrDif.
	  * Monto diferencia en créditos en moneda nacional
	  */
	public void setAmtAcctCrDif(BigDecimal AmtAcctCrDif);

	/** Get AmtAcctCrDif.
	  * Monto diferencia en créditos en moneda nacional
	  */
	public BigDecimal getAmtAcctCrDif();

    /** Column name AmtAcctCrTo */
    public static final String COLUMNNAME_AmtAcctCrTo = "AmtAcctCrTo";

	/** Set AmtAcctCrTo.
	  * Monto crédito final en moneda nacional
	  */
	public void setAmtAcctCrTo(BigDecimal AmtAcctCrTo);

	/** Get AmtAcctCrTo.
	  * Monto crédito final en moneda nacional
	  */
	public BigDecimal getAmtAcctCrTo();

    /** Column name AmtAcctDr */
    public static final String COLUMNNAME_AmtAcctDr = "AmtAcctDr";

	/** Set Accounted Debit.
	  * Accounted Debit Amount
	  */
	public void setAmtAcctDr(BigDecimal AmtAcctDr);

	/** Get Accounted Debit.
	  * Accounted Debit Amount
	  */
	public BigDecimal getAmtAcctDr();

    /** Column name AmtAcctDrDif */
    public static final String COLUMNNAME_AmtAcctDrDif = "AmtAcctDrDif";

	/** Set AmtAcctDrDif.
	  * Monto diferencia en débitos en moneda nacional
	  */
	public void setAmtAcctDrDif(BigDecimal AmtAcctDrDif);

	/** Get AmtAcctDrDif.
	  * Monto diferencia en débitos en moneda nacional
	  */
	public BigDecimal getAmtAcctDrDif();

    /** Column name AmtAcctDrTo */
    public static final String COLUMNNAME_AmtAcctDrTo = "AmtAcctDrTo";

	/** Set AmtAcctDrTo.
	  * Monto débito final en moneda nacional
	  */
	public void setAmtAcctDrTo(BigDecimal AmtAcctDrTo);

	/** Get AmtAcctDrTo.
	  * Monto débito final en moneda nacional
	  */
	public BigDecimal getAmtAcctDrTo();

    /** Column name AmtSourceCr */
    public static final String COLUMNNAME_AmtSourceCr = "AmtSourceCr";

	/** Set Source Credit.
	  * Source Credit Amount
	  */
	public void setAmtSourceCr(BigDecimal AmtSourceCr);

	/** Get Source Credit.
	  * Source Credit Amount
	  */
	public BigDecimal getAmtSourceCr();

    /** Column name AmtSourceCrDif */
    public static final String COLUMNNAME_AmtSourceCrDif = "AmtSourceCrDif";

	/** Set AmtSourceCrDif.
	  * Monto diferencia para créditos en moneda orígen
	  */
	public void setAmtSourceCrDif(BigDecimal AmtSourceCrDif);

	/** Get AmtSourceCrDif.
	  * Monto diferencia para créditos en moneda orígen
	  */
	public BigDecimal getAmtSourceCrDif();

    /** Column name AmtSourceDr */
    public static final String COLUMNNAME_AmtSourceDr = "AmtSourceDr";

	/** Set Source Debit.
	  * Source Debit Amount
	  */
	public void setAmtSourceDr(BigDecimal AmtSourceDr);

	/** Get Source Debit.
	  * Source Debit Amount
	  */
	public BigDecimal getAmtSourceDr();

    /** Column name AmtSourceDrDif */
    public static final String COLUMNNAME_AmtSourceDrDif = "AmtSourceDrDif";

	/** Set AmtSourceDrDif.
	  * Monto diferencia para débitos en moneda origen
	  */
	public void setAmtSourceDrDif(BigDecimal AmtSourceDrDif);

	/** Get AmtSourceDrDif.
	  * Monto diferencia para débitos en moneda origen
	  */
	public BigDecimal getAmtSourceDrDif();

    /** Column name C_BPartner_ID */
    public static final String COLUMNNAME_C_BPartner_ID = "C_BPartner_ID";

	/** Set Business Partner .
	  * Identifies a Business Partner
	  */
	public void setC_BPartner_ID(int C_BPartner_ID);

	/** Get Business Partner .
	  * Identifies a Business Partner
	  */
	public int getC_BPartner_ID();

	public I_C_BPartner getC_BPartner() throws RuntimeException;

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

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID(int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public I_C_DocType getC_DocType() throws RuntimeException;

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

    /** Column name CurrencyRate */
    public static final String COLUMNNAME_CurrencyRate = "CurrencyRate";

	/** Set Rate.
	  * Currency Conversion Rate
	  */
	public void setCurrencyRate(BigDecimal CurrencyRate);

	/** Get Rate.
	  * Currency Conversion Rate
	  */
	public BigDecimal getCurrencyRate();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct(Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name DocumentNoRef */
    public static final String COLUMNNAME_DocumentNoRef = "DocumentNoRef";

	/** Set DocumentNoRef.
	  * Numero de documento referenciado
	  */
	public void setDocumentNoRef(String DocumentNoRef);

	/** Get DocumentNoRef.
	  * Numero de documento referenciado
	  */
	public String getDocumentNoRef();

    /** Column name Fact_Acct_ID */
    public static final String COLUMNNAME_Fact_Acct_ID = "Fact_Acct_ID";

	/** Set Accounting Fact	  */
	public void setFact_Acct_ID(int Fact_Acct_ID);

	/** Get Accounting Fact	  */
	public int getFact_Acct_ID();

	public I_Fact_Acct getFact_Acct() throws RuntimeException;

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

    /** Column name MultiplyRate */
    public static final String COLUMNNAME_MultiplyRate = "MultiplyRate";

	/** Set Multiply Rate.
	  * Rate to multiple the source by to calculate the target.
	  */
	public void setMultiplyRate(BigDecimal MultiplyRate);

	/** Get Multiply Rate.
	  * Rate to multiple the source by to calculate the target.
	  */
	public BigDecimal getMultiplyRate();

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

    /** Column name Z_DifCambioDet_ID */
    public static final String COLUMNNAME_Z_DifCambioDet_ID = "Z_DifCambioDet_ID";

	/** Set Z_DifCambioDet ID	  */
	public void setZ_DifCambioDet_ID(int Z_DifCambioDet_ID);

	/** Get Z_DifCambioDet ID	  */
	public int getZ_DifCambioDet_ID();

    /** Column name Z_DifCambio_ID */
    public static final String COLUMNNAME_Z_DifCambio_ID = "Z_DifCambio_ID";

	/** Set Z_DifCambio ID	  */
	public void setZ_DifCambio_ID(int Z_DifCambio_ID);

	/** Get Z_DifCambio ID	  */
	public int getZ_DifCambio_ID();

	public I_Z_DifCambio getZ_DifCambio() throws RuntimeException;
}
