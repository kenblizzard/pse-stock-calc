package io.github.kenblizzard.pse_stock_calculator.service;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.kenblizzard.pse_stock_calculator.model.Stock;
import io.github.kenblizzard.pse_stock_calculator.model.TransactionFeeComponents;
import io.github.kenblizzard.pse_stock_calculator.util.Constants;

import static io.github.kenblizzard.pse_stock_calculator.util.Constants.FIREBASE_FEE_COMMISSION;
import static io.github.kenblizzard.pse_stock_calculator.util.Constants.FIREBASE_FEE_PSE_TRANS;
import static io.github.kenblizzard.pse_stock_calculator.util.Constants.FIREBASE_FEE_SALES_TAX;
import static io.github.kenblizzard.pse_stock_calculator.util.Constants.FIREBASE_FEE_SCCP;
import static io.github.kenblizzard.pse_stock_calculator.util.Constants.FIREBASE_FEE_TRANSACTION;
import static io.github.kenblizzard.pse_stock_calculator.util.Constants.FIREBASE_FEE_VAT;

public class StocksCalculator {

    public static TransactionFeeComponents TRANSACTION_FEE_BASE_VALUES = new TransactionFeeComponents(0.0025, 0.12,
            0.00005, 0.0001, 0.006);

    public static void initFirebaseTransactionFeeValues() {

        Log.d("tes test est", "teasda");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FIREBASE_FEE_TRANSACTION);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double commissionFee = (double) dataSnapshot.child(FIREBASE_FEE_COMMISSION).getValue();
                double vatFee = (double) dataSnapshot.child(FIREBASE_FEE_VAT).getValue();
                double pseTrans = (double) dataSnapshot.child(FIREBASE_FEE_PSE_TRANS).getValue();
                double sccp = (double) dataSnapshot.child(FIREBASE_FEE_SCCP).getValue();
                double salesTax = (double) dataSnapshot.child(FIREBASE_FEE_SALES_TAX).getValue();


                Log.d("test", commissionFee + "");

                TRANSACTION_FEE_BASE_VALUES = new TransactionFeeComponents(commissionFee, vatFee, pseTrans, sccp, salesTax);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


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


    public static double calculateTotalSharesPrice(Double price, Long numberOfShares, Double totalFees) {
        return (price * numberOfShares) + totalFees;
    }


    /*public static double adjustPriceBasedOnTickSize(String strPrice, int fluctuationDecimalPlace) {
        BigDecimal doublePrice = new BigDecimal( Double.parseDouble(strPrice));

        Double tickSize = getTickSize(doublePrice.doubleValue());

        doublePrice.setScale(fluctuationDecimalPlace, Ro)
    }*/

    public static double getTickSize(double price) {
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

    public static long roundNumberOfSharesByBoardLot(double price, long numberOfShares) {
        return numberOfShares - (numberOfShares % getBoardLot(price));
    }


    public static int getBoardLot(double price) {
        if (price >= 0.0001 && price <= 0.0099) {
            return 1000000;
        } else if (price >= 0.0100 && price <= 0.0490) {
            return 100000;
        } else if (price >= 0.0500 && price <= 0.2490) {
            return 10000;
        } else if (price >= 0.2500 && price <= 0.4950) {
            return 10000;
        } else if (price >= 0.5000 && price <= 4.9900) {
            return 1000;
        } else if (price >= 5.0000 && price <= 9.99) {
            return 100;
        } else if (price >= 10.0000 && price <= 19.9800) {
            return 100;
        } else if (price >= 20.0000 && price <= 49.9500) {
            return 100;
        } else if (price >= 50.0000 && price <= 99.9500) {
            return 10;
        } else if (price >= 100.0000 && price <= 199.9000) {
            return 10;
        } else if (price >= 200.0000 && price <= 499.8000) {
            return 10;
        } else if (price >= 500.0000 && price <= 999.5000) {
            return 10;
        } else if (price >= 1000.0000 && price <= 1999.0000) {
            return 5;
        } else if (price >= 2000.0000 && price <= 4998.0000) {
            return 5;
        } else {
            return 5;
        }
    }
}
