package io.github.kenblizzard.pse_stock_calculator.model;

import java.util.UUID;

public class Stock {

    private Long id;
    private String name;
    private Long numberOfShare;
    private Double buyPrice;
    private Double sellPrice;

    public Stock() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getNumberOfShares() {
        return numberOfShare;
    }

    public void setNumberOfShares(Long numberOfShare) {
        this.numberOfShare = numberOfShare == null ? 0 : numberOfShare;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
