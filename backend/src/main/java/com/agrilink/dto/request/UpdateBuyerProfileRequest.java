package com.agrilink.dto.request;

import com.agrilink.entity.enums.BuyerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateBuyerProfileRequest {

    @NotBlank(message = "Business name is required")
    @Size(max = 150, message = "Business name must not exceed 150 characters")
    private String businessName;

    @NotNull(message = "Buyer type is required")
    private BuyerType buyerType;

    @Size(max = 20, message = "GSTIN must not exceed 20 characters")
    private String gstin;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank(message = "District is required")
    @Size(max = 100, message = "District must not exceed 100 characters")
    private String district;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 10, message = "Pincode must not exceed 10 characters")
    private String pincode;

    public UpdateBuyerProfileRequest() {
    }

    public UpdateBuyerProfileRequest(String businessName, BuyerType buyerType, String gstin, String address, String district, String state, String pincode) {
        this.businessName = businessName;
        this.buyerType = buyerType;
        this.gstin = gstin;
        this.address = address;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public BuyerType getBuyerType() {
        return buyerType;
    }

    public void setBuyerType(BuyerType buyerType) {
        this.buyerType = buyerType;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
