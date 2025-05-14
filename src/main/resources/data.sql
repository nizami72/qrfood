-- Ресторан
INSERT INTO restaurant (id, name, address, phone)
VALUES (1, 'Burger House', 'Baku, Nizami street', '+994-55-123-45-67');

-- Столы
INSERT INTO tableqr (id, table_number, qr_code_url, restaurant_id)
VALUES
    (1, 1, 'https://example.com/qr?table=1', 1),
    (2, 2, 'https://example.com/qr?table=2', 1),
    (3, 3, 'https://example.com/qr?table=3', 1),
    (4, 4, 'https://example.com/qr?table=4', 1),
    (5, 5, 'https://example.com/qr?table=5', 1);

-- Категории
INSERT INTO menuCategory (id, name, restaurant_id)
VALUES
    (1, 'Бургеры', 1),
    (2, 'Напитки', 1);

-- Блюда
INSERT INTO menu_item (id, name, description, price, is_available, category_id)
VALUES
    (1, 'Чизбургер', 'Сыр, мясо, булочка', 5.50, true, 1),
    (2, 'Биг Бургер', 'Двойной мясной', 8.00, true, 1),
    (3, 'Кола', '0.5л холодная', 1.50, true, 2),
    (4, 'Спрайт', '0.5л', 1.50, true, 2);
