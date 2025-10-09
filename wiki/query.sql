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


-- SQL-скрипт для миграции таблицы 'dish' в MySQL

-- Шаг 1: Добавляем новую колонку `dish_status`.
-- Мы используем VARCHAR(255), так как это стандартный способ хранения
-- Java Enum, аннотированных как @Enumerated(EnumType.STRING).
-- Колонка временно создается как NULLABLE, чтобы можно было добавить ее в таблицу с данными.
ALTER TABLE dish ADD COLUMN dish_status VARCHAR(255);

-- Шаг 2: Заполняем новую колонку на основе значений из старой `is_available`.
-- Логика: если блюдо было доступно (true), его статус становится AVAILABLE.
-- Если было недоступно (false), мы считаем его архивированным (ARCHIVED).
UPDATE dish
SET dish_status = CASE
                      WHEN is_available = TRUE THEN 'AVAILABLE'
                      ELSE 'ARCHIVED'
    END;

-- Шаг 3: Теперь, когда все строки в новой колонке имеют значение,
-- мы можем безопасно установить ограничение NOT NULL, как того требует ваша Entity.
ALTER TABLE dish MODIFY COLUMN dish_status VARCHAR(255) NOT NULL;

-- Шаг 4: После успешной миграции данных удаляем старую колонку `is_available`.
ALTER TABLE dish DROP COLUMN is_available;
