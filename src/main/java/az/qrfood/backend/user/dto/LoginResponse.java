package az.qrfood.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для ответа на успешную аутентификацию.
 * Содержит JWT токен и ID пользователя, которые клиент будет использовать для дальнейших запросов.
 */
@Data // Lombok аннотация для геттеров, сеттеров, toString, equals, hashCode
@NoArgsConstructor // Lombok аннотация для конструктора без аргументов
@AllArgsConstructor // Lombok аннотация для конструктора со всеми аргументами
public class LoginResponse {
    private String jwt;
    private Long userId;

    // Constructor with just JWT for backward compatibility
    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }
}
