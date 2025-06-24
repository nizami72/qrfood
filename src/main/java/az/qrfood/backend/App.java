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

@SpringBootApplication
@Log4j2
public class App {

    public App(UserService userService) {
        this.userService = userService;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final UserService userService;

    @Bean
    public CommandLineRunner demoData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        String u = "nizami.budagov@gmail.com";
        return args -> {
            if (userRepository.findByUsername(u).isEmpty()) {
                userService.createAdminUser(
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
