package com.agrilink.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class FarmerProfileResponse {

    private Long id;
    private BigDecimal farmSizeAcres;
    private String village;
    private String district;
    private String state;
    private String pincode;
    private String primaryCrops;
    private boolean isVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public FarmerProfileResponse() {
    }

    public FarmerProfileResponse(Long id, BigDecimal farmSizeAcres, String village, String district, String state, String pincode, String primaryCrops, boolean isVerified, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.farmSizeAcres = farmSizeAcres;
        this.village = village;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
        this.primaryCrops = primaryCrops;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getFarmSizeAcres() {
        return farmSizeAcres;
    }

    public void setFarmSizeAcres(BigDecimal farmSizeAcres) {
        this.farmSizeAcres = farmSizeAcres;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPrimaryCrops() {
        return primaryCrops;
    }

    public void setPrimaryCrops(String primaryCrops) {
        this.primaryCrops = primaryCrops;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
