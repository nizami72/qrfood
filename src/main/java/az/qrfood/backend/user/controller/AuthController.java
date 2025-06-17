package az.qrfood.backend.user.controller;

import az.qrfood.backend.common.response.ApiResponse;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.dto.LoginRequest;
import az.qrfood.backend.user.dto.LoginResponse;
import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.UserRegistrationRequest;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.service.UserProfileService;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.CustomUserDetailsService;
import az.qrfood.backend.user.util.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for handling requests related to authentication and registration.
 */
@RestController
@RequestMapping("/api/auth")
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
     * @param loginRequest LoginRequest object containing username, password, and optionally eateryId.
     * @return ResponseEntity with JWT token on success or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
        log.debug("Login request: {}", loginRequest);
        try {
            log.debug("Attempting to authenticate user using AuthenticationManager");
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.debug("If the credentials are incorrect, return a 401 Unauthorized error.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Password or user name invalid"));
        }

        log.debug("Attempting to authenticate user using CustomUserDetailsService");
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        // Get user ID and record login in user profile
        Long userId = null;
        Long eateryId = loginRequest.getEateryId();
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getEmail());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            userProfileService.recordLogin(user);

            // Get the user profile to get the ID
            Optional<UserProfile> userProfileOptional = userProfileService.findProfileByUser(user);
            if (userProfileOptional.isPresent()) {
                UserProfile userProfile = userProfileOptional.get();
                userId = userProfile.getId();

                // If eateryId is not provided in the request, but the user has restaurants,
                // use the first one as the default
                if (eateryId == null && userProfile.getRestaurantIds() != null && !userProfile.getRestaurantIds().isEmpty()) {
                    eateryId = userProfile.getRestaurantIds().get(0);
                }

                // Verify that the user has access to the specified eatery
                if (eateryId != null && (userProfile.getRestaurantIds() == null || !userProfile.getRestaurantIds().contains(eateryId))) {
                    log.debug("User does not have access to the specified eatery: {}", eateryId);
                    eateryId = null; // Reset eateryId if user doesn't have access
                }
            }
        }

        log.debug("Generating JWT Token with eateryId: {}", eateryId);
        final String jwt = eateryId != null ? jwtUtil.generateToken(userDetails, eateryId) : jwtUtil.generateToken(userDetails);

        log.debug("Return token, user ID, and eatery ID");
        LoginResponse response = new LoginResponse(jwt, userId, eateryId);
        return ResponseEntity.ok(response);
    }

    /**
     * POST for registering a new user with a restaurant.
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
        user.setUsername(userDto.getEmail());

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
        eateryDto.setNumberOfTables(1); // Default to 1 table
        eateryDto.setOwnerProfileId(userProfile.getId()); // Set the owner profile ID

        // Save the restaurant to the database
        Long eateryId = eateryService.createEatery(eateryDto);

        // Add the restaurant ID to the user profile
        userProfileService.addRestaurantToProfile(userProfile, eateryId);

        log.debug("User [{}] and eatery [{}] successfully created.",
                userProfile.getUser(),
                eateryId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok( "User and a eatery successfully created!", null));
    }


    /**
     * Endpoint to check if a user is logged in and return user information.
     * 
     * @return ResponseEntity with user information if authenticated, or a message if not.
     */
    @GetMapping("/status")
    public ResponseEntity<?> status() {
        // Get the current authentication from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {

            // Get the username from the authenticated user
            String username = authentication.getName();
            log.debug("User is authenticated: {}", username);

            // Get the user entity from the repository
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // Get the user profile
                Optional<UserProfile> userProfileOptional = userProfileService.findProfileByUser(user);
                if (userProfileOptional.isPresent()) {
                    UserProfile userProfile = userProfileOptional.get();

                    // Create a response with user information
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("authenticated", true);
                    userInfo.put("username", user.getUsername());
                    userInfo.put("userId", user.getId());
                    userInfo.put("profileId", userProfile.getId());
                    userInfo.put("roles", user.getRoles());
                    userInfo.put("phones", userProfile.getPhones());
                    userInfo.put("isActive", userProfile.getIsActive());
                    userInfo.put("lastLogin", userProfile.getLastLogin());
                    userInfo.put("restaurantIds", userProfile.getRestaurantIds());

                    return ResponseEntity.ok(userInfo);
                }
            }
        }

        // If not authenticated or user not found, return a message
        return ResponseEntity.ok(Map.of("authenticated", false, "message", "User not authenticated"));
    }

    /**
     * POST for registering a new user without creating a restaurant.
     * The user will be linked to an existing eatery.
     *
     * @param request UserRegistrationRequest object containing user data and eateryId.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/register-user")
    public ResponseEntity<?> registerUserOnly(@RequestBody UserRegistrationRequest request) {
        // Create a new User entity
        User user = new User();
        user.setUsername(request.getEmail());

        // Check if a user with the same username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "User with this email already exists!"));
        }

        // Hash the password before saving to the database
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set the default role for the new user (ROLE_ADMIN for restaurant staff)
        user.setRoles(new HashSet<>(Collections.singletonList("ROLE_ADMIN")));

        // Save the user to the database
        user = userRepository.save(user);

        // Create a user profile for the user
        List<String> phones = new ArrayList<>();
        // If there is a name in the request, add it to the profile
        if (request.getName() != null && !request.getName().isEmpty()) {
            phones.add(request.getName());
        }
        UserProfile userProfile = userProfileService.createUserProfile(user, phones);

        // Add the eatery ID to the user profile
        userProfileService.addRestaurantToProfile(userProfile, request.getEateryId());

        log.debug("User [{}] successfully created and linked to eatery [{}].",
                userProfile.getUser(),
                request.getEateryId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User successfully created and linked to eatery!", null));
    }

    /**
     * Endpoint to regenerate JWT token with a new eatery ID.
     * This is used when the user changes the selected eatery in the frontend.
     *
     * @param requestBody Map containing the eateryId key with the ID of the newly selected eatery.
     * @return ResponseEntity with the new JWT token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, Long> requestBody) {
        Long eateryId = requestBody.get("eateryId");
        log.debug("Refresh token request with eateryId: {}", eateryId);

        // Get the current authentication from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || 
            authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        // Get the username from the authenticated user
        String username = authentication.getName();
        log.debug("Refreshing token for user: {}", username);

        // Get the user entity from the repository
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }

        User user = userOptional.get();

        // Get the user profile
        Optional<UserProfile> userProfileOptional = userProfileService.findProfileByUser(user);
        if (userProfileOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User profile not found"));
        }

        UserProfile userProfile = userProfileOptional.get();
        Long userId = userProfile.getId();

        // Verify that the user has access to the specified eatery
        if (eateryId != null && (userProfile.getRestaurantIds() == null || !userProfile.getRestaurantIds().contains(eateryId))) {
            log.debug("User does not have access to the specified eatery: {}", eateryId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "User does not have access to the specified eatery"));
        }

        // Load user details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Generate a new JWT token with the eatery ID
        log.debug("Generating new JWT Token with eateryId: {}", eateryId);
        final String jwt = eateryId != null ? jwtUtil.generateToken(userDetails, eateryId) : jwtUtil.generateToken(userDetails);

        // Return the new token
        log.debug("Return new token with eatery ID");
        LoginResponse response = new LoginResponse(jwt, userId, eateryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user logout.
     * In a JWT-based authentication system, the server doesn't maintain session state.
     * The client is responsible for removing the JWT token from storage.
     * This endpoint simply returns a success response to confirm the logout action.
     *
     * @return ResponseEntity with success message.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Get the current authentication from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Log the logout attempt
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            log.debug("User logged out: {}", authentication.getName());
        }

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Return success response
        return ResponseEntity.ok(Map.of("success", true, "message", "Logout successful"));
    }
}
