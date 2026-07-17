/* ============================================================================
   FU Car Renting Management System  -  Database creation + seed script (v2.0)
   Target  : Microsoft SQL Server
    Database: FUFUCarRentingSystem_DB
   Login   : test / test   (see section 0)

   Schema follows Assignment 01 v2.0 - section "2. Database Design":
     Account     (AccountID, AccountName, Email, Password, Role)
     CarProducer (ProducerID, ProducerName, Address, Country)
     Car         (CarID, CarName, CarModelYear, Color, Capacity, Description,
                  ImportDate, ProducerID, RentPrice, Status)
     Customer    (CustomerID, FullName, Mobile, Birthday, IdentityCard,
                  LicenceNumber, LicenceDate, AccountID)
     CarRental   (CarRentID, CustomerID, CarID, PickupDate, ReturnDate,
                  RentPrice, Status)
     Review      (ID, CarRentID, ReviewStar, Comment)

   Type rules (from Lưu ý.txt):
     - All identity keys are BIGINT  (Java Long), IDENTITY(1,1).
     - "name" fields are NVARCHAR(255); AccountName is VARCHAR(255).
     - Date fields are DATE (Java LocalDate).
     - PickupDate / ReturnDate are DATETIME2 (Java LocalDateTime).
     - All fields are required (NOT NULL).
     - CarRental enforces PickupDate < ReturnDate.
============================================================================ */

----------------------------------------------------------------------------
-- 0. Create database + login/user  (run once, in the master context)
----------------------------------------------------------------------------
IF DB_ID(N'FUCarRentingSystem_DB') IS NULL
BEGIN
    CREATE DATABASE [FUCarRentingSystem_DB];
END
GO

-- SQL login "test" with password "test".
-- CHECK_POLICY = OFF lets the weak password through on dev machines.
IF SUSER_ID(N'test') IS NULL
BEGIN
    CREATE LOGIN [test] WITH PASSWORD = N'test',
        CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF,
        DEFAULT_DATABASE = [FUCarRentingSystem_DB];
END
GO

USE [FUCarRentingSystem_DB];
GO

-- Map the login to a db user and give it full rights on this database.
IF USER_ID(N'test') IS NULL
BEGIN
    CREATE USER [test] FOR LOGIN [test];
    ALTER ROLE db_owner ADD MEMBER [test];
END
GO

----------------------------------------------------------------------------
-- 1. Drop existing tables (child tables first, for a clean re-run)
----------------------------------------------------------------------------
IF OBJECT_ID(N'dbo.Review',      N'U') IS NOT NULL DROP TABLE dbo.Review;
IF OBJECT_ID(N'dbo.CarRental',   N'U') IS NOT NULL DROP TABLE dbo.CarRental;
IF OBJECT_ID(N'dbo.Car',         N'U') IS NOT NULL DROP TABLE dbo.Car;
IF OBJECT_ID(N'dbo.Customer',    N'U') IS NOT NULL DROP TABLE dbo.Customer;
IF OBJECT_ID(N'dbo.CarProducer', N'U') IS NOT NULL DROP TABLE dbo.CarProducer;
IF OBJECT_ID(N'dbo.Account',     N'U') IS NOT NULL DROP TABLE dbo.Account;
GO

----------------------------------------------------------------------------
-- 2. Account  (AccountID, AccountName, Email, Password, Role)
----------------------------------------------------------------------------
CREATE TABLE dbo.Account (
    AccountID    BIGINT        IDENTITY(1,1) NOT NULL,
    AccountName  VARCHAR(255)                NOT NULL,
    Email        VARCHAR(255)                NOT NULL,
    Password     VARCHAR(255)                NOT NULL,
    Role         NVARCHAR(20)                NOT NULL,   -- 'Admin' / 'Customer'
    CONSTRAINT PK_Account PRIMARY KEY (AccountID),
    CONSTRAINT UQ_Account_Email UNIQUE (Email),
    CONSTRAINT CK_Account_Role  CHECK (Role IN (N'Admin', N'Customer'))
);
GO

----------------------------------------------------------------------------
-- 3. CarProducer  (ProducerID, ProducerName, Address, Country)
----------------------------------------------------------------------------
CREATE TABLE dbo.CarProducer (
    ProducerID    BIGINT        IDENTITY(1,1) NOT NULL,
    ProducerName  NVARCHAR(255)               NOT NULL,
    Address       NVARCHAR(255)               NOT NULL,
    Country       NVARCHAR(255)               NOT NULL,
    CONSTRAINT PK_CarProducer PRIMARY KEY (ProducerID)
);
GO

