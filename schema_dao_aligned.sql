-- Schema aligned with the DAO queries (table names + columns).
-- Drops the original SQL-file tables so this becomes the single source of truth.

USE property_management;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS leases;
DROP TABLE IF EXISTS apartments;
DROP TABLE IF EXISTS houses;
DROP TABLE IF EXISTS townhouses;
DROP TABLE IF EXISTS property;
DROP TABLE IF EXISTS tenants;
DROP TABLE IF EXISTS PaymentTable;
DROP TABLE IF EXISTS leaseTable;
DROP TABLE IF EXISTS ApartmentTable;
DROP TABLE IF EXISTS HouseTable;
DROP TABLE IF EXISTS TownHouseTable;
DROP TABLE IF EXISTS PropertyTable;
DROP TABLE IF EXISTS TenantTable;

-- ── tenants ──────────────────────────────────────────────────────────────
CREATE TABLE tenants (
    credential   INT          NOT NULL,
    first_name   VARCHAR(50),
    last_name    VARCHAR(50),
    phone_number INT,
    email        VARCHAR(80),
    status       VARCHAR(20),
    PRIMARY KEY (credential)
);

-- ── property (base table) ────────────────────────────────────────────────
CREATE TABLE property (
    propertyID   INT             NOT NULL,
    type         VARCHAR(20),
    floor_size   VARCHAR(20),
    full_address VARCHAR(120),
    location     VARCHAR(60),
    market_value DECIMAL(12, 2),
    rental_cost  DECIMAL(10, 2),
    availability BOOLEAN,
    PRIMARY KEY (propertyID)
);

-- ── houses ───────────────────────────────────────────────────────────────
CREATE TABLE houses (
    propertyID   INT             NOT NULL,
    floor_size   VARCHAR(20),
    full_address VARCHAR(120),
    location     VARCHAR(60),
    market_value DECIMAL(12, 2),
    rental_cost  DECIMAL(10, 2),
    availability BOOLEAN,
    plot_size    DECIMAL(10, 2),
    PRIMARY KEY (propertyID)
);

-- ── apartments ───────────────────────────────────────────────────────────
CREATE TABLE apartments (
    propertyID   INT             NOT NULL,
    unit_no      INT,
    backyard     BOOLEAN,
    floor_size   VARCHAR(20),
    full_address VARCHAR(120),
    location     VARCHAR(60),
    market_value DECIMAL(12, 2),
    rental_cost  DECIMAL(10, 2),
    availability BOOLEAN,
    floor_level  INT,
    elevator     BOOLEAN,
    PRIMARY KEY (propertyID)
);

-- ── townhouses ───────────────────────────────────────────────────────────
CREATE TABLE townhouses (
    propertyID   INT             NOT NULL,
    unit_no      INT,
    backyard     BOOLEAN,
    floor_size   VARCHAR(20),
    full_address VARCHAR(120),
    location     VARCHAR(60),
    market_value DECIMAL(12, 2),
    rental_cost  DECIMAL(10, 2),
    availability BOOLEAN,
    PRIMARY KEY (propertyID)
);

-- ── leases ───────────────────────────────────────────────────────────────
CREATE TABLE leases (
    leaseID            INT             NOT NULL,
    tenantID           INT,
    propertyID         INT,
    start_date         DATE,
    end_date           DATE,
    due_date           DATE,
    rent_amount        DECIMAL(10, 2),
    security_deposit   DECIMAL(10, 2),
    late_penalty_rate  DECIMAL(5, 4),
    grace_period       INT,
    is_active          BOOLEAN,
    PRIMARY KEY (leaseID)
);

-- ── payments ─────────────────────────────────────────────────────────────
CREATE TABLE payments (
    receipt      INT             NOT NULL,
    is_partial   BOOLEAN,
    amount       DECIMAL(10, 2),
    payment_date DATE,
    status       VARCHAR(20),
    leaseID      INT,
    PRIMARY KEY (receipt)
);

SET FOREIGN_KEY_CHECKS = 1;

-- ── seed data ────────────────────────────────────────────────────────────

INSERT INTO tenants (credential, first_name, last_name, phone_number, email, status) VALUES
    (1001, 'Alice',   'Mukasa',   811234567, 'alice@example.com',   'Active'),
    (1002, 'Bryan',   'Naidoo',   822345678, 'bryan@example.com',   'Active'),
    (1003, 'Chiamaka','Okonkwo',  833456789, 'chiamaka@example.com','Active'),
    (1004, 'David',   'Shipanga', 844567890, 'david@example.com',   'Blacklisted'),
    (1005, 'Esther',  'Kavari',   855678901, 'esther@example.com',  'Active');

INSERT INTO property (propertyID, type, floor_size, full_address, location, market_value, rental_cost, availability) VALUES
    (101, 'House',     '180',  '12 Acacia Ave, Klein Windhoek', 'Windhoek',  2200000.00,  9500.00, FALSE),
    (102, 'House',     '210',  '5 Baobab St, Pioneers Park',    'Windhoek',  2750000.00, 11000.00, TRUE),
    (201, 'Apartment', '65',   '8 Independence Ave, CBD',       'Windhoek',   950000.00,  6500.00, FALSE),
    (202, 'Apartment', '80',   '8 Independence Ave, CBD',       'Windhoek',  1100000.00,  7800.00, TRUE),
    (301, 'TownHouse', '140',  '4 Olive Crescent, Olympia',     'Windhoek',  1850000.00,  8800.00, FALSE),
    (302, 'TownHouse', '155',  '4 Olive Crescent, Olympia',     'Windhoek',  1950000.00,  9100.00, TRUE);

INSERT INTO houses (propertyID, floor_size, full_address, location, market_value, rental_cost, availability, plot_size) VALUES
    (101, '180', '12 Acacia Ave, Klein Windhoek', 'Windhoek', 2200000.00,  9500.00, FALSE, 600.00),
    (102, '210', '5 Baobab St, Pioneers Park',    'Windhoek', 2750000.00, 11000.00, TRUE,  720.00);

INSERT INTO apartments (propertyID, unit_no, backyard, floor_size, full_address, location, market_value, rental_cost, availability, floor_level, elevator) VALUES
    (201, 12, FALSE, '65', '8 Independence Ave, CBD', 'Windhoek',  950000.00, 6500.00, FALSE, 3, TRUE),
    (202, 14, FALSE, '80', '8 Independence Ave, CBD', 'Windhoek', 1100000.00, 7800.00, TRUE,  4, TRUE);

INSERT INTO townhouses (propertyID, unit_no, backyard, floor_size, full_address, location, market_value, rental_cost, availability) VALUES
    (301, 1, TRUE,  '140', '4 Olive Crescent, Olympia', 'Windhoek', 1850000.00, 8800.00, FALSE),
    (302, 2, FALSE, '155', '4 Olive Crescent, Olympia', 'Windhoek', 1950000.00, 9100.00, TRUE);

INSERT INTO leases (leaseID, tenantID, propertyID, start_date, end_date, due_date, rent_amount, security_deposit, late_penalty_rate, grace_period, is_active) VALUES
    (5001, 1001, 101, '2026-01-01', '2026-12-31', '2026-01-01',  9500.00, 19000.00, 0.0500, 5, TRUE),
    (5002, 1002, 201, '2026-02-01', '2027-01-31', '2026-02-01',  6500.00, 13000.00, 0.0500, 5, TRUE),
    (5003, 1003, 301, '2026-03-15', '2026-09-14', '2026-03-15',  8800.00, 17600.00, 0.0750, 7, TRUE);

INSERT INTO payments (receipt, is_partial, amount, payment_date, status, leaseID) VALUES
    (9001, FALSE,  9500.00, '2026-01-01', 'FULL',    5001),
    (9002, FALSE,  9500.00, '2026-02-01', 'FULL',    5001),
    (9003, FALSE,  6500.00, '2026-02-01', 'FULL',    5002),
    (9004, TRUE,   3000.00, '2026-03-15', 'PARTIAL', 5003),
    (9005, TRUE,   5800.00, '2026-03-20', 'FULL',    5003);
