IF DB_ID(N'FUCarRentingSystem_DB') IS NULL
BEGIN
    CREATE DATABASE [FUCarRentingSystem_DB];
END
GO

IF SUSER_ID(N'test') IS NULL
BEGIN
    CREATE LOGIN [test] WITH PASSWORD = N'test',
        CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF,
        DEFAULT_DATABASE = [FUCarRentingSystem_DB];
END
GO

USE [FUCarRentingSystem_DB];
GO

IF USER_ID(N'test') IS NULL
BEGIN
    CREATE USER [test] FOR LOGIN [test];
    ALTER ROLE db_owner ADD MEMBER [test];
END
GO

IF OBJECT_ID(N'dbo.Review',      N'U') IS NOT NULL DROP TABLE dbo.Review;
IF OBJECT_ID(N'dbo.CarRental',   N'U') IS NOT NULL DROP TABLE dbo.CarRental;
IF OBJECT_ID(N'dbo.Car',         N'U') IS NOT NULL DROP TABLE dbo.Car;
IF OBJECT_ID(N'dbo.Customer',    N'U') IS NOT NULL DROP TABLE dbo.Customer;
IF OBJECT_ID(N'dbo.CarProducer', N'U') IS NOT NULL DROP TABLE dbo.CarProducer;
IF OBJECT_ID(N'dbo.Account',     N'U') IS NOT NULL DROP TABLE dbo.Account;
GO

CREATE TABLE dbo.Account (
    AccountID    INT            IDENTITY(1,1) NOT NULL,
    AccountName  NVARCHAR(100)                NOT NULL,
    Email        VARCHAR(200)                 NOT NULL,
    Password     VARCHAR(200)                 NOT NULL,
    Role         NVARCHAR(10)                 NOT NULL,
    CONSTRAINT PK_Account PRIMARY KEY (AccountID),
    CONSTRAINT UQ_Account_Email UNIQUE (Email),
    CONSTRAINT CK_Account_Role  CHECK (Role IN (N'Admin', N'Customer'))
);
GO

CREATE TABLE dbo.CarProducer (
    ProducerID    INT            IDENTITY(1,1) NOT NULL,
    ProducerName  NVARCHAR(100)                NOT NULL,
    Address       NVARCHAR(200)                NOT NULL,
    Country       NVARCHAR(100)                NOT NULL,
    CONSTRAINT PK_CarProducer PRIMARY KEY (ProducerID)
);
GO

CREATE TABLE dbo.Car (
    CarID         INT             IDENTITY(1,1) NOT NULL,
    CarName       NVARCHAR(200)                 NOT NULL,
    CarModelYear  INT                           NOT NULL,
    Color         NVARCHAR(50)                  NOT NULL,
    Capacity      INT                           NOT NULL,
    Description   NVARCHAR(1000)                NOT NULL,
    ImportDate    DATE                          NOT NULL,
    ProducerID    INT                           NOT NULL,
    RentPrice     DECIMAL(10,0)                 NOT NULL,
    Status        NVARCHAR(10)                  NOT NULL,
    CONSTRAINT PK_Car PRIMARY KEY (CarID),
    CONSTRAINT FK_Car_CarProducer FOREIGN KEY (ProducerID)
        REFERENCES dbo.CarProducer (ProducerID),
    CONSTRAINT CK_Car_Capacity  CHECK (Capacity > 0),
    CONSTRAINT CK_Car_RentPrice CHECK (RentPrice >= 0)
);
GO

CREATE TABLE dbo.Customer (
    CustomerID     INT            IDENTITY(1,1) NOT NULL,
    FullName       NVARCHAR(200)                NOT NULL,
    Mobile         VARCHAR(15)                  NOT NULL,
    Birthday       DATE                         NOT NULL,
    IdentityCard   VARCHAR(20)                  NOT NULL,
    LicenceNumber  VARCHAR(20)                  NOT NULL,
    LicenceDate    DATE                         NOT NULL,
    AccountID      INT                          NOT NULL,
    CONSTRAINT PK_Customer PRIMARY KEY (CustomerID),
    CONSTRAINT FK_Customer_Account FOREIGN KEY (AccountID)
        REFERENCES dbo.Account (AccountID),
    CONSTRAINT UQ_Customer_Account UNIQUE (AccountID)
);
GO

CREATE TABLE dbo.CarRental (
    CarRenID    INT             IDENTITY(1,1) NOT NULL,
    CustomerID  INT                           NOT NULL,
    CarID       INT                           NOT NULL,
    PickupDate  DATE                          NOT NULL,
    ReturnDate  DATE                          NOT NULL,
    RentPrice   DECIMAL(10,0)                 NOT NULL,
    Status      NVARCHAR(10)                  NOT NULL,
    CONSTRAINT PK_CarRental PRIMARY KEY (CarRenID),
    CONSTRAINT FK_CarRental_Customer FOREIGN KEY (CustomerID)
        REFERENCES dbo.Customer (CustomerID),
    CONSTRAINT FK_CarRental_Car FOREIGN KEY (CarID)
        REFERENCES dbo.Car (CarID),
    CONSTRAINT CK_CarRental_Dates     CHECK (PickupDate < ReturnDate),
    CONSTRAINT CK_CarRental_RentPrice CHECK (RentPrice >= 0)
);
GO

