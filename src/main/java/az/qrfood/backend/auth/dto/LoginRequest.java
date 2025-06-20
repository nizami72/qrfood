package az.qrfood.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для запроса на аутентификацию (логин).
 * Содержит имя пользователя, пароль и ID активного ресторана, отправляемые клиентом.
 */
@Data // Lombok аннотация для геттеров, сеттеров, toString, equals, hashCode
@NoArgsConstructor // Lombok аннотация для конструктора без аргументов
@AllArgsConstructor // Lombok аннотация для конструктора со всеми аргументами
public class LoginRequest {
    private String email;
    private String password;
    private Long eateryId; // ID активного ресторана
}