----------------------------------------------------------------------------
-- 4. Car  (CarID, CarName, CarModelYear, Color, Capacity, Description,
--          ImportDate, ProducerID, RentPrice, Status)
----------------------------------------------------------------------------
CREATE TABLE dbo.Car (
    CarID         BIGINT         IDENTITY(1,1) NOT NULL,
    CarName       NVARCHAR(255)                NOT NULL,
    CarModelYear  INT                          NOT NULL,
    Color         NVARCHAR(50)                 NOT NULL,
    Capacity      INT                          NOT NULL,
    Description   NVARCHAR(1000)               NOT NULL,
    ImportDate    DATE                         NOT NULL,
    ProducerID    BIGINT                       NOT NULL,
    RentPrice     DECIMAL(18,2)                NOT NULL,
    Status        NVARCHAR(20)                 NOT NULL,  -- 'Available' / 'Rented' / 'Inactive'
    CONSTRAINT PK_Car PRIMARY KEY (CarID),
    CONSTRAINT FK_Car_CarProducer FOREIGN KEY (ProducerID)
        REFERENCES dbo.CarProducer (ProducerID),
    CONSTRAINT CK_Car_Capacity  CHECK (Capacity > 0),
    CONSTRAINT CK_Car_RentPrice CHECK (RentPrice >= 0)
);
GO

----------------------------------------------------------------------------
-- 5. Customer  (CustomerID, FullName, Mobile, Birthday, IdentityCard,
--               LicenceNumber, LicenceDate, AccountID)
----------------------------------------------------------------------------
CREATE TABLE dbo.Customer (
    CustomerID     BIGINT        IDENTITY(1,1) NOT NULL,
    FullName       NVARCHAR(255)               NOT NULL,
    Mobile         VARCHAR(20)                 NOT NULL,
    Birthday       DATE                        NOT NULL,
    IdentityCard   VARCHAR(20)                 NOT NULL,
    LicenceNumber  VARCHAR(20)                 NOT NULL,
    LicenceDate    DATE                        NOT NULL,
    AccountID      BIGINT                      NOT NULL,
    CONSTRAINT PK_Customer PRIMARY KEY (CustomerID),
    CONSTRAINT FK_Customer_Account FOREIGN KEY (AccountID)
        REFERENCES dbo.Account (AccountID),
    CONSTRAINT UQ_Customer_Account UNIQUE (AccountID)   -- one profile per account
);
GO

----------------------------------------------------------------------------
-- 6. CarRental  (CarRentID, CustomerID, CarID, PickupDate, ReturnDate,
--                RentPrice, Status)
----------------------------------------------------------------------------
CREATE TABLE dbo.CarRental (
    CarRentID   BIGINT        IDENTITY(1,1) NOT NULL,
    CustomerID  BIGINT                      NOT NULL,
    CarID       BIGINT                      NOT NULL,
    PickupDate  DATETIME2                   NOT NULL,
    ReturnDate  DATETIME2                   NOT NULL,
    RentPrice   DECIMAL(18,2)               NOT NULL,
    Status      NVARCHAR(20)                NOT NULL,  -- 'Pending' / 'Renting' / 'Completed' / 'Cancelled'
    CONSTRAINT PK_CarRental PRIMARY KEY (CarRentID),
    CONSTRAINT FK_CarRental_Customer FOREIGN KEY (CustomerID)
        REFERENCES dbo.Customer (CustomerID),
    CONSTRAINT FK_CarRental_Car FOREIGN KEY (CarID)
        REFERENCES dbo.Car (CarID),
    CONSTRAINT CK_CarRental_Dates     CHECK (PickupDate < ReturnDate),
    CONSTRAINT CK_CarRental_RentPrice CHECK (RentPrice >= 0)
);
GO

----------------------------------------------------------------------------
-- 7. Review  (ID, CarRentID, ReviewStar, Comment)
----------------------------------------------------------------------------
CREATE TABLE dbo.Review (
    ID          BIGINT        IDENTITY(1,1) NOT NULL,
    CarRentID   BIGINT                      NOT NULL,
    ReviewStar  INT                         NOT NULL,
    Comment     NVARCHAR(500)               NOT NULL,
    CONSTRAINT PK_Review PRIMARY KEY (ID),
    CONSTRAINT FK_Review_CarRental FOREIGN KEY (CarRentID)
        REFERENCES dbo.CarRental (CarRentID),
    CONSTRAINT UQ_Review_CarRental UNIQUE (CarRentID),   -- one review per rental
    CONSTRAINT CK_Review_Star CHECK (ReviewStar BETWEEN 1 AND 5)
);
GO

