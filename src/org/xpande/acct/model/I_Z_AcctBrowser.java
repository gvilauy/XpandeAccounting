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

/** Generated Interface for Z_AcctBrowser
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0
 */
public interface I_Z_AcctBrowser 
{

    /** TableName=Z_AcctBrowser */
    public static final String Table_Name = "Z_AcctBrowser";

    /** AD_Table_ID=1000292 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AccountType */
    public static final String COLUMNNAME_AccountType = "AccountType";

	/** Set Account Type.
	  * Indicates the type of account
	  */
	public void setAccountType (String AccountType);

	/** Get Account Type.
	  * Indicates the type of account
	  */
	public String getAccountType();

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

    /** Column name Anulado */
    public static final String COLUMNNAME_Anulado = "Anulado";

	/** Set Anulado.
	  * Si esta anulado o no
	  */
	public void setAnulado (boolean Anulado);

	/** Get Anulado.
	  * Si esta anulado o no
	  */
	public boolean isAnulado();

    /** Column name C_AcctSchema_ID */
    public static final String COLUMNNAME_C_AcctSchema_ID = "C_AcctSchema_ID";

	/** Set Accounting Schema.
	  * Rules for accounting
	  */
	public void setC_AcctSchema_ID (int C_AcctSchema_ID);

	/** Get Accounting Schema.
	  * Rules for accounting
	  */
	public int getC_AcctSchema_ID();

	public I_C_AcctSchema getC_AcctSchema() throws RuntimeException;

    /** Column name C_BP_Group_ID */
    public static final String COLUMNNAME_C_BP_Group_ID = "C_BP_Group_ID";

	/** Set Business Partner Group.
	  * Business Partner Group
	  */
	public void setC_BP_Group_ID (int C_BP_Group_ID);

	/** Get Business Partner Group.
	  * Business Partner Group
	  */
	public int getC_BP_Group_ID();

	public I_C_BP_Group getC_BP_Group() throws RuntimeException;

    /** Column name C_Currency_2_ID */
    public static final String COLUMNNAME_C_Currency_2_ID = "C_Currency_2_ID";

	/** Set C_Currency_2_ID.
	  * Moneda secundaria para procesos
	  */
	public void setC_Currency_2_ID (int C_Currency_2_ID);

	/** Get C_Currency_2_ID.
	  * Moneda secundaria para procesos
	  */
	public int getC_Currency_2_ID();

    /** Column name C_Currency_ID */
    public static final String COLUMNNAME_C_Currency_ID = "C_Currency_ID";

	/** Set Currency.
	  * The Currency for this record
	  */
	public void setC_Currency_ID (int C_Currency_ID);

	/** Get Currency.
	  * The Currency for this record
	  */
	public int getC_Currency_ID();

	public I_C_Currency getC_Currency() throws RuntimeException;

    /** Column name Conciliado */
    public static final String COLUMNNAME_Conciliado = "Conciliado";

	/** Set Conciliado.
	  * Conciliado si o no
	  */
	public void setConciliado (boolean Conciliado);

	/** Get Conciliado.
	  * Conciliado si o no
	  */
	public boolean isConciliado();

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

    /** Column name Depositado */
    public static final String COLUMNNAME_Depositado = "Depositado";

	/** Set Depositado.
	  * Si esta o no depositado
	  */
	public void setDepositado (boolean Depositado);

	/** Get Depositado.
	  * Si esta o no depositado
	  */
	public boolean isDepositado();

    /** Column name Editable */
    public static final String COLUMNNAME_Editable = "Editable";

	/** Set Editable.
	  * SI un registro es o no editable
	  */
	public void setEditable (boolean Editable);

	/** Get Editable.
	  * SI un registro es o no editable
	  */
	public boolean isEditable();

    /** Column name Emitido */
    public static final String COLUMNNAME_Emitido = "Emitido";

	/** Set Emitido.
	  * Documento emitido
	  */
	public void setEmitido (boolean Emitido);

	/** Get Emitido.
	  * Documento emitido
	  */
	public boolean isEmitido();

    /** Column name EndDate */
    public static final String COLUMNNAME_EndDate = "EndDate";

	/** Set End Date.
	  * Last effective date (inclusive)
	  */
	public void setEndDate (Timestamp EndDate);

	/** Get End Date.
	  * Last effective date (inclusive)
	  */
	public Timestamp getEndDate();

    /** Column name Entregado */
    public static final String COLUMNNAME_Entregado = "Entregado";

	/** Set Entregado.
	  * Si esta entregado o no
	  */
	public void setEntregado (boolean Entregado);

	/** Get Entregado.
	  * Si esta entregado o no
	  */
	public boolean isEntregado();

    /** Column name FiltroEstadoMPago */
    public static final String COLUMNNAME_FiltroEstadoMPago = "FiltroEstadoMPago";

	/** Set FiltroEstadoMPago.
	  * Si se desea filtrar o no información según estado de medios de pago
	  */
	public void setFiltroEstadoMPago (boolean FiltroEstadoMPago);

	/** Get FiltroEstadoMPago.
	  * Si se desea filtrar o no información según estado de medios de pago
	  */
	public boolean isFiltroEstadoMPago();

    /** Column name FiltroManual */
    public static final String COLUMNNAME_FiltroManual = "FiltroManual";

	/** Set FiltroManual.
	  * Si se indica o no filtros de información de manera manual
	  */
	public void setFiltroManual (boolean FiltroManual);

	/** Get FiltroManual.
	  * Si se indica o no filtros de información de manera manual
	  */
	public boolean isFiltroManual();

    /** Column name IncCtaSaldoSinMov */
    public static final String COLUMNNAME_IncCtaSaldoSinMov = "IncCtaSaldoSinMov";

	/** Set IncCtaSaldoSinMov.
	  * Incluye cuentas contables con saldo sin movimientos
	  */
	public void setIncCtaSaldoSinMov (boolean IncCtaSaldoSinMov);

	/** Get IncCtaSaldoSinMov.
	  * Incluye cuentas contables con saldo sin movimientos
	  */
	public boolean isIncCtaSaldoSinMov();

    /** Column name IncCtaSinSaldoConMov */
    public static final String COLUMNNAME_IncCtaSinSaldoConMov = "IncCtaSinSaldoConMov";

	/** Set IncCtaSinSaldoConMov.
	  * Incluye cuentas contables sin saldo con movimientos
	  */
	public void setIncCtaSinSaldoConMov (boolean IncCtaSinSaldoConMov);

	/** Get IncCtaSinSaldoConMov.
	  * Incluye cuentas contables sin saldo con movimientos
	  */
	public boolean isIncCtaSinSaldoConMov();

    /** Column name IncCtaSinSaldoSinMov */
    public static final String COLUMNNAME_IncCtaSinSaldoSinMov = "IncCtaSinSaldoSinMov";

	/** Set IncCtaSinSaldoSinMov.
	  * Incluye cuentas contables sin saldo sin movimiento
	  */
	public void setIncCtaSinSaldoSinMov (boolean IncCtaSinSaldoSinMov);

	/** Get IncCtaSinSaldoSinMov.
	  * Incluye cuentas contables sin saldo sin movimiento
	  */
	public boolean isIncCtaSinSaldoSinMov();

    /** Column name IncInfoAuditoria */
    public static final String COLUMNNAME_IncInfoAuditoria = "IncInfoAuditoria";

	/** Set IncInfoAuditoria.
	  * Incluye información de auditoría
	  */
	public void setIncInfoAuditoria (boolean IncInfoAuditoria);

	/** Get IncInfoAuditoria.
	  * Incluye información de auditoría
	  */
	public boolean isIncInfoAuditoria();

    /** Column name IncInfoDocumento */
    public static final String COLUMNNAME_IncInfoDocumento = "IncInfoDocumento";

	/** Set IncInfoDocumento.
	  * Incluye información de documentos
	  */
	public void setIncInfoDocumento (boolean IncInfoDocumento);

	/** Get IncInfoDocumento.
	  * Incluye información de documentos
	  */
	public boolean isIncInfoDocumento();

    /** Column name IncInfoMedioPago */
    public static final String COLUMNNAME_IncInfoMedioPago = "IncInfoMedioPago";

	/** Set IncInfoMedioPago.
	  * Incluye información de medios de pago
	  */
	public void setIncInfoMedioPago (boolean IncInfoMedioPago);

	/** Get IncInfoMedioPago.
	  * Incluye información de medios de pago
	  */
	public boolean isIncInfoMedioPago();

    /** Column name IncInfoPartner */
    public static final String COLUMNNAME_IncInfoPartner = "IncInfoPartner";

	/** Set IncInfoPartner.
	  * Incluye información de socios de negocio
	  */
	public void setIncInfoPartner (boolean IncInfoPartner);

	/** Get IncInfoPartner.
	  * Incluye información de socios de negocio
	  */
	public boolean isIncInfoPartner();

