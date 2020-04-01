package org.xpande.acct.utils;

import org.compiere.util.Env;

import java.math.BigDecimal;

/**
 * Clase para atributos de información en contabilizaciones multimoneda.
 * Product: Adempiere ERP & CRM Smart Business Solution. Localization : Uruguay - Xpande
 * Xpande. Created by Gabriel Vila on 4/1/20.
 */
public class InfoMultiCurrency {

    public int cuurencyID = -1;
    public BigDecimal amtSource = Env.ZERO;
    public BigDecimal amtAcct = Env.ZERO;

}
