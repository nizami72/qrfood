CREATE USER 'qrfood'@'localhost' IDENTIFIED BY 'HJuy67Qw@HjymPa';
GRANT SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE ON qrfood.* TO 'qrfood'@'localhost';
SELECT `CURRENT_USER`(), USER();


use qrfood;

CREATE TABLE restaurant (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(255) NOT NULL,
                            address VARCHAR(255),
                            geo_lat DOUBLE,
                            geo_lng DOUBLE
);

CREATE TABLE table_qr (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          restaurant_id BIGINT NOT NULL,
                          table_number INT NOT NULL,
                          qr_code_url VARCHAR(512),
                          FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);

CREATE TABLE menuCategory (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          restaurant_id BIGINT NOT NULL,
                          name VARCHAR(255) NOT NULL,
                          FOREIGN KEY (restaurant_id) REFERENCES restaurant(id)
);

CREATE TABLE menu_item (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           restaurant_id BIGINT NOT NULL,
                           category_id BIGINT,
                           name VARCHAR(255) NOT NULL,
                           description TEXT,
                           image_url VARCHAR(512),
                           price DECIMAL(10,2) NOT NULL,
                           is_available BOOLEAN DEFAULT TRUE,
                           FOREIGN KEY (restaurant_id) REFERENCES restaurant(id),
                           FOREIGN KEY (category_id) REFERENCES menuCategory(id)
);

CREATE TABLE customer_order (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                table_id BIGINT NOT NULL,
                                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                status VARCHAR(50) NOT NULL DEFAULT 'NEW',
                                total_price DECIMAL(10,2),
                                FOREIGN KEY (table_id) REFERENCES table_qr(id)
);

CREATE TABLE order_item (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            menu_item_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            note VARCHAR(255),
                            price DECIMAL(10,2) NOT NULL,
                            FOREIGN KEY (order_id) REFERENCES customer_order(id),
                            FOREIGN KEY (menu_item_id) REFERENCES menu_item(id)
);
