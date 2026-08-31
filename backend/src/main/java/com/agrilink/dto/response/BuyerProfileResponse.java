package com.agrilink.dto.response;

import com.agrilink.entity.enums.BuyerType;

import java.time.OffsetDateTime;

public class BuyerProfileResponse {

    private Long id;
    private String businessName;
    private BuyerType buyerType;
    private String gstin;
    private String address;
    private String district;
    private String state;
    private String pincode;
    private boolean isVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public BuyerProfileResponse() {
    }

    public BuyerProfileResponse(Long id, String businessName, BuyerType buyerType, String gstin, String address, String district, String state, String pincode, boolean isVerified, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.businessName = businessName;
        this.buyerType = buyerType;
        this.gstin = gstin;
        this.address = address;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
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
