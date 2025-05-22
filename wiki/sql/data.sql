INSERT INTO restaurant (name, address, geo_lat, geo_lng) VALUES
                                                             ('Yemen Al Saeed Restaurant', 'Nizami st., Baku', 40.3738333, 49.8439049),
                                                             ('Nur Lounge', 'Azadlig Ave., Baku', 40.3653738, 49.8276407),
                                                             ('The Cheese Restaurant', '5B Tbilisi Ave., Baku', 40.3966861, 49.871122),
                                                             ('Novawood Azerbaijan', '10 Jafar Jabbarli St., Baku', 40.3802008, 49.862466),
                                                             ('Şuşa', '14 Rasul Rza St., Baku', 40.3911814, 49.8552719),
                                                             ('Mala Praqa restoranı', '9W Ahmad Rajabli St., Baku', 40.3923024, 49.9416703),
                                                             ('Notes Gastronomy&Bar', '41 Nizami St., Baku', 40.3769566, 49.8451627),
                                                             ('Gənclik Şadlıq Sarayı', '14 Ganjlik Ave., Baku', 40.4022855, 49.8613645),
                                                             ('Niaqara', 'CR Neftchilar Ave., Baku', 40.4008564, 49.8315594),
                                                             ('Bakida deniz kenarı restoranlar', 'G5 Baku Seaside Blvd., Baku', 40.5143386, 50.1512586),
                                                             ('Latitude & Longitude Bar Lounge', '4, 6 Əziz Əliyev St., Baku', 40.3700000, 49.8300000),
                                                             ('Sushi Room Baku', '91 Nizami St., Baku', 40.3775000, 49.8920000),
                                                             ('Pivnaya Apteka', '147 Neftçilər Ave., Baku', 40.3705000, 49.8540000),
                                                             ('Mangal Steak House', '5 Aydin Nasirov St., Baku', 40.3850000, 49.8650000),
                                                             ('Caffeine Baku', '64 Heydar Aliyev Ave., Baku', 40.3940000, 49.8820000);

-- Вставка в таблицу restaurant_phone (связь по имени ресторана)
INSERT INTO restaurant_phone (phone, restaurant_id) VALUES
                                                        ('+994 50 123 45 67', (SELECT id FROM restaurant WHERE name = 'Yemen Al Saeed Restaurant')),
                                                        ('+994 50 234 56 78', (SELECT id FROM restaurant WHERE name = 'Nur Lounge')),
                                                        ('+994 50 345 67 89', (SELECT id FROM restaurant WHERE name = 'The Cheese Restaurant')),
                                                        ('+994 50 456 78 90', (SELECT id FROM restaurant WHERE name = 'Novawood Azerbaijan')),
                                                        ('+994 50 567 89 01', (SELECT id FROM restaurant WHERE name = 'Şuşa')),
                                                        ('+994 50 678 90 12', (SELECT id FROM restaurant WHERE name = 'Mala Praqa restoranı')),
                                                        ('+994 50 789 01 23', (SELECT id FROM restaurant WHERE name = 'Notes Gastronomy&Bar')),
                                                        ('+994 50 890 12 34', (SELECT id FROM restaurant WHERE name = 'Gənclik Şadlıq Sarayı')),
                                                        ('+994 50 901 23 45', (SELECT id FROM restaurant WHERE name = 'Niaqara')),
                                                        ('+994 50 012 34 56', (SELECT id FROM restaurant WHERE name = 'Bakida deniz kenarı restoranlar')),
                                                        ('+994 51 206 85 81', (SELECT id FROM restaurant WHERE name = 'Latitude & Longitude Bar Lounge')),
                                                        ('+994 55 587 90 30', (SELECT id FROM restaurant WHERE name = 'Sushi Room Baku')),
                                                        ('+994 50 254 54 43', (SELECT id FROM restaurant WHERE name = 'Pivnaya Apteka')),
                                                        ('+994 50 261 20 20', (SELECT id FROM restaurant WHERE name = 'Mangal Steak House')),
                                                        ('+994 50 207 27 87', (SELECT id FROM restaurant WHERE name = 'Caffeine Baku'));

-- SQL script to fill Category and MenuItem tables with fake data

CREATE TABLE common_category (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 code VARCHAR(50) NOT NULL UNIQUE,  -- A unique code like "BREAD_PASTRY"
                                 icon_url VARCHAR(512),             -- Optional icon for the category
                                 display_order INT DEFAULT 0        -- For controlling display order
);

-- Table for localized category names
CREATE TABLE common_category_translation (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             category_id BIGINT NOT NULL,
                                             locale VARCHAR(10) NOT NULL,       -- e.g., "en", "az", "ru"
                                             name VARCHAR(255) NOT NULL,
                                             FOREIGN KEY (category_id) REFERENCES common_category(id),
                                             UNIQUE KEY (category_id, locale)   -- Each category can have only one translation per locale
);


-- Assuming restaurant with ID 1 exists
-- If not, you may need to create a restaurant first or adjust the restaurant_id

-- Insert 10 menu categories
INSERT INTO qrfood.menu_category (eatery_id, name) VALUES
                                                   (6, 'Breads & Pastries'),
                                                   (6, 'Breakfast Dishes'),
                                                   (6, 'Desserts & Sweets'),
                                                   (6, 'Drinks & Beverages'),
                                                   (6, 'Kebabs & Grilled Meats'),
                                                   (6, 'Rice Dishes'),
                                                   (6, 'Soups'),
                                                   (6, 'Stews & Braised Dishes'),
                                                   (6, 'Vegetarian & Vegan Dishes'),
                                                   (6, 'Appetizers & Starters');

-- Insert menu items for Breads & Pastries (Category ID 1)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (6, 1, 'Tandoor Bread', 'Traditional bread baked in clay oven', 2.50, 'https://example.com/images/tandoor-bread.jpg', true),
                                                                                                          (6, 1, 'Lavash', 'Thin flatbread made with flour, water, and salt', 1.75, 'https://example.com/images/lavash.jpg', true),
                                                                                                          (6, 1, 'Cheese Pastry', 'Flaky pastry filled with feta cheese and herbs', 3.25, 'https://example.com/images/cheese-pastry.jpg', true),
                                                                                                          (6, 1, 'Meat Samsa', 'Baked pastry with spiced meat filling', 4.00, 'https://example.com/images/meat-samsa.jpg', true);

-- Insert menu items for Breakfast Dishes (Category ID 2)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (7, 2, 'Shakshuka', 'Eggs poached in spiced tomato sauce with peppers', 8.50, 'https://example.com/images/shakshuka.jpg', true),
                                                                                                          (7, 2, 'Kuku', 'Persian herb frittata with barberries and walnuts', 7.25, 'https://example.com/images/kuku.jpg', true),
                                                                                                          (7, 2, 'Menemen', 'Scrambled eggs with tomatoes, peppers, and spices', 6.75, 'https://example.com/images/menemen.jpg', true),
                                                                                                          (7, 2, 'Breakfast Platter', 'Assortment of cheese, olives, honey, jam, and bread', 12.00, 'https://example.com/images/breakfast-platter.jpg', true),
                                                                                                          (7, 2, 'Omelette', 'Fluffy omelette with herbs and cheese', 5.50, 'https://example.com/images/omelette.jpg', true);

-- Insert menu items for Desserts & Sweets (Category ID 3)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 3, 'Baklava', 'Layered pastry with nuts and honey syrup', 4.50, 'https://example.com/images/baklava.jpg', true),
                                                                                                          (1, 3, 'Kunafa', 'Sweet cheese pastry soaked in sugar syrup', 5.25, 'https://example.com/images/kunafa.jpg', true),
                                                                                                          (1, 3, 'Rice Pudding', 'Creamy rice pudding with cinnamon and rose water', 3.75, 'https://example.com/images/rice-pudding.jpg', true),
                                                                                                          (1, 3, 'Halva', 'Sweet tahini-based confection with pistachios', 4.00, 'https://example.com/images/halva.jpg', true),
                                                                                                          (1, 3, 'Lokum', 'Turkish delight with various flavors', 3.50, 'https://example.com/images/lokum.jpg', true),
                                                                                                          (1, 3, 'Saffron Ice Cream', 'Traditional ice cream with saffron and pistachios', 6.00, 'https://example.com/images/saffron-ice-cream.jpg', true);

-- Insert menu items for Drinks & Beverages (Category ID 4)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 4, 'Turkish Coffee', 'Strong coffee brewed with cardamom', 3.50, 'https://example.com/images/turkish-coffee.jpg', true),
                                                                                                          (1, 4, 'Mint Tea', 'Fresh mint leaves steeped in hot water', 2.75, 'https://example.com/images/mint-tea.jpg', true),
                                                                                                          (1, 4, 'Ayran', 'Yogurt-based savory drink with salt', 2.50, 'https://example.com/images/ayran.jpg', true),
                                                                                                          (1, 4, 'Pomegranate Juice', 'Freshly squeezed pomegranate juice', 4.25, 'https://example.com/images/pomegranate-juice.jpg', true),
                                                                                                          (1, 4, 'Rosewater Lemonade', 'Lemonade infused with rosewater', 3.75, 'https://example.com/images/rosewater-lemonade.jpg', true);

-- Insert menu items for Kebabs & Grilled Meats (Category ID 5)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 5, 'Lamb Kebab', 'Grilled lamb skewers with spices and herbs', 14.50, 'https://example.com/images/lamb-kebab.jpg', true),
                                                                                                          (1, 5, 'Chicken Shish', 'Marinated chicken pieces grilled on skewers', 12.75, 'https://example.com/images/chicken-shish.jpg', true),
                                                                                                          (1, 5, 'Beef Kofta', 'Spiced ground beef formed into cylinders and grilled', 13.25, 'https://example.com/images/beef-kofta.jpg', true),
                                                                                                          (1, 5, 'Mixed Grill', 'Assortment of grilled meats with vegetables', 18.00, 'https://example.com/images/mixed-grill.jpg', true),
                                                                                                          (1, 5, 'Lamb Chops', 'Grilled lamb chops with herbs and spices', 16.50, 'https://example.com/images/lamb-chops.jpg', true),
                                                                                                          (1, 5, 'Adana Kebab', 'Spicy minced meat kebab from Adana region', 14.00, 'https://example.com/images/adana-kebab.jpg', true),
                                                                                                          (1, 5, 'Fish Kebab', 'Grilled fish skewers with lemon and herbs', 15.25, 'https://example.com/images/fish-kebab.jpg', true);

