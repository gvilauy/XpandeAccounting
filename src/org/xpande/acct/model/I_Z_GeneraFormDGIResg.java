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

/** Generated Interface for Z_GeneraFormDGIResg
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_GeneraFormDGIResg 
{

    /** TableName=Z_GeneraFormDGIResg */
    public static final String Table_Name = "Z_GeneraFormDGIResg";

    /** AD_Table_ID=1000319 */
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

    /** Column name AmtBase */
    public static final String COLUMNNAME_AmtBase = "AmtBase";

	/** Set AmtBase.
	  * Monto base
	  */
	public void setAmtBase(BigDecimal AmtBase);

	/** Get AmtBase.
	  * Monto base
	  */
	public BigDecimal getAmtBase();

    /** Column name AmtBaseMO */
    public static final String COLUMNNAME_AmtBaseMO = "AmtBaseMO";

	/** Set AmtBaseMO.
	  * Monto base en moneda origen
	  */
	public void setAmtBaseMO(BigDecimal AmtBaseMO);

	/** Get AmtBaseMO.
	  * Monto base en moneda origen
	  */
	public BigDecimal getAmtBaseMO();

    /** Column name AmtRetencion */
    public static final String COLUMNNAME_AmtRetencion = "AmtRetencion";

	/** Set AmtRetencion.
	  * Monto retención
	  */
	public void setAmtRetencion(BigDecimal AmtRetencion);

	/** Get AmtRetencion.
	  * Monto retención
	  */
	public BigDecimal getAmtRetencion();

    /** Column name AmtRetencionMO */
    public static final String COLUMNNAME_AmtRetencionMO = "AmtRetencionMO";

	/** Set AmtRetencionMO.
	  * Monto retención en Moneda Origen que es igual a la moneda del documento
	  */
	public void setAmtRetencionMO(BigDecimal AmtRetencionMO);

	/** Get AmtRetencionMO.
	  * Monto retención en Moneda Origen que es igual a la moneda del documento
	  */
	public BigDecimal getAmtRetencionMO();

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

    /** Column name C_Invoice_ID */
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";

	/** Set Invoice.
	  * Invoice Identifier
	  */
	public void setC_Invoice_ID(int C_Invoice_ID);

	/** Get Invoice.
	  * Invoice Identifier
	  */
	public int getC_Invoice_ID();

	public I_C_Invoice getC_Invoice() throws RuntimeException;

    /** Column name C_Period_ID */
    public static final String COLUMNNAME_C_Period_ID = "C_Period_ID";

	/** Set Period.
	  * Period of the Calendar
	  */
	public void setC_Period_ID(int C_Period_ID);

	/** Get Period.
	  * Period of the Calendar
	  */
	public int getC_Period_ID();

	public I_C_Period getC_Period() throws RuntimeException;

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

    /** Column name C_TaxGroup_ID */
    public static final String COLUMNNAME_C_TaxGroup_ID = "C_TaxGroup_ID";

	/** Set Tax Group	  */
	public void setC_TaxGroup_ID(int C_TaxGroup_ID);

	/** Get Tax Group	  */
	public int getC_TaxGroup_ID();

	public org.eevolution.model.I_C_TaxGroup getC_TaxGroup() throws RuntimeException;

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

    /** Column name DateDoc */
    public static final String COLUMNNAME_DateDoc = "DateDoc";

	/** Set Document Date.
	  * Date of the Document
	  */
	public void setDateDoc(Timestamp DateDoc);

	/** Get Document Date.
	  * Date of the Document
	  */
	public Timestamp getDateDoc();

    /** Column name DateRefResguardo */
    public static final String COLUMNNAME_DateRefResguardo = "DateRefResguardo";

	/** Set DateRefResguardo.
	  * Fecha referencia de un Resguardo a Socio de Negocio
	  */
	public void setDateRefResguardo(Timestamp DateRefResguardo);

	/** Get DateRefResguardo.
	  * Fecha referencia de un Resguardo a Socio de Negocio
	  */
	public Timestamp getDateRefResguardo();

    /** Column name DocBaseType */
    public static final String COLUMNNAME_DocBaseType = "DocBaseType";

	/** Set Document BaseType.
	  * Logical type of document
	  */
	public void setDocBaseType(String DocBaseType);

	/** Get Document BaseType.
	  * Logical type of document
	  */
	public String getDocBaseType();

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

    /** Column name PorcRetencion */
    public static final String COLUMNNAME_PorcRetencion = "PorcRetencion";

	/** Set PorcRetencion.
	  * Porcentaje Retención 
	  */
	public void setPorcRetencion(BigDecimal PorcRetencion);

	/** Get PorcRetencion.
	  * Porcentaje Retención 
	  */
	public BigDecimal getPorcRetencion();

    /** Column name Reference */
    public static final String COLUMNNAME_Reference = "Reference";

	/** Set Reference.
	  * Reference for this record
	  */
	public void setReference(String Reference);

	/** Get Reference.
	  * Reference for this record
	  */
	public String getReference();

    /** Column name TaxID */
    public static final String COLUMNNAME_TaxID = "TaxID";

	/** Set Tax ID.
	  * Tax Identification
	  */
	public void setTaxID(String TaxID);

	/** Get Tax ID.
	  * Tax Identification
	  */
	public String getTaxID();

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

    /** Column name Z_AcctConfigRubroDGI_ID */
    public static final String COLUMNNAME_Z_AcctConfigRubroDGI_ID = "Z_AcctConfigRubroDGI_ID";

	/** Set Z_AcctConfigRubroDGI ID	  */
	public void setZ_AcctConfigRubroDGI_ID(int Z_AcctConfigRubroDGI_ID);

	/** Get Z_AcctConfigRubroDGI ID	  */
	public int getZ_AcctConfigRubroDGI_ID();

	public I_Z_AcctConfigRubroDGI getZ_AcctConfigRubroDGI() throws RuntimeException;

    /** Column name Z_GeneraFormDGI_ID */
    public static final String COLUMNNAME_Z_GeneraFormDGI_ID = "Z_GeneraFormDGI_ID";

	/** Set Z_GeneraFormDGI ID	  */
	public void setZ_GeneraFormDGI_ID(int Z_GeneraFormDGI_ID);

	/** Get Z_GeneraFormDGI ID	  */
	public int getZ_GeneraFormDGI_ID();

	public I_Z_GeneraFormDGI getZ_GeneraFormDGI() throws RuntimeException;

    /** Column name Z_GeneraFormDGIResg_ID */
    public static final String COLUMNNAME_Z_GeneraFormDGIResg_ID = "Z_GeneraFormDGIResg_ID";

	/** Set Z_GeneraFormDGIResg ID	  */
	public void setZ_GeneraFormDGIResg_ID(int Z_GeneraFormDGIResg_ID);

	/** Get Z_GeneraFormDGIResg ID	  */
	public int getZ_GeneraFormDGIResg_ID();

    /** Column name Z_ResguardoSocioDoc_ID */
    public static final String COLUMNNAME_Z_ResguardoSocioDoc_ID = "Z_ResguardoSocioDoc_ID";

	/** Set Z_ResguardoSocioDoc ID	  */
	public void setZ_ResguardoSocioDoc_ID(int Z_ResguardoSocioDoc_ID);

	/** Get Z_ResguardoSocioDoc ID	  */
	public int getZ_ResguardoSocioDoc_ID();

    /** Column name Z_ResguardoSocioDocRet_ID */
    public static final String COLUMNNAME_Z_ResguardoSocioDocRet_ID = "Z_ResguardoSocioDocRet_ID";

	/** Set Z_ResguardoSocioDocRet ID	  */
	public void setZ_ResguardoSocioDocRet_ID(int Z_ResguardoSocioDocRet_ID);

	/** Get Z_ResguardoSocioDocRet ID	  */
	public int getZ_ResguardoSocioDocRet_ID();

    /** Column name Z_ResguardoSocio_ID */
    public static final String COLUMNNAME_Z_ResguardoSocio_ID = "Z_ResguardoSocio_ID";

	/** Set Z_ResguardoSocio ID	  */
	public void setZ_ResguardoSocio_ID(int Z_ResguardoSocio_ID);

	/** Get Z_ResguardoSocio ID	  */
	public int getZ_ResguardoSocio_ID();

    /** Column name Z_RetencionSocio_ID */
    public static final String COLUMNNAME_Z_RetencionSocio_ID = "Z_RetencionSocio_ID";

	/** Set Z_RetencionSocio ID	  */
	public void setZ_RetencionSocio_ID(int Z_RetencionSocio_ID);

	/** Get Z_RetencionSocio ID	  */
	public int getZ_RetencionSocio_ID();
}
