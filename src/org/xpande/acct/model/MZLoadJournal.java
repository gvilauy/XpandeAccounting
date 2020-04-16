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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.Doc;
import org.compiere.impexp.ImpFormat;
import org.compiere.model.*;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.xpande.acct.utils.AccountUtils;
import org.xpande.comercial.utils.ComercialUtils;
import org.xpande.core.utils.CurrencyUtils;
import org.xpande.core.utils.DateUtils;
import org.xpande.financial.model.*;

/** Generated Model for Z_LoadJournal
 *  @author Adempiere (generated) 
 *  @version Release 3.9.0 - $Id$ */
public class MZLoadJournal extends X_Z_LoadJournal implements DocAction, DocOptions {

	/**
	 *
	 */
	private static final long serialVersionUID = 20190411L;

    /** Standard Constructor */
    public MZLoadJournal (Properties ctx, int Z_LoadJournal_ID, String trxName)
    {
      super (ctx, Z_LoadJournal_ID, trxName);
    }

    /** Load Constructor */
    public MZLoadJournal (Properties ctx, ResultSet rs, String trxName)
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

			options[newIndex++] = DocumentEngine.ACTION_None;
			//options[newIndex++] = DocumentEngine.ACTION_ReActivate;
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

		/*
		//	Std Period open?
		if (!MPeriod.isOpen(getCtx(), getDateDoc(), dt.getDocBaseType(), getAD_Org_ID()))
		{
			m_processMsg = "@PeriodClosed@";
			return DocAction.STATUS_Invalid;
		}
		*/

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

		// Obtengo lineas confirmadas cargadas desde el archivo de interface
		List<MZLoadJournalFile> loadJournalFileList = this.getLinesConfirmed();

		// Si no tengo lineas aviso y salgo.
		if (loadJournalFileList.size() <= 0){
			m_processMsg = "El documento no tiene lineas para procesar";
			return DocAction.STATUS_Invalid;
		}

		// Valido que no haya cuentas contables asociadas a impuestos que no tengan el valor del impuesto en dicha linea.
		String sql = " select count(a.*) as contador " +
				" from Z_LoadJournalFile a " +
				" inner join c_elementvalue ev on a.c_elementvalue_id = ev.c_elementvalue_id " +
				" where a.z_loadjournal_id =" + this.get_ID() +
				" and a.c_tax_id is null " +
				" and ev.istaxaccount='Y' ";
		int contador = DB.getSQLValueEx(get_TrxName(), sql);
		if (contador > 0){
			return "No se puede Completar este Documento, ya que tiene lineas con cuentas contables que requieren un valor para IMPUESTO";
		}

		// Valido que no haya cuentas contables asociadas a Retenciones que no tengan el valor de la retencion en su linea
		sql = " select count(a.*) as contador " +
				" from Z_LoadJournalFile a " +
				" inner join c_elementvalue ev on a.c_elementvalue_id = ev.c_elementvalue_id " +
				" where a.z_loadjournal_id =" + this.get_ID() +
				" and a.z_retencionsocio_id is null " +
				" and ev.IsRetencionAcct='Y' ";
		contador = DB.getSQLValueEx(get_TrxName(), sql);
		if (contador > 0){
			return "No se puede Completar este Documento, ya que tiene lineas con cuentas contables que requieren un valor para RETENCION";
		}

		MAcctSchema schema = (MAcctSchema) this.getC_AcctSchema();

		int adOrgIDAux = 0, cDocTypeIDAux = 0;
		Timestamp dateAcctAux = null;

		MJournal journal = null;

