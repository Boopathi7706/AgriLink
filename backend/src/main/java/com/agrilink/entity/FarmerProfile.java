package com.agrilink.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Farmer profile entity containing agricultural and farm location attributes.
 */
@Entity
@Table(name = "farmer_profiles")
public class FarmerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "farm_size_acres", precision = 6, scale = 2)
    private BigDecimal farmSizeAcres;

    @Column(name = "village", length = 100)
    private String village;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "primary_crops", length = 255)
    private String primaryCrops;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public FarmerProfile() {
    }

    public FarmerProfile(User user, BigDecimal farmSizeAcres, String village, String district, String state, String pincode, String primaryCrops) {
        this.user = user;
        this.farmSizeAcres = farmSizeAcres;
        this.village = village;
        this.district = district;
        this.state = state;
        this.pincode = pincode;
        this.primaryCrops = primaryCrops;
        this.isVerified = false;
    }

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FarmerProfile that = (FarmerProfile) o;
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        return user != null && Objects.equals(user.getId(), that.user != null ? that.user.getId() : null);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return user != null && user.getId() != null ? Objects.hash(user.getId()) : 0;
    }

    @Override
    public String toString() {
        return "FarmerProfile{" +
                "id=" + id +
                ", farmSizeAcres=" + farmSizeAcres +
                ", village='" + village + '\'' +
                ", district='" + district + '\'' +
                ", state='" + state + '\'' +
                ", pincode='" + pincode + '\'' +
                ", primaryCrops='" + primaryCrops + '\'' +
                ", isVerified=" + isVerified +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
