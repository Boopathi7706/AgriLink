package com.agrilink.dto.response;

import com.agrilink.entity.enums.AccountStatus;
import com.agrilink.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private AccountStatus status;
    private FarmerProfileResponse farmerProfile;
    private BuyerProfileResponse buyerProfile;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String name, String email, String phoneNumber, Role role, AccountStatus status, FarmerProfileResponse farmerProfile, BuyerProfileResponse buyerProfile, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.status = status;
        this.farmerProfile = farmerProfile;
        this.buyerProfile = buyerProfile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public FarmerProfileResponse getFarmerProfile() {
        return farmerProfile;
    }

    public void setFarmerProfile(FarmerProfileResponse farmerProfile) {
        this.farmerProfile = farmerProfile;
    }

    public BuyerProfileResponse getBuyerProfile() {
        return buyerProfile;
    }

    public void setBuyerProfile(BuyerProfileResponse buyerProfile) {
        this.buyerProfile = buyerProfile;
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
