-- =================================================================
-- AgriLink Phase 1 Foundation Migration
-- Purpose: Verify PostgreSQL connectivity and Flyway migration setup
-- =================================================================

CREATE TABLE IF NOT EXISTS system_metadata (
    id VARCHAR(50) PRIMARY KEY,
    property_key VARCHAR(100) NOT NULL UNIQUE,
    property_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert initial metadata entry to verify DML execution
INSERT INTO system_metadata (id, property_key, property_value)
VALUES ('SYS-001', 'schema_version', '1.0.0-foundation')
ON CONFLICT (property_key) DO NOTHING;
