package az.qrfood.backend.auth.controller;

import az.qrfood.backend.auth.dto.LoginRequest;
import az.qrfood.backend.auth.dto.LoginResponse;
import az.qrfood.backend.auth.dto.RecreateTokenOnEateryChangeRequest;
import az.qrfood.backend.auth.dto.TokenRefreshRequest;
import az.qrfood.backend.auth.dto.TokenRefreshResponse;
import az.qrfood.backend.auth.entity.RefreshToken;
import az.qrfood.backend.auth.exception.TokenRefreshException;
import az.qrfood.backend.auth.service.CustomUserDetailsService;
import az.qrfood.backend.auth.service.RefreshTokenService;
import az.qrfood.backend.auth.util.JwtUtil;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.response.ApiResponse;
import az.qrfood.backend.common.response.ResponseCodes;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final RefreshTokenService refreshTokenService;

    /**
     * Constructs the AuthController with necessary dependencies.
     *
     * @param authenticationManager The Spring Security AuthenticationManager for user authentication.
     * @param userDetailsService    The custom user details service for loading user information.
     * @param jwtUtil               The utility for generating and validating JWT tokens.
     * @param userRepository        The repository for accessing user data.
     * @param userProfileService    The service for managing user profiles.
     * @param refreshTokenService   The service for managing refresh tokens.
     */
    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          UserProfileService userProfileService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
        this.refreshTokenService = refreshTokenService;
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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
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
                if (eateryId == null && userProfile.getEateries() != null && !userProfile.getEateries().isEmpty()) {
                    eateryId = userProfile.getEateries().getFirst().getId();
                }

                // Verify that the user has access to the specified eatery
                if (eateryId != null) {
                    final Long finalEateryId = eateryId;
                    boolean hasAccess = userProfile.getEateries().stream()
                            .anyMatch(eatery -> eatery.getId().equals(finalEateryId));
                    if (!hasAccess) {
                        log.debug("User does not have access to the specified eatery: {}", eateryId);
                        eateryId = null; // Reset eateryId if the user doesn't have access
                    }
                }
            }
        }

        log.debug("Generating JWT Token with eateryId: {}", eateryId);
        final String jwt = jwtUtil.generateToken(userDetails, eateryId);

        // Generate refresh token
        log.debug("Generating refresh token for user");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userOptional.get());

        log.debug("Return token, refresh token, user ID, and eatery ID");
        LoginResponse response = new LoginResponse(jwt, refreshToken.getToken(), userId, eateryId);
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
                    // Extract eatery IDs from the eateries list
                    List<Long> restaurantIds = userProfile.getEateries().stream()
                            .map(Eatery::getId)
                            .collect(Collectors.toList());
                    userInfo.put("restaurantIds", restaurantIds);

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
     * @param request A {@link RecreateTokenOnEateryChangeRequest} containing the ID of the newly selected eatery.
     * @return A {@link ResponseEntity} containing a {@link LoginResponse} with the new JWT token,
     *         user ID, and the updated eatery ID. Returns {@code HttpStatus.UNAUTHORIZED} if not authenticated,
     *         {@code HttpStatus.NOT_FOUND} if user or profile not found, or {@code HttpStatus.FORBIDDEN}
     *         if the user does not have access to the specified eatery.
     */
    @PostMapping("${auth.refresh}")
//    [[recreateTokenOnEateryChange]]
    public ResponseEntity<?> recreateTokenOnEateryChange(@Valid @RequestBody RecreateTokenOnEateryChangeRequest request) {
        Long eateryId = request.getEateryId();
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
        if (eateryId != null) {
            final Long finalEateryId = eateryId;
            boolean hasAccess = userProfile.getEateries().stream()
                    .anyMatch(eatery -> eatery.getId().equals(finalEateryId));
            if (!hasAccess) {
                log.debug("User does not have access to the specified eatery: {}", eateryId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "User does not have access to the specified eatery"));
            }
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
     * Endpoint for refreshing an access token using a refresh token.
     * <p>
     * This endpoint accepts a refresh token and, if valid, generates a new access token.
     * </p>
     *
     * @param request A {@link TokenRefreshRequest} containing the refresh token.
     * @return A {@link ResponseEntity} containing a {@link TokenRefreshResponse} with the new access token
     *         and the refresh token. Returns {@code HttpStatus.FORBIDDEN} if the refresh token is invalid or expired.
     */
    @PostMapping("${auth.token.refresh}")
    public ResponseEntity<?> refreshToken(HttpServletRequest request1, @Valid @RequestBody TokenRefreshRequest request) {

        Long eateryId = null;

        String authorizationHeader = request1.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);

            // Extract eateryId from JWT token
            try {
                eateryId = jwtUtil.extractClaim(jwt, claims -> claims.get("eateryId", Long.class));
            } catch (Exception e) {
                log.error("Error extracting eateryId from JWT token", e);
                throw new TokenRefreshException(jwt, "Invalid JWT token");
            }
        }

        String requestRefreshToken = request.getRefreshToken();

        Long finalEateryId = eateryId;
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(refreshToken -> {
                    User user = refreshToken.getUser();

                    // Load user details
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

                    // Generate a new access token
                    String token = jwtUtil.generateToken(userDetails, finalEateryId);

                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken, finalEateryId));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
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
    @GetMapping("${auth.logout}")
    //[[logout]]
    public ResponseEntity<?> logout() {
        // Get the current authentication from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Log the logout attempt and delete refresh token
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            log.debug("User logged out: {}", username);

            // Get the user entity from the repository and delete their refresh token
            Optional<User> userOptional = userRepository.findByUsername(username);
            userOptional.ifPresent(refreshTokenService::deleteByUser);
        }

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Return success response
        return ResponseEntity.ok(Map.of("success", true, "message", "Logout successful"));
    }
}
