-- Sample data for web_bansach database

-- ============ DISABLE FOREIGN KEY CHECKS ============
SET FOREIGN_KEY_CHECKS = 0;

-- ============ TRUNCATE ALL TABLES (clears data and resets AUTO_INCREMENT) ============
TRUNCATE TABLE wishlists;
TRUNCATE TABLE payments;
TRUNCATE TABLE reviews;
TRUNCATE TABLE vouchers;
TRUNCATE TABLE order_items;
TRUNCATE TABLE orders;
TRUNCATE TABLE cart_items;
TRUNCATE TABLE carts;
TRUNCATE TABLE inventory;
TRUNCATE TABLE books;
TRUNCATE TABLE discounts;
TRUNCATE TABLE categories;
TRUNCATE TABLE authors;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;

-- ============ RE-ENABLE FOREIGN KEY CHECKS ============
SET FOREIGN_KEY_CHECKS = 1;

-- ============ ROLES ============
INSERT INTO roles (roles_name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (roles_name) VALUES ('ROLE_USER');

-- ============ USERS ============
INSERT INTO users (username, password, email, full_name, phone, address, is_active, created_at) VALUES 
('admin01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'admin@bookstore.com', 'Admin User', '0912345678', '123 Admin Street', 1, NOW()),
('user001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user001@example.com', 'Nguyen Van A', '0901111111', 'Ha Noi', 1, NOW()),
('user002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user002@example.com', 'Tran Thi B', '0902222222', 'Ho Chi Minh', 1, NOW()),
('user003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user003@example.com', 'Le Van C', '0903333333', 'Da Nang', 1, NOW()),
('user004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user004@example.com', 'Pham Thi D', '0904444444', 'Can Tho', 1, NOW()),
('user005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user005@example.com', 'Hoang Van E', '0905555555', 'Hai Phong', 1, NOW()),
('user006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user006@example.com', 'Vu Thi F', '0906666666', 'Vinh Phuc', 1, NOW()),
('user007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user007@example.com', 'Dang Van G', '0907777777', 'Thai Nguyen', 1, NOW()),
('user008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user008@example.com', 'Bui Thi H', '0908888888', 'Bac Ninh', 1, NOW()),
('user009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36DRjk3u', 'user009@example.com', 'Ngo Van I', '0909999999', 'Quang Ninh', 1, NOW());

-- ============ USER_ROLES ============
-- Admin user gets ADMIN role
INSERT INTO user_roles (user_id, roles_id) VALUES (1, 1);
-- Other users get USER role
INSERT INTO user_roles (user_id, roles_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (3, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (4, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (5, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (6, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (7, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (8, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (9, 2);
INSERT INTO user_roles (user_id, roles_id) VALUES (10, 2);

-- ============ AUTHORS ============
INSERT INTO authors (author_name, biography, created_at) VALUES 
('Nguyen Nhat Anh', 'Nha van va tac gia noi tieng cua Viet Nam, chuyen viet cac tieu thuyet dung cho thieu nhi', NOW()),
('Tran Huu Kham', 'Nha van, tao tac hang loat cac tac pham viet ve cuoc song hang ngay', NOW()),
('Dang Viet Hoang', 'Tac gia cac truyen tranh va truyen co tich Viet Nam', NOW()),
('Ngo Thi Nham', 'Nha van, tac gia kham pha tinh than va tam ly con nguoi', NOW()),
('Duong Thu Huong', 'Tac gia nhan sac va mien phuong cua dai van', NOW()),
('Hoang Cau Chi', 'Tac gia cua nhieu cuon tieu thuyet lang man', NOW()),
('Pham Van Thao', 'Nha van va nha phe binh van hoc', NOW()),
('Le Thi Phuong', 'Tac gia nhieu cuon sach tap tran cho thanh nien', NOW()),
('Tran Quoc Tuan', 'Nha van chuyen viet ve lich su Viet Nam', NOW()),
('Vu Trong Phung', 'Tac gia truyen thong va tieu thuyet xa hoi', NOW());

-- ============ CATEGORIES ============
INSERT INTO categories (category_name, description, is_active, created_at, updated_at, deleted_at) VALUES 
('Trinh Tham', 'Cac cuon tieu thuyet trinh tham va ky bi', 1, NOW(), NOW(), NULL),
('Tinh Yeu', 'Truyen yeu thuong va tinh cam', 1, NOW(), NOW(), NULL),
('Ly Lá»‹ch Su', 'Sach ve lich su, lich su van de chinh tri', 1, NOW(), NOW(), NULL),
('Khoa Hoc Vien Tuong', 'Sach khoa hoc giáº£ tÆ°á»Ÿng, tÆ°Æ¡ng lai', 1, NOW(), NOW(), NULL),
('Tieu Thuyet Hien Thuc', 'Tieu thuyet viet ve cuoc song hang ngay', 1, NOW(), NOW(), NULL),
('Tieu Thuyet Nhu Lam', 'Tieu thuyet humoristic va vui nhon', 1, NOW(), NOW(), NULL),
('Phat Trien Ca Nhan', 'Sach ve phat trien ban than va ky nang song', 1, NOW(), NOW(), NULL),
('Kinh Te Va Doanh Nghiep', 'Sach ve kinh te, kinh doanh va tai chinh', 1, NOW(), NOW(), NULL),
('Van Hoc Dien Tich', 'Cac tac pham van hoc kinh dien', 1, NOW(), NOW(), NULL),
('Sach Cho Tre Em', 'Cac cuon sach dac biet cho tre em', 1, NOW(), NOW(), NULL);

-- ============ DISCOUNTS ============
INSERT INTO discounts (name, discount_percent, start_date, end_date, is_active, created_at) VALUES 
('Summer Sale', 10, '2026-06-01', '2026-08-31', 1, NOW()),
('Black Friday', 25, '2026-11-01', '2026-11-30', 1, NOW()),
('New Year', 15, '2026-01-01', '2026-01-31', 1, NOW()),
('Valentine', 20, '2026-02-01', '2026-02-14', 1, NOW()),
('Easter Sale', 12, '2026-03-15', '2026-03-31', 1, NOW()),
('Tet Holiday', 30, '2026-01-15', '2026-02-05', 0, NOW()),
('Mid Year', 8, '2026-06-15', '2026-06-30', 1, NOW()),
('Back to School', 18, '2026-08-01', '2026-09-30', 1, NOW()),
('Anniversary', 22, '2026-05-01', '2026-05-31', 0, NOW()),
('Clearance', 40, '2026-12-01', '2026-12-31', 1, NOW());

-- ============ BOOKS ============
INSERT INTO books (title, publisher, publication_year, isbn, price, description, author_id, category_id, discount_id, created_at) VALUES 
('Tro Choi Hoang Vuong', 'NXB Tre', 2020, '978-0000001', 85000.00, 'Tieu thuyet trinh tham kich thich', 1, 1, 1, NOW()),
('Tinh Yeu Do', 'NXB Hoa Anh Dao', 2019, '978-0000002', 95000.00, 'Truyen yeu thuong cam dong', 2, 2, 2, NOW()),
('Lich Su Viet Nam', 'NXB Tri Thuc', 2021, '978-0000003', 125000.00, 'Lich su chi tiet cua Viet Nam', 9, 3, NULL, NOW()),
('Hanh Tinh Toi', 'NXB Sao Mai', 2022, '978-0000004', 110000.00, 'Tieu thuyet khoa hoc vien tuong', 4, 4, 3, NOW()),
('Cuoc Song Hang Ngay', 'NXB Phuong Nam', 2020, '978-0000005', 75000.00, 'Tieu thuyet hien thuc dep sac', 5, 5, 4, NOW()),
('Dieu Ngu Nhan Kiep', 'NXB Phuong Dong', 2019, '978-0000006', 65000.00, 'Tieu thuyet humoristic hay ca', 6, 6, 5, NOW()),
('Phat Trien Suc Manh', 'NXB Tre', 2021, '978-0000007', 105000.00, 'Sach phat trien ca nhan huu ich', 3, 7, NULL, NOW()),
('Kinh Te Hien Dai', 'NXB Tri Thuc', 2022, '978-0000008', 155000.00, 'Sach kinh te va doanh nghiep', 7, 8, 6, NOW()),
('Tac Pham Kinh Dien', 'NXB Van Hoa', 2018, '978-0000009', 145000.00, 'Van hoc dien tich nhat ban', 8, 9, 7, NOW()),
('Cho Thoi Nieu Truoc', 'NXB Nhi Dong', 2020, '978-0000010', 45000.00, 'Sach co tich cho tre em', 10, 10, 8, NOW());

-- ============ INVENTORY ============
INSERT INTO inventory (book_id, quantity, updated_at) VALUES 
(1, 50, NOW()),
(2, 35, NOW()),
(3, 20, NOW()),
(4, 15, NOW()),
(5, 60, NOW()),
(6, 40, NOW()),
(7, 25, NOW()),
(8, 10, NOW()),
(9, 30, NOW()),
(10, 100, NOW());

-- ============ CARTS ============
INSERT INTO carts (user_id, created_at, updated_at) VALUES 
(1, NOW(), NOW()),
(2, NOW(), NOW()),
(3, NOW(), NOW()),
(4, NOW(), NOW()),
(5, NOW(), NOW()),
(6, NOW(), NOW()),
(7, NOW(), NOW()),
(8, NOW(), NOW()),
(9, NOW(), NOW()),
(10, NOW(), NOW());

-- ============ CART_ITEMS ============
INSERT INTO cart_items (cart_id, book_id, quantity, price) VALUES 
(1, 1, 2, 85000.00),
(1, 3, 1, 125000.00),
(2, 2, 1, 95000.00),
(3, 4, 2, 110000.00),
(4, 5, 3, 75000.00),
(5, 6, 1, 65000.00),
(6, 7, 2, 105000.00),
(7, 8, 1, 155000.00),
(8, 9, 2, 145000.00),
(9, 10, 4, 45000.00);

-- ============ ORDERS ============
INSERT INTO orders (user_id, order_date, status, total_amount, receiver_name, receiver_phone, shipping_address, shipping_fee, shipping_method, tracking_code, updated_at) VALUES 
(2, NOW(), 'COMPLETED', 285000.00, 'Nguyen Van A', '0901111111', 'Ha Noi', 30000.00, 'Standard', 'VN001', NOW()),
(3, NOW(), 'SHIPPING', 95000.00, 'Tran Thi B', '0902222222', 'Ho Chi Minh', 25000.00, 'Express', 'VN002', NOW()),
(4, NOW(), 'CONFIRMED', 330000.00, 'Le Van C', '0903333333', 'Da Nang', 35000.00, 'Standard', 'VN003', NOW()),
(5, NOW(), 'PENDING', 225000.00, 'Pham Thi D', '0904444444', 'Can Tho', 30000.00, 'Standard', 'VN004', NOW()),
(6, NOW(), 'COMPLETED', 105000.00, 'Hoang Van E', '0905555555', 'Hai Phong', 25000.00, 'Express', 'VN005', NOW()),
(7, NOW(), 'COMPLETED', 155000.00, 'Vu Thi F', '0906666666', 'Vinh Phuc', 20000.00, 'Standard', 'VN006', NOW()),
(8, NOW(), 'SHIPPING', 285000.00, 'Dang Van G', '0907777777', 'Thai Nguyen', 30000.00, 'Standard', 'VN007', NOW()),
(9, NOW(), 'COMPLETED', 155000.00, 'Bui Thi H', '0908888888', 'Bac Ninh', 25000.00, 'Express', 'VN008', NOW()),
(10, NOW(), 'PENDING', 290000.00, 'Ngo Van I', '0909999999', 'Quang Ninh', 35000.00, 'Standard', 'VN009', NOW()),
(2, DATE_ADD(NOW(), INTERVAL -7 DAY), 'COMPLETED', 180000.00, 'Nguyen Van A', '0901111111', 'Ha Noi', 20000.00, 'Standard', 'VN010', NOW());

-- ============ ORDER_ITEMS ============
INSERT INTO order_items (order_id, book_id, quantity, price) VALUES 
(1, 1, 2, 85000.00),
(1, 3, 1, 125000.00),
(2, 2, 1, 95000.00),
(3, 4, 2, 110000.00),
(3, 5, 1, 75000.00),
(4, 5, 3, 75000.00),
(5, 6, 1, 65000.00),
(6, 7, 1, 105000.00),
(7, 8, 1, 155000.00),
(8, 9, 2, 145000.00);

-- ============ PAYMENTS ============
INSERT INTO payments (order_id, amount, payment_method, status, transaction_id, paid_at, created_at, updated_at, callback_verified) VALUES 
(1, 345000.00, 'SEPAY', 'SUCCESS', 'SEP-001', NOW(), NOW(), NOW(), 1),
(2, 120000.00, 'SEPAY', 'SUCCESS', 'SEP-002', NOW(), NOW(), NOW(), 1),
(3, 365000.00, 'SEPAY', 'SUCCESS', 'SEP-003', NOW(), NOW(), NOW(), 1),
(4, 255000.00, 'SEPAY', 'PENDING', 'SEP-004', NULL, NOW(), NOW(), 0),
(5, 130000.00, 'SEPAY', 'SUCCESS', 'SEP-005', NOW(), NOW(), NOW(), 1),
(6, 180000.00, 'SEPAY', 'SUCCESS', 'SEP-006', NOW(), NOW(), NOW(), 1),
(7, 315000.00, 'SEPAY', 'SUCCESS', 'SEP-007', NOW(), NOW(), NOW(), 1),
(8, 180000.00, 'SEPAY', 'SUCCESS', 'SEP-008', NOW(), NOW(), NOW(), 1),
(9, 325000.00, 'SEPAY', 'PENDING', 'SEP-009', NULL, NOW(), NOW(), 0),
(10, 200000.00, 'SEPAY', 'SUCCESS', 'SEP-010', NOW(), NOW(), NOW(), 1);

-- ============ REVIEWS ============
INSERT INTO reviews (user_id, book_id, rating, comment, created_at) VALUES 
(2, 1, 5, 'Sach hay va kho theo, ko the dut giua', NOW()),
(3, 2, 4, 'Truyen tinh yeu xuc dong, dang yeu', NOW()),
(4, 3, 5, 'Lich su chi tiet va dung xac', NOW()),
(5, 4, 3, 'Khoa hoc vien tuong sai thuc te', NOW()),
(6, 5, 4, 'Cuoc song hang ngay vua de hieu', NOW()),
(7, 6, 5, 'Sach vui nhon va mua cu tro', NOW()),
(8, 7, 4, 'Phat trien ca nhan hay co ich', NOW()),
(9, 8, 5, 'Kinh te hien dai va chi tiet', NOW()),
(10, 9, 4, 'Tac pham kinh dien nen doc lan', NOW()),
(2, 10, 5, 'Sach cho tre em rat dep va huai', NOW());

-- ============ VOUCHERS ============
INSERT INTO vouchers (code, discount_percent, max_discount, quantity, expired_at, created_at) VALUES 
('WELCOME10', 10, 100000.00, 50, '2026-12-31', NOW()),
('SUMMER20', 20, 200000.00, 30, '2026-08-31', NOW()),
('SAVE15', 15, 150000.00, 40, '2026-06-30', NOW()),
('SPECIAL25', 25, 250000.00, 20, '2026-05-31', NOW()),
('LUCKY5', 5, 50000.00, 100, '2026-12-31', NOW()),
('AUTUMN18', 18, 180000.00, 25, '2026-09-30', NOW()),
('WINTER30', 30, 300000.00, 15, '2026-02-28', NOW()),
('FLASH12', 12, 120000.00, 60, '2026-04-30', NOW()),
('MEMBER22', 22, 220000.00, 35, '2026-07-31', NOW()),
('EXTRA8', 8, 80000.00, 75, '2026-06-15', NOW());

-- ============ WISHLISTS ============
INSERT INTO wishlists (user_id, book_id, created_at) VALUES 
(2, 3, NOW()),
(2, 5, NOW()),
(3, 1, NOW()),
(3, 7, NOW()),
(4, 2, NOW()),
(4, 8, NOW()),
(5, 4, NOW()),
(5, 9, NOW()),
(6, 6, NOW()),
(6, 10, NOW());
