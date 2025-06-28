package az.qrfood.backend;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

/**
 * entry point for the QR Food Order backend application.
 * <p>
 * This class initializes the Spring Boot application and includes a
 * {@link CommandLineRunner} to set up initial data, such as creating a default
 * super admin user when the application starts for the first time.
 * </p>
 */
@SpringBootApplication
@Log4j2
public class App {

    private final UserService userService;

    /**
     * Constructs the App with a UserService dependency.
     *
     * @param userService The service for user-related operations.
     */
    public App(UserService userService) {
        this.userService = userService;
    }

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    /**
     * Creates a {@link CommandLineRunner} bean that initializes a default super admin user.
     * <p>
     * This runner checks if the default super admin user exists in the database.
     * If not, it creates a new user with the role {@code SUPER_ADMIN}. This is useful
     * for initial setup and ensuring the system has an administrator from the start.
     * </p>
     *
     * @param userRepository  The repository for accessing user data.
     * @param passwordEncoder The encoder for hashing passwords.
     * @return A {@link CommandLineRunner} instance.
     */
    @Bean
    public CommandLineRunner demoData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        String u = "nizami.budagov@gmail.com";
        return args -> {
            if (userRepository.findByUsername(u).isEmpty()) {
                userService.registerAdminAndEatery(
                        RegisterRequest.builder()
                                .restaurant(null)
                                .user(RegisterRequest.UserDto.builder()
                                        .email("nizami.budagov@gmail.com")
                                        .password("qqqq1111")
                                        .roles(Set.of(Role.fromString("SUPER_ADMIN")))
                                        .build())
                                .userProfileRequest(RegisterRequest.UserProfileRequest.builder()
                                        .name("Nizami Budagov")
                                        .phone("994 50 4679933")
                                        .build())
                                .build());
                log.debug("Super admin role created");
            }
        };
    }
}
