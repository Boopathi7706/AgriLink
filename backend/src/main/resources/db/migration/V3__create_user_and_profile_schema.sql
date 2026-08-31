-- =================================================================
-- AgriLink Authentication, User, and Profiles Schema
-- Version: V3
-- Purpose: Create tables for Users, Farmer Profiles, and Buyer Profiles
-- =================================================================

-- -----------------------------------------------------------------
-- 1. Core Users Table
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(25) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Role and Status Integrity Constraints
    CONSTRAINT chk_users_role CHECK (role IN ('FARMER', 'BUYER', 'ADMIN')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'PENDING_VERIFICATION', 'SUSPENDED', 'DEACTIVATED'))
);

-- Case-insensitive Unique Index for Email
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_lower ON users (LOWER(email));

-- Unique Index for Phone Number
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone ON users (phone_number);

-- Index for administrative and role/status queries
CREATE INDEX IF NOT EXISTS idx_users_role_status ON users (role, status);

-- -----------------------------------------------------------------
-- 2. Farmer Profiles Table (1:1 with users)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS farmer_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    farm_size_acres NUMERIC(6, 2) CHECK (farm_size_acres >= 0),
    village VARCHAR(100),
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10),
    primary_crops VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_farmer_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Geographic filtering for farmer profiles
CREATE INDEX IF NOT EXISTS idx_farmer_profiles_location ON farmer_profiles (state, district);

-- -----------------------------------------------------------------
-- 3. Buyer Profiles Table (1:1 with users)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buyer_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    business_name VARCHAR(150) NOT NULL,
    buyer_type VARCHAR(50) NOT NULL,
    gstin VARCHAR(20),
    address VARCHAR(255),
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10),
    is_verified BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_buyer_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_buyer_type CHECK (buyer_type IN ('WHOLESALER', 'RETAILER', 'PROCESSOR', 'EXPORTER', 'INDIVIDUAL'))
);

-- Geographic filtering for buyer profiles
CREATE INDEX IF NOT EXISTS idx_buyer_profiles_location ON buyer_profiles (state, district);
