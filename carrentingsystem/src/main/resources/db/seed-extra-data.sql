-- ============================================================
-- Seed thêm dữ liệu test (chạy SAU CarRentingSystem_DB.sql)
-- Không DROP table. Chạy 1 lần trên DB đã có schema + seed gốc.
--
-- Login test (password đơn giản, BCrypt cost 10):
--   admin / admin
--   john, mary, david, anna, peter, lisa, tom, sophia / 123
-- ============================================================

USE [FUCarRentingSystem_DB];
GO

-- Hash dùng chung:
--   admin -> $2a$10$JM6magVvjjXHHRiIqcw1Q.YYZsDET6wttQ20oL3QRP52tUSHWJ1JC
--   123   -> $2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS

-- 1) Đồng bộ password seed gốc (đơn giản)
UPDATE dbo.Account
SET Password = '$2a$10$JM6magVvjjXHHRiIqcw1Q.YYZsDET6wttQ20oL3QRP52tUSHWJ1JC'
WHERE AccountName = N'admin';

UPDATE dbo.Account
SET Password = '$2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS'
WHERE AccountName IN (N'john', N'mary', N'david');
GO

-- 2) Account customer mới (bỏ qua nếu đã có email)
INSERT INTO dbo.Account (AccountName, Email, Password, Role)
SELECT v.AccountName, v.Email, v.Password, v.Role
FROM (VALUES
    (N'anna',   'anna@example.com',   '$2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS', N'Customer'),
    (N'peter',  'peter@example.com',  '$2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS', N'Customer'),
    (N'lisa',   'lisa@example.com',   '$2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS', N'Customer'),
    (N'tom',    'tom@example.com',    '$2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS', N'Customer'),
    (N'sophia', 'sophia@example.com', '$2a$10$dvEwtdRyGc4qyhR0gyL5zOlAqH7gYDfAfqMzC0tUUPQR.TzakDRBS', N'Customer')
) AS v(AccountName, Email, Password, Role)
WHERE NOT EXISTS (SELECT 1 FROM dbo.Account a WHERE a.Email = v.Email);
GO

-- 3) Customer gắn account mới
INSERT INTO dbo.Customer (FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate, AccountID)
SELECT v.FullName, v.Mobile, v.Birthday, v.IdentityCard, v.LicenceNumber, v.LicenceDate, a.AccountID
FROM (VALUES
    (N'anna',   N'Anna Pham',    '0934567890', '1997-04-12', '045678901', 'B2-100001', '2019-06-01'),
    (N'peter',  N'Peter Hoang',  '0945678901', '1993-11-08', '056789012', 'B2-100002', '2017-03-15'),
    (N'lisa',   N'Lisa Vo',      '0956789012', '1999-01-25', '067890123', 'B1-100003', '2021-09-20'),
    (N'tom',    N'Tom Nguyen',   '0967890123', '1991-08-30', '078901234', 'C-100004',  '2015-12-10'),
    (N'sophia', N'Sophia Dang',  '0978901234', '1996-06-18', '089012345', 'B2-100005', '2018-11-05')
) AS v(AccountName, FullName, Mobile, Birthday, IdentityCard, LicenceNumber, LicenceDate)
INNER JOIN dbo.Account a ON a.AccountName = v.AccountName
WHERE NOT EXISTS (SELECT 1 FROM dbo.Customer c WHERE c.AccountID = a.AccountID);
GO

-- 4) Hãng xe thêm
INSERT INTO dbo.CarProducer (ProducerName, Address, Country)
SELECT v.ProducerName, v.Address, v.Country
FROM (VALUES
    (N'Honda',  N'2-1-1 Minami-Aoyama, Tokyo',     N'Japan'),
    (N'Kia',    N'12 Heolleung-ro, Seoul',          N'South Korea'),
    (N'BMW',    N'Petuelring 130, Munich',          N'Germany')
) AS v(ProducerName, Address, Country)
WHERE NOT EXISTS (SELECT 1 FROM dbo.CarProducer p WHERE p.ProducerName = v.ProducerName);
GO

