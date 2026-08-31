-- =================================================================
-- AgriLink Market Price Discovery Schema
-- Version: V2
-- Purpose: Create tables for Mandis (Markets), Commodities, and Market Prices
-- =================================================================

-- -----------------------------------------------------------------
-- 1. Mandis (Agricultural Produce Market Committees / Markets)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS mandis (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    district VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    latitude DECIMAL(9, 6) CHECK (latitude BETWEEN -90.0 AND 90.0),
    longitude DECIMAL(9, 6) CHECK (longitude BETWEEN -180.0 AND 180.0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_mandi_name_location UNIQUE (name, district, state)
);

-- -----------------------------------------------------------------
-- 2. Commodities (Agricultural Produce / Crops)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS commodities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- -----------------------------------------------------------------
-- 3. Market Prices (Daily price records across mandis)
-- -----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS market_prices (
    id BIGSERIAL PRIMARY KEY,
    mandi_id BIGINT NOT NULL,
    commodity_id BIGINT NOT NULL,
    min_price NUMERIC(10, 2) NOT NULL,
    max_price NUMERIC(10, 2) NOT NULL,
    modal_price NUMERIC(10, 2) NOT NULL,
    price_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Key Relationships
    CONSTRAINT fk_market_prices_mandi
        FOREIGN KEY (mandi_id)
        REFERENCES mandis(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_market_prices_commodity
        FOREIGN KEY (commodity_id)
        REFERENCES commodities(id)
        ON DELETE RESTRICT,

    -- Unique price record per commodity per mandi per date
    CONSTRAINT uq_mandi_commodity_date UNIQUE (mandi_id, commodity_id, price_date),

    -- Price Validity & Business Rule Constraints
    CONSTRAINT chk_min_price_non_negative CHECK (min_price >= 0),
    CONSTRAINT chk_max_price_gte_min CHECK (max_price >= min_price),
    CONSTRAINT chk_modal_price_range CHECK (modal_price >= min_price AND modal_price <= max_price)
);

-- =================================================================
-- Indexes for High-Performance Price Discovery Queries
-- =================================================================

-- Geographic filtering for mandis
CREATE INDEX IF NOT EXISTS idx_mandis_state_district ON mandis(state, district);

-- Category filtering for commodities
CREATE INDEX IF NOT EXISTS idx_commodities_category ON commodities(category);

-- Essential for commodity price discovery over time across mandis
CREATE INDEX IF NOT EXISTS idx_market_prices_commodity_date ON market_prices(commodity_id, price_date DESC);

-- Essential for mandi-level daily price lookups
CREATE INDEX IF NOT EXISTS idx_market_prices_mandi_date ON market_prices(mandi_id, price_date DESC);

-- Date-based aggregation and chronological indexing
CREATE INDEX IF NOT EXISTS idx_market_prices_date ON market_prices(price_date DESC);
