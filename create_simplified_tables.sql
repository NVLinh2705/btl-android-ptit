-- PostgreSQL script to create simplified tables for Vietnamese administrative units.

-- Drop tables if they exist to ensure a clean slate.
DROP TABLE IF EXISTS wards;
DROP TABLE IF EXISTS districts;
DROP TABLE IF EXISTS provinces;

-- Create the provinces table
CREATE TABLE provinces (
    code VARCHAR(20) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    name_en VARCHAR(255),
    full_name VARCHAR(255) NOT NULL
);

-- Create the districts table
CREATE TABLE districts (
    code VARCHAR(20) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    name_en VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    province_code VARCHAR(20) NOT NULL,
    CONSTRAINT fk_province
        FOREIGN KEY(province_code) 
        REFERENCES provinces(code)
        ON DELETE CASCADE
);

-- Create an index for faster lookups by province_code
CREATE INDEX idx_districts_province_code ON districts(province_code);

-- Create the wards table
CREATE TABLE wards (
    code VARCHAR(20) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    name_en VARCHAR(255),
    full_name VARCHAR(255) NOT NULL,
    district_code VARCHAR(20) NOT NULL,
    CONSTRAINT fk_district
        FOREIGN KEY(district_code) 
        REFERENCES districts(code)
        ON DELETE CASCADE
);

-- Create an index for faster lookups by district_code
CREATE INDEX idx_wards_district_code ON wards(district_code);
