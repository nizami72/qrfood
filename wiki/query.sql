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
    ADD FOREIGN KEY (kitchen_department_id) REFERENCES kitchen_departments (id) ON DELETE SET NULL;


ALTER TABLE dish
    ADD COLUMN dish_status VARCHAR(255);

UPDATE dish
SET dish_status = CASE
                      WHEN is_available = TRUE THEN 'AVAILABLE'
                      ELSE 'ARCHIVED'
    END;

ALTER TABLE dish
    MODIFY COLUMN dish_status VARCHAR(255) NOT NULL;

ALTER TABLE dish
    DROP COLUMN is_available;

# Table in an eatery
ALTER TABLE table_in_eatery
    MODIFY COLUMN status VARCHAR(255) NOT NULL;

alter table table_in_eatery
    drop constraint table_in_eatery_chk_1;

# Category modidieng
ALTER TABLE category
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP;


ALTER TABLE category
    ADD COLUMN category_status VARCHAR(255) AFTER eatery_id;


insert into email_templates (template_key, locale, subject_template, body_html, description, updated_at)
values ('WELCOME',
        'en',
        'Welcome to qrfood',
        '<p>Click the link below to log in:</p>
    <p><a th:href="${magicLinkUrl}">Log in now</a></p>
    <p>If you did not request this, please ignore this email.</p>',
        'First letter',
        now());

insert into email_templates (template_key, locale, subject_template, body_html, description, updated_at)
values ('WELCOME',
        'az',
        'Qrfood-a xoş gəlmisiniz',
        '<p>Giriş etmək üçün aşağıdakı linkə klikləyin:</p>
    <p><a th:href="${magicLinkUrl}">Daxil ol</a></p>
    <p>Əgər bu sorğunu siz göndərməmisinizsə, zəhmət olmasa bu məktubu nəzərə almayın.</p>',
        'İlk məktub',
        now());

insert into email_templates (template_key, subject_template, body_html, description, updated_at)
values ('WELCOME',
        'Добро пожаловать в qrfood',
        '<p>Нажмите на ссылку ниже, чтобы войти:</p>
    <p><a th:href="${magicLinkUrl}">Войти</a></p>
    <p>Если вы это не запрашивали, пожалуйста, проигнорируйте это письмо.</p>',
        'Первое письмо',
        now());

insert into email_templates (template_key, subject_template, body_html, description, updated_at)
values ('magic_link',
        'Welcome to qrfood',
        '
        <h3 style="margin: 0 0 20px 0; color: #333333; font-size: 20px; font-weight: bold;"
    th:text="#{login.title}">
    Sign in to QR Food
</h3>

<p style="margin: 0 0 20px 0; font-size: 16px; line-height: 1.5; color: #333333;"
   th:text="#{login.intro(${adminName})}">
   Hello, User. We received a request...
</p>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
    <tr>
        <td align="center" style="padding: 10px 0 30px 0;">
            <a th:href="${magicLinkUrl}" target="_blank"
               style="background-color: #28a745; color: #ffffff; font-family: sans-serif; font-size: 16px; font-weight: bold; text-decoration: none; padding: 14px 24px; border-radius: 5px; display: inline-block; border: 1px solid #28a745;"
               th:text="#{login.button}">
               🔓 Log In Now
            </a>
        </td>
    </tr>
</table>

<div style="background-color: #f8f9fa; border-left: 4px solid #6c757d; padding: 15px; margin-bottom: 25px; font-size: 14px; color: #555555;">
    <p style="margin: 0 0 5px 0;">
        <strong th:text="#{login.details.header}">Request Details:</strong>
    </p>
    <ul style="margin: 0; padding-left: 20px;">
        <li>
            <strong th:text="#{login.details.time}">Time:</strong>
            <span th:text="${requestTime}">12:00 PM</span>
        </li>
        <li>
            <strong th:text="#{login.details.device}">Device:</strong>
            <span th:text="${deviceInfo}">Chrome on Windows</span>
        </li>
        <li>
            <strong th:text="#{login.details.location}">Location (IP):</strong>
            <span th:text="${ipAddress}">192.168.1.1</span>
        </li>
    </ul>
</div>

<p style="margin: 0 0 20px 0; font-size: 14px; color: #666666; line-height: 1.5;"
   th:text="#{login.security_warning}">
   If you did not request this link...
</p>

<hr style="border: 0; border-top: 1px solid #eeeeee; margin: 20px 0;">

<p style="margin: 0; font-size: 12px; color: #999999; line-height: 1.4;">
    <span th:text="#{login.fallback_text}">Link not working? Copy this URL:</span><br>
    <a th:href="${magicLinkUrl}" style="color: #28a745; text-decoration: underline; word-break: break-all;">
        <span th:text="${magicLinkUrl}">https://qrfood.com/auth...</span>
    </a>
</p>',
        'First letter',
        now());


alter table qrfood.eatery
    add onboarding_status varchar(64) not null;

