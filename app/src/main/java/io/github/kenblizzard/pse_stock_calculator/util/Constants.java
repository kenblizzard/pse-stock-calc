package io.github.kenblizzard.pse_stock_calculator.util;

public final class Constants {

    public enum TRANSACTION_TYPE {
        TRANSACTION_TYPE_BUY,
        TRANSACTION_TYPE_SELL
    }



    public static String FIREBASE_FEE_TRANSACTION = "transfee_values";
    public static String FIREBASE_FEE_COMMISSION = "commission";
    public static String FIREBASE_FEE_VAT = "vat";
    public static String FIREBASE_FEE_PSE_TRANS = "pse_trans";
    public static String FIREBASE_FEE_SCCP = "sccp";
    public static String FIREBASE_FEE_SALES_TAX = "sales_tax";
}
