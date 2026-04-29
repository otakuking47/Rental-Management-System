CREATE database PropertyManagement;
use PropertyManagement;
create table admin(
	adminId int not null auto_increment,
	username varchar(20),
	passwordHash varchar(64),

	primary key (adminId)
);
CREATE TABLE LeaseTable (
    LeaseID         INT PRIMARY KEY AUTO_INCREMENT,
    startDate       DATE NOT NULL,
    endDate         DATE NOT NULL CHECK (endDate > startDate),
    rentAmount      DECIMAL(10,2) NOT NULL CHECK (rentAmount > 0),
    securityDeposit DECIMAL(10,2) NOT NULL CHECK (securityDeposit >= 0),
    dueDate         DATE NOT NULL,
    gracePeriod     INT NOT NULL DEFAULT 0 CHECK (gracePeriod >= 0),
    latePenaltyRate DECIMAL(5,2) NOT NULL CHECK (latePenaltyRate >= 0)
);
CREATE TABLE Apartment (
    unitNo          INT PRIMARY KEY,
    floorLevel      INT NOT NULL CHECK (floorLevel >= 1),
    elevator        BOOLEAN NOT NULL DEFAULT FALSE,
    backyard        BOOLEAN NOT NULL DEFAULT FALSE
);

