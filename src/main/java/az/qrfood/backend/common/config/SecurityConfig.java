package az.qrfood.backend.common.config;

import az.qrfood.backend.common.CustomAuthenticationEntryPoint;
import az.qrfood.backend.user.filter.EateryIdCheckFilter;
import az.qrfood.backend.user.filter.JwtRequestFilter;
import az.qrfood.backend.user.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;

    private final CustomAuthenticationEntryPoint customEntryPoint;

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final EateryIdCheckFilter eateryIdCheckFilter;
    private final CorsConfigurationSource corsConfig;

    @Value("${segment.api.client.all}")
    String segmentApiClientAll;

    /**
     * Определяет менеджер аутентификации.
     * Этот бин необходим для ручной аутентификации пользователя, например, в AuthController.
     *
     * @param config Конфигурация аутентификации, предоставляемая Spring Security.
     * @return Экземпляр AuthenticationManager.
     * @throws Exception при ошибке получения менеджера аутентификации.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    public SecurityConfig(
            PasswordEncoder passwordEncoder,
            CustomAuthenticationEntryPoint customEntryPoint,
            CustomUserDetailsService userDetailsService,
            JwtRequestFilter jwtRequestFilter,
            EateryIdCheckFilter eateryIdCheckFilter,
            @Qualifier("cors") CorsConfigurationSource corsConfig) {
        this.passwordEncoder = passwordEncoder;
        this.customEntryPoint = customEntryPoint;
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.eateryIdCheckFilter = eateryIdCheckFilter;
        this.corsConfig = corsConfig;
    }

    /**
     * Определяет цепочку фильтров безопасности для HTTP-запросов.
     * Здесь настраиваются правила авторизации для различных URL-путей.
     *
     * @param http Объект HttpSecurity для настройки безопасности.
     * @return Цепочка фильтров безопасности.
     * @throws Exception при ошибке конфигурации.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF для REST API, так как используем JWT
                .authorizeHttpRequests(authorize -> authorize
                        // =======================================================================    PERMIT ALL SECTION
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/qrcode/**").permitAll()
                        .requestMatchers("/image/**").permitAll()
                        .requestMatchers("/api/eateries/*/categories").permitAll()
                        .requestMatchers("/api/eateries/*").permitAll()
                        .requestMatchers("/api/eatery/*").permitAll()
                        .requestMatchers("/api/config/image-paths").permitAll()
                        .requestMatchers("/api/orders/*").permitAll()
                        .requestMatchers("/api/orders/status/*").permitAll()
                        .requestMatchers("/api/client/eatery/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tables/*").permitAll()
                        // ============================================================================    ADMIN SECTION
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // ===================================================================    USER AND ADMIN SECTION
                        // Требуем роль "USER" или "ADMIN" для доступа к /api/user/**
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/tables/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/eatery/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/order-items/**").hasAnyRole("USER", "ADMIN")
                        // ==============================================================    ALL OTHERS NEED TO HAVE JWT
                        // Все остальные запросы требуют аутентификации (наличия валидного JWT)
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfig))
                .sessionManagement(session -> session
                        // Устанавливаем политику создания сессий как STATELESS (без сохранения состояния)
                        // Это критично для JWT, так как токен содержит всю необходимую информацию.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Добавляем наш JWT-фильтр перед стандартным фильтром аутентификации по имени пользователя/паролю
        // Это гарантирует, что JWT будет проверен перед тем, как Spring Security будет принимать решения об авторизации.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Добавляем фильтр проверки eateryId после JWT-фильтра
        // Это гарантирует, что проверка eateryId будет выполнена после аутентификации пользователя
        http.addFilterAfter(eateryIdCheckFilter, JwtRequestFilter.class);

        return http.build();
    }

    /**
     * Определяет провайдер аутентификации.
     *
     * @return Экземпляр AuthenticationProvider.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
