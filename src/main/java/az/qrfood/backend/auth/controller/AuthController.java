package az.qrfood.backend.auth.controller;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.auth.dto.LoginResponse;
import az.qrfood.backend.auth.service.CustomUserDetailsService;
import az.qrfood.backend.auth.util.JwtUtil;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for handling user authentication and registration.
 * <p>
 * This controller provides endpoints for user login, checking authentication status,
 * refreshing JWT tokens, and logging out. It interacts with Spring Security's
 * authentication mechanisms and JWT utilities.
 * </p>
 */
@RestController
@Log4j2
@Tag(name = "Auth", description = "API endpoints for managing auth calls")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    /**
     * Constructs the AuthController with necessary dependencies.
     *
     * @param authenticationManager The Spring Security AuthenticationManager for user authentication.
     * @param userDetailsService    The custom user details service for loading user information.
     * @param jwtUtil               The utility for generating and validating JWT tokens.
     * @param userRepository        The repository for accessing user data.
     * @param userProfileService    The service for managing user profiles.
     */
    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          UserProfileService userProfileService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
    }

    /**
     * Endpoint for user login.
     * <p>
     * Accepts a {@link LoginRequest} containing username (email) and password.
     * Authenticates the user and, upon successful authentication, generates a JWT token.
     * It also handles associating the user with an eatery if applicable.
     * </p>
     *
     * @param loginRequest The {@link LoginRequest} object containing user credentials and optional eatery ID.
     * @return A {@link ResponseEntity} containing a {@link LoginResponse} with the JWT token,
     *         user ID, and eatery ID on success, or an error message with {@code HttpStatus.UNAUTHORIZED}
     *         if authentication fails.
     */
    @PostMapping("${auth.login}")
    @Operation(summary = "Logins a user", description = "Logins user, use email as login and password")
    // [[login]]
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
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

        // Get user ID and record login in the user profile
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

                // If eateryId is not provided in the request, but a user has restaurants, use the first one as default
                if (eateryId == null && userProfile.getRestaurantIds() != null && !userProfile.getRestaurantIds().isEmpty()) {
                    eateryId = userProfile.getRestaurantIds().getFirst();
                }

                // Verify that the user has access to the specified eatery
                if (eateryId != null && (userProfile.getRestaurantIds() == null || !userProfile.getRestaurantIds().contains(eateryId))) {
                    log.debug("User does not have access to the specified eatery: {}", eateryId);
                    eateryId = null; // Reset eateryId if the user doesn't have access
                }
            }
        }

        log.debug("Generating JWT Token with eateryId: {}", eateryId);
        final String jwt = jwtUtil.generateToken(userDetails, eateryId);

        log.debug("Return token, user ID, and eatery ID");
        LoginResponse response = new LoginResponse(jwt, userId, eateryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to check if a user is logged in and return their information.
     * <p>
     * This method retrieves the current authentication from the security context
     * and, if the user is authenticated, returns a map containing various user details
     * such as username, user ID, profile ID, roles, and associated restaurant IDs.
     * </p>
     *
     * @return A {@link ResponseEntity} with user information if authenticated,
     *         or a message indicating that the user is not authenticated.
     */
    @GetMapping("${auth.status}")
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
     * Endpoint to regenerate a JWT token with a potentially new eatery ID.
     * <p>
     * This is typically used when the user changes their selected eatery in the frontend,
     * requiring a new token that reflects access to the new eatery.
     * </p>
     *
     * @param requestBody A {@link Map} containing the "eateryId" key with the ID of the newly selected eatery.
     * @return A {@link ResponseEntity} containing a {@link LoginResponse} with the new JWT token,
     *         user ID, and the updated eatery ID. Returns {@code HttpStatus.UNAUTHORIZED} if not authenticated,
     *         {@code HttpStatus.NOT_FOUND} if user or profile not found, or {@code HttpStatus.FORBIDDEN}
     *         if the user does not have access to the specified eatery.
     */
    @PostMapping("${auth.refresh}")
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
        final String jwt = jwtUtil.generateToken(userDetails, eateryId);

        // Return the new token
        log.debug("Return new token with eatery ID");
        LoginResponse response = new LoginResponse(jwt, userId, eateryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user logout.
     * <p>
     * In a JWT-based authentication system, the server typically doesn't maintain session state.
     * Therefore, this endpoint primarily serves to clear the security context on the server-side
     * and signals to the client that they should remove their JWT token from local storage.
     * </p>
     *
     * @return A {@link ResponseEntity} with a success message confirming the logout action.
     */
    @PostMapping("${auth.logout}")
    //[[logout]]
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
