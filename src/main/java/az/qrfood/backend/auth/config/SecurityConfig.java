package az.qrfood.backend.auth.config;

import static org.springframework.http.HttpMethod.GET;

import az.qrfood.backend.common.CustomAuthenticationEntryPoint;
import az.qrfood.backend.auth.filter.EateryIdCheckFilter;
import az.qrfood.backend.auth.filter.JwtRequestFilter;
import az.qrfood.backend.auth.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security configuration for the QR Food Order backend application.
 * <p>
 * This class defines the security chain, authentication providers, and
 * authorization rules for various API endpoints. It configures JWT-based
 * authentication and role-based access control.
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enables Spring Security's pre/post annotations for method-level security
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final CustomAuthenticationEntryPoint customEntryPoint;
    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final EateryIdCheckFilter eateryIdCheckFilter;
    private final CorsConfigurationSource corsConfig;

    @Value("${api.user.register}")
    String apiUserRegister;
    @Value("${api.eatery}")
    String apiEateryArgCategoryArg;

    /**
     * Constructs the SecurityConfig with necessary dependencies.
     *
     * @param passwordEncoder    The password encoder for user authentication.
     * @param customEntryPoint   The custom authentication entry point for handling unauthorized access.
     * @param userDetailsService The custom user details service for loading user-specific data.
     * @param jwtRequestFilter   The JWT request filter for validating JWT tokens.
     * @param eateryIdCheckFilter The filter for checking eatery ID permissions.
     * @param corsConfig         The CORS configuration source.
     */
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
     * Defines the AuthenticationManager bean.
     * <p>
     * This bean is essential for manual user authentication, for example,
     * within an authentication controller.
     * </p>
     *
     * @param config The AuthenticationConfiguration provided by Spring Security.
     * @return An instance of {@link AuthenticationManager}.
     * @throws Exception if an error occurs while retrieving the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * This method sets up authorization rules for various URL paths,
     * disables CSRF for REST APIs (as JWT is used), configures CORS,
     * and sets the session creation policy to STATELESS for JWT-based authentication.
     * It also adds custom JWT and Eatery ID check filters to the chain.
     * </p>
     *
     * @param http The {@link HttpSecurity} object for configuring security.
     * @return The configured {@link SecurityFilterChain}.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for REST APIs, as JWT is used
                .authorizeHttpRequests(authorize -> authorize
                        // =======================================================================    PERMIT ALL SECTION
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/qrcode/**").permitAll()
                        .requestMatchers("/image/**").permitAll()
                        .requestMatchers("/api/config/image-paths").permitAll()
                        .requestMatchers("/api/orders/*").permitAll()
                        .requestMatchers("/api/orders/status/*").permitAll()
                        .requestMatchers("/api/client/eatery/**").permitAll()
                        .requestMatchers(GET, "/api/eatery/*").permitAll()
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/admin.html").permitAll()
                        .requestMatchers("/redoc.html").permitAll()
                        .requestMatchers("/index.html").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()

                        .requestMatchers(apiUserRegister).permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/table/**").permitAll()

                        .requestMatchers("/api/eatery/**").permitAll()
                        // ============================================================================    ADMIN SECTION
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // ===================================================================    USER AND ADMIN SECTION
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "EATERY_ADMIN")
                        .requestMatchers("/api/table/**").hasAnyRole("EATERY_ADMIN", "SUPER_ADMIN")
//                        .requestMatchers("/api/eatery/**").hasAnyRole("EATERY_ADMIN", "SUPER_ADMIN")
                        .requestMatchers("/api/order-items/**").hasAnyRole("USER", "ADMIN")
                        // ==============================================================    ALL OTHERS NEED TO HAVE JWT
                        // All other requests require authentication (presence of a valid JWT)
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(corsConfig))
                .sessionManagement(session -> session
                        // Set session creation policy to STATELESS (no session state)
                        // This is critical for JWT, as the token contains all necessary information.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Add our JWT filter before the standard UsernamePasswordAuthenticationFilter
        // This ensures that the JWT is validated before Spring Security makes authorization decisions.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Add the eateryIdCheckFilter after the JwtRequestFilter
        // This ensures that the eateryId check is performed after user authentication
        http.addFilterAfter(eateryIdCheckFilter, JwtRequestFilter.class);

        return http.build();
    }

    /**
     * Defines the AuthenticationProvider bean.
     * <p>
     * This provider uses a {@link DaoAuthenticationProvider} to authenticate users
     * against the {@link CustomUserDetailsService} and uses the configured
     * {@link PasswordEncoder} for password verification.
     * </p>
     *
     * @return An instance of {@link AuthenticationProvider}.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

}
