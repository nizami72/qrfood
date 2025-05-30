package az.qrfood.backend.user.controller;

import az.qrfood.backend.user.User;
import az.qrfood.backend.user.dto.LoginRequest;
import az.qrfood.backend.user.dto.LoginResponse;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.profile.UserProfile;
import az.qrfood.backend.user.profile.UserProfileService;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.CustomUserDetailsService;
import az.qrfood.backend.user.util.JwtUtil;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.eatery.dto.EateryDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * REST controller for handling requests related to authentication and registration.
 */
@RestController
@RequestMapping("/api/auth") // Базовый путь для всех эндпоинтов в этом контроллере
@Log4j2
public class AuthController {

    //<editor-fold desc="Fields">
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EateryService eateryService;
    private final UserProfileService userProfileService;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          EateryService eateryService,
                          UserProfileService userProfileService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eateryService = eateryService;
        this.userProfileService = userProfileService;
    }
    //</editor-fold>

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

        // Record login in user profile
        userRepository.findByUsername(loginRequest.getUsername()).ifPresent(user -> {
            userProfileService.recordLogin(user);
        });

        log.debug("Generating JWT Token");
        final String jwt = jwtUtil.generateToken(userDetails);

        log.debug("Return token");
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    /**
     * Endpoint for registering a new user with a restaurant.
     *
     * @param registerRequest RegisterRequest object containing user and restaurant data.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Extract user information from the DTO
        RegisterRequest.UserDto userDto = registerRequest.getUser();

        // Create a new User entity
        User user = new User();
        user.setUsername(userDto.getEmail()); // Using email as username

        // Check if a user with the same username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Пользователь с таким email уже существует!"));
        }

        // Hash the password before saving to the database
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Set the default role for the new user (ROLE_ADMIN for restaurant owners)
        user.setRoles(new HashSet<>(Collections.singletonList("ROLE_ADMIN")));

        // Save the user to the database
        user = userRepository.save(user);

        // Create a user profile for the user
        List<String> phones = new ArrayList<>();
        // If there are phone numbers in the request, add them to the profile
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            // Using name field temporarily for phone until UI is updated
            phones.add(userDto.getName());
        }
        UserProfile userProfile = userProfileService.createUserProfile(user, phones);

        // Extract restaurant information from the DTO
        RegisterRequest.RestaurantDto restaurantDto = registerRequest.getRestaurant();

        // Create a new EateryDto object
        EateryDto eateryDto = new EateryDto();
        eateryDto.setName(restaurantDto.getName());
        eateryDto.setTablesAmount(1); // Default to 1 table
        eateryDto.setOwnerProfileId(userProfile.getId()); // Set the owner profile ID

        // Save the restaurant to the database
        Long eateryId = eateryService.createEatery(eateryDto);

        // Add the restaurant ID to the user profile
        userProfileService.addRestaurantToProfile(userProfile, eateryId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Пользователь и ресторан успешно зарегистрированы!"));
    }


    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.status(200).body(Map.of("message", "ok"));
    }
}
