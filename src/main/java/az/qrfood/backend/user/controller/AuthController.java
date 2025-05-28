package az.qrfood.backend.user.controller;

import az.qrfood.backend.user.User;
import az.qrfood.backend.user.dto.LoginRequest;
import az.qrfood.backend.user.dto.LoginResponse;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.CustomUserDetailsService;
import az.qrfood.backend.user.util.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

/**
 * REST controller for handling requests related to authentication and registration.
 */
@RestController
@RequestMapping("/api/auth") // Базовый путь для всех эндпоинтов в этом контроллере
@Log4j2
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор для внедрения зависимостей.
     */
    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint for user login.
     * Accepts username and password, authenticates them and returns JWT token.
     *
     * @param loginRequest LoginRequest object containing username and password.
     * @return ResponseEntity with JWT token on success or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        try {
            log.debug("Attempting to authenticate user using AuthenticationManager");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.debug("If the credentials are incorrect, return a 401 Unauthorized error.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Неверное имя пользователя или пароль"));
        }

        log.debug("Attempting to authenticate user using CustomUserDetailsService");
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        log.debug("Generating  JWT Token");
        final String jwt = jwtUtil.generateToken(userDetails);

        log.debug("Return token");
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    /**
     * Endpoint for registering a new user.

     * @param user User object containing registration data (username, password).
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Проверяем, существует ли уже пользователь с таким именем
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Пользователь с таким именем уже существует!"));
        }

        // Хешируем пароль перед сохранением в базе данных
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Устанавливаем роль по умолчанию для нового пользователя
        user.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));
        userRepository.save(user); // Сохраняем пользователя

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Пользователь успешно зарегистрирован!"));
    }
}