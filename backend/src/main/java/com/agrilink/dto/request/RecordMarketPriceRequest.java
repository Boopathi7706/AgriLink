package com.agrilink.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecordMarketPriceRequest {

    @NotNull(message = "Mandi ID is required")
    private Long mandiId;

    @NotNull(message = "Commodity ID is required")
    private Long commodityId;

    @NotNull(message = "Minimum price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum price must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Minimum price must have at most 2 decimal places")
    private BigDecimal minPrice;

    @NotNull(message = "Maximum price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Maximum price must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Maximum price must have at most 2 decimal places")
    private BigDecimal maxPrice;

    @NotNull(message = "Modal price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Modal price must be non-negative")
    @Digits(integer = 8, fraction = 2, message = "Modal price must have at most 2 decimal places")
    private BigDecimal modalPrice;

    @NotNull(message = "Price date is required")
    @PastOrPresent(message = "Price date cannot be in the future")
    private LocalDate priceDate;

    public RecordMarketPriceRequest() {
    }

    public RecordMarketPriceRequest(Long mandiId, Long commodityId, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal modalPrice, LocalDate priceDate) {
        this.mandiId = mandiId;
        this.commodityId = commodityId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.modalPrice = modalPrice;
        this.priceDate = priceDate;
    }

    @AssertTrue(message = "Maximum price must be greater than or equal to minimum price")
    public boolean isMaxPriceValid() {
        if (minPrice == null || maxPrice == null) {
            return true; // Handled by @NotNull
        }
        return maxPrice.compareTo(minPrice) >= 0;
    }

    @AssertTrue(message = "Modal price must be between minimum price and maximum price")
    public boolean isModalPriceValid() {
        if (minPrice == null || maxPrice == null || modalPrice == null) {
            return true; // Handled by @NotNull
        }
        return modalPrice.compareTo(minPrice) >= 0 && modalPrice.compareTo(maxPrice) <= 0;
    }

    // Getters and Setters

    public Long getMandiId() {
        return mandiId;
    }

    public void setMandiId(Long mandiId) {
        this.mandiId = mandiId;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getModalPrice() {
        return modalPrice;
    }

    public void setModalPrice(BigDecimal modalPrice) {
        this.modalPrice = modalPrice;
    }

    public LocalDate getPriceDate() {
        return priceDate;
    }

    public void setPriceDate(LocalDate priceDate) {
        this.priceDate = priceDate;
    }
}
