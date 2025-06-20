package az.qrfood.backend.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Утилитарный класс для работы с JWT (JSON Web Tokens).
 * Отвечает за создание, извлечение информации и валидацию токенов.
 */
@Component
public class JwtUtil {

    // Секретный ключ для подписи JWT. Должен быть достаточно длинным и храниться в безопасности.
    // Рекомендуется получать его из переменных окружения или другого безопасного источника.
    @Value("${jwt.secret:yourVerySecretKeyThatIsAtLeast256BitsLongAndShouldBeChangedInProduction}")
    private String secret;

    // Время жизни токена в миллисекундах (например, 10 часов)
    @Value("${jwt.expiration:36000000}")
    private long expiration; // 10 часов

    private Key getSigningKey() {
        // Генерируем ключ из секретной строки
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Извлекает имя пользователя из JWT токена.
     * @param token JWT токен.
     * @return Имя пользователя.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения срока действия из JWT токена.
     * @param token JWT токен.
     * @return Дата истечения срока действия.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает определенное утверждение (claim) из JWT токена.
     * @param token JWT токен.
     * @param claimsResolver Функция для разрешения утверждения.
     * @param <T> Тип утверждения.
     * @return Извлеченное утверждение.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все утверждения (claims) из JWT токена.
     * @param token JWT токен.
     * @return Объект Claims, содержащий все утверждения.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Проверяет, истек ли срок действия JWT токена.
     * @param token JWT токен.
     * @return true, если срок действия токена истек, false в противном случае.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Генерирует JWT токен для заданного пользователя.
     * @param userDetails Информация о пользователе.
     * @return Сгенерированный JWT токен.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Добавляем роли пользователя в claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Генерирует JWT токен для заданного пользователя с указанным ID ресторана.
     * @param userDetails Информация о пользователе.
     * @param eateryId ID активного ресторана.
     * @return Сгенерированный JWT токен.
     */
    public String generateToken(UserDetails userDetails, Long eateryId) {
        Map<String, Object> claims = new HashMap<>();
        // Добавляем роли пользователя в claims
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        // Добавляем ID активного ресторана в claims
        if (eateryId != null) {
            claims.put("eateryId", eateryId);
        }
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Создает JWT токен.
     * @param claims Утверждения (claims) для включения в токен.
     * @param subject Субъект токена (обычно имя пользователя).
     * @return Созданный JWT токен.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Валидирует JWT токен.
     * @param token JWT токен.
     * @param userDetails Информация о пользователе.
     * @return true, если токен действителен для данного пользователя и не истек, false в противном случае.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