		// Recorro lineas
		for (MZLoadJournalFile loadJournalFile: loadJournalFileList){

			// Corte por organización - documento - fecha contable
			if ((loadJournalFile.getAD_OrgTrx_ID() != adOrgIDAux) || (loadJournalFile.getC_DocTypeTarget_ID() != cDocTypeIDAux)
				|| (dateAcctAux == null) || (loadJournalFile.getDateAcct().getTime() != dateAcctAux.getTime())){

				// Si tengo cabezal de asiento seteado anteriormente
				if (journal != null){

					// Completo asiento anterior
					if (!journal.processIt(DocAction.ACTION_Complete)){
						/*
						m_processMsg = "No se pudo Completar Asiento : Organización = " + journal.getAD_Org_ID() + ", Documento = " + journal.getC_DocType_ID() +
							 ", Fecha = " + journal.getDateAcct().toString() +  ", Total Debitos = " + journal.getTotalDr() + ", Total Credito = " + journal.getTotalCr() +
								" - " + journal.getProcessMsg();
						return DocAction.STATUS_Invalid;

						 */
					}
					journal.saveEx();
				}

				// Nuevo cabezal de asiento contable
				journal = new MJournal(getCtx(), 0, get_TrxName());
				journal.setAD_Org_ID(loadJournalFile.getAD_OrgTrx_ID());
				journal.setC_AcctSchema_ID(this.getC_AcctSchema_ID());
				journal.setC_DocType_ID(loadJournalFile.getC_DocTypeTarget_ID());
				journal.setDescription(this.getDescription());
				journal.setDateDoc(this.getDateDoc());
				journal.setDateAcct(loadJournalFile.getDateAcct());

				MPeriod period = MPeriod.get(getCtx(), loadJournalFile.getDateAcct(), loadJournalFile.getAD_OrgTrx_ID());
				if ((period == null) || (period.get_ID() <= 0)){
					m_processMsg = "No se obtuvo Período Contable para Organización : " + loadJournalFile.getAD_OrgTrx_ID() + ", " +
							"Fecha : " + loadJournalFile.getDateAcct().toString();
					return DocAction.STATUS_Invalid;
				}
				journal.setGL_Category_ID(1000000);
				journal.setC_Period_ID(period.get_ID());
				journal.setC_Currency_ID(schema.getC_Currency_ID());
				journal.setCurrencyRate(Env.ONE);
				journal.setC_ConversionType_ID(114);
				journal.set_ValueOfColumn("Z_LoadJournal_ID", this.get_ID());
				journal.saveEx();

				adOrgIDAux = loadJournalFile.getAD_OrgTrx_ID();
				cDocTypeIDAux = loadJournalFile.getC_DocTypeTarget_ID();
				dateAcctAux = loadJournalFile.getDateAcct();
			}

			// Linea de asiento manual
			MJournalLine journalLine = new MJournalLine(journal);
			journalLine.setAD_Org_ID(journal.getAD_Org_ID());
			journalLine.setGL_Journal_ID(journal.get_ID());
			journalLine.setDescription(loadJournalFile.getDescription());
			journalLine.setAmtSourceDr(loadJournalFile.getAmtSourceDr());
			journalLine.setAmtSourceCr(loadJournalFile.getAmtSourceCr());
			journalLine.setC_Currency_ID(loadJournalFile.getC_Currency_ID());
			journalLine.setC_ConversionType_ID(114);
			journalLine.setCurrencyRate(loadJournalFile.getMultiplyRate());
			//journalLine.setAmtAcctDr(loadJournalFile.getAmtAcctDr());
			//journalLine.setAmtAcctCr(loadJournalFile.getAmtAcctCr());
			journalLine.setDateAcct(journal.getDateAcct());
			journalLine.setC_UOM_ID(100);
			if (loadJournalFile.getQtyEntered() != null){
				journalLine.setQty(loadJournalFile.getQtyEntered());
			}
			journalLine.setAccount_ID(loadJournalFile.getC_ElementValue_ID());
			if (loadJournalFile.getC_Activity_ID() > 0){
				journalLine.setC_Activity_ID(loadJournalFile.getC_Activity_ID());
			}
			if (loadJournalFile.getC_BPartner_ID() > 0){
				journalLine.setC_BPartner_ID(loadJournalFile.getC_BPartner_ID());
			}
			if (loadJournalFile.getM_Product_ID() > 0){
				journalLine.setM_Product_ID(loadJournalFile.getM_Product_ID());
			}
			if (loadJournalFile.getC_Tax_ID() > 0){
				journalLine.set_ValueOfColumn("C_Tax_ID", loadJournalFile.getC_Tax_ID());
			}
			if (loadJournalFile.getZ_RetencionSocio_ID() > 0){
				journalLine.set_ValueOfColumn("Z_RetencionSocio_ID", loadJournalFile.getZ_RetencionSocio_ID());
			}

			if (loadJournalFile.getDueDate() != null){
				journalLine.set_ValueOfColumn("DueDate", loadJournalFile.getDueDate());
			}

			journalLine.saveEx();
		}

