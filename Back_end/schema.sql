-- Schema generated from JPA entities in com.example.web_bansach.model
-- MySQL 8+

CREATE DATABASE IF NOT EXISTS web_bansach
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE web_bansach;

CREATE TABLE IF NOT EXISTS roles (
  roles_id BIGINT NOT NULL AUTO_INCREMENT,
  roles_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (roles_id),
  UNIQUE KEY uk_roles_name (roles_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  full_name VARCHAR(255),
  phone VARCHAR(255),
  address VARCHAR(255),
  is_active TINYINT(1) DEFAULT 1,
  created_at DATETIME,
  deleted_at DATETIME,
  PRIMARY KEY (user_id),
  UNIQUE KEY uk_users_username (username),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  roles_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, roles_id),
  KEY idx_user_roles_roles_id (roles_id),
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_user_roles_role
    FOREIGN KEY (roles_id) REFERENCES roles (roles_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS authors (
  author_id BIGINT NOT NULL AUTO_INCREMENT,
  author_name VARCHAR(255),
  biography TEXT,
  created_at DATETIME,
  PRIMARY KEY (author_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categories (
  category_id BIGINT NOT NULL AUTO_INCREMENT,
  category_name VARCHAR(255) NOT NULL,
  description TEXT,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL,
  updated_at DATETIME,
  deleted_at DATETIME,
  PRIMARY KEY (category_id),
  UNIQUE KEY uk_categories_category_name (category_name)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS discounts (
  discount_id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  discount_percent INT,
  start_date DATETIME,
  end_date DATETIME,
  is_active TINYINT(1) DEFAULT 1,
  created_at DATETIME,
  PRIMARY KEY (discount_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS books (
  book_id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255),
  publisher VARCHAR(255),
  publication_year INT,
  isbn VARCHAR(255),
  price DECIMAL(19, 2),
  description TEXT,
  cover_image VARCHAR(255),
  created_at DATETIME,
  deleted_at DATETIME,
  author_id BIGINT,
  category_id BIGINT,
  discount_id BIGINT,
  PRIMARY KEY (book_id),
  UNIQUE KEY uk_books_isbn (isbn),
  KEY idx_books_author_id (author_id),
  KEY idx_books_category_id (category_id),
  KEY idx_books_discount_id (discount_id),
  CONSTRAINT fk_books_author
    FOREIGN KEY (author_id) REFERENCES authors (author_id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT fk_books_category
    FOREIGN KEY (category_id) REFERENCES categories (category_id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT fk_books_discount
    FOREIGN KEY (discount_id) REFERENCES discounts (discount_id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory (
  id BIGINT NOT NULL AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  quantity INT,
  updated_at DATETIME,
  PRIMARY KEY (id),
  UNIQUE KEY uk_inventory_book_id (book_id),
  CONSTRAINT fk_inventory_book
    FOREIGN KEY (book_id) REFERENCES books (book_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS carts (
  cart_id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME,
  updated_at DATETIME,
  user_id BIGINT,
  PRIMARY KEY (cart_id),
  UNIQUE KEY uk_carts_user_id (user_id),
  CONSTRAINT fk_carts_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cart_items (
  cart_item_id BIGINT NOT NULL AUTO_INCREMENT,
  quantity INT,
  price DECIMAL(19, 2),
  cart_id BIGINT,
  book_id BIGINT,
  PRIMARY KEY (cart_item_id),
  KEY idx_cart_items_cart_id (cart_id),
  KEY idx_cart_items_book_id (book_id),
  CONSTRAINT fk_cart_items_cart
    FOREIGN KEY (cart_id) REFERENCES carts (cart_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_cart_items_book
    FOREIGN KEY (book_id) REFERENCES books (book_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
  order_id BIGINT NOT NULL AUTO_INCREMENT,
  order_date DATETIME,
  status VARCHAR(50),
  total_amount DECIMAL(19, 2),
  receiver_name VARCHAR(255),
  receiver_phone VARCHAR(255),
  shipping_address TEXT,
  shipping_fee DECIMAL(19, 2),
  shipping_method VARCHAR(255),
  tracking_code VARCHAR(255),
  voucher_code VARCHAR(50),
  voucher_discount DECIMAL(19, 2),
  updated_at DATETIME,
  user_id BIGINT,
  PRIMARY KEY (order_id),
  KEY idx_orders_user_id (user_id),
  CONSTRAINT fk_orders_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT chk_orders_status
    CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'COMPLETED', 'CANCELLED'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS order_items (
  order_item_id BIGINT NOT NULL AUTO_INCREMENT,
  quantity INT,
  price DECIMAL(19, 2),
  order_id BIGINT,
  book_id BIGINT,
  PRIMARY KEY (order_item_id),
  KEY idx_order_items_order_id (order_id),
  KEY idx_order_items_book_id (book_id),
  CONSTRAINT fk_order_items_order
    FOREIGN KEY (order_id) REFERENCES orders (order_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_order_items_book
    FOREIGN KEY (book_id) REFERENCES books (book_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  amount DECIMAL(19, 2),
  payment_method VARCHAR(255),
  status VARCHAR(255),
  transaction_id VARCHAR(255),
  payment_url VARCHAR(1000),
  callback_signature VARCHAR(255),
  callback_received_at DATETIME,
  callback_verified TINYINT(1) DEFAULT 0,
  paid_at DATETIME,
  created_at DATETIME,
  updated_at DATETIME,
  order_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY uk_payments_order_id (order_id),
  CONSTRAINT fk_payments_order
    FOREIGN KEY (order_id) REFERENCES orders (order_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT NOT NULL AUTO_INCREMENT,
  rating INT,
  comment TEXT,
  created_at DATETIME,
  user_id BIGINT,
  book_id BIGINT,
  PRIMARY KEY (id),
  KEY idx_reviews_user_id (user_id),
  KEY idx_reviews_book_id (book_id),
  CONSTRAINT fk_reviews_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_reviews_book
    FOREIGN KEY (book_id) REFERENCES books (book_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS vouchers (
  voucher_id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(255),
  discount_percent INT,
  max_discount DECIMAL(19, 2),
  quantity INT,
  expired_at DATE,
  created_at DATETIME,
  updated_at DATETIME,
  PRIMARY KEY (voucher_id),
  UNIQUE KEY uk_vouchers_code (code)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS wishlists (
  user_id BIGINT NOT NULL,
  book_id BIGINT NOT NULL,
  created_at DATETIME,
  PRIMARY KEY (user_id, book_id),
  KEY idx_wishlists_book_id (book_id),
  CONSTRAINT fk_wishlists_user
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT fk_wishlists_book
    FOREIGN KEY (book_id) REFERENCES books (book_id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
) ENGINE=InnoDB;