CREATE TABLE dbo.Review (
    ID          INT             IDENTITY(1,1) NOT NULL,
    CarRenID    INT                           NOT NULL,
    ReviewStar  INT                           NOT NULL,
    Comment     NVARCHAR(500)                 NOT NULL,
    CONSTRAINT PK_Review PRIMARY KEY (ID),
    CONSTRAINT FK_Review_CarRental FOREIGN KEY (CarRenID)
        REFERENCES dbo.CarRental (CarRenID),
    CONSTRAINT UQ_Review_CarRental UNIQUE (CarRenID),
    CONSTRAINT CK_Review_Star CHECK (ReviewStar BETWEEN 1 AND 5)
);
GO

INSERT INTO dbo.Account (AccountName, Email, Password, Role) VALUES
    ('admin',  'admin@fucar.vn',  '$2a$10$ADlZpyC9pD1agBnGcZN94eQXuQAJrIAB02Ej6wmllrmtoKPkCR4oa', N'Admin'),
    ('john',   'john@example.com','$2a$10$PDIFiCXYtF5VTlVkiDq9f.GziURR2aH74Cm4sKCOnOo66sTclcIg2',  N'Customer'),
    ('mary',   'mary@example.com','$2a$10$c4FMI.8tMUBdAhnngwVcUuNYJHaNv784kGdqyJSJP55CKpWDh.85W',  N'Customer'),
    ('david',  'david@example.com','$2a$10$1W.BlCn0lOSO.2Aokvl69OFV0rPG97rNqW.j6k06uPzUeLAdUIgRK',N'Customer');
GO

INSERT INTO dbo.CarProducer (ProducerName, Address, Country) VALUES
    (N'Toyota',    N'1 Toyota-cho, Toyota City',        N'Japan'),
    (N'Ford',      N'1 American Road, Dearborn, MI',    N'USA'),
    (N'Hyundai',   N'12 Heolleung-ro, Seocho-gu, Seoul',N'South Korea'),
    (N'Mercedes',  N'1 Mercedesstrasse, Stuttgart',     N'Germany');
GO

INSERT INTO dbo.Car
    (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
VALUES
    (N'Toyota Vios',     2022, N'White', 5, N'Compact sedan, automatic',        '2022-06-15', 1, 50,  N'Available'),
    (N'Toyota Innova',   2021, N'Silver',7, N'7-seat MPV, family friendly',     '2021-09-10', 1, 70,  N'Available'),
    (N'Ford Ranger',     2023, N'Black', 5, N'Pickup truck, 4x4',              '2023-01-20', 2, 90,  N'Available'),
    (N'Hyundai Accent',  2022, N'Red',   5, N'Economy sedan, fuel efficient',   '2022-03-05', 3, 45,  N'Available'),
    (N'Mercedes C200',   2023, N'Black', 5, N'Luxury sedan, leather interior',  '2023-04-18', 4, 150, N'Rented'),
    (N'Ford Everest',    2020, N'Grey',  7, N'7-seat SUV (kept for history)',   '2020-11-30', 2, 110, N'Inactive');
GO

INSERT INTO dbo.Customer
    (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
VALUES
    (N'John Nguyen',  '0901234567', '1995-03-10', '012345678', 'B2-998877', '2018-05-01', 2),
    (N'Mary Tran',    '0912345678', '1998-07-22', '023456789', 'B1-554433', '2020-08-15', 3),
    (N'David Le',     '0987654321', '1990-12-01', '034567890', 'C-112233',  '2016-02-20', 4);
GO

INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status) VALUES
    (1, 1, '2024-07-01', '2024-07-05', 200, N'Completed'),
    (1, 3, '2024-08-10', '2024-08-12', 180, N'Completed'),
    (2, 4, '2024-09-15', '2024-09-18', 135, N'Completed'),
    (3, 5, '2025-06-20', '2025-06-25', 750, N'Renting'),
    (2, 2, '2025-07-01', '2025-07-03', 140, N'Pending');
GO

INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment) VALUES
    (1, 5, N'Great car, very clean and fuel efficient.'),
    (2, 4, N'Powerful truck, a bit thirsty on fuel.'),
    (3, 5, N'Smooth ride and easy to drive in the city.');
GO

PRINT N'FUCarRentingSystem_DB created and seeded successfully.';
GO
