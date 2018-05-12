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


    public static String FIREBASE_ANALYTICS_MENU_CLICK = "MENU_CLICK";
    public static String FIREBASE_ANALYTICS_MENU_STOCKS_PRICE_CALC = "MENU_STOCKS_PRICE_CALC";
    public static String FIREBASE_ANALYTICS_MENU_BUDGET_STOCS_CALC = "MENU_BUDGET_STOCS_CALC";



    public static String FRAGMENT_TAG_STOCKS_PRICE = "STOCKS_PRICE";
    public static String FRAGMENT_TAG_BUDGET_BP    = "BUDGET_BP";
    public static String FRAGMENT_TAG_AVERAGE_UP_DOWN = "AVERAGE_UP_DOWN";
    public static String FRAGMENT_TAG_CASH_DIVIDENDS = "CASH_DIVIDENDS";
}
