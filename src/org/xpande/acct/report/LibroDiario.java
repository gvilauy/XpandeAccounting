package org.xpande.acct.report;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 1/29/23.
 */
public class LibroDiario extends SvrProcess {

    private final String TABLA_REPORTE = "Z_RP_LibroDiario";
    public int adClientID = 0;
    public int adOrgID = 0;
    public Timestamp startDate = null;
    public Timestamp endDate = null;


    @Override
    protected void prepare() {

        ProcessInfoParameter[] para = getParameter();

        for (int i = 0; i < para.length; i++) {

            String name = para[i].getParameterName();

            if (name != null) {
                if (para[i].getParameter() != null) {
                    if (name.trim().equalsIgnoreCase("AD_Client_ID")) {
                        this.adClientID = ((BigDecimal) para[i].getParameter()).intValueExact();
                    } else if (name.trim().equalsIgnoreCase("AD_Org_ID")) {
                        this.adOrgID = ((BigDecimal) para[i].getParameter()).intValueExact();
                    } else if (name.trim().equalsIgnoreCase("DateAcct")) {
                        this.startDate = (Timestamp) para[i].getParameter();
                        this.endDate = (Timestamp) para[i].getParameter_To();
                    }
                }
            }
        }
    }

    @Override
    protected String doIt() throws Exception {

        // Elimina información anterior de las tablas del reporte para este usuario
        this.deleteData();

        // Obtiene información inicial del reporte
        this.getData();

        return "OK";
    }

    private void deleteData() {

        try {
            String action = " delete from " + TABLA_REPORTE + " where ad_user_id =" + this.getAD_User_ID();
            DB.executeUpdateEx(action, null);
        } catch (Exception e) {
            throw new AdempiereException(e);
        }
    }

    private void getData() {

        String sql, action;

        try {
            action = " insert into " + TABLA_REPORTE + "(ad_client_id, ad_org_id, ad_table_id, record_id, account_id, value, name, dateacct, " +
                        " docname, taxid, ad_user_id, amtacctdr, amtacctcr) ";

            sql = " select a.ad_client_id, a.ad_org_id, a.ad_table_id, a.record_id, "  +
                    " a.account_id, b.value, b.name, a.dateacct, doc.name as docname, " +
                    " bp.taxid, " + this.getAD_User_ID() + ", " +
                    " sum(a.amtacctdr) as amtacctdr, sum(a.amtacctcr) as  amtacctcr" +
                    " from fact_acct a " +
                    " inner join c_elementvalue b on a.account_id = b.c_elementvalue_id " +
                    " left outer join c_doctype doc on a.c_doctype_id = doc.c_doctype_id " +
                    " left outer join c_bpartner bp on a.c_bpartner_id = bp.c_bpartner_id " +
                    " where a.ad_client_id =" + this.adClientID +
                    " and a.ad_org_id =" + this.adOrgID +
                    " and a.dateacct between '" + this.startDate + "' and '" + this.endDate + "' " +
                    " group by 1,2,3,4,5,6,7,8,9,10,11 ";

            DB.executeUpdateEx(action + sql, null);

            action = " update " + TABLA_REPORTE +
                    " set startdate ='" + this.startDate + "', " +
                    " enddate ='" + this.endDate + "', " +
                    " amttransportdr = 0, amttransportcr = 0 " +
                    " where ad_user_id =" + this.getAD_User_ID();
            DB.executeUpdateEx(action, null);

        } catch (Exception e) {
            throw new AdempiereException(e);
        }

    }
}