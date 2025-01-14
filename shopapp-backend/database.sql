CREATE DATABASE shopapp;
USE shopapp;

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    date_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0
);

ALTER TABLE users
ADD COLUMN role_id INT

CREATE TABLE roles(
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

ALTER TABLE users
ADD FOREIGN KEY (role_id) REFERENCES roles(id)

CREATE TABLE tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

--login with facebook and google
CREATE TABLE social_accounts(
    id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
    name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

--category table--
CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL DEFAULT '' COMMENT 'Tên danh mục, vd: đồ điện tử'
);

--product table--
CREATE TABLE products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(350) COMMENT 'Tên sản phẩm',
    price FLOAT NOT NULL CHECK (price >= 0),
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

ALTER TABLE products AUTO_INCREMENT = 1;

CREATE TABLE product_images (
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_product_images_product_id
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR(300)
);

--Order--
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phonr_number VARCHAR(200) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    total_money FLOAT CHECK (total_money >= 0),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE orders ADD COLUMN `shipping_method` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `shipping_address` VARCHAR(200);
ALTER TABLE orders ADD COLUMN `shipping_date` DATE;
ALTER TABLE orders ADD COLUMN `shipping_number` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `payment_method` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `payment_date` DATE;

--Xóa 1 đơn hàng => xóa mềm
ALTER TABLE orders ADD COLUMN active TINYINT(1);

--Trạng thái đơn hàng chỉ được nhận một giá trị cụ thể
ALTER TABLE orders
MODIFY COLUMN status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled')
COMMENT 'trạng thái đơn hàng';

--Order detail--
CREATE TABLE order_detail(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id),
    price FLOAT CHECK (price >=0),
    number_of_products int CHECK (number_of_products > 0),
    total_money FLOAT CHECK (total_money >= 0),
    color VARCHAR(20) DEFAULT ''
);

CREATE TABLE comments(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    user_id INT,
    content VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
	FOREIGN KEY (product_id) REFERENCES products(id),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS coupons (
	id INT PRIMARY KEY AUTO_INCREMENT,
	code VARCHAR(50) NOT NULL,
	active BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE orders
ADD COLUMN coupon_id INT,
ADD CONSTRAINT fk_orders_coupon
FOREIGN KEY (coupon_id) REFERENCES coupons(id);

ALTER TABLE order_detail
ADD COLUMN coupon_id INT,
ADD CONSTRAINT fk_order_detail_coupon
FOREIGN KEY (coupon_id) REFERENCES coupons(id);

CREATE TABLE IF NOT EXISTS coupon_conditions (
	id INT PRIMARY KEY AUTO_INCREMENT,
	coupon_id INT NOT NULL,
	attribute VARCHAR(255) NOT NULL,
	operator VARCHAR(10) NOT NULL,
	value VARCHAR(255) NOT NULL,
	discount_amount DECIMAL(5, 2) NOT NULL,
	FOREIGN KEY (coupon_id) REFERENCES coupons(id)
);

INSERT INTO coupons(id, code) VALUES (1, 'ABC123'),
INSERT INTO coupons(id, code) VALUES (2, 'ANHNAM20');
	
INSERT INTO coupon_conditions (id, coupon_id, attribute, operator, value, discount_amount)
VALUES (1, 1, 'minumum_amount', '>', '100', 10);
INSERT INTO coupon_conditions (id, coupon_id, attribute, operator, value, discount_amount)
VALUES (2, 1, 'applicable_date', 'BETWEEN', '2025-01-09', 5);
INSERT INTO coupon_conditions (id, coupon_id, attribute, operator, value, discount_amount)
VALUES (3, 2, 'minumum_amount', '>', '200', 20);