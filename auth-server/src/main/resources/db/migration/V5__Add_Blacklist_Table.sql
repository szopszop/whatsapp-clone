-- V5__Add_Blacklist_Table_Schema.sql

CREATE TABLE token_blacklist (
                                 id BIGSERIAL PRIMARY KEY,
                                 jwt_id VARCHAR(100) NOT NULL UNIQUE,
                                 expiry_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);