-- 5) Xe thêm (đủ Available / Rented / Inactive + phân trang)
INSERT INTO dbo.Car (CarName, CarModelYear, Color, Capacity, Description, ImportDate, ProducerID, RentPrice, Status)
SELECT v.CarName, v.CarModelYear, v.Color, v.Capacity, v.Description, v.ImportDate, p.ProducerID, v.RentPrice, v.Status
FROM (VALUES
    (N'Toyota',    N'Toyota Camry',      2023, N'White',  5, N'Mid-size sedan, comfortable',     '2023-02-10', 75,  N'Available'),
    (N'Toyota',    N'Toyota Fortuner',   2022, N'Black',  7, N'SUV 7 seats, diesel',             '2022-08-22', 95,  N'Available'),
    (N'Ford',      N'Ford Territory',    2023, N'Blue',   5, N'Compact SUV, modern cabin',       '2023-05-01', 80,  N'Available'),
    (N'Hyundai',   N'Hyundai Tucson',    2021, N'Silver', 5, N'Crossover, good for city',        '2021-07-14', 85,  N'Available'),
    (N'Mercedes',  N'Mercedes GLC300',   2023, N'White',  5, N'Luxury SUV',                      '2023-09-09', 180, N'Available'),
    (N'Honda',     N'Honda City',        2022, N'Red',    5, N'Compact sedan, CVT',              '2022-04-18', 48,  N'Available'),
    (N'Honda',     N'Honda CR-V',        2023, N'Grey',   5, N'Popular family SUV',              '2023-03-12', 88,  N'Available'),
    (N'Kia',       N'Kia Seltos',        2022, N'Orange', 5, N'Stylish crossover',               '2022-11-30', 65,  N'Available'),
    (N'Kia',       N'Kia Carnival',      2021, N'Black',  7, N'MPV 7 seats',                     '2021-12-05', 100, N'Rented'),
    (N'BMW',       N'BMW 320i',          2023, N'Blue',   5, N'Sport sedan',                     '2023-06-20', 160, N'Available'),
    (N'BMW',       N'BMW X3',            2022, N'White',  5, N'Premium compact SUV',             '2022-10-08', 170, N'Rented'),
    (N'Hyundai',   N'Hyundai i10',       2019, N'Yellow', 4, N'City car (inactive fleet)',       '2019-05-15', 30,  N'Inactive'),
    (N'Ford',      N'Ford Focus',        2018, N'Green',  5, N'Hatchback retired',               '2018-08-01', 40,  N'Inactive')
) AS v(ProducerName, CarName, CarModelYear, Color, Capacity, Description, ImportDate, RentPrice, Status)
INNER JOIN dbo.CarProducer p ON p.ProducerName = v.ProducerName
WHERE NOT EXISTS (SELECT 1 FROM dbo.Car c WHERE c.CarName = v.CarName);
GO

