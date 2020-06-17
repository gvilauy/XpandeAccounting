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
		log.info("reActivateIt - " + toString());
		setProcessed(false);
		if (reverseCorrectIt())
			return true;
		return false;
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

				MZAcctCierreLin cierreLin = new MZAcctCierreLin(getCtx(), 0, get_TrxName());
				cierreLin.setAD_Org_ID(this.getAD_Org_ID());
				cierreLin.setZ_AcctCierre_ID(this.get_ID());
				cierreLin.setC_ElementValue_ID(elementValue.get_ID());
				cierreLin.setCodigoCuenta(elementValue.getValue());
				cierreLin.setC_Currency_ID(acctSchema.getC_Currency_ID());
				cierreLin.setAmtAcctDr(rs.getBigDecimal("sumdr"));
				cierreLin.setAmtAcctCr(rs.getBigDecimal("sumcr"));
				cierreLin.setAmtSourceDr(Env.ZERO);
				cierreLin.setAmtSourceCr(Env.ZERO);
				cierreLin.setDifferenceAmt(Env.ZERO);
				cierreLin.setAmtAcctDrTo(Env.ZERO);
				cierreLin.setAmtAcctCrTo(Env.ZERO);
				cierreLin.setDiffAmtSource(Env.ZERO);
				cierreLin.setAmtSourceDrTo(Env.ZERO);
				cierreLin.setAmtSourceCrTo(Env.ZERO);

				if (elementValue.getAccountType().equalsIgnoreCase("R")){

					cierreLin.setDifferenceAmt(cierreLin.getAmtAcctCr().subtract(cierreLin.getAmtAcctDr()));

					// Acct
					if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
						cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt().negate());
					}
					else {
						cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt());
					}

				}
				else if (elementValue.getAccountType().equalsIgnoreCase("E")){

					cierreLin.setDifferenceAmt(cierreLin.getAmtAcctDr().subtract(cierreLin.getAmtAcctCr()));

					// Acct
					if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
						cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt().negate());
					}
					else{
						cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt());
					}
				}

				cierreLin.saveEx();
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

			sql = " select f.account_id, f.c_currency_id, " +
					" sum(round(f.amtsourcedr,2)) as sumsourcedr, sum(round(f.amtsourcecr,2)) as sumsourcecr, " +
					" sum(round(f.amtacctdr,2)) as sumdr, sum(round(f.amtacctcr,2)) as sumcr " +
					" from fact_acct f " +
					" inner join c_elementvalue ev on f.account_id = ev.c_elementvalue_id " +
					" where f.ad_client_id =" + this.getAD_Client_ID() +
					" and f.ad_org_id =" + this.getAD_Org_ID() +
					" and f.c_acctschema_id =" + this.getC_AcctSchema_ID() +
					" and f.dateacct between '" + this.getStartDate() + "' and '" + this.getDateAcct() + "' " +
					" and ev.accounttype in ('A','L','O') " +
					" and ev.issummary='N' " +
					" group by f.account_id, f.c_currency_id " +
					" order by f.account_id, f.c_currency_id ";

			pstmt = DB.prepareStatement(sql, get_TrxName());
			rs = pstmt.executeQuery();

			while (rs.next()){

				MElementValue elementValue = new MElementValue(getCtx(), rs.getInt("account_id"), null);
				if (elementValue.getC_Currency_ID() <= 0){
					elementValue.setC_Currency_ID(acctSchema.getC_Currency_ID());
				}

				boolean newLinea = true;
				MZAcctCierreLin cierreLin = this.getLineByAccount(elementValue.get_ID());
				if ((cierreLin == null) || (cierreLin.get_ID() <= 0)){
					cierreLin = new MZAcctCierreLin(getCtx(), 0, get_TrxName());
				}
				else {
					newLinea = false;
				}

				if (newLinea){
					cierreLin.setAD_Org_ID(this.getAD_Org_ID());
					cierreLin.setZ_AcctCierre_ID(this.get_ID());
					cierreLin.setC_ElementValue_ID(elementValue.get_ID());
					cierreLin.setCodigoCuenta(elementValue.getValue());
					cierreLin.setC_Currency_ID(elementValue.getC_Currency_ID());
					cierreLin.setAmtAcctDr(rs.getBigDecimal("sumdr"));
					cierreLin.setAmtAcctCr(rs.getBigDecimal("sumcr"));
					cierreLin.setAmtSourceDr(Env.ZERO);
					cierreLin.setAmtSourceCr(Env.ZERO);
					cierreLin.setDifferenceAmt(Env.ZERO);
					cierreLin.setAmtAcctDrTo(Env.ZERO);
					cierreLin.setAmtAcctCrTo(Env.ZERO);
					cierreLin.setDiffAmtSource(Env.ZERO);
					cierreLin.setAmtSourceDrTo(Env.ZERO);
					cierreLin.setAmtSourceCrTo(Env.ZERO);
				}

				if ((elementValue.getAccountType().equalsIgnoreCase("A"))
						|| (elementValue.getAccountType().equalsIgnoreCase("O"))){

					if (newLinea){
						cierreLin.setDifferenceAmt(cierreLin.getAmtAcctCr().subtract(cierreLin.getAmtAcctDr()));
					}
					else {
						cierreLin.setDifferenceAmt(cierreLin.getDifferenceAmt().add(cierreLin.getAmtAcctCr().subtract(cierreLin.getAmtAcctDr())));
					}

					if (elementValue.getC_Currency_ID() == rs.getInt("c_currency_id")){
						cierreLin.setAmtSourceDr(rs.getBigDecimal("sumsourcedr"));
						cierreLin.setAmtSourceCr(rs.getBigDecimal("sumsourcecr"));
						cierreLin.setDiffAmtSource(cierreLin.getAmtSourceCr().subtract(cierreLin.getAmtSourceDr()));
					}

					// Acct
					if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
						cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt().negate());
					}
					else {
						cierreLin.setAmtAcctDrTo(cierreLin.getDifferenceAmt());
					}

					// Source
					if (cierreLin.getDiffAmtSource().compareTo(Env.ZERO) < 0){
						cierreLin.setAmtSourceDrTo(cierreLin.getDiffAmtSource().negate());
					}
					else {
						cierreLin.setAmtSourceDrTo(cierreLin.getDiffAmtSource());
					}
				}
				else if (elementValue.getAccountType().equalsIgnoreCase("L")){

					if (newLinea){
						cierreLin.setDifferenceAmt(cierreLin.getAmtAcctDr().subtract(cierreLin.getAmtAcctCr()));
					}
					else {
						cierreLin.setDifferenceAmt(cierreLin.getDifferenceAmt().add(cierreLin.getAmtAcctDr().subtract(cierreLin.getAmtAcctCr())));
					}

					if (elementValue.getC_Currency_ID() == rs.getInt("c_currency_id")) {
						cierreLin.setAmtSourceDr(rs.getBigDecimal("sumsourcedr"));
						cierreLin.setAmtSourceCr(rs.getBigDecimal("sumsourcecr"));
						cierreLin.setDiffAmtSource(cierreLin.getAmtSourceCr().subtract(cierreLin.getAmtSourceCr()));
					}

					// Acct
					if (cierreLin.getDifferenceAmt().compareTo(Env.ZERO) < 0){
						cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt().negate());
					}
					else{
						cierreLin.setAmtAcctCrTo(cierreLin.getDifferenceAmt());
					}

					// Source
					if (cierreLin.getDiffAmtSource().compareTo(Env.ZERO) < 0){
						cierreLin.setAmtSourceCrTo(cierreLin.getDiffAmtSource().negate());
					}
					else{
						cierreLin.setAmtSourceCrTo(cierreLin.getDiffAmtSource());
					}

				}

				cierreLin.saveEx();
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
}