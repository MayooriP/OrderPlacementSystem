-- Sample data for testing
USE order_system;

-- Insert sample customers (only if they don't exist)
INSERT INTO customers (user_id, full_name, email, created_date_time, status, first_name, encrypted_phone_number, encrypted_email)
SELECT 737755, 'John Doe', 'john.doe@example.com', NOW(), 'ACTIVE', 'John', 'enc_1234567890', 'enc_john.doe@example.com'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM customers WHERE user_id = 737755);

INSERT INTO customers (user_id, full_name, email, created_date_time, status, first_name, encrypted_phone_number, encrypted_email)
SELECT 737756, 'Jane Smith', 'jane.smith@example.com', NOW(), 'ACTIVE', 'Jane', 'enc_0987654321', 'enc_jane.smith@example.com'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM customers WHERE user_id = 737756);

INSERT INTO customers (user_id, full_name, email, created_date_time, status, first_name, encrypted_phone_number, encrypted_email)
SELECT 737757, 'Robert Johnson', 'robert.johnson@example.com', NOW(), 'ACTIVE', 'Robert', 'enc_1122334455', 'enc_robert.johnson@example.com'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM customers WHERE user_id = 737757);

-- Insert sample restaurant (only if it doesn't exist)
INSERT INTO restaurants (name, description, address, phone_number, email, opening_hours, created_date_time, status)
SELECT 'Mama Eatz', 'Delicious home-style cooking', '123 Main St, Anytown, USA', '555-123-4567', 'info@mamaeatz.com', 'Tue-Sun: 11:00 AM - 2:00 PM, 5:00 PM - 10:00 PM', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM restaurants WHERE name = 'Mama Eatz');

-- Insert restaurant working hours (only if they don't exist)
INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'TUESDAY', '11:00:00', '14:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'TUESDAY' AND start_time = '11:00:00' AND end_time = '14:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'TUESDAY', '17:00:00', '22:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'TUESDAY' AND start_time = '17:00:00' AND end_time = '22:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'WEDNESDAY', '11:00:00', '14:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'WEDNESDAY' AND start_time = '11:00:00' AND end_time = '14:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'WEDNESDAY', '17:00:00', '22:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'WEDNESDAY' AND start_time = '17:00:00' AND end_time = '22:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'THURSDAY', '11:00:00', '14:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'THURSDAY' AND start_time = '11:00:00' AND end_time = '14:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'THURSDAY', '17:00:00', '22:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'THURSDAY' AND start_time = '17:00:00' AND end_time = '22:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'FRIDAY', '11:00:00', '14:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'FRIDAY' AND start_time = '11:00:00' AND end_time = '14:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'FRIDAY', '17:00:00', '22:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'FRIDAY' AND start_time = '17:00:00' AND end_time = '22:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'SATURDAY', '11:00:00', '14:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'SATURDAY' AND start_time = '11:00:00' AND end_time = '14:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'SATURDAY', '17:00:00', '22:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'SATURDAY' AND start_time = '17:00:00' AND end_time = '22:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'SUNDAY', '11:00:00', '14:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'SUNDAY' AND start_time = '11:00:00' AND end_time = '14:00:00'
);

INSERT INTO restaurant_working_hours (restaurant_id, day_of_the_week, start_time, end_time, created_date_time, status)
SELECT 1, 'SUNDAY', '17:00:00', '22:00:00', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (
    SELECT 1 FROM restaurant_working_hours
    WHERE restaurant_id = 1 AND day_of_the_week = 'SUNDAY' AND start_time = '17:00:00' AND end_time = '22:00:00'
);