    /** Column name IncInfoProd */
    public static final String COLUMNNAME_IncInfoProd = "IncInfoProd";

	/** Set IncInfoProd.
	  * Incluye información de productos
	  */
	public void setIncInfoProd (boolean IncInfoProd);

	/** Get IncInfoProd.
	  * Incluye información de productos
	  */
	public boolean isIncInfoProd();

    /** Column name IncInfoRetencion */
    public static final String COLUMNNAME_IncInfoRetencion = "IncInfoRetencion";

	/** Set IncInfoRetencion.
	  * Incluye información de retenciones
	  */
	public void setIncInfoRetencion (boolean IncInfoRetencion);

	/** Get IncInfoRetencion.
	  * Incluye información de retenciones
	  */
	public boolean isIncInfoRetencion();

    /** Column name IncInfoTax */
    public static final String COLUMNNAME_IncInfoTax = "IncInfoTax";

	/** Set IncInfoTax.
	  * Incluye información de impuestos
	  */
	public void setIncInfoTax (boolean IncInfoTax);

	/** Get IncInfoTax.
	  * Incluye información de impuestos
	  */
	public boolean isIncInfoTax();

    /** Column name IncSaldoInicial */
    public static final String COLUMNNAME_IncSaldoInicial = "IncSaldoInicial";

	/** Set IncSaldoInicial.
	  * Si se incluye o no el saldo inicial
	  */
	public void setIncSaldoInicial (boolean IncSaldoInicial);

	/** Get IncSaldoInicial.
	  * Si se incluye o no el saldo inicial
	  */
	public boolean isIncSaldoInicial();

    /** Column name IncTotMensual */
    public static final String COLUMNNAME_IncTotMensual = "IncTotMensual";

	/** Set IncTotMensual.
	  * Si incluye o no totales mensuales en informe
	  */
	public void setIncTotMensual (boolean IncTotMensual);

	/** Get IncTotMensual.
	  * Si incluye o no totales mensuales en informe
	  */
	public boolean isIncTotMensual();

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

    /** Column name IsCierreDiferencial */
    public static final String COLUMNNAME_IsCierreDiferencial = "IsCierreDiferencial";

	/** Set IsCierreDiferencial.
	  * Si se considera o no cierre de cuentas diferenciales
	  */
	public void setIsCierreDiferencial (boolean IsCierreDiferencial);

	/** Get IsCierreDiferencial.
	  * Si se considera o no cierre de cuentas diferenciales
	  */
	public boolean isCierreDiferencial();

    /** Column name IsCierreIntegral */
    public static final String COLUMNNAME_IsCierreIntegral = "IsCierreIntegral";

	/** Set IsCierreIntegral.
	  * Si considera o no cierra y apertura de cuentas integrales
	  */
	public void setIsCierreIntegral (boolean IsCierreIntegral);

	/** Get IsCierreIntegral.
	  * Si considera o no cierra y apertura de cuentas integrales
	  */
	public boolean isCierreIntegral();

    /** Column name M_Product_Category_ID */
    public static final String COLUMNNAME_M_Product_Category_ID = "M_Product_Category_ID";

	/** Set Product Category.
	  * Category of a Product
	  */
	public void setM_Product_Category_ID (int M_Product_Category_ID);

	/** Get Product Category.
	  * Category of a Product
	  */
	public int getM_Product_Category_ID();

	public I_M_Product_Category getM_Product_Category() throws RuntimeException;

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name ProcessButton */
    public static final String COLUMNNAME_ProcessButton = "ProcessButton";

	/** Set ProcessButton	  */
	public void setProcessButton (String ProcessButton);

	/** Get ProcessButton	  */
	public String getProcessButton();

    /** Column name ProcessButton2 */
    public static final String COLUMNNAME_ProcessButton2 = "ProcessButton2";

	/** Set ProcessButton2.
	  * Botón de Proceso
	  */
	public void setProcessButton2 (String ProcessButton2);

	/** Get ProcessButton2.
	  * Botón de Proceso
	  */
	public String getProcessButton2();

    /** Column name ProcessButton3 */
    public static final String COLUMNNAME_ProcessButton3 = "ProcessButton3";

	/** Set ProcessButton3.
	  * Botón para proceso
	  */
	public void setProcessButton3 (String ProcessButton3);

	/** Get ProcessButton3.
	  * Botón para proceso
	  */
	public String getProcessButton3();

    /** Column name ProcessButton4 */
    public static final String COLUMNNAME_ProcessButton4 = "ProcessButton4";

	/** Set ProcessButton4.
	  * Botón de Proceso
	  */
	public void setProcessButton4 (String ProcessButton4);

	/** Get ProcessButton4.
	  * Botón de Proceso
	  */
	public String getProcessButton4();

    /** Column name ProductType */
    public static final String COLUMNNAME_ProductType = "ProductType";

	/** Set Product Type.
	  * Type of product
	  */
	public void setProductType (String ProductType);

	/** Get Product Type.
	  * Type of product
	  */
	public String getProductType();

    /** Column name Reemplazado */
    public static final String COLUMNNAME_Reemplazado = "Reemplazado";

	/** Set Reemplazado.
	  * Reemplazado si o no
	  */
	public void setReemplazado (boolean Reemplazado);

	/** Get Reemplazado.
	  * Reemplazado si o no
	  */
	public boolean isReemplazado();

    /** Column name StartDate */
    public static final String COLUMNNAME_StartDate = "StartDate";

	/** Set Start Date.
	  * First effective day (inclusive)
	  */
	public void setStartDate (Timestamp StartDate);

	/** Get Start Date.
	  * First effective day (inclusive)
	  */
	public Timestamp getStartDate();

    /** Column name TextoFiltro */
    public static final String COLUMNNAME_TextoFiltro = "TextoFiltro";

	/** Set TextoFiltro.
	  * Texto genérico para filtro de valores
	  */
	public void setTextoFiltro (String TextoFiltro);

	/** Get TextoFiltro.
	  * Texto genérico para filtro de valores
	  */
	public String getTextoFiltro();

    /** Column name TextoFiltro2 */
    public static final String COLUMNNAME_TextoFiltro2 = "TextoFiltro2";

	/** Set TextoFiltro2.
	  * Texto genérico para filtro de valores
	  */
	public void setTextoFiltro2 (String TextoFiltro2);

	/** Get TextoFiltro2.
	  * Texto genérico para filtro de valores
	  */
	public String getTextoFiltro2();

    /** Column name TipoAcctBrowser */
    public static final String COLUMNNAME_TipoAcctBrowser = "TipoAcctBrowser";

	/** Set TipoAcctBrowser.
	  * Tipo de consulta en el Navegador Contable
	  */
	public void setTipoAcctBrowser (String TipoAcctBrowser);

	/** Get TipoAcctBrowser.
	  * Tipo de consulta en el Navegador Contable
	  */
	public String getTipoAcctBrowser();

    /** Column name TipoBalanceAcct */
    public static final String COLUMNNAME_TipoBalanceAcct = "TipoBalanceAcct";

	/** Set TipoBalanceAcct.
	  * Tipo de Balance Contable
	  */
	public void setTipoBalanceAcct (String TipoBalanceAcct);

	/** Get TipoBalanceAcct.
	  * Tipo de Balance Contable
	  */
	public String getTipoBalanceAcct();

    /** Column name TipoFiltroMonAcct */
    public static final String COLUMNNAME_TipoFiltroMonAcct = "TipoFiltroMonAcct";

	/** Set TipoFiltroMonAcct.
	  * Tipo filtro de moneda para reportes contables
	  */
	public void setTipoFiltroMonAcct (String TipoFiltroMonAcct);

	/** Get TipoFiltroMonAcct.
	  * Tipo filtro de moneda para reportes contables
	  */
	public String getTipoFiltroMonAcct();

    /** Column name TipoMayorAcct */
    public static final String COLUMNNAME_TipoMayorAcct = "TipoMayorAcct";

	/** Set TipoMayorAcct.
	  * Tipo de Mayor Contable
	  */
	public void setTipoMayorAcct (String TipoMayorAcct);

	/** Get TipoMayorAcct.
	  * Tipo de Mayor Contable
	  */
	public String getTipoMayorAcct();

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

    /** Column name Z_AcctBrowser_ID */
    public static final String COLUMNNAME_Z_AcctBrowser_ID = "Z_AcctBrowser_ID";

	/** Set Z_AcctBrowser ID	  */
	public void setZ_AcctBrowser_ID (int Z_AcctBrowser_ID);

	/** Get Z_AcctBrowser ID	  */
	public int getZ_AcctBrowser_ID();

    /** Column name Z_DataFiltro_ID */
    public static final String COLUMNNAME_Z_DataFiltro_ID = "Z_DataFiltro_ID";

	/** Set Z_DataFiltro ID	  */
	public void setZ_DataFiltro_ID (int Z_DataFiltro_ID);

	/** Get Z_DataFiltro ID	  */
	public int getZ_DataFiltro_ID();
}
