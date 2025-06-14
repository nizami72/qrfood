package az.qrfood.backend;

import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.HashSet;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		String u = "admin@qrfood.az";
		return args -> {
			if (userRepository.findByUsername(u).isEmpty()) {
				User user = new User();
				user.setUsername(u);
				user.setPassword(passwordEncoder.encode("admin")); // Хешируем "password"
				user.setRoles(new HashSet<>(Collections.singletonList("ADMIN")));
				userRepository.save(user);
				System.out.println("Создан тестовый пользователь: user/password");
			}
		};
	}

}
