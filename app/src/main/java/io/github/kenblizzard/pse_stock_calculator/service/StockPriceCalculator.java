package io.github.kenblizzard.pse_stock_calculator.service;

import io.github.kenblizzard.pse_stock_calculator.model.Stock;
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents;
import io.github.kenblizzard.pse_stock_calculator.util.Constants;

public class StockPriceCalculator {

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
                priceXshares * transactionFeeComponents.getSalesTax() ;


        TransactionFeeComponents newTransactionFee =
                new TransactionFeeComponents(commissionFee, vatFee, pseTransactionFee, sccpFee, salesTax);
        newTransactionFee.calculateTotalFee();

        return newTransactionFee;

    }

    public static double calculateTotalSharesPrice(Stock stock,
                                            TransactionFeeComponents transactionFeeComponents,
                                            Constants.TRANSACTION_TYPE transaction_type) {
        if(transactionFeeComponents.getTotalFee() == null || transactionFeeComponents.getTotalFee() == 0) {
            transactionFeeComponents = calculateTransactionFee(stock, transactionFeeComponents, transaction_type);
        }

        if(transaction_type == Constants.TRANSACTION_TYPE.TRANSACTION_TYPE_BUY) {
            return calculateTotalSharesPrice(stock.getBuyPrice(), stock.getNumberOfShares(), transactionFeeComponents.getTotalFee());
        }
        else {
            return calculateTotalSharesPrice(stock.getSellPrice(), stock.getNumberOfShares(), -transactionFeeComponents.getTotalFee());
        }

    }


    public static double calculateTotalSharesPrice(Double price, Integer numberOfShares, Double totalFees) {
        return (price * numberOfShares) + totalFees;
    }
}
