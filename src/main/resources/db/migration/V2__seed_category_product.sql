-- Seed data for testing categories and products.
-- This migration uses NOT EXISTS checks to avoid duplicate rows on name + classify.

-- Categories
INSERT INTO categories (name, classify, description)
SELECT 'Fast Food', 'FOOD', 'Quick meals and snacks'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Fast Food' AND classify = 'FOOD'
);

INSERT INTO categories (name, classify, description)
SELECT 'Main Course', 'FOOD', 'Rice, noodles, and full meals'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Main Course' AND classify = 'FOOD'
);

INSERT INTO categories (name, classify, description)
SELECT 'Dessert', 'FOOD', 'Sweet treats and cakes'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Dessert' AND classify = 'FOOD'
);

INSERT INTO categories (name, classify, description)
SELECT 'Coffee', 'DRINK', 'Coffee-based beverages'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Coffee' AND classify = 'DRINK'
);

INSERT INTO categories (name, classify, description)
SELECT 'Tea', 'DRINK', 'Tea-based beverages'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Tea' AND classify = 'DRINK'
);

INSERT INTO categories (name, classify, description)
SELECT 'Juice', 'DRINK', 'Fresh fruit juices'
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = 'Juice' AND classify = 'DRINK'
);

-- Products: FOOD
INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Classic Beef Burger',
    'Grilled beef patty, lettuce, tomato, and house sauce',
    6.50,
    'https://example.com/products/beef-burger.png',
    120,
    'FOOD',
    (SELECT id FROM categories WHERE name = 'Fast Food' AND classify = 'FOOD' LIMIT 1),
    4.50,
    18,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Classic Beef Burger' AND classify = 'FOOD'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Chicken Wrap',
    'Tortilla wrap with grilled chicken and mixed vegetables',
    5.20,
    'https://example.com/products/chicken-wrap.png',
    90,
    'FOOD',
    (SELECT id FROM categories WHERE name = 'Fast Food' AND classify = 'FOOD' LIMIT 1),
    4.20,
    12,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Chicken Wrap' AND classify = 'FOOD'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Grilled Pork Rice',
    'Steamed rice with grilled pork and pickled vegetables',
    4.80,
    'https://example.com/products/grilled-pork-rice.png',
    150,
    'FOOD',
    (SELECT id FROM categories WHERE name = 'Main Course' AND classify = 'FOOD' LIMIT 1),
    4.60,
    25,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Grilled Pork Rice' AND classify = 'FOOD'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Spaghetti Bolognese',
    'Italian-style pasta with rich meat sauce',
    7.30,
    'https://example.com/products/spaghetti-bolognese.png',
    80,
    'FOOD',
    (SELECT id FROM categories WHERE name = 'Main Course' AND classify = 'FOOD' LIMIT 1),
    4.40,
    16,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Spaghetti Bolognese' AND classify = 'FOOD'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Chocolate Lava Cake',
    'Warm chocolate cake with molten center',
    3.90,
    'https://example.com/products/chocolate-lava-cake.png',
    60,
    'FOOD',
    (SELECT id FROM categories WHERE name = 'Dessert' AND classify = 'FOOD' LIMIT 1),
    4.70,
    31,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Chocolate Lava Cake' AND classify = 'FOOD'
);

-- Products: DRINK
INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Americano',
    'Espresso with hot water',
    2.40,
    'https://example.com/products/americano.png',
    200,
    'DRINK',
    (SELECT id FROM categories WHERE name = 'Coffee' AND classify = 'DRINK' LIMIT 1),
    4.30,
    22,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Americano' AND classify = 'DRINK'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Caramel Latte',
    'Espresso with steamed milk and caramel syrup',
    3.80,
    'https://example.com/products/caramel-latte.png',
    140,
    'DRINK',
    (SELECT id FROM categories WHERE name = 'Coffee' AND classify = 'DRINK' LIMIT 1),
    4.60,
    28,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Caramel Latte' AND classify = 'DRINK'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Peach Oolong Tea',
    'Fragrant oolong tea with peach flavor',
    3.10,
    'https://example.com/products/peach-oolong-tea.png',
    110,
    'DRINK',
    (SELECT id FROM categories WHERE name = 'Tea' AND classify = 'DRINK' LIMIT 1),
    4.20,
    14,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Peach Oolong Tea' AND classify = 'DRINK'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Lemon Black Tea',
    'Black tea served with fresh lemon',
    2.90,
    'https://example.com/products/lemon-black-tea.png',
    130,
    'DRINK',
    (SELECT id FROM categories WHERE name = 'Tea' AND classify = 'DRINK' LIMIT 1),
    4.10,
    10,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Lemon Black Tea' AND classify = 'DRINK'
);

INSERT INTO products (name, description, price, image_url, stock, classify, category_id, avg_rating, rating_count, is_available)
SELECT
    'Orange Juice',
    'Freshly squeezed orange juice',
    3.40,
    'https://example.com/products/orange-juice.png',
    95,
    'DRINK',
    (SELECT id FROM categories WHERE name = 'Juice' AND classify = 'DRINK' LIMIT 1),
    4.50,
    19,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE name = 'Orange Juice' AND classify = 'DRINK'
);
