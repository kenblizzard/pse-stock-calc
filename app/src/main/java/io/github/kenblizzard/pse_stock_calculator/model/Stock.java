package io.github.kenblizzard.pse_stock_calculator.model;

public class Stock {

    private String name;
    private Integer numberOfShare;
    private Double buyPrice;
    private Double sellPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfShares() {
        return numberOfShare;
    }

    public void setNumberOfShares(Integer numberOfShare) {
        this.numberOfShare = numberOfShare;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice == null ? 0 : buyPrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice == null ? 0 : sellPrice;
    }
}