-- Insert menu items for Rice Dishes (Category ID 6)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 6, 'Pilaf', 'Fluffy rice cooked with broth and spices', 5.50, 'https://example.com/images/pilaf.jpg', true),
                                                                                                          (1, 6, 'Jeweled Rice', 'Saffron rice with dried fruits and nuts', 7.25, 'https://example.com/images/jeweled-rice.jpg', true),
                                                                                                          (1, 6, 'Lamb Biryani', 'Fragrant rice dish with lamb and spices', 13.50, 'https://example.com/images/lamb-biryani.jpg', true),
                                                                                                          (1, 6, 'Vegetable Rice', 'Rice cooked with seasonal vegetables', 6.75, 'https://example.com/images/vegetable-rice.jpg', true);

-- Insert menu items for Soups (Category ID 7)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 7, 'Lentil Soup', 'Red lentil soup with spices and lemon', 5.25, 'https://example.com/images/lentil-soup.jpg', true),
                                                                                                          (1, 7, 'Chicken Soup', 'Hearty chicken soup with vegetables and herbs', 6.50, 'https://example.com/images/chicken-soup.jpg', true),
                                                                                                          (1, 7, 'Yogurt Soup', 'Creamy yogurt soup with mint and rice', 5.75, 'https://example.com/images/yogurt-soup.jpg', true);

-- Insert menu items for Stews & Braised Dishes (Category ID 8)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 8, 'Lamb Stew', 'Slow-cooked lamb with vegetables and spices', 14.25, 'https://example.com/images/lamb-stew.jpg', true),
                                                                                                          (1, 8, 'Eggplant Stew', 'Braised eggplant with tomatoes and spices', 10.50, 'https://example.com/images/eggplant-stew.jpg', true),
                                                                                                          (1, 8, 'Okra Stew', 'Okra cooked with tomatoes, onions, and spices', 9.75, 'https://example.com/images/okra-stew.jpg', true),
                                                                                                          (1, 8, 'Beef Tagine', 'Slow-cooked beef with dried fruits and spices', 15.00, 'https://example.com/images/beef-tagine.jpg', true),
                                                                                                          (1, 8, 'Chicken Stew', 'Braised chicken with herbs and vegetables', 12.50, 'https://example.com/images/chicken-stew.jpg', true);

-- Insert menu items for Vegetarian & Vegan Dishes (Category ID 9)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 9, 'Falafel Plate', 'Crispy chickpea fritters with tahini sauce and salad', 9.50, 'https://example.com/images/falafel-plate.jpg', true),
                                                                                                          (1, 9, 'Stuffed Grape Leaves', 'Vine leaves stuffed with rice, herbs, and spices', 8.25, 'https://example.com/images/stuffed-grape-leaves.jpg', true),
                                                                                                          (1, 9, 'Hummus', 'Creamy chickpea dip with olive oil and spices', 5.75, 'https://example.com/images/hummus.jpg', true),
                                                                                                          (1, 9, 'Baba Ganoush', 'Smoky eggplant dip with tahini and garlic', 6.00, 'https://example.com/images/baba-ganoush.jpg', true),
                                                                                                          (1, 9, 'Vegetable Couscous', 'Steamed couscous with seasonal vegetables', 8.50, 'https://example.com/images/vegetable-couscous.jpg', true),
                                                                                                          (1, 9, 'Spinach Borek', 'Pastry filled with spinach and feta cheese', 7.25, 'https://example.com/images/spinach-borek.jpg', true);

-- Insert menu items for Appetizers & Starters (Category ID 10)
INSERT INTO menu_item (restaurant_id, category_id, name, description, price, image_url, is_available) VALUES
                                                                                                          (1, 10, 'Mezze Platter', 'Assortment of dips, olives, and pickles with bread', 14.50, 'https://example.com/images/mezze-platter.jpg', true),
                                                                                                          (1, 10, 'Stuffed Mushrooms', 'Mushrooms filled with herbs and cheese', 7.75, 'https://example.com/images/stuffed-mushrooms.jpg', true),
                                                                                                          (1, 10, 'Calamari', 'Fried squid rings with garlic aioli', 9.25, 'https://example.com/images/calamari.jpg', true),
                                                                                                          (1, 10, 'Halloumi', 'Grilled Cypriot cheese with herbs and lemon', 8.50, 'https://example.com/images/halloumi.jpg', true),
                                                                                                          (1, 10, 'Olives & Pickles', 'Assorted marinated olives and house pickles', 5.00, 'https://example.com/images/olives-pickles.jpg', true);