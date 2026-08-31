package com.agrilink.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceDiscoveryResponse {

    private Long commodityId;
    private String commodityName;
    private String category;
    private LocalDate priceDate;
    private BigDecimal lowestModalPrice;
    private BigDecimal highestModalPrice;
    private BigDecimal averageModalPrice;
    private int totalMandisReporting;
    private List<MarketPriceResponse> mandiPrices = new ArrayList<>();

    public PriceDiscoveryResponse() {
    }

    public PriceDiscoveryResponse(Long commodityId, String commodityName, String category, LocalDate priceDate,
                                  BigDecimal lowestModalPrice, BigDecimal highestModalPrice, BigDecimal averageModalPrice,
                                  int totalMandisReporting, List<MarketPriceResponse> mandiPrices) {
        this.commodityId = commodityId;
        this.commodityName = commodityName;
        this.category = category;
        this.priceDate = priceDate;
        this.lowestModalPrice = lowestModalPrice;
        this.highestModalPrice = highestModalPrice;
        this.averageModalPrice = averageModalPrice;
        this.totalMandisReporting = totalMandisReporting;
        this.mandiPrices = mandiPrices != null ? mandiPrices : new ArrayList<>();
    }

    // Getters and Setters

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }

    public BigDecimal getLowestModalPrice() {
        return lowestModalPrice;
    }

    public void setLowestModalPrice(BigDecimal lowestModalPrice) {
        this.lowestModalPrice = lowestModalPrice;
    }

    public BigDecimal getHighestModalPrice() {
        return highestModalPrice;
    }

    public void setHighestModalPrice(BigDecimal highestModalPrice) {
        this.highestModalPrice = highestModalPrice;
    }

    public BigDecimal getAverageModalPrice() {
        return averageModalPrice;
    }

    public void setAverageModalPrice(BigDecimal averageModalPrice) {
        this.averageModalPrice = averageModalPrice;
    }

    public int getTotalMandisReporting() {
        return totalMandisReporting;
    }

    public void setTotalMandisReporting(int totalMandisReporting) {
        this.totalMandisReporting = totalMandisReporting;
    }

    public List<MarketPriceResponse> getMandiPrices() {
        return mandiPrices;
    }

    public void setMandiPrices(List<MarketPriceResponse> mandiPrices) {
        this.mandiPrices = mandiPrices;
    }
}
