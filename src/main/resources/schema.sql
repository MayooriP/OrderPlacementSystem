-- Create database if not exists (this will be handled by Spring Boot's createDatabaseIfNotExist=true parameter)
-- CREATE DATABASE IF NOT EXISTS order_system;
USE order_system;

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    full_name VARCHAR(255),
    email VARCHAR(255),
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50),
    first_name VARCHAR(255),
    DOB DATE,
    gender ENUM('Male', 'Female', 'Prefer not to say'),
    encrypted_phone_number VARCHAR(255) NOT NULL,
    encrypted_email VARCHAR(255)
);

-- Restaurants table
CREATE TABLE IF NOT EXISTS restaurants (
    restaurant_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    address TEXT,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    opening_hours VARCHAR(255),
    logo_url VARCHAR(255),
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50)
);

-- Restaurant working hours table
CREATE TABLE IF NOT EXISTS restaurant_working_hours (
    restaurant_working_id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT,
    day_of_the_week VARCHAR(10),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50),
    date DATE,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id)
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50)
);

-- Subcategories table
CREATE TABLE IF NOT EXISTS subcategories (
    subcategory_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    category_id INT,
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Menu items table
CREATE TABLE IF NOT EXISTS menu_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id INT,
    subcategory_id INT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    available BOOLEAN,
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50),
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (subcategory_id) REFERENCES subcategories(subcategory_id)
);

-- Variants table
CREATE TABLE IF NOT EXISTS variants (
    variant_id INT AUTO_INCREMENT PRIMARY KEY,
    variant_name VARCHAR(255) NOT NULL,
    item_id INT NOT NULL,
    variant_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    available BOOLEAN NOT NULL,
    created_date_time DATETIME,
    created_by VARCHAR(255),
    last_modified_date_time DATETIME,
    last_modified_by VARCHAR(255),
    status VARCHAR(50),
    caution TEXT,
    attribute VARCHAR(255),
    listing_order INT NOT NULL,
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);

-- Carts table
CREATE TABLE IF NOT EXISTS carts (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    total_amount DECIMAL(10, 2),
    created_date_time DATETIME,
    last_modified_date_time DATETIME,
    status VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Cart items table
CREATE TABLE IF NOT EXISTS cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    item_id INT NOT NULL,
    variant_id INT,
    quantity INT,
    price DECIMAL(10, 2),
    subtotal DECIMAL(10, 2),
    special_instructions VARCHAR(255),
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id),
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id),
    FOREIGN KEY (variant_id) REFERENCES variants(variant_id)
);

-- Coupons table
CREATE TABLE IF NOT EXISTS coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    coupon_code VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    status ENUM('Active', 'Inactive'),
    coupon_discount_percentage INT NOT NULL,
    max_amount INT NOT NULL,
    coupon_name VARCHAR(255) NOT NULL,
    min_order_value FLOAT NOT NULL,
    discount_type VARCHAR(255) NOT NULL,
    limit_per_user INT,
    start_date DATETIME NOT NULL,
    end_date DATETIME,
    image_url VARCHAR(255)
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id VARCHAR(36) PRIMARY KEY,
    customer_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    status ENUM('Pending', 'Paid', 'Failed', 'Refunded'),
    payment_method VARCHAR(50) NOT NULL,
    payment_date DATETIME,
    transaction_id VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(36) PRIMARY KEY,
    customer_id INT,
    restaurant_id INT,
    payment_id VARCHAR(36),
    order_date DATETIME NOT NULL,
    delivery_date DATETIME NOT NULL,
    status ENUM('Received', 'Preparing', 'ReadyToPickup', 'OrderCompleted', 'Cancelled'),
    coupon_id INT,
    cooking_instructions VARCHAR(80),
    pickup_instructions VARCHAR(80),
    last_modified_date_time DATETIME,
    status_history JSON,
    square_order_id VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(restaurant_id),
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id),
    FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id)
);

-- Order items table
CREATE TABLE IF NOT EXISTS order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL,
    item_id INT,
    variant_id INT,
    item_name VARCHAR(255),
    category_name VARCHAR(255),
    subcategory_name VARCHAR(255),
    variant_name VARCHAR(255),
    price DECIMAL(10, 2),
    quantity INT,
    subtotal DECIMAL(10, 2),
    special_instructions VARCHAR(255),
    is_free_item BOOLEAN,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id),
    FOREIGN KEY (variant_id) REFERENCES variants(variant_id)
);

-- Vouchers table
CREATE TABLE IF NOT EXISTS vouchers (
    voucher_id INT AUTO_INCREMENT PRIMARY KEY,
    voucher_code VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    status ENUM('Active', 'Inactive', 'Expired', 'Used'),
    discount_percentage INT,
    discount_amount DECIMAL(10, 2),
    free_item_id INT,
    customer_id INT,
    expiry_date DATETIME,
    is_used BOOLEAN,
    used_date DATETIME,
    created_date_time DATETIME,
    FOREIGN KEY (free_item_id) REFERENCES menu_items(item_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

-- Referrals table
CREATE TABLE IF NOT EXISTS referrals (
    referral_id INT AUTO_INCREMENT PRIMARY KEY,
    referral_code VARCHAR(255) NOT NULL UNIQUE,
    referrer_id INT NOT NULL,
    referred_id INT,
    is_used BOOLEAN,
    used_date DATETIME,
    created_date_time DATETIME,
    status ENUM('Active', 'Used', 'Expired'),
    FOREIGN KEY (referrer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (referred_id) REFERENCES customers(customer_id)
);

-- Reward items table
CREATE TABLE IF NOT EXISTS reward_items (
    reward_item_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    item_id INT NOT NULL,
    is_used BOOLEAN,
    expiry_date DATETIME,
    used_date DATETIME,
    created_date_time DATETIME,
    status ENUM('Active', 'Used', 'Expired'),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (item_id) REFERENCES menu_items(item_id)
);

-- Reward vouchers table
CREATE TABLE IF NOT EXISTS reward_vouchers (
    reward_voucher_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    discount_percentage INT,
    discount_amount DECIMAL(10, 2),
    is_used BOOLEAN,
    expiry_date DATETIME,
    used_date DATETIME,
    created_date_time DATETIME,
    status ENUM('Active', 'Used', 'Expired'),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);