-- 6) Giao dịch thuê thêm (đủ status + ngày đa dạng)
-- CustomerID / CarID lấy theo tên để không phụ thuộc identity tuyệt đối
INSERT INTO dbo.CarRental (CustomerID, CarID, PickupDate, ReturnDate, RentPrice, Status)
SELECT c.CustomerID, car.CarID, v.PickupDate, v.ReturnDate, v.RentPrice, v.Status
FROM (VALUES
    -- Completed (doanh thu + review)
    (N'John Nguyen',  N'Toyota Camry',    '2024-10-01', '2024-10-04', 225,  N'Completed'),
    (N'Mary Tran',    N'Honda City',      '2024-11-05', '2024-11-07', 96,   N'Completed'),
    (N'David Le',     N'Honda CR-V',      '2024-12-10', '2024-12-15', 440,  N'Completed'),
    (N'Anna Pham',    N'Kia Seltos',      '2025-01-08', '2025-01-10', 130,  N'Completed'),
    (N'Peter Hoang',  N'BMW 320i',        '2025-02-01', '2025-02-03', 320,  N'Completed'),
    (N'Lisa Vo',      N'Toyota Fortuner', '2025-02-14', '2025-02-18', 380,  N'Completed'),
    (N'Tom Nguyen',   N'Ford Territory',  '2025-03-01', '2025-03-05', 320,  N'Completed'),
    (N'Sophia Dang',  N'Hyundai Tucson',  '2025-03-12', '2025-03-14', 170,  N'Completed'),
    (N'John Nguyen',  N'Mercedes GLC300', '2025-04-01', '2025-04-03', 360,  N'Completed'),
    (N'Mary Tran',    N'Toyota Innova',   '2025-04-20', '2025-04-22', 140,  N'Completed'),
    -- Renting
    (N'Anna Pham',    N'Kia Carnival',    '2026-06-01', '2026-06-08', 700,  N'Renting'),
    (N'Peter Hoang',  N'BMW X3',          '2026-06-10', '2026-06-15', 850,  N'Renting'),
    -- Pending
    (N'Lisa Vo',      N'Toyota Vios',     '2026-07-01', '2026-07-03', 100,  N'Pending'),
    (N'Tom Nguyen',   N'Ford Ranger',     '2026-07-05', '2026-07-08', 270,  N'Pending'),
    (N'Sophia Dang',  N'Honda City',      '2026-07-10', '2026-07-12', 96,   N'Pending'),
    -- Cancelled
    (N'David Le',     N'Hyundai Accent',  '2025-05-01', '2025-05-03', 90,   N'Cancelled'),
    (N'John Nguyen',  N'Toyota Camry',    '2025-05-10', '2025-05-12', 150,  N'Cancelled'),
    (N'Mary Tran',    N'Kia Seltos',      '2025-06-01', '2025-06-04', 195,  N'Cancelled')
) AS v(CustomerName, CarName, PickupDate, ReturnDate, RentPrice, Status)
INNER JOIN dbo.Customer c ON c.FullName = v.CustomerName
INNER JOIN dbo.Car car ON car.CarName = v.CarName
WHERE NOT EXISTS (
    SELECT 1 FROM dbo.CarRental r
    WHERE r.CustomerID = c.CustomerID
      AND r.CarID = car.CarID
      AND r.PickupDate = v.PickupDate
      AND r.ReturnDate = v.ReturnDate
);
GO

-- 7) Review cho các giao dịch Completed chưa có review
INSERT INTO dbo.Review (CarRenID, ReviewStar, Comment)
SELECT r.CarRenID, v.ReviewStar, v.Comment
FROM (VALUES
    (N'John Nguyen',  N'Toyota Camry',    '2024-10-01', 5, N'Xe êm, điều hòa mát, rất hài lòng.'),
    (N'Mary Tran',    N'Honda City',      '2024-11-05', 4, N'Nhỏ gọn dễ lái trong phố.'),
    (N'David Le',     N'Honda CR-V',      '2024-12-10', 5, N'Gia đình đi chơi rất thoải mái.'),
    (N'Anna Pham',    N'Kia Seltos',      '2025-01-08', 3, N'Ổn nhưng khoang hành lý hơi chật.'),
    (N'Peter Hoang',  N'BMW 320i',        '2025-02-01', 5, N'Lái đã, cảm giác thể thao.'),
    (N'Lisa Vo',      N'Toyota Fortuner', '2025-02-14', 4, N'Cao ráo, phù hợp đường dài.'),
    (N'Tom Nguyen',   N'Ford Territory',  '2025-03-01', 4, N'Nội thất mới, pin êm.'),
    (N'Sophia Dang',  N'Hyundai Tucson',  '2025-03-12', 5, N'Tốt, sẽ thuê lại.'),
    (N'John Nguyen',  N'Mercedes GLC300', '2025-04-01', 5, N'Đẳng cấp, dịch vụ xe sạch sẽ.'),
    (N'Mary Tran',    N'Toyota Innova',   '2025-04-20', 2, N'Xe hơi cũ, mùi nội thất chưa ổn.')
) AS v(CustomerName, CarName, PickupDate, ReviewStar, Comment)
INNER JOIN dbo.Customer c ON c.FullName = v.CustomerName
INNER JOIN dbo.Car car ON car.CarName = v.CarName
INNER JOIN dbo.CarRental r
    ON r.CustomerID = c.CustomerID
   AND r.CarID = car.CarID
   AND r.PickupDate = v.PickupDate
   AND r.Status = N'Completed'
WHERE NOT EXISTS (SELECT 1 FROM dbo.Review rv WHERE rv.CarRenID = r.CarRenID);
GO

PRINT N'seed-extra-data.sql completed.';
PRINT N'Login: admin/admin | customers (john,mary,david,anna,peter,lisa,tom,sophia)/123';
GO
