ALTER TABLE client_device_orders
    DROP FOREIGN KEY FK8yaisbhnu6t24u7mfix67nswn;

ALTER TABLE client_device_orders
    ADD CONSTRAINT FK8yaisbhnu6t24u7mfix67nswn
        FOREIGN KEY (order_id) REFERENCES `order` (id) ON DELETE CASCADE;


ALTER TABLE client_device_orders
    DROP FOREIGN KEY FK8yaisbhnu6t24u7mfix67nswn;


ALTER TABLE client_device_orders
    ADD CONSTRAINT FK8yaisbhnu6t24u7mfix67nswn
        FOREIGN KEY (order_id) REFERENCES `order` (id) ON DELETE CASCADE;


CREATE TABLE kitchen_departments
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    restaurant_id BIGINT       NOT NULL,
    INDEX idx_restaurant_id (restaurant_id),
    FOREIGN KEY (restaurant_id) REFERENCES eatery (id) ON DELETE CASCADE
);


ALTER TABLE dish
    ADD COLUMN kitchen_department_id BIGINT NULL,
    ADD FOREIGN KEY (kitchen_department_id) REFERENCES kitchen_departments(id) ON DELETE SET NULL;


ALTER TABLE dish ADD COLUMN dish_status VARCHAR(255);

UPDATE dish
SET dish_status = CASE
                      WHEN is_available = TRUE THEN 'AVAILABLE'
                      ELSE 'ARCHIVED'
    END;

ALTER TABLE dish MODIFY COLUMN dish_status VARCHAR(255) NOT NULL;

ALTER TABLE dish DROP COLUMN is_available;

# Table in an eatery
ALTER TABLE table_in_eatery MODIFY COLUMN status VARCHAR(255) NOT NULL;

alter table table_in_eatery
    drop constraint table_in_eatery_chk_1;


# Category modidieng
ALTER TABLE category
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP;


ALTER TABLE category ADD COLUMN category_status VARCHAR(255) AFTER eatery_id;
