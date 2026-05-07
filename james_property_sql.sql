CREATE DATABASE IF NOT EXISTS property_management;
USE property_management;

CREATE TABLE TenantTable (
    credential   INT NOT NULL,
    first_name   VARCHAR(20),
    last_name    VARCHAR(20),
    phone_no     INT,
    email        VARCHAR(20),
    status       VARCHAR(10),
    PRIMARY KEY (credential)
);

CREATE TABLE PropertyTable (
    propertyID   INT NOT NULL AUTO_INCREMENT,
    type         VARCHAR(9),
    floor_size   VARCHAR(10),
    full_address VARCHAR(40),
    Location     VARCHAR(30),
    market_value DECIMAL(10, 2),
    rental_cost  DECIMAL(10, 2),
    availablity  BOOLEAN,
    PRIMARY KEY (propertyID)
);

CREATE TABLE ApartmentTable (
    propertyID   INT NOT NULL,
    unit_no      INT,
    backyard     BOOLEAN,
    floor_size   VARCHAR(10),
    full_address VARCHAR(40),
    Location     VARCHAR(30),
    market_value DECIMAL(10, 2),
    rental_cost  DECIMAL(10, 2),
    availablity  BOOLEAN,
    elevator     BOOLEAN,
    floor_level  INT,
    PRIMARY KEY (propertyID),
    CONSTRAINT fk_apartment_property
        FOREIGN KEY (propertyID) REFERENCES PropertyTable (propertyID)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE HouseTable (
    propertyID   INT NOT NULL,
    floor_size   VARCHAR(10),
    full_address VARCHAR(40),
    Location     VARCHAR(30),
    market_value DECIMAL(10, 2),
    rental_cost  DECIMAL(10, 2),
    availablity  BOOLEAN,
    plot_size    DECIMAL(10, 2),
    PRIMARY KEY (propertyID),
    CONSTRAINT fk_house_property
        FOREIGN KEY (propertyID) REFERENCES PropertyTable (propertyID)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE TownHouseTable (
    propertyID   INT NOT NULL,
    unit_no      INT,
    backyard     BOOLEAN,
    floor_size   VARCHAR(10),
    full_address VARCHAR(40),
    Location     VARCHAR(30),
    market_value DECIMAL(10, 2),
    rental_cost  DECIMAL(10, 2),
    availablity  BOOLEAN,
    PRIMARY KEY (propertyID),
    CONSTRAINT fk_townhouse_property
        FOREIGN KEY (propertyID) REFERENCES PropertyTable (propertyID)
        ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE leaseTable (
    leaseID              INT NOT NULL AUTO_INCREMENT,
    tenantID             INT NOT NULL,
    propertyID           INT NOT NULL,
    start_date           DATE,
    end_date             DATE,
    rent_amount          DECIMAL(10, 2),
    security_deposit     DECIMAL(10, 2),
    due_date             DATE,
    grace_period         INT, -- days
    late_penalty_rate    DECIMAL(2, 2),
    is_active            BOOLEAN,
    PRIMARY KEY (leaseID),
    CONSTRAINT fk_lease_tenant
        FOREIGN KEY (tenantID)   REFERENCES TenantTable   (credential)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_lease_property
        FOREIGN KEY (propertyID) REFERENCES PropertyTable (propertyID)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE PaymentTable (
    receipt       INT NOT NULL,
    leaseID       INT NOT NULL,
    is_partial    BOOLEAN,
    amount        DECIMAL(10, 2),
    payment_date  DATE,
    status        VARCHAR(10), -- e.g. 'paid', 'late', 'partial'
    PRIMARY KEY (receipt),
    CONSTRAINT fk_payment_lease
        FOREIGN KEY (leaseID) REFERENCES leaseTable (leaseID)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_lease_tenant   ON leaseTable   (tenantID);
CREATE INDEX idx_lease_property ON leaseTable   (propertyID);
CREATE INDEX idx_payment_lease  ON PaymentTable (leaseID);
CREATE INDEX idx_property_type  ON PropertyTable (type);
