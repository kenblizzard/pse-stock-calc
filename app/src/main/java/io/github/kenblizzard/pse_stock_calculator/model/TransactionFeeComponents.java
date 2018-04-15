package io.github.kenblizzard.pse_stock_calculator.model;

public class TransactionFeeComponents {
    private Double commission;
    private Double vat;
    private Double pseTransaction;
    private Double sccp;
    private Double salesTax;
    private Double totalFee;

    public TransactionFeeComponents(Double commission, Double vat, Double pseTransaction, Double sccp, Double salesTax) {
        this.commission = commission;
        this.vat = vat;
        this.pseTransaction = pseTransaction;
        this.sccp = sccp;
        this.salesTax = salesTax;
    }

    public Double getCommission() {
        return commission;
    }

    public void setCommission(Double commission) {
        this.commission = commission;
    }

    public Double getVat() {
        return vat;
    }

    public void setVat(Double vat) {
        this.vat = vat;
    }

    public Double getPseTransaction() {
        return pseTransaction;
    }

    public void setPseTransaction(Double pseTransfer) {
        this.pseTransaction = pseTransfer;
    }

    public Double getSccp() {
        return sccp;
    }

    public void setSccp(Double sccp) {
        this.sccp = sccp;
    }

    public Double getSalesTax() {
        return salesTax;
    }

    public void setSalesTax(Double salesTax) {
        this.salesTax = salesTax;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void calculateTotalFee() {
        this.totalFee = getCommission() + getSalesTax() + getSccp() + getVat() + getPseTransaction();
    }
}
