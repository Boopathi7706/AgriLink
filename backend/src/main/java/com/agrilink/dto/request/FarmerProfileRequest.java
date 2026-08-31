package com.agrilink.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class FarmerProfileRequest {

    @DecimalMin(value = "0.0", inclusive = true, message = "Farm size must be non-negative")
    private BigDecimal farmSizeAcres;

    @Size(max = 100, message = "Village name must not exceed 100 characters")
    private String village;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;

    @Size(max = 255, message = "Primary crops must not exceed 255 characters")
    private String primaryCrops;

    public FarmerProfileRequest() {
    }

    public FarmerProfileRequest(BigDecimal farmSizeAcres, String village, String district, String state, String pincode, String primaryCrops) {
        this.farmSizeAcres = farmSizeAcres;
        this.village = village;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
        this.primaryCrops = primaryCrops;
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
}