		// Si tengo cabezal de asiento seteado anteriormente
		if (journal != null){

			// Completo asiento anterior
			if (!journal.processIt(DocAction.ACTION_Complete)){
				/*
				m_processMsg = "No se pudo Completar Asiento : Organización = " + journal.getAD_Org_ID() + ", Documento = " + journal.getC_DocType_ID() +
						", Fecha = " + journal.getDateAcct().toString() +  ", Total Debitos = " + journal.getTotalDr() + ", Total Credito = " + journal.getTotalCr() +
						" - " + journal.getProcessMsg();
				return DocAction.STATUS_Invalid;

				 */
			}
			journal.saveEx();
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

		// Before Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
		if (m_processMsg != null)
			return false;

		// Reactivo asientos completados asociados a esta carga
		List<MJournal> journalCompletosList = this.getJournalsCompleted();
		for (MJournal journal: journalCompletosList){
			if (!journal.processIt(DocAction.ACTION_ReActivate)){
				m_processMsg = "No se pudo reactivar el asiento contable numero : " + journal.getDocumentNo();
				return false;
			}
		}

		// Elimino todos los asientos asociados a esta carga
		String action = " delete from gl_journal where z_loadjournal_id =" + this.get_ID();
		DB.executeUpdateEx(action, get_TrxName());

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

		this.setProcessed(true);
		this.setDocStatus(DOCSTATUS_Voided);
		this.setDocAction(DOCACTION_None);

		return true;

	}	//	voidIt


	/***
	 * Obtiene y retorna modelos de asientos contables completos y generados por esta carga.
	 * Xpande. Created by Gabriel Vila on 4/13/19.
	 * @return
	 */
	private List<MJournal> getJournalsCompleted() {

		String whereClause = "z_loadjournal_id =" + this.get_ID() +
				" AND " + X_GL_Journal.COLUMNNAME_DocStatus + " ='CO'";

		List<MJournal> lines = new Query(getCtx(), I_GL_Journal.Table_Name, whereClause, get_TrxName()).list();

		return lines;
	}


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
      StringBuffer sb = new StringBuffer ("MZLoadJournal[")
        .append(getSummary()).append("]");
      return sb.toString();
    }


	/***
	 * Metodo que ejecuta el proceso de interface desde archivo para carga de asientos contables.
	 * Xpande. Created by Gabriel Vila on 4/11/19.
	 */
	public void executeInterface(){

		try{

			// Elimino información anterior.
			this.deleteFileData();

			// Lee lineas de archivo
			this.getDataFromFile();

			// Valida lineas de archivo y trae información asociada.
			this.setDataFromFile();

		}
		catch (Exception e){
			throw new AdempiereException(e);
		}
	}


	/***
	 * Elimina información leída desde archivo.
	 * Xpande. Created by Gabriel Vila on 4/11/19.
	 */
	private void deleteFileData() {

		String action = "";

		try{
			action = " delete from " + I_Z_LoadJournalFile.Table_Name +
					" where " + X_Z_LoadJournalFile.COLUMNNAME_Z_LoadJournal_ID + " =" + this.get_ID();
			DB.executeUpdateEx(action, get_TrxName());
		}
		catch (Exception e){
			throw new AdempiereException(e);
		}
	}


	/***
	 * Proceso que lee archivo de interface.
	 * Xpande. Created by Gabriel Vila on 4/11/19.
	 */
	public void getDataFromFile() {

		FileReader fReader = null;
		BufferedReader bReader = null;

		String lineaArchivo = null;
		String mensaje = "";
		String action = "";


		try{

			// Formato de importación de archivo de interface para carga de asientos contables
			ImpFormat formatoImpArchivo = ImpFormat.load("Accounting_CargaAsientos");

			// Abro archivo
			File archivo = new File(this.getFileName());
			fReader = new FileReader(archivo);
			bReader = new BufferedReader(fReader);

			int contLineas = 0;
			int lineaID = 0;

			// Leo lineas del archivo
			lineaArchivo = bReader.readLine();

			while (lineaArchivo != null) {

				lineaArchivo = lineaArchivo.replace("'", "");
				//lineaArchivo = lineaArchivo.replace(",", "");
				contLineas++;

				lineaID = formatoImpArchivo.updateDB(lineaArchivo, getCtx(), get_TrxName());

				if (lineaID <= 0){
					MZLoadJournalFile loadJournalFile = new MZLoadJournalFile(getCtx(), 0, get_TrxName());
					loadJournalFile.setZ_LoadJournal_ID(this.get_ID());
					loadJournalFile.setLineNumber(contLineas);
					loadJournalFile.setFileLineText(lineaArchivo);
					loadJournalFile.setIsConfirmed(false);
					loadJournalFile.setErrorMsg("Formato de Linea Incorrecto.");
					loadJournalFile.saveEx();
					/*
					mensaje = "Error al procesar linea " + contLineas + " : " + lineaArchivo;
					throw new AdempiereException(mensaje);
					*/
				}
				else{
					// Seteo atributos de linea procesada en tabla
					action = " update " + I_Z_LoadJournalFile.Table_Name +
							" set " + X_Z_LoadJournalFile.COLUMNNAME_Z_LoadJournal_ID + " = " + this.get_ID() + ", " +
							" LineNumber =" + contLineas + ", " +
							" FileLineText ='" + lineaArchivo + "' " +
							" where " + X_Z_LoadJournalFile.COLUMNNAME_Z_LoadJournalFile_ID + " = " + lineaID;
					DB.executeUpdateEx(action, get_TrxName());
				}

				lineaArchivo = bReader.readLine();
			}

			this.setQtyCount(contLineas);
			this.saveEx();

		}
		catch (Exception e){
			throw new AdempiereException(e);
		}
		finally {
			if (bReader != null){
				try{
					bReader.close();
					if (fReader != null){
						fReader.close();
					}
				}
				catch (Exception e){
					log.log(Level.SEVERE, e.getMessage());
				}
			}
		}
	}


	/***
	 * Valida lineas leídas desde archivo y carga información asociada.
	 * Xpande. Created by Gabriel Vila on 4/11/19.
	 */
	private void setDataFromFile() {

		try{

			int contadorOK = 0;
			int contadorError = 0;

			MAcctSchema schema = (MAcctSchema) this.getC_AcctSchema();

			List<MZLoadJournalFile> loadJournalFileList = this.getLines();
			for (MZLoadJournalFile loadJournalFile : loadJournalFileList){

				if (loadJournalFile.getErrorMsg() != null){
					contadorError++;
					continue;
				}

				loadJournalFile.setIsConfirmed(true);

				// Organizacion
				if (loadJournalFile.getAD_OrgTrx_ID() <= 0){
					loadJournalFile.setIsConfirmed(false);
					loadJournalFile.setErrorMsg("No existe Organización con ese Número o la misma debe ser distinta de CERO.");
				}
				else{
					MOrg orgTrx = new MOrg(getCtx(), loadJournalFile.getAD_OrgTrx_ID(), null);
					if ((orgTrx == null) || (orgTrx.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Organización con ese Número");
					}
				}

				// Documento
				if (loadJournalFile.getC_DocTypeTarget_ID() <= 0){
					loadJournalFile.setIsConfirmed(false);
					loadJournalFile.setErrorMsg("No existe Documento con ese ID o el mismo debe ser distinto de CERO.");
				}
				else{
					MDocType docType = new MDocType(getCtx(), loadJournalFile.getC_DocTypeTarget_ID(), null);
					if ((docType == null) || (docType.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Documento con ese ID");
					}
					else{
						if (!docType.getDocBaseType().equalsIgnoreCase(Doc.DOCTYPE_GLJournal)){
							loadJournalFile.setIsConfirmed(false);
							loadJournalFile.setErrorMsg("El Documento no tiene asociado el Tipo : Nota de Contabilidad");
						}
					}
				}

				// Fecha Contable
				if ((loadJournalFile.getFechaAcctCadena() == null) || (loadJournalFile.getFechaAcctCadena().trim().equalsIgnoreCase(""))){
					loadJournalFile.setIsConfirmed(false);
					loadJournalFile.setErrorMsg("Debe indicar Fecha Contable");
				}
				else{
					Timestamp fecAcct = DateUtils.convertStringToTimestamp_ddMMyyyy(loadJournalFile.getFechaAcctCadena());
					if (fecAcct == null){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("Formato de Fecha Contable inválido : " + loadJournalFile.getFechaAcctCadena());
					}
					loadJournalFile.setDateAcct(fecAcct);
				}

				// Codigo cuenta contable
				if ((loadJournalFile.getCodigoCuenta() == null) || (loadJournalFile.getCodigoCuenta().trim().equalsIgnoreCase(""))){
					loadJournalFile.setIsConfirmed(false);
					loadJournalFile.setErrorMsg("Debe indicar Código de Cuenta Contable en la Linea del Archivo");
				}
				else{
					MElementValue elementValue = AccountUtils.getElementValueByValue(getCtx(), loadJournalFile.getCodigoCuenta().trim(), null);
					if ((elementValue == null) || (elementValue.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Cuenta Contable definida en el sistema con ese Código : " + loadJournalFile.getCodigoCuenta().trim());
					}
					else{
						loadJournalFile.setC_ElementValue_ID(elementValue.get_ID());
					}
				}

				// Moneda
				if (loadJournalFile.getC_Currency_ID() <= 0){
					loadJournalFile.setIsConfirmed(false);
					loadJournalFile.setErrorMsg("Falta indicar Moneda en la linea del archivo.");
				}
				else{
					MCurrency currency = new MCurrency(getCtx(), loadJournalFile.getC_Currency_ID(), null);
					if ((currency == null) || (currency.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Moneda definida en el sistema con ese número : " + loadJournalFile.getC_Currency_ID());
					}
				}

				// Socio de Negocio
				if ((loadJournalFile.getTaxID() != null) && (!loadJournalFile.getTaxID().trim().equalsIgnoreCase(""))){
					MBPartner partner = ComercialUtils.getPartnerByTaxID(getCtx(), loadJournalFile.getTaxID(), null);
					if ((partner == null) || (partner.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Socio de Negocio definido en el sistema con ese Número de Identificación : " + loadJournalFile.getTaxID());
					}
					else{
						loadJournalFile.setC_BPartner_ID(partner.get_ID());
					}
				}

				// Codigo Centro de Costos
				if ((loadJournalFile.getCodigoCCosto() != null) && (!loadJournalFile.getCodigoCCosto().trim().equalsIgnoreCase(""))){
					MActivity activity = AccountUtils.getActivityByValue(getCtx(), loadJournalFile.getCodigoCCosto().trim(), null);
					if ((activity == null) || (activity.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Centro de Costos definido en el sistema con ese Código : " + loadJournalFile.getCodigoCCosto().trim());
					}
					else{
						loadJournalFile.setC_Activity_ID(activity.get_ID());
					}
				}

				// Fecha de Vencimiento
				if ((loadJournalFile.getVencCadena() != null) && (!loadJournalFile.getVencCadena().trim().equalsIgnoreCase(""))){
					Timestamp fecVenc = DateUtils.convertStringToTimestamp_ddMMyyyy(loadJournalFile.getVencCadena());
					if (fecVenc == null){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("Formato de Fecha de Vencimiento inválido : " + loadJournalFile.getVencCadena());
					}
					loadJournalFile.setDueDate(fecVenc);
				}

				// Codigo de Producto
				if ((loadJournalFile.getCodigoProducto() != null) && (!loadJournalFile.getCodigoProducto().trim().equalsIgnoreCase(""))){
					MProduct product = ComercialUtils.getProductByValue(getCtx(), loadJournalFile.getCodigoProducto().trim(), null);
					if ((product == null) || (product.get_ID() <= 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("No existe Producto definido en el sistema con ese Código : " + loadJournalFile.getCodigoProducto().trim());
					}
					else{
						loadJournalFile.setM_Product_ID(product.get_ID());
					}
				}

				// Debe indicar al menos un importe Debíto o Crédito.
				if ((loadJournalFile.getAmtSourceDr() == null) || (loadJournalFile.getAmtSourceDr().compareTo(Env.ZERO) == 0)){
					if ((loadJournalFile.getAmtSourceCr() == null) || (loadJournalFile.getAmtSourceCr().compareTo(Env.ZERO) == 0)){
						loadJournalFile.setIsConfirmed(false);
						loadJournalFile.setErrorMsg("Debe indicar Monto Débito o Crédito en la linea del archivo");
					}
				}

				// Montos
				if (loadJournalFile.getAmtSourceDr() == null) loadJournalFile.setAmtSourceDr(Env.ZERO);
				if (loadJournalFile.getAmtSourceCr() == null) loadJournalFile.setAmtSourceCr(Env.ZERO);

				if (loadJournalFile.getC_Currency_ID() != schema.getC_Currency_ID()){

					BigDecimal rate = loadJournalFile.getMultiplyRate();

					// Si no tengo tasa de cambio desde interface, obtengo tasa para fecha contable de la linea
					if ((rate == null) || (rate.compareTo(Env.ONE) <= 0)){

						rate = CurrencyUtils.getCurrencyRate(getCtx(), this.getAD_Client_ID(), 0, loadJournalFile.getC_Currency_ID(), schema.getC_Currency_ID(),
								114, loadJournalFile.getDateAcct(), null);

						// Si no obtuve tasa de cambio desde el sistema para la fecha contable del cabezal del asiento, aviso y salgo
						if ((rate == null) || (rate.compareTo(Env.ONE) <= 0)){
							loadJournalFile.setIsConfirmed(false);
							MCurrency currency = (MCurrency) loadJournalFile.getC_Currency();
							loadJournalFile.setErrorMsg("No se obtuvo Tasa de Cambio para Moneda : " + currency.getISO_Code() + ", " +
														"Fecha : " + loadJournalFile.getDateAcct().toString());
						}
						else{
							loadJournalFile.setMultiplyRate(rate);
							loadJournalFile.setAmtAcctDr(loadJournalFile.getAmtSourceDr().multiply(rate).setScale(2, RoundingMode.HALF_UP));
							loadJournalFile.setAmtAcctCr(loadJournalFile.getAmtSourceCr().multiply(rate).setScale(2, RoundingMode.HALF_UP));
						}
					}
					else{
						loadJournalFile.setAmtAcctDr(loadJournalFile.getAmtSourceDr().multiply(rate).setScale(2, RoundingMode.HALF_UP));
						loadJournalFile.setAmtAcctCr(loadJournalFile.getAmtSourceCr().multiply(rate).setScale(2, RoundingMode.HALF_UP));
					}

				}
				else{
					loadJournalFile.setMultiplyRate(Env.ONE);
					loadJournalFile.setAmtAcctDr(loadJournalFile.getAmtSourceDr());
					loadJournalFile.setAmtAcctCr(loadJournalFile.getAmtSourceCr());
				}

				if (loadJournalFile.isConfirmed()){
					contadorOK++;
				}
				else{
					contadorError++;
				}

				loadJournalFile.saveEx();
			}

			this.setQty(contadorOK);
			this.setQtyReject(contadorError);
			this.saveEx();

		}
		catch (Exception e){
			throw new AdempiereException(e);
		}
	}


	/***
	 * Obtiene y retorna lineas de este documento.
	 * Xpande. Created by Gabriel Vila on 4/11/19.
	 * @return
	 */
	public List<MZLoadJournalFile> getLines(){

		String whereClause = X_Z_LoadJournalFile.COLUMNNAME_Z_LoadJournal_ID + " =" + this.get_ID();

		List<MZLoadJournalFile> lines = new Query(getCtx(), I_Z_LoadJournalFile.Table_Name, whereClause, get_TrxName()).list();

		return lines;
	}

	/***
	 * Obtiene y retorna lineas confirmadas de este documento.
	 * Xpande. Created by Gabriel Vila on 4/11/19.
	 * @return
	 */
	public List<MZLoadJournalFile> getLinesConfirmed(){

		String whereClause = X_Z_LoadJournalFile.COLUMNNAME_Z_LoadJournal_ID + " =" + this.get_ID() +
				" AND " + X_Z_LoadJournalFile.COLUMNNAME_IsConfirmed + " ='Y' ";

		List<MZLoadJournalFile> lines = new Query(getCtx(), I_Z_LoadJournalFile.Table_Name, whereClause, get_TrxName())
				.setOrderBy(" AD_OrgTrx_ID, C_DocTypeTarget_ID, DateAcct ").list();

		return lines;
	}


}