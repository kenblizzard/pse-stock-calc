package io.github.kenblizzard.pse_stock_calculator.service;

import java.math.BigDecimal;
import java.math.MathContext;

import io.github.kenblizzard.pse_stock_calculator.model.Stock;
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents;
import io.github.kenblizzard.pse_stock_calculator.util.Constants;

public class StocksCalculator {

    public static TransactionFeeComponents calculateTransactionFee(Stock stock,
                                                                   TransactionFeeComponents transactionFeeComponents,
                                                                   Constants.TRANSACTION_TYPE transaction_type) {


        Double price = transaction_type == Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY ?
                stock.getBuyPrice() : stock.getSellPrice();

        Double priceXshares = price * stock.getNumberOfShares();

        Double commissionFee = priceXshares * transactionFeeComponents.getCommission();

        commissionFee = commissionFee > 20.00 ? commissionFee : 20.00;

        Double vatFee = commissionFee * transactionFeeComponents.getVat();

        Double pseTransactionFee = priceXshares * transactionFeeComponents.getPseTransaction();

        Double sccpFee = priceXshares * transactionFeeComponents.getSccp();

        Double salesTax = transaction_type == Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY ? 0 :
                priceXshares * transactionFeeComponents.getSalesTax();


        TransactionFeeComponents newTransactionFee =
                new TransactionFeeComponents(commissionFee, vatFee, pseTransactionFee, sccpFee, salesTax);
        newTransactionFee.calculateTotalFee();

        return newTransactionFee;

    }

    public static double calculateTotalSharesPrice(Stock stock,
                                                   TransactionFeeComponents transactionFeeComponents,
                                                   Constants.TRANSACTION_TYPE transaction_type) {
        if (transactionFeeComponents.getTotalFee() == null || transactionFeeComponents.getTotalFee() == 0) {
            transactionFeeComponents = calculateTransactionFee(stock, transactionFeeComponents, transaction_type);
        }

        if (transaction_type == Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY) {
            return calculateTotalSharesPrice(stock.getBuyPrice(), stock.getNumberOfShares(), transactionFeeComponents.getTotalFee());
        } else {
            return calculateTotalSharesPrice(stock.getSellPrice(), stock.getNumberOfShares(), -transactionFeeComponents.getTotalFee());
        }

    }


    public static double calculateTotalSharesPrice(Double price, Integer numberOfShares, Double totalFees) {
        return (price * numberOfShares) + totalFees;
    }


    /*public static double adjustPriceBasedOnTickSize(String strPrice, int fluctuationDecimalPlace) {
        BigDecimal doublePrice = new BigDecimal( Double.parseDouble(strPrice));

        Double tickSize = getTickSize(doublePrice.doubleValue());

        doublePrice.setScale(fluctuationDecimalPlace, Ro)
    }*/

    private static double getTickSize(double price) {
        if (price >= 0.0001 && price <= 0.0099) {
            return 0.0001;
        } else if (price >= 0.0100 && price <= 0.0490) {
            return 0.0010;
        } else if (price >= 0.0500 && price <= 0.2490) {
            return 0.0010;
        } else if (price >= 0.2500 && price <= 0.4950) {
            return 0.0050;
        } else if (price >= 0.5000 && price <= 4.9900) {
            return 0.0100;
        } else if (price >= 5.0000 && price <= 9.99) {
            return 0.0100;
        } else if (price >= 10.0000 && price <= 19.9800) {
            return 0.0200;
        } else if (price >= 20.0000 && price <= 49.9500) {
            return 0.0500;
        } else if (price >= 50.0000 && price <= 99.9500) {
            return 0.0500;
        } else if (price >= 100.0000 && price <= 199.9000) {
            return 0.1000;
        } else if (price >= 200.0000 && price <= 499.8000) {
            return 0.2000;
        } else if (price >= 500.0000 && price <= 999.5000) {
            return 0.5000;
        } else if (price >= 1000.0000 && price <= 1999.0000) {
            return 1.0000;
        } else if (price >= 2000.0000 && price <= 4998.0000) {
            return 2.0000;
        } else {
            return 5.0000;
        }
    }
}
