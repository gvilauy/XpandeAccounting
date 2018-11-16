package org.xpande.acct.utils;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.*;
import org.compiere.util.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import static org.compiere.acct.Doc.ACCTTYPE_BankAsset;
import static org.compiere.acct.Doc.ACCTTYPE_BankInTransit;

/**
 * Metodos staticos para intercambio de información de cuentas contables.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 10/2/18.
 */
public final class AccountUtils {


    /***
     * Metodo que retorna id de combinación de cuenta para determinada cuenta bancaria y AcctType.
     * Xpande. Created by Gabriel Vila on 10/2/18.
     * @param ctx
     * @param AcctType
     * @param cBankAccountID
     * @param as
     * @param trxName
     * @return
     */
    public static int getBankValidCombinationID (Properties ctx, int AcctType, int cBankAccountID, MAcctSchema as, String trxName){

        int value = -1;

        String sql = "";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            String fieldAccount = "";

            if (AcctType == ACCTTYPE_BankInTransit)
            {
                fieldAccount = "B_InTransit_Acct";
            }
            else if (AcctType == ACCTTYPE_BankAsset)
            {
                fieldAccount = "B_Asset_Acct";
            }
            else{
                return -1;
            }

            sql = "SELECT " +  fieldAccount  + " FROM C_BankAccount_Acct WHERE C_BankAccount_ID=? AND C_AcctSchema_ID =? ";

        	pstmt = DB.prepareStatement(sql, trxName);
            pstmt.setInt (1, cBankAccountID);
            pstmt.setInt (2, as.getC_AcctSchema_ID());

        	rs = pstmt.executeQuery();

        	if (rs.next()){
        	    value = rs.getInt(1);
        	}

        }
        catch (Exception e){
            throw new AdempiereException(e);
        }
        finally {
            DB.close(rs, pstmt);
        	rs = null; pstmt = null;
        }

        return value;
    }


    /***
     * Obtiene y retorna modelo de asiento contable manual segun número de documento recibido.
     * Xpande. Created by Gabriel Vila on 11/16/18
     * @param ctx
     * @param documentNo
     * @param trxName
     * @return
     */
    public static MJournal getJournalByDocumentNo(Properties ctx, String documentNo, String trxName){

        String whereClause = X_GL_Journal.COLUMNNAME_DocumentNo + " ='" + documentNo + "'";

        MJournal model = new Query(ctx, I_GL_Journal.Table_Name, whereClause, trxName).first();

        return model;
    }

}
