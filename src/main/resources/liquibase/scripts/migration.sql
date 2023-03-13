-- liquibase formatted sql

-- changeSet aShadrin:1
CREATE TABLE colors
(
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE stock_cotton_socks
(
    id          BIGSERIAL PRIMARY KEY,
    cotton_part INTEGER NOT NULL CHECK (cotton_part >= 0 and cotton_part <= 100 ),
    quantity    INTEGER NOT NULL,
    color_id    BIGINT REFERENCES colors (id) ON DELETE CASCADE
);

-- changeSet aShadrin:2
ALTER TABLE stock_cotton_socks
    ADD CONSTRAINT quantity CHECK (quantity > 0);