----------------------------------------------------------------------------
-- 8. Sample seed data
----------------------------------------------------------------------------
-- 8.1 Accounts  (1 admin + 3 customers).  Plain-text passwords for dev only.
INSERT INTO dbo.Account (AccountName, Email, Password, Role) VALUES
    ('admin',  'admin@fucar.vn',  '$2a$10$ADlZpyC9pD1agBnGcZN94eQXuQAJrIAB02Ej6wmllrmtoKPkCR4oa', N'Admin'),
    ('john',   'john@example.com','$2a$10$PDIFiCXYtF5VTlVkiDq9f.GziURR2aH74Cm4sKCOnOo66sTclcIg2',  N'Customer'),
    ('mary',   'mary@example.com','$2a$10$c4FMI.8tMUBdAhnngwVcUuNYJHaNv784kGdqyJSJP55CKpWDh.85W',  N'Customer'),
    ('david',  'david@example.com','$2a$10$1W.BlCn0lOSO.2Aokvl69OFV0rPG97rNqW.j6k06uPzUeLAdUIgRK',N'Customer');
GO

-- 8.2 Car producers
INSERT INTO dbo.CarProducer (ProducerName, Address, Country) VALUES
    (N'Toyota',    N'1 Toyota-cho, Toyota City',        N'Japan'),
    (N'Ford',      N'1 American Road, Dearborn, MI',    N'USA'),
    (N'Hyundai',   N'12 Heolleung-ro, Seocho-gu, Seoul',N'South Korea'),
    (N'Mercedes',  N'1 Mercedesstrasse, Stuttgart',     N'Germany');
GO

-- 8.3 Cars
INSERT INTO dbo.Car
    (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
VALUES
    (N'Toyota Vios',     2022, N'White', 5, N'Compact sedan, automatic',        '2022-06-15', 1, 50.00,  N'Available'),
    (N'Toyota Innova',   2021, N'Silver',7, N'7-seat MPV, family friendly',     '2021-09-10', 1, 70.00,  N'Available'),
    (N'Ford Ranger',     2023, N'Black', 5, N'Pickup truck, 4x4',              '2023-01-20', 2, 90.00,  N'Available'),
    (N'Hyundai Accent',  2022, N'Red',   5, N'Economy sedan, fuel efficient',   '2022-03-05', 3, 45.00,  N'Available'),
    (N'Mercedes C200',   2023, N'Black', 5, N'Luxury sedan, leather interior',  '2023-04-18', 4, 150.00, N'Available'),
    (N'Ford Everest',    2020, N'Grey',  7, N'7-seat SUV (kept for history)',   '2020-11-30', 2, 110.00, N'Inactive');
GO

-- 8.4 Customers  (linked to the 3 customer accounts: IDs 2,3,4)
INSERT INTO dbo.Customer
    (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
VALUES
    (N'John Nguyen',  '0901234567', '1995-03-10', '012345678', 'B2-998877', '2018-05-01', 2),
    (N'Mary Tran',    '0912345678', '1998-07-22', '023456789', 'B1-554433', '2020-08-15', 3),
    (N'David Le',     '0987654321', '1990-12-01', '034567890', 'C-112233',  '2016-02-20', 4);
GO

-- 8.5 Car rentals  (LocalDateTime values: include the time-of-day)
INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status) VALUES
    (1, 1, '2024-07-01 08:00:00', '2024-07-05 08:00:00', 200.00, N'Completed'),
    (1, 3, '2024-08-10 09:30:00', '2024-08-12 18:00:00', 180.00, N'Completed'),
    (2, 4, '2024-09-15 07:00:00', '2024-09-18 07:00:00', 135.00, N'Completed'),
    (3, 5, '2025-06-20 10:00:00', '2025-06-25 10:00:00', 750.00, N'Renting'),
    (2, 2, '2025-07-01 08:00:00', '2025-07-03 08:00:00', 140.00, N'Pending');
GO

-- 8.6 Reviews  (one per completed rental)
INSERT INTO dbo.Review (CarRentID, ReviewStar, Comment) VALUES
    (1, 5, N'Great car, very clean and fuel efficient.'),
    (2, 4, N'Powerful truck, a bit thirsty on fuel.'),
    (3, 5, N'Smooth ride and easy to drive in the city.');
GO

PRINT N'FUCarRentingSystem_DB created and seeded successfully.';
GO
