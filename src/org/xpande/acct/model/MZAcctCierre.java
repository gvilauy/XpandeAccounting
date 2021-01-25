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
import java.util.Date;
import java.util.HashMap;
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
import org.xpande.core.utils.DateUtils;
import org.xpande.retail.model.MZAstoVtaRecMP;

/** Generated Model for Z_AcctCierre
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class MZAcctCierre extends X_Z_AcctCierre implements DocAction, DocOptions {

	/**
	 *
	 */
	private static final long serialVersionUID = 20200613L;

    /** Standard Constructor */
    public MZAcctCierre (Properties ctx, int Z_AcctCierre_ID, String trxName)
    {
      super (ctx, Z_AcctCierre_ID, trxName);
    }

    /** Load Constructor */
    public MZAcctCierre (Properties ctx, ResultSet rs, String trxName)
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
			//options[newIndex++] = DocumentEngine.ACTION_Void;
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

		// Genero documento de apertura cuando se trata de cierre de cuentas integrales.
		if (this.getDocBaseType().equalsIgnoreCase("CJI")){
			m_processMsg = this.setAsientoAperturaIntegrales();
			if (m_processMsg != null)
				return DocAction.STATUS_Invalid;
		}

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
		log.info("voidIt - " + toString());
		return closeIt();
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
		String action = "";

		log.info("reActivateIt - " + toString());

		// Before reActivate
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REACTIVATE);
		if (m_processMsg != null)
			return false;

		// Control de período contable
		MPeriod.testPeriodOpen(getCtx(), this.getDateAcct(), this.getC_DocType_ID(), this.getAD_Org_ID());

		// Elimino asientos contables
		MFactAcct.deleteEx(this.get_Table_ID(), this.get_ID(), get_TrxName());


		// Si tengo asiento de Apertura de Saldos asociado a este cierre
		if (this.getZ_AcctApertura_ID() > 0){
			// Elimino asiento de Apertura de Saldos
			MZAcctApertura acctApertura = (MZAcctApertura) this.getZ_AcctApertura();
			if (!acctApertura.processIt(DocumentEngine.ACTION_ReActivate)){
				if (acctApertura.getProcessMsg() != null){
					m_processMsg = acctApertura.getProcessMsg();
				}
				else {
					m_processMsg = "No se pudo reactivar el asiento de Apertura asociado a este asiento de cierre.";
				}
				return false;
			}
			acctApertura.deleteEx(true);

			action = " update z_acctcierre set z_acctapertura_id = null where z_acctcierre_id =" + this.get_ID();
			DB.executeUpdateEx(action, get_TrxName());
		}


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
		return 0;
	}	//	getC_Currency_ID

    @Override
    public String toString()
    {
      StringBuffer sb = new StringBuffer ("MZAcctCierre[")
        .append(getSummary()).append("]");
      return sb.toString();
    }


	/***
	 * Obtiene información de saldos contables a considerar.
	 * Xpande. Created by Gabriel Vila on 6/13/20.
	 * @return
	 */
	public void getData(){

		try{

			// Elimino información anterior.
			String action = " delete from z_acctcierrelin where z_acctcierre_id =" + this.get_ID();
			DB.executeUpdateEx(action, get_TrxName());

			MDocType docType = (MDocType) this.getC_DocType();

			// Si es cierre de cuentas diferenciales
			if (docType.getDocBaseType().equalsIgnoreCase("CJD")){
				this.getSaldosDiferenciales();
			}
			// Si es cierre de cuentas integrales
			else if (docType.getDocBaseType().equalsIgnoreCase("CJI")){
				this.getSaldosIntegrales();
			}

			// Elimino cuentas que no tuvieron movimientos
			action = " delete from z_acctcierrelin where z_acctcierre_id =" + this.get_ID() +
					" and (amtacctdr =0 and amtacctcr =0 and amtsourcedr =0 and amtsourcecr =0) ";
			DB.executeUpdateEx(action, get_TrxName());

			// Actualizo totales
			String sql = " select sum(AmtAcctDrTo) from z_acctcierrelin where z_acctcierre_id =" + this.get_ID();
			BigDecimal totalAcctDr = DB.getSQLValueBDEx(get_TrxName(), sql);

			sql = " select sum(AmtAcctCrTo) from z_acctcierrelin where z_acctcierre_id =" + this.get_ID();
			BigDecimal totalAcctCr = DB.getSQLValueBDEx(get_TrxName(), sql);

			action = " update z_acctcierre set totalacctdr =" + totalAcctDr + ", " +
						" totalacctcr =" + totalAcctCr +
						" where z_acctcierre_id =" + this.get_ID();

			DB.executeUpdateEx(action, get_TrxName());

			// Actualizo monto de Resultado del Ejercicio para cierre de cuentas integrales
			if (docType.getDocBaseType().equalsIgnoreCase("CJD")){

				action = " update z_acctcierre set totalamt = totalacctdr - totalacctcr " +
						" where z_acctcierre_id =" + this.get_ID();
				DB.executeUpdateEx(action, get_TrxName());
			}

		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}
	}


	/***
	 * Obtiene información de saldos de cuentas diferenciales a considerar.
	 * Xpande. Created by Gabriel Vila on 6/13/20.
	 * @return
	 */
	private void getSaldosDiferenciales(){

		String sql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{

			MAcctSchema acctSchema = ((MAcctSchema) this.getC_AcctSchema());

		    sql = " select f.account_id, sum(round(f.amtacctdr,2)) as sumdr, sum(round(f.amtacctcr,2)) as sumcr " +
					" from fact_acct f " +
					" inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
					" where f.ad_client_id =" + this.getAD_Client_ID() +
					" and f.ad_org_id =" + this.getAD_Org_ID() +
					" and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
					" and f.dateacct between '" + this.getStartDate() + "' and '" + this.getDateAcct() + "' " +
					" and ev.accounttype in ('E','R') " +
					" and ev.issummary='N' " +
					" group by f.account_id " +
					" order by f.account_id ";


			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while (rs.next()){

				MElementValue elementValue = new MElementValue(getCtx(), rs.getInt("account_id"), null);

				BigDecimal amtAcctDR = rs.getBigDecimal("sumdr");
				BigDecimal amtAcctCR = rs.getBigDecimal("sumcr");

				this.setCierreLin(acctSchema, elementValue, acctSchema.getC_Currency_ID(), amtAcctDR, amtAcctCR,  Env.ZERO, Env.ZERO, -1);
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
	 * Obtiene información de saldos de cuentas integrales a considerar.
	 * Xpande. Created by Gabriel Vila on 6/13/20.
	 * @return
	 */
	private void getSaldosIntegrales(){

		String sql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			MAcctSchema acctSchema = ((MAcctSchema) this.getC_AcctSchema());

			String whereClause = " f.ad_client_id =" + this.getAD_Client_ID() +
					" and f.ad_org_id =" + this.getAD_Org_ID() +
					" and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
					" and f.dateacct between '" + this.getStartDate() + "' and '" + this.getDateAcct() + "' " +
					" and ev.accounttype in ('A','L','O') " +
					" and ev.issummary='N' ";

			// Si tengo que detallar cuentas por socio de negocio
			if (this.isBPartner()){
				this.getSaldosIntegralesBPartner();
				whereClause += " and ev.IsAcctCierreBP ='N' ";
			}

			sql = " select f.account_id, " +
					" sum(round(f.amtacctdr,2)) as sumdr, sum(round(f.amtacctcr,2)) as sumcr " +
					" from fact_acct f " +
					" inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
					" where " + whereClause +
					" group by f.account_id " +
					" order by f.account_id ";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while (rs.next()){

				MElementValue elementValue = new MElementValue(getCtx(), rs.getInt("account_id"), null);
				if (elementValue.getC_Currency_ID() <= 0){
					elementValue.setC_Currency_ID(acctSchema.getC_Currency_ID());
				}

				BigDecimal amtAcctDR = rs.getBigDecimal("sumdr");
				BigDecimal amtAcctCR = rs.getBigDecimal("sumcr");
				BigDecimal amtSourceDR = amtAcctDR;
				BigDecimal amtSourceCR = amtAcctCR;

				if (elementValue.getC_Currency_ID() != acctSchema.getC_Currency_ID()){
					amtSourceDR = this.getAccountSumSaldoMO(elementValue.get_ID(), elementValue.getC_Currency_ID(), -1, true, whereClause);
					amtSourceCR = this.getAccountSumSaldoMO(elementValue.get_ID(), elementValue.getC_Currency_ID(), -1, false, whereClause);
				}

				this.setCierreLin(acctSchema, elementValue, elementValue.getC_Currency_ID(), amtAcctDR, amtAcctCR, amtSourceDR, amtSourceCR, -1);
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
	 * Obtiene suma de saldo contable segun determinados parametros recibidos.
	 * Xpande. Created by Gabriel Vila on 6/22/20.
	 * @param accountID
	 * @param cCurrencyID
	 * @param cBPartnerID
	 * @param isDebit
	 * @param whereClause
	 * @return
	 */
	public BigDecimal getAccountSumSaldoMO(int accountID, int cCurrencyID, int cBPartnerID, boolean isDebit, String whereClause){

		String sql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		BigDecimal saldo = Env.ZERO;

		try{
			String sumField = "amtSourceDr";
			if (!isDebit) sumField = "amtSourceCr";

			if (cBPartnerID > 0){
				whereClause += " and f.c_bpartner_id =" + cBPartnerID;
			}

		    sql = " select sum(round(f." + sumField + ",2)) as saldomo " +
					" from fact_acct f " +
					" inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
					" where " + whereClause +
					" and f.account_id =" + accountID +
					" and f.c_currency_id =" + cCurrencyID;

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			if(rs.next()){
				saldo = rs.getBigDecimal("saldomo");
				if (saldo == null) saldo = Env.ZERO;
			}
		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}
		finally {
		    DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return saldo;
	}

	/***
	 * Obtiene información de saldos de cuentas integrales a considerar abiertas por socio de negocio.
	 * Xpande. Created by Gabriel Vila on 6/20/20.
	 * @return
	 */
	private void getSaldosIntegralesBPartner(){

		String sql = "";
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			HashMap<Integer, BigDecimal> hashRates = new HashMap<Integer, BigDecimal>();
			MAcctSchema acctSchema = ((MAcctSchema) this.getC_AcctSchema());

			// Si NO tengo que detallar cuentas por socio de negocio, salgo
			if (!this.isBPartner()){
				return;
			}

			String whereClause = " f.ad_client_id =" + this.getAD_Client_ID() +
					" and f.ad_org_id =" + this.getAD_Org_ID() +
					" and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
					" and f.dateacct between '" + this.getStartDate() + "' and '" + this.getDateAcct() + "' " +
					" and ev.accounttype in ('A','L','O') " +
					" and ev.issummary='N' " +
					" and ev.IsAcctCierreBP ='Y' ";

			sql = " select f.account_id, f.c_bpartner_id, " +
					" sum(round(f.amtacctdr,2)) as sumdr, sum(round(f.amtacctcr,2)) as sumcr " +
					" from fact_acct f " +
					" inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
					" where " + whereClause +
					" group by f.account_id, f.c_bpartner_id " +
					" order by f.account_id, f.c_bpartner_id ";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while (rs.next()){

				MElementValue elementValue = new MElementValue(getCtx(), rs.getInt("account_id"), null);
				if (elementValue.getC_Currency_ID() <= 0){
					elementValue.setC_Currency_ID(acctSchema.getC_Currency_ID());
				}

				BigDecimal amtAcctDR = rs.getBigDecimal("sumdr");
				BigDecimal amtAcctCR = rs.getBigDecimal("sumcr");
				BigDecimal amtSourceDR = amtAcctDR;
				BigDecimal amtSourceCR = amtAcctCR;

				int cBPartnerID = rs.getInt("c_bpartner_id");

				if (elementValue.getC_Currency_ID() != acctSchema.getC_Currency_ID()){
					amtSourceDR = this.getAccountSumSaldoMO(elementValue.get_ID(), elementValue.getC_Currency_ID(),
															cBPartnerID ,true, whereClause);
					amtSourceCR = this.getAccountSumSaldoMO(elementValue.get_ID(), elementValue.getC_Currency_ID(),
															cBPartnerID, false, whereClause);
				}

				this.setCierreLin(acctSchema, elementValue, elementValue.getC_Currency_ID(), amtAcctDR, amtAcctCR, amtSourceDR, amtSourceCR, cBPartnerID);
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
	 * Obtiene y retorna linea de este documento según ID de cuenta contable recibido.
	 * Xpande. Created by Gabriel Vila on 6/17/20.
	 * @param cElementValueID
	 * @return
	 */
	private MZAcctCierreLin getLineByAccount(int cElementValueID) {

		String whereClause = X_Z_AcctCierreLin.COLUMNNAME_Z_AcctCierre_ID + " =" + this.get_ID() +
				" AND " + X_Z_AcctCierreLin.COLUMNNAME_C_ElementValue_ID + " =" + cElementValueID;

		MZAcctCierreLin model = new Query(getCtx(), I_Z_AcctCierreLin.Table_Name, whereClause, get_TrxName()).first();

		return model;
	}

	/***
	 * Obtiene y retorna lineas de este documento.
	 * Xpande. Created by Gabriel Vila on 6/17/20.
	 * @return
	 */
	public List<MZAcctCierreLin> getLines() {

		String whereClause = X_Z_AcctCierreLin.COLUMNNAME_Z_AcctCierre_ID + " =" + this.get_ID();

		List<MZAcctCierreLin> lines = new Query(getCtx(), I_Z_AcctCierreLin.Table_Name, whereClause, get_TrxName()).list();

		return lines;
	}

	/***
	 * Genero y completo documento de Apertura de Saldos Contables asociado a cierre de cuentas integrales.
	 * Xpande. Created by Gabriel Vila on 6/18/20.
	 * @return
	 */
	private String setAsientoAperturaIntegrales() {

		String message = null;

		try{

			MDocType[] docTypes = MDocType.getOfDocBaseType(getCtx(), "AJI");
			if (docTypes.length <= 0){
				return "No se pudo obtener Documento para Asiento de Saldos Contables (AJI)";
			}
			MDocType docType = docTypes[0];

			Date dateFechaAux = new Date(this.getDateAcct().getTime());
			dateFechaAux =  DateUtils.addDays(dateFechaAux, 1);
			Timestamp dateAcct = new Timestamp(dateFechaAux.getTime());

			MZAcctApertura acctApertura = new MZAcctApertura(getCtx(), 0, get_TrxName());
			acctApertura.setAD_Org_ID(this.getAD_Org_ID());
			acctApertura.setC_DocType_ID(docType.get_ID());
			acctApertura.setDateDoc(dateAcct);
			acctApertura.setDateAcct(dateAcct);
			acctApertura.setC_AcctSchema_ID(this.getC_AcctSchema_ID());
			acctApertura.setDescription("Generada Automaticamente desde Cierre de Saldos Contables número :" + this.getDocumentNo());
			acctApertura.setIsBPartner(this.isBPartner());
			acctApertura.saveEx();

			// Lineas
			List<MZAcctCierreLin> cierreLinList = this.getLines();
			for (MZAcctCierreLin cierreLin: cierreLinList){
				MZAcctAperturaLin aperturaLin = new MZAcctAperturaLin(getCtx(), 0, get_TrxName());
				aperturaLin.setZ_AcctApertura_ID(acctApertura.get_ID());
				aperturaLin.setAD_Org_ID(this.getAD_Org_ID());
				aperturaLin.setC_ElementValue_ID(cierreLin.getC_ElementValue_ID());
				aperturaLin.setCodigoCuenta(cierreLin.getCodigoCuenta());
				aperturaLin.setC_Currency_ID(cierreLin.getC_Currency_ID());

				if (this.isBPartner()){
					aperturaLin.setC_BPartner_ID(cierreLin.getC_BPartner_ID());
				}

				// Doy vuelta el asiento
				aperturaLin.setAmtAcctDr(cierreLin.getAmtAcctCrTo());
				aperturaLin.setAmtAcctCr(cierreLin.getAmtAcctDrTo());

				aperturaLin.setCurrencyRate(cierreLin.getCurrencyRate());

				// Doy vuelta el asiento
				aperturaLin.setAmtSourceDr(cierreLin.getAmtSourceCrTo());
				aperturaLin.setAmtSourceCr(cierreLin.getAmtSourceDrTo());

				aperturaLin.saveEx();
			}

			if (!acctApertura.processIt(DocAction.ACTION_Complete)){
				message = "No se pudo completar documento de Apertura de Saldos Contables";
				if (acctApertura.getProcessMsg() != null){
					message += " - " + acctApertura.getProcessMsg();
				}
				return message;
			}
			acctApertura.saveEx();

			this.setZ_AcctApertura_ID(acctApertura.get_ID());

		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}

		return null;
	}


	/***
	 * Guarda información en linea de cierre.
	 * Xpande. Created by Gabriel Vila on 6/20/20.
	 * @param acctSchema
	 * @param elementValue
	 * @param cCurrencyID
	 * @param amtAcctDR
	 * @param amtAcctCR
	 * @param amtSourceDR
	 * @param amtSourceCR
	 * @param cBPartnerID
	 */
	private void setCierreLin(MAcctSchema acctSchema, MElementValue elementValue, int cCurrencyID,
							  BigDecimal amtAcctDR,  BigDecimal amtAcctCR,
							  BigDecimal amtSourceDR, BigDecimal amtSourceCR, int cBPartnerID){

		try{

			MZAcctCierreLin cierreLin = new MZAcctCierreLin(getCtx(), 0, get_TrxName());

			cierreLin.setAD_Org_ID(this.getAD_Org_ID());
			cierreLin.setZ_AcctCierre_ID(this.get_ID());
			cierreLin.setC_ElementValue_ID(elementValue.get_ID());
			cierreLin.setCodigoCuenta(elementValue.getValue());
			cierreLin.setC_Currency_ID(cCurrencyID);
			cierreLin.setAmtAcctDr(Env.ZERO);
			cierreLin.setAmtAcctCr(Env.ZERO);
			cierreLin.setAmtSourceDr(Env.ZERO);
			cierreLin.setAmtSourceCr(Env.ZERO);
			cierreLin.setDifferenceAmt(Env.ZERO);
			cierreLin.setAmtAcctDrTo(Env.ZERO);
			cierreLin.setAmtAcctCrTo(Env.ZERO);
			cierreLin.setDiffAmtSource(Env.ZERO);
			cierreLin.setAmtSourceDrTo(Env.ZERO);
			cierreLin.setAmtSourceCrTo(Env.ZERO);
			if (cBPartnerID > 0){
				cierreLin.setC_BPartner_ID(cBPartnerID);
			}

			if (elementValue.getAccountType().equalsIgnoreCase("R")){

				cierreLin.setDifferenceAmt(amtAcctCR.subtract(amtAcctDR));

				// Acct
				if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
					cierreLin.setAmtAcctDr(cierreLin.getDifferenceAmt().negate());
					cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt().negate());
				}
				else {
					cierreLin.setAmtAcctCr(cierreLin.getDifferenceAmt());
					cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt());
				}

			}
			else if (elementValue.getAccountType().equalsIgnoreCase("E")){

				cierreLin.setDifferenceAmt(amtAcctDR.subtract(amtAcctCR));

				// Acct
				if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
					cierreLin.setAmtAcctCr(cierreLin.getDifferenceAmt().negate());
					cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt().negate());
				}
				else{
					cierreLin.setAmtAcctDr(cierreLin.getDifferenceAmt());
					cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt());
				}
			}
			else if ((elementValue.getAccountType().equalsIgnoreCase("A"))
					|| (elementValue.getAccountType().equalsIgnoreCase("O"))){

				cierreLin.setDifferenceAmt(amtAcctCR.subtract(amtAcctDR));
				cierreLin.setDiffAmtSource(amtSourceCR.subtract(amtSourceDR));

				// Acct
				if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
					cierreLin.setAmtAcctDr(cierreLin.getDifferenceAmt().negate());
					cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt().negate());
				}
				else {
					cierreLin.setAmtAcctCr(cierreLin.getDifferenceAmt());
					cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt());
				}

				// Source
				if (cierreLin.getDiffAmtSource().compareTo(Env.ZERO) < 0){
					cierreLin.setAmtSourceDr(cierreLin.getDiffAmtSource().negate());
					cierreLin.setAmtSourceCrTo(cierreLin.getDiffAmtSource().negate());
				}
				else {
					cierreLin.setAmtSourceCr(cierreLin.getDiffAmtSource());
					cierreLin.setAmtSourceDrTo(cierreLin.getDiffAmtSource());
				}
			}
			else if (elementValue.getAccountType().equalsIgnoreCase("L")){

				cierreLin.setDifferenceAmt(amtAcctDR.subtract(amtAcctCR));
				cierreLin.setDiffAmtSource(amtSourceDR.subtract(amtSourceCR));

				// Acct
				if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
					cierreLin.setAmtAcctCr(cierreLin.getDifferenceAmt().negate());
					cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt().negate());
				}
				else{
					cierreLin.setAmtAcctDr(cierreLin.getDifferenceAmt());
					cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt());
				}

				// Source
				if (cierreLin.getDiffAmtSource().compareTo(Env.ZERO) < 0){
					cierreLin.setAmtSourceCr(cierreLin.getDiffAmtSource().negate());
					cierreLin.setAmtSourceDrTo(cierreLin.getDiffAmtSource().negate());
				}
				else{
					cierreLin.setAmtSourceDr(cierreLin.getDiffAmtSource());
					cierreLin.setAmtSourceCrTo(cierreLin.getDiffAmtSource());
				}

			}

			BigDecimal currencyRate = Env.ONE;
			if (elementValue.getC_Currency_ID() != acctSchema.getC_Currency_ID()){
				if ((cierreLin.getAmtSourceDrTo() != null) && (cierreLin.getAmtSourceDrTo().compareTo(Env.ZERO) != 0)){
					currencyRate = cierreLin.getAmtAcctDrTo().divide(cierreLin.getAmtSourceDrTo(), 3, RoundingMode.HALF_UP);
				}
				else if ((cierreLin.getAmtSourceCrTo() != null) && (cierreLin.getAmtSourceCrTo().compareTo(Env.ZERO) != 0)){
					currencyRate = cierreLin.getAmtAcctCrTo().divide(cierreLin.getAmtSourceCrTo(), 3, RoundingMode.HALF_UP);
				}
			}

			cierreLin.setCurrencyRate(currencyRate);
			cierreLin.saveEx();

		}
		catch (Exception e){
		    throw new AdempiereException(e);
		}
	}

}