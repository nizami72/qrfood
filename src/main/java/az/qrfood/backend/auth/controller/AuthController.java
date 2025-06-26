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
 * REST controller for handling requests related to authentication and registration.
 */
@RestController
@Log4j2
@Tag(name = "Auth", description = "API endpoints for managing auth calls")
public class AuthController {

    //<editor-fold desc="Fields">
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    //</editor-fold>

    //<editor-fold desc="Constructor">
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
    //</editor-fold>

    /**
     * Endpoint for user login.
     * Accepts username and password, authenticates them and returns JWT token.
     *
     * @param loginRequest LoginRequest object containing username, password and optionally eateryId.
     * @return ResponseEntity with JWT token on a success or error message.
     */
    @PostMapping("${auth.login}")
    @Operation(summary = "Logins a user", description = "Logins user, use email as login and password")
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

                // If eateryId is not provided in the request, but the user has restaurants,
                // use the first one as the default
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
        final String jwt = eateryId != null ? jwtUtil.generateToken(userDetails, eateryId) : jwtUtil.generateToken(userDetails);

        log.debug("Return token, user ID, and eatery ID");
        LoginResponse response = new LoginResponse(jwt, userId, eateryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to check if a user is logged in and return user information.
     * 
     * @return ResponseEntity with user information if authenticated, or a message if not.
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
     * Endpoint to regenerate JWT token with a new eatery ID.
     * This is used when the user changes the selected eatery in the frontend.
     *
     * @param requestBody Map containing the eateryId key with the ID of the newly selected eatery.
     * @return ResponseEntity with the new JWT token.
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
        final String jwt = eateryId != null ? jwtUtil.generateToken(userDetails, eateryId) : jwtUtil.generateToken(userDetails);

        // Return the new token
        log.debug("Return new token with eatery ID");
        LoginResponse response = new LoginResponse(jwt, userId, eateryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for user logout.
     * In a JWT-based authentication system, the server doesn't maintain the session state.
     * The client is responsible for removing the JWT token from storage.
     * This endpoint simply returns a success response to confirm the logout action.
     *
     * @return ResponseEntity with a success message.
     */
    @PostMapping("${auth.logout}")
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
