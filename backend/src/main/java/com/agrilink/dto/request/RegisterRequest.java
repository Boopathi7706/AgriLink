package com.agrilink.dto.request;

import com.agrilink.entity.enums.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be a valid 10-digit number")
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private Role role;

    @Valid
    private FarmerProfileRequest farmerProfile;

    @Valid
    private BuyerProfileRequest buyerProfile;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password, String phoneNumber, Role role, FarmerProfileRequest farmerProfile, BuyerProfileRequest buyerProfile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.farmerProfile = farmerProfile;
        this.buyerProfile = buyerProfile;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public FarmerProfileRequest getFarmerProfile() {
        return farmerProfile;
    }

    public void setFarmerProfile(FarmerProfileRequest farmerProfile) {
        this.farmerProfile = farmerProfile;
    }

    public BuyerProfileRequest getBuyerProfile() {
        return buyerProfile;
    }

    public void setBuyerProfile(BuyerProfileRequest buyerProfile) {
        this.buyerProfile = buyerProfile;
    }
}
