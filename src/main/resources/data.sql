-- Ресторан
create database qrfood;

INSERT INTO eatery (id, name, address, phone)
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
INSERT INTO category (id, name, restaurant_id)
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


create table `order`
(
    id         bigint auto_increment
        primary key,
    created_at datetime(6)                                                    null,
    notes      varchar(255)                                                   null,
    status     enum ('CANCELLED', 'DELIVERED', 'IN_PROGRESS', 'NEW', 'READY') null,
    table_id   bigint                                                         not null,
    constraint FKunsbex1d87n849uscsv0lloc
        foreign key (table_id) references table_in_restaurant (id)
)
    charset = utf8mb4;