-- Insert categories (only if they don't exist)
INSERT INTO categories (name, description, created_date_time, status)
SELECT 'Appetizers', 'Starters and small plates', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Appetizers');

INSERT INTO categories (name, description, created_date_time, status)
SELECT 'Main Courses', 'Hearty entrees', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Main Courses');

INSERT INTO categories (name, description, created_date_time, status)
SELECT 'Desserts', 'Sweet treats', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Desserts');

INSERT INTO categories (name, description, created_date_time, status)
SELECT 'Beverages', 'Drinks and refreshments', NOW(), 'ACTIVE'
FROM dual WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Beverages');

-- Insert subcategories
INSERT INTO subcategories (name, description, category_id, created_date_time, status)
VALUES
('Soups', 'Hot and comforting soups', 1, NOW(), 'ACTIVE'),
('Salads', 'Fresh and healthy salads', 1, NOW(), 'ACTIVE'),
('Pasta', 'Italian pasta dishes', 2, NOW(), 'ACTIVE'),
('Burgers', 'Gourmet burgers', 2, NOW(), 'ACTIVE'),
('Cakes', 'Delicious cakes', 3, NOW(), 'ACTIVE'),
('Ice Cream', 'Cold and creamy treats', 3, NOW(), 'ACTIVE'),
('Hot Drinks', 'Coffee and tea', 4, NOW(), 'ACTIVE'),
('Cold Drinks', 'Refreshing cold beverages', 4, NOW(), 'ACTIVE');

-- Insert menu items
INSERT INTO menu_items (name, description, category_id, subcategory_id, price, available, created_date_time, status)
VALUES
('Tomato Soup', 'Classic tomato soup with basil', 1, 1, 5.99, true, NOW(), 'ACTIVE'),
('Caesar Salad', 'Crisp romaine lettuce with Caesar dressing', 1, 2, 7.99, true, NOW(), 'ACTIVE'),
('Spaghetti Bolognese', 'Spaghetti with rich meat sauce', 2, 3, 12.99, true, NOW(), 'ACTIVE'),
('Classic Burger', 'Beef patty with lettuce, tomato, and cheese', 2, 4, 10.99, true, NOW(), 'ACTIVE'),
('Chocolate Cake', 'Rich chocolate cake with ganache', 3, 5, 6.99, true, NOW(), 'ACTIVE'),
('Vanilla Ice Cream', 'Creamy vanilla ice cream', 3, 6, 4.99, true, NOW(), 'ACTIVE'),
('Cappuccino', 'Espresso with steamed milk and foam', 4, 7, 3.99, true, NOW(), 'ACTIVE'),
('Iced Tea', 'Refreshing iced tea with lemon', 4, 8, 2.99, true, NOW(), 'ACTIVE');

-- Insert variants
INSERT INTO variants (variant_name, item_id, variant_type, price, available, created_date_time, status, listing_order)
VALUES
('Small', 1, 'SIZE', 5.99, true, NOW(), 'ACTIVE', 1),
('Large', 1, 'SIZE', 7.99, true, NOW(), 'ACTIVE', 2),
('Regular', 2, 'SIZE', 7.99, true, NOW(), 'ACTIVE', 1),
('With Chicken', 2, 'ADDON', 9.99, true, NOW(), 'ACTIVE', 2),
('Regular', 3, 'SIZE', 12.99, true, NOW(), 'ACTIVE', 1),
('Family Size', 3, 'SIZE', 18.99, true, NOW(), 'ACTIVE', 2),
('Regular', 4, 'SIZE', 10.99, true, NOW(), 'ACTIVE', 1),
('Double Patty', 4, 'ADDON', 14.99, true, NOW(), 'ACTIVE', 2),
('Slice', 5, 'SIZE', 6.99, true, NOW(), 'ACTIVE', 1),
('Whole Cake', 5, 'SIZE', 29.99, true, NOW(), 'ACTIVE', 2),
('Single Scoop', 6, 'SIZE', 4.99, true, NOW(), 'ACTIVE', 1),
('Double Scoop', 6, 'SIZE', 7.99, true, NOW(), 'ACTIVE', 2),
('Small', 7, 'SIZE', 3.99, true, NOW(), 'ACTIVE', 1),
('Large', 7, 'SIZE', 4.99, true, NOW(), 'ACTIVE', 2),
('Regular', 8, 'SIZE', 2.99, true, NOW(), 'ACTIVE', 1),
('Large', 8, 'SIZE', 3.99, true, NOW(), 'ACTIVE', 2);

-- Insert coupons
INSERT INTO coupons (coupon_code, description, status, coupon_discount_percentage, max_amount, coupon_name, min_order_value, discount_type, start_date, end_date)
VALUES
('WELCOME10', 'Get 10% off your first order', 'Active', 10, 50, 'Welcome Discount', 20.00, 'PERCENTAGE', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),
('SUMMER25', 'Summer special: 25% off', 'Active', 25, 100, 'Summer Special', 50.00, 'PERCENTAGE', NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY)),
('FLAT20', 'Flat $20 off on orders above $100', 'Active', 0, 20, 'Flat Discount', 100.00, 'FIXED', NOW(), DATE_ADD(NOW(), INTERVAL 45 DAY));

-- Insert sample carts for each customer
INSERT INTO carts (customer_id, total_amount, created_date_time, last_modified_date_time, status)
VALUES
(1, 26.97, NOW(), NOW(), 'ACTIVE'),
(2, 22.98, NOW(), NOW(), 'ACTIVE'),
(3, 35.97, NOW(), NOW(), 'ACTIVE');

-- Insert cart items for first customer
INSERT INTO cart_items (cart_id, item_id, variant_id, quantity, price, subtotal, special_instructions)
VALUES
(1, 1, 1, 1, 5.99, 5.99, 'Extra hot please'),
(1, 4, 7, 1, 10.99, 10.99, 'No onions'),
(1, 7, 13, 2, 3.99, 7.98, 'Extra foam');

-- Insert cart items for second customer
INSERT INTO cart_items (cart_id, item_id, variant_id, quantity, price, subtotal, special_instructions)
VALUES
(2, 2, 3, 1, 7.99, 7.99, 'Dressing on the side'),
(2, 5, 9, 2, 6.99, 13.98, 'Extra frosting');

-- Insert cart items for third customer
INSERT INTO cart_items (cart_id, item_id, variant_id, quantity, price, subtotal, special_instructions)
VALUES
(3, 3, 5, 1, 12.99, 12.99, 'Extra sauce'),
(3, 4, 8, 1, 14.99, 14.99, 'Well done'),
(3, 8, 15, 2, 2.99, 5.98, 'Extra ice');

-- Insert sample vouchers
INSERT INTO vouchers (voucher_code, description, status, discount_percentage, customer_id, expiry_date, is_used, created_date_time)
VALUES
('BDAY2023', 'Birthday special voucher', 'Active', 15, 1, DATE_ADD(NOW(), INTERVAL 30 DAY), false, NOW()),
('LOYAL10', 'Loyalty reward voucher', 'Active', 10, 2, DATE_ADD(NOW(), INTERVAL 60 DAY), false, NOW());

-- Insert sample referrals
INSERT INTO referrals (referral_code, referrer_id, is_used, created_date_time, status)
VALUES
('REF123', 1, false, NOW(), 'Active'),
('REF456', 2, false, NOW(), 'Active');
