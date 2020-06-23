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

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.core.utils.CurrencyUtils;
import org.xpande.financial.model.MZMedioPagoItem;
import org.xpande.financial.model.MZOrdenPago;
import org.xpande.financial.model.MZPago;

/** Generated Model for Z_DifCambio
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class MZDifCambio extends X_Z_DifCambio implements DocAction, DocOptions {

	/**
	 *
	 */
	private static final long serialVersionUID = 20190711L;

    /** Standard Constructor */
    public MZDifCambio (Properties ctx, int Z_DifCambio_ID, String trxName)
    {
      super (ctx, Z_DifCambio_ID, trxName);
    }

    /** Load Constructor */
    public MZDifCambio (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }


	@Override
	public int customizeValidActions(String docStatus, Object processing, String orderType, String isSOTrx, int AD_Table_ID, String[] docAction, String[] options, int index) {

		int newIndex = 0;

		if ((docStatus.equalsIgnoreCase(STATUS_Drafted))
				|| (docStatus.equalsIgnoreCase(STATUS_Invalid))
				|| (docStatus.equalsIgnoreCase(STATUS_InProgress))){

			options[newIndex++] = DocumentEngine.ACTION_Complete;

		}
		else if (docStatus.equalsIgnoreCase(STATUS_Completed)){

			options[newIndex++] = DocumentEngine.ACTION_ReActivate;
			options[newIndex++] = DocumentEngine.ACTION_Void;
		}

		return newIndex;
	}


	/**
	 * 	Get Document Info
	 *	@return document info (untranslated)
	 */
	public String getDocumentInfo()
	{
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		return dt.getName() + " " + getDocumentNo();
	}	//	getDocumentInfo

	/**
	 * 	Create PDF
	 *	@return File or null
	 */
	public File createPDF ()
	{
		try
		{
			File temp = File.createTempFile(get_TableName() + get_ID() +"_", ".pdf");
			return createPDF (temp);
		}
		catch (Exception e)
		{
			log.severe("Could not create PDF - " + e.getMessage());
		}
		return null;
	}	//	getPDF

	/**
	 * 	Create PDF file
	 *	@param file output file
	 *	@return file if success
	 */
	public File createPDF (File file)
	{
	//	ReportEngine re = ReportEngine.get (getCtx(), ReportEngine.INVOICE, getC_Invoice_ID());
	//	if (re == null)
			return null;
	//	return re.getPDF(file);
	}	//	createPDF

	
	/**************************************************************************
	 * 	Process document
	 *	@param processAction document action
	 *	@return true if performed
	 */
	public boolean processIt (String processAction)
	{
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}	//	processIt
	
	/**	Process Message 			*/
	private String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;

	/**
	 * 	Unlock Document.
	 * 	@return true if success 
	 */
	public boolean unlockIt()
	{
		log.info("unlockIt - " + toString());
	//	setProcessing(false);
		return true;
	}	//	unlockIt
	
	/**
	 * 	Invalidate Document
	 * 	@return true if success 
	 */
	public boolean invalidateIt()
	{
		log.info("invalidateIt - " + toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}	//	invalidateIt
	
	/**
	 *	Prepare Document
	 * 	@return new status (In Progress or Invalid) 
	 */
	public String prepareIt()
	{
		log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());

		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateDoc(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		//	Add up Amounts
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}	//	prepareIt
	
	/**
	 * 	Approve Document
	 * 	@return true if success 
	 */
	public boolean  approveIt()
	{
		log.info("approveIt - " + toString());
		setIsApproved(true);
		return true;
	}	//	approveIt
	
	/**
	 * 	Reject Approval
	 * 	@return true if success 
	 */
	public boolean rejectIt()
	{
		log.info("rejectIt - " + toString());
		setIsApproved(false);
		return true;
	}	//	rejectIt
	
	/**
	 * 	Complete Document
	 * 	@return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt()
	{
		//	Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		
		//	Implicit Approval
		if (!isApproved())
			approveIt();
		log.info(toString());
		//

		/*
		// Guardo historial de diferencia de cambio aplicada a registros contables.
		MSequence sequence = MSequence.get(getCtx(), I_Z_AcctFactDifCam.Table_Name, get_TrxName());
		String action = " insert into Z_AcctFactDifCam (Z_AcctFactDifCam_ID, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, " +
				"Z_DifCambio_ID, DateTrx, CurrencyRate, Fact_Acct_ID) ";
		String sql = " select nextid(" + sequence.get_ID() + ",'N'), a.ad_client_id, a.ad_org_id, a.isactive, a.created, a.createdby, a.updated, a.updatedby, " +
				" a.Z_DifCambio_ID, a.DateDoc, round(a.CurrencyRate, 3), b.fact_acct_id " +
				" from z_difcambio a " +
				" inner join Z_DifCambioDet b on a.z_difcambio_id = b.z_difcambio_id " +
				" where a.z_difcambio_id =" + this.get_ID();
		DB.executeUpdateEx(action + sql, get_TrxName());
		*/

		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		//	Set Definitive Document No
		setDefiniteDocumentNo();

		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}	//	completeIt
	
	/**
	 * 	Set the definite document number after completed
	 */
	private void setDefiniteDocumentNo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if (dt.isOverwriteDateOnComplete()) {
			setDateDoc(new Timestamp(System.currentTimeMillis()));
		}
		if (dt.isOverwriteSeqOnComplete()) {
			String value = null;
			int index = p_info.getColumnIndex("C_DocType_ID");
			if (index == -1)
				index = p_info.getColumnIndex("C_DocTypeTarget_ID");
			if (index != -1)		//	get based on Doc Type (might return null)
				value = DB.getDocumentNo(get_ValueAsInt(index), get_TrxName(), true);
			if (value != null) {
				setDocumentNo(value);
			}
		}
	}

	/**
	 * 	Void Document.
	 * 	Same as Close.
	 * 	@return true if success 
	 */
	public boolean voidIt()
	{
		log.info(toString());

		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		// Control de período contable
		MPeriod.testPeriodOpen(getCtx(), this.getDateDoc(), this.getC_DocType_ID(), this.getAD_Org_ID());

		// Elimino asientos contables
		MFactAcct.deleteEx(this.get_Table_ID(), this.get_ID(), get_TrxName());

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		setProcessed(true);
		setDocStatus(DOCSTATUS_Voided);
		setDocAction(DOCACTION_None);
		return true;

	}	//	voidIt
	
	/**
	 * 	Close Document.
	 * 	Cancel not delivered Qunatities
	 * 	@return true if success 
	 */
	public boolean closeIt()
	{
		log.info("closeIt - " + toString());

		//	Close Not delivered Qty
		setDocAction(DOCACTION_None);
		return true;
	}	//	closeIt
	
	/**
	 * 	Reverse Correction
	 * 	@return true if success 
	 */
	public boolean reverseCorrectIt()
	{
		log.info("reverseCorrectIt - " + toString());
		return false;
	}	//	reverseCorrectionIt
	
	/**
	 * 	Reverse Accrual - none
	 * 	@return true if success 
	 */
	public boolean reverseAccrualIt()
	{
		log.info("reverseAccrualIt - " + toString());
		return false;
	}	//	reverseAccrualIt
	
	/** 
	 * 	Re-activate
	 * 	@return true if success 
	 */
	public boolean reActivateIt()
	{
		log.info("reActivateIt - " + toString());

		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		// Control de período contable
		MPeriod.testPeriodOpen(getCtx(), this.getDateDoc(), this.getC_DocType_ID(), this.getAD_Org_ID());

		// Elimino asientos contables
		MFactAcct.deleteEx(this.get_Table_ID(), this.get_ID(), get_TrxName());

		/*
		// Elimino historial de esta diferencia de cambio en reigtros contables
		String action = " delete from Z_AcctFactDifCam where z_difcambio_id =" + this.get_ID();
		DB.executeUpdateEx(action, get_TrxName());
		*/

		// After reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REACTIVATE);
		if (m_processMsg != null)
			return false;

		this.setProcessed(false);
		this.setPosted(false);
		this.setDocStatus(DOCSTATUS_InProgress);
		this.setDocAction(DOCACTION_Complete);

		return true;
	}	//	reActivateIt
	
	
	/*************************************************************************
	 * 	Get Summary
	 *	@return Summary of Document
	 */
	public String getSummary()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getDocumentNo());
	//	sb.append(": ")
	//		.append(Msg.translate(getCtx(),"TotalLines")).append("=").append(getTotalLines())
	//		.append(" (#").append(getLines(false).length).append(")");
		//	 - Description
		if (getDescription() != null && getDescription().length() > 0)
			sb.append(" - ").append(getDescription());
		return sb.toString();
	}	//	getSummary

	/**
	 * 	Get Process Message
	 *	@return clear text error message
	 */
	public String getProcessMsg()
	{
		return m_processMsg;
	}	//	getProcessMsg
	
	/**
	 * 	Get Document Owner (Responsible)
	 *	@return AD_User_ID
	 */
	public int getDoc_User_ID()
	{
	//	return getSalesRep_ID();
		return 0;
	}	//	getDoc_User_ID

	/**
	 * 	Get Document Approval Amount
	 *	@return amount
	 */
	public BigDecimal getApprovalAmt()
	{
		return null;	//getTotalLines();
	}	//	getApprovalAmt
	
	/**
	 * 	Get Document Currency
	 *	@return C_Currency_ID
	 */
	public int getC_Currency_ID()
	{
	//	MPriceList pl = MPriceList.get(getCtx(), getM_PriceList_ID());
	//	return pl.getC_Currency_ID();
		return super.getC_Currency_ID();
	}	//	getC_Currency_ID

    @Override
    public String toString()
    {
      StringBuffer sb = new StringBuffer ("MZDifCambio[")
        .append(getSummary()).append("]");
      return sb.toString();
    }

	@Override
	protected boolean beforeSave(boolean newRecord) {

		try{

			// Me aseguro organización
			if (this.getAD_Org_ID() == 0){
				log.saveError("ATENCIÓN", "Debe Indicar Organización a considerar (no se acepta organización = * )");
				return false;
			}

			// Valido rango de fechas
			if (this.getStartDate().after(this.getDateDoc())){
				log.saveError("ATENCIÓN", "Rango de fechas no válido");
				return false;
			}

			MAcctSchema acctSchema = (MAcctSchema) this.getC_AcctSchema();

			// Valido que la moneda de proceso no sea igual a la moneda del esquema contable
			if (this.getC_Currency_ID() == acctSchema.getC_Currency_ID()){
				log.saveError("ATENCIÓN", "Debe Indicar Moneda distinta a la Moneda del Esquema Contable seleccionado");
				return false;
			}


			if ((newRecord) || (is_ValueChanged(X_Z_DifCambio.COLUMNNAME_DateDoc))){

				// Cargo tasa de cambio del día del documento
				BigDecimal rate = CurrencyUtils.getCurrencyRate(getCtx(), this.getAD_Client_ID(), 0, this.getC_Currency_ID(), acctSchema.getC_Currency_ID(),
											114, this.getDateDoc(), get_TrxName());
				if (rate == null){
					log.saveError("ATENCIÓN", "No hay Tasa de Cambio cargada en el sistema para moneda y fecha indicada.");
					return false;
				}

				this.setCurrencyRate(rate);
			}

			this.setDateAcct(this.getDateDoc());

		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}

		return true;
	}


	/***
	 * Obtiene y carga información de detalle de movimientos a procesar.
	 * Xpande. Created by Gabriel Vila on 7/11/19.
	 * @return
	 */
	public String getData() {

		String message = null;

		String sql = "", action = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{

			// Elimino info anterior
			action = " delete from z_difcambiodet where z_difcambio_id =" + this.get_ID();
			DB.executeUpdateEx(action, get_TrxName());

			MAcctSchema acctSchema = (MAcctSchema) this.getC_AcctSchema();

		    sql = " select fa.fact_acct_id, fa.account_id, fa.c_currency_id, coalesce(fa.amtsourcedr,0) as amtsourcedr, coalesce(fa.amtsourcecr,0) as amtsourcecr," +
					" coalesce(fa.amtacctdr,0) as amtacctdr, coalesce(fa.amtacctcr,0) as amtacctcr, " +
					" fa.dateacct, fa.currencyrate, fa.c_doctype_id, fa.documentnoref, fa.c_bpartner_id " +
					" from fact_acct fa  " +
					" inner join c_elementvalue ev on fa.account_id = ev.c_elementvalue_id " +
					" where fa.ad_client_id =" + this.getAD_Client_ID() +
					" and fa.ad_org_id =" + this.getAD_Org_ID() +
					" and fa.c_acctschema_id =" + this.getC_AcctSchema_ID() +
					" and fa.c_currency_id =" + this.getC_Currency_ID() +
					" and fa.dateacct between '" + this.getStartDate() + "' and '" + this.getDateDoc() + "' " +
					" and fa.ad_table_id !=" + this.get_Table_ID() +
					" and ev.AccountType IN ('A','L') " +
					" and ev.isforeigncurrency ='Y' " +
					" order by ev.value, fa.dateacct ";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while(rs.next()){

				MZDifCambioDet difCambioDet = new MZDifCambioDet(getCtx(), 0, get_TrxName());
				difCambioDet.setAD_Org_ID(this.getAD_Org_ID());
				difCambioDet.setZ_DifCambio_ID(this.get_ID());
				difCambioDet.setC_ElementValue_ID(rs.getInt("account_id"));
				difCambioDet.setDateAcct(rs.getTimestamp("dateacct"));
				difCambioDet.setC_DocType_ID(rs.getInt("c_doctype_id"));
				difCambioDet.setDocumentNoRef(rs.getString("documentnoref"));
				difCambioDet.setAmtSourceDr(rs.getBigDecimal("amtsourcedr").setScale(2, RoundingMode.HALF_UP));
				difCambioDet.setAmtSourceCr(rs.getBigDecimal("amtsourcecr").setScale(2, RoundingMode.HALF_UP));
				difCambioDet.setAmtAcctDr(rs.getBigDecimal("amtacctdr").setScale(2, RoundingMode.HALF_UP));
				difCambioDet.setAmtAcctCr(rs.getBigDecimal("amtacctcr").setScale(2, RoundingMode.HALF_UP));
				difCambioDet.setCurrencyRate(this.getCurrencyRate());
				difCambioDet.setC_Currency_ID(this.getC_Currency_ID());
				difCambioDet.setFact_Acct_ID(rs.getInt("fact_acct_id"));

				if (rs.getInt("c_bpartner_id") > 0){
					difCambioDet.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
				}

				BigDecimal rate = rs.getBigDecimal("currencyrate");

				/*
				// Busco tasa de cambio en proceso anterior de diferencia de cambio para este registro contable.
				MZAcctFactDifCam factDifCam = MZAcctFactDifCam.getLastByFactID(getCtx(), rs.getInt("fact_acct_id"), this.getStartDate(), null);
				if ((factDifCam != null) && (factDifCam.get_ID() > 0)){
					rate = factDifCam.getCurrencyRate();
				}
				*/

				// Redondeo tasa de cambio
				if ((rate == null) || (rate.compareTo(Env.ZERO) <= 0)){
					// Obtengo tasa de cambio para ese dia
					rate = CurrencyUtils.getCurrencyRate(getCtx(), this.getAD_Client_ID(), 0, this.getC_Currency_ID(), acctSchema.getC_Currency_ID(),
									114, difCambioDet.getDateAcct(), get_TrxName());
					if ((rate == null) || (rate.compareTo(Env.ZERO) == 0)){
						if (difCambioDet.getAmtSourceDr().compareTo(Env.ZERO) != 0){
							rate = difCambioDet.getAmtAcctDr().divide(difCambioDet.getAmtSourceDr(), 3, RoundingMode.HALF_UP);
						}
						else{
							rate = difCambioDet.getAmtAcctCr().divide(difCambioDet.getAmtSourceCr(), 3, RoundingMode.HALF_UP);
						}
					}
				}
				difCambioDet.setMultiplyRate(rate.setScale(3, RoundingMode.HALF_UP));

				// Diferencia debitos-creditos MO
				BigDecimal difSourceDR = Env.ZERO, difSourceCR = Env.ZERO;
				if (difCambioDet.getAmtSourceDr().compareTo(difCambioDet.getAmtSourceCr()) >= 0){
					difSourceDR = difCambioDet.getAmtSourceDr().subtract(difCambioDet.getAmtSourceCr());
				}
				else{
					difSourceCR = difCambioDet.getAmtSourceCr().subtract(difCambioDet.getAmtSourceDr());
				}

				difCambioDet.setAmtSourceDrDif(difSourceDR);
				difCambioDet.setAmtSourceCrDif(difSourceCR);

				// Diferencia debitos-creditos MN
				BigDecimal difAcctDR = Env.ZERO, difAcctCR = Env.ZERO;
				if (difCambioDet.getAmtAcctDr().compareTo(difCambioDet.getAmtAcctCr()) >= 0){
					difAcctDR = difCambioDet.getAmtAcctDr().subtract(difCambioDet.getAmtAcctCr());
				}
				else{
					difAcctCR = difCambioDet.getAmtAcctCr().subtract(difCambioDet.getAmtAcctDr());
				}

				difCambioDet.setAmtAcctDrDif(difAcctDR);
				difCambioDet.setAmtAcctCrDif(difAcctCR);


				// Asiento DifCambio Debitos - Creditos
				BigDecimal difSourceDRAcct = Env.ZERO;
				if (difSourceDR.compareTo(Env.ZERO) > 0){
					difSourceDRAcct = difSourceDR.multiply(this.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP);
				}
				difSourceDRAcct = difSourceDRAcct.subtract(difAcctDR);

				BigDecimal difSourceCRAcct = Env.ZERO;
				if (difSourceCR.compareTo(Env.ZERO) > 0){
					difSourceCRAcct = difSourceCR.multiply(this.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP);
				}
				difSourceCRAcct = difSourceCRAcct.subtract(difAcctCR);

				// Resto diferencias y segun signo, va al debe o al haber
				BigDecimal saldoAsiento = difSourceDRAcct.subtract(difSourceCRAcct);
				if (saldoAsiento.compareTo(Env.ZERO) > 0){
					difCambioDet.setAmtAcctDrTo(saldoAsiento);
					difCambioDet.setAmtAcctCrTo(Env.ZERO);
				}
				else{
					difCambioDet.setAmtAcctDrTo(Env.ZERO);
					difCambioDet.setAmtAcctCrTo(saldoAsiento.negate());
				}

				difCambioDet.saveEx();
			}

			// Actualizo tabla de lineas sumarizadas
			this.updateDataLineas();

		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}
		finally {
		    DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return message;
	}


	/***
	 * Actualiza datos resumidos en lineas de este documento.
	 * Xpande. Created by Gabriel Vila on 7/11/19.
	 */
	private void updateDataLineas() {

		String sql = "", action = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String whereClause = "";

		try{

			// Elimino info anterior
			action = " delete from z_difcambiolin where z_difcambio_id =" + this.get_ID();
			DB.executeUpdateEx(action, get_TrxName());

			// Si tengo que detallar cuentas por socio de negocio
			if (this.isBPartner()){
				this.updateDataLineasBPartner();
				whereClause += " and ev.IsAcctCierreBP ='N' ";
			}

			sql = " select a.ad_org_id, ev.value, a.c_elementvalue_id, sum(amtsourcedr) as amtsourcedr, sum(amtsourcecr) as amtsourcecr, " +
					" sum(amtacctdr) as amtacctdr, sum(amtacctcr) as amtacctcr " +
					" from z_difcambiodet a " +
					" inner join c_elementvalue ev on a.c_elementvalue_id = ev.c_elementvalue_id " +
					" where z_difcambio_id =" + this.get_ID() + whereClause +
					" group by a.ad_org_id, ev.value, a.c_elementvalue_id " +
					" order by a.ad_org_id, ev.value, a.c_elementvalue_id ";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while(rs.next()){

				BigDecimal amtAcctDR = rs.getBigDecimal("amtacctdr");
				BigDecimal amtAcctCR = rs.getBigDecimal("amtacctcr");
				BigDecimal amtSourceDR = rs.getBigDecimal("amtsourcedr");
				BigDecimal amtSourceCR = rs.getBigDecimal("amtsourcecr");
				int cElementValueID = rs.getInt("c_elementvalue_id");

				this.setDiferenciaLin(cElementValueID, -1, amtAcctDR, amtAcctCR, amtSourceDR, amtSourceCR);

			}

		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}
		finally {
		    DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	}

	private void updateDataLineasBPartner() {

		String sql = "", action = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{

			// Si no tengo que detallar cuentas por socio de negocio, no hago nada.
			if (!this.isBPartner()){
				return;
			}

			sql = " select a.ad_org_id, ev.value, a.c_elementvalue_id, a.c_bpartner_id, sum(amtsourcedr) as amtsourcedr, sum(amtsourcecr) as amtsourcecr, " +
					" sum(amtacctdr) as amtacctdr, sum(amtacctcr) as amtacctcr " +
					" from z_difcambiodet a " +
					" inner join c_elementvalue ev on a.c_elementvalue_id = ev.c_elementvalue_id " +
					" where z_difcambio_id =" + this.get_ID() +
					" and ev.IsAcctCierreBP ='Y' " +
					" group by a.ad_org_id, ev.value, a.c_elementvalue_id, a.c_bpartner_id " +
					" order by a.ad_org_id, ev.value, a.c_elementvalue_id, a.c_bpartner_id ";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while(rs.next()){

				BigDecimal amtAcctDR = rs.getBigDecimal("amtacctdr");
				BigDecimal amtAcctCR = rs.getBigDecimal("amtacctcr");
				BigDecimal amtSourceDR = rs.getBigDecimal("amtsourcedr");
				BigDecimal amtSourceCR = rs.getBigDecimal("amtsourcecr");
				int cElementValueID = rs.getInt("c_elementvalue_id");
				int cBPartnerID = rs.getInt("c_bpartner_id");

				this.setDiferenciaLin(cElementValueID, cBPartnerID, amtAcctDR, amtAcctCR, amtSourceDR, amtSourceCR);
			}

		}
		catch (Exception e){
			throw new AdempiereException(e);
		}
		finally {
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}
	}

	/***
	 * Guarda linea de diferencia de cambio con sumarización de información.
	 * @param cElementValueID
	 * @param cBPartnerID
	 * @param amtAcctDR
	 * @param amtAcctCR
	 * @param amtSourceDR
	 * @param amtSourceCR
	 */
	private void setDiferenciaLin(int cElementValueID, int cBPartnerID, BigDecimal amtAcctDR, BigDecimal amtAcctCR, BigDecimal amtSourceDR, BigDecimal amtSourceCR) {

		try{
			MZDifCambioLin difCambioLin = new MZDifCambioLin(getCtx(), 0, get_TrxName());
			difCambioLin.setAD_Org_ID(this.getAD_Org_ID());
			difCambioLin.setZ_DifCambio_ID(this.get_ID());
			difCambioLin.setCurrencyRate(this.getCurrencyRate());
			difCambioLin.setC_ElementValue_ID(cElementValueID);
			difCambioLin.setC_Currency_ID(this.getC_Currency_ID());

			if (cBPartnerID > 0){
				difCambioLin.setC_BPartner_ID(cBPartnerID);
			}

			difCambioLin.setAmtSourceDr(amtSourceDR);
			difCambioLin.setAmtSourceCr(amtSourceCR);
			difCambioLin.setAmtAcctDr(amtAcctDR);
			difCambioLin.setAmtAcctCr(amtAcctCR);

			// Diferencia debitos-creditos MO
			BigDecimal difSourceDR = Env.ZERO, difSourceCR = Env.ZERO;
			if (difCambioLin.getAmtSourceDr().compareTo(difCambioLin.getAmtSourceCr()) >= 0){
				difSourceDR = difCambioLin.getAmtSourceDr().subtract(difCambioLin.getAmtSourceCr());
			}
			else{
				difSourceCR = difCambioLin.getAmtSourceCr().subtract(difCambioLin.getAmtSourceDr());
			}

			difCambioLin.setAmtSourceDrDif(difSourceDR);
			difCambioLin.setAmtSourceCrDif(difSourceCR);

			// Diferencia debitos-creditos MN
			BigDecimal difAcctDR = Env.ZERO, difAcctCR = Env.ZERO;
			if (difCambioLin.getAmtAcctDr().compareTo(difCambioLin.getAmtAcctCr()) >= 0){
				difAcctDR = difCambioLin.getAmtAcctDr().subtract(difCambioLin.getAmtAcctCr());
			}
			else{
				difAcctCR = difCambioLin.getAmtAcctCr().subtract(difCambioLin.getAmtAcctDr());
			}

			difCambioLin.setAmtAcctDrDif(difAcctDR);
			difCambioLin.setAmtAcctCrDif(difAcctCR);

			// Asiento DifCambio Debitos - Creditos
			BigDecimal difSourceDRAcct = Env.ZERO;
			if (difSourceDR.compareTo(Env.ZERO) > 0){
				difSourceDRAcct = difSourceDR.multiply(this.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP);
			}
			difSourceDRAcct = difSourceDRAcct.subtract(difAcctDR);

			BigDecimal difSourceCRAcct = Env.ZERO;
			if (difSourceCR.compareTo(Env.ZERO) > 0){
				difSourceCRAcct = difSourceCR.multiply(this.getCurrencyRate()).setScale(2, RoundingMode.HALF_UP);
			}
			difSourceCRAcct = difSourceCRAcct.subtract(difAcctCR);

			// Resto diferencias y segun signo, va al debe o al haber
			BigDecimal saldoAsiento = difSourceDRAcct.subtract(difSourceCRAcct);
			if (saldoAsiento.compareTo(Env.ZERO) > 0){
				difCambioLin.setAmtAcctDrTo(saldoAsiento);
				difCambioLin.setAmtAcctCrTo(Env.ZERO);
			}
			else{
				difCambioLin.setAmtAcctDrTo(Env.ZERO);
				difCambioLin.setAmtAcctCrTo(saldoAsiento.negate());
			}

			difCambioLin.saveEx();
		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}
	}


	/***
	 * Obtiene y retorna lineas de este documento.
	 * Xpande. Created by Gabriel Vila on 7/11/19.
	 * @return
	 */
	public List<MZDifCambioLin> getLines() {

		String whereClause = X_Z_DifCambioLin.COLUMNNAME_Z_DifCambio_ID + " =" + this.get_ID();

		List<MZDifCambioLin> lines = new Query(getCtx(), I_Z_DifCambioLin.Table_Name, whereClause, get_TrxName()).list();

		return lines;
	}

}