package az.qrfood.backend.common.config;

import az.qrfood.backend.common.Util;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import java.util.List;
import java.util.Locale;

/**
 * Main Spring configuration class for the QR Food Order backend application.
 * <p>
 * This class defines various beans and configurations, including OpenAPI documentation,
 * password encoding, locale resolution, CORS settings, and a startup runner for
 * creating necessary folders.
 * </p>
 */
@OpenAPIDefinition(
        info = @Info(
                title = "QR Order API",
                version = "1.0",
                description = "Contactless restaurant ordering system via QR code", // Translated from Russian
                contact = @Contact(
                        name = "QR Food Support",
                        email = "support@qrfood.az",
                        url = "https://qrfood.az/support"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT authentication description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@org.springframework.context.annotation.Configuration
public class Configuration {

    @Value("${folder.root.images.eatery}")
    private String imagesFolder;

    @Value("${base.url}")
    private String baseUrl;

    /**
     * Configures and provides a custom OpenAPI bean.
     * This sets up the server URL for the Swagger UI.
     *
     * @return A custom {@link OpenAPI} instance.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(baseUrl);
        server.setDescription("Server URL");

        return new OpenAPI()
                .addServersItem(server);
    }

    /**
     * Provides a {@link PasswordEncoder} bean using BCrypt hashing algorithm.
     *
     * @return An instance of {@link BCryptPasswordEncoder}.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the {@link LocaleResolver} to use the Accept-Language header.
     * Sets the default locale to Azerbaijani ("az").
     *
     * @return An instance of {@link AcceptHeaderLocaleResolver}.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("az"));
        return resolver;
    }

    /**
     * Provides a {@link CommandLineRunner} bean that executes on application startup.
     * This runner ensures that the necessary image folders are created if they don't exist.
     *
     * @return A {@link CommandLineRunner} instance.
     */
    @Bean
    public CommandLineRunner runAtStartup() {
        return args -> {
            Util.createFolderIfNotExists(imagesFolder);
        };
    }

    /**
     * Configures and provides a {@link CorsConfigurationSource} bean for Cross-Origin Resource Sharing.
     * This allows specific origins, HTTP methods, and headers for API requests.
     *
     * @return An instance of {@link UrlBasedCorsConfigurationSource}.
     */
    @Bean
    @Qualifier("cors")
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://192.168.1.76:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // Must be false when using "*" for allowed origins

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
