package az.qrfood.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) для ответа на успешную аутентификацию.
 * Содержит JWT токен, который клиент будет использовать для дальнейших запросов.
 */
@Data // Lombok аннотация для геттеров, сеттеров, toString, equals, hashCode
@NoArgsConstructor // Lombok аннотация для конструктора без аргументов
@AllArgsConstructor // Lombok аннотация для конструктора со всеми аргументами
public class LoginResponse {
    private String jwt;
}