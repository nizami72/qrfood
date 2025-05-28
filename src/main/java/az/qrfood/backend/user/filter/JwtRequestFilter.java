package az.qrfood.backend.user.filter;

import az.qrfood.backend.user.service.CustomUserDetailsService;
import az.qrfood.backend.user.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для обработки JWT токенов в каждом запросе.
 * Проверяет наличие и валидность JWT в заголовке Authorization.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Конструктор для внедрения зависимостей.
     * @param userDetailsService Пользовательский сервис для загрузки данных пользователя.
     * @param jwtUtil Утилита для работы с JWT.
     */
    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Выполняет фильтрацию запроса.
     * Извлекает JWT, валидирует его и устанавливает аутентификацию в SecurityContext.
     * @param request HTTP-запрос.
     * @param response HTTP-ответ.
     * @param filterChain Цепочка фильтров.
     * @throws ServletException при ошибке сервлета.
     * @throws IOException при ошибке ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Проверяем наличие заголовка Authorization и его формат (должен начинаться с "Bearer ")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Извлекаем сам токен
            username = jwtUtil.extractUsername(jwt); // Извлекаем имя пользователя из токена
        }

        // Если имя пользователя извлечено и текущий SecurityContext не содержит аутентификации
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Валидируем токен
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Если токен валиден, создаем объект аутентификации
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Устанавливаем детали аутентификации из HTTP-запроса
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Устанавливаем объект аутентификации в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Передаем запрос дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
}