package az.qrfood.backend.user.controller;

import az.qrfood.backend.auth.service.CustomUserDetailsService;
import az.qrfood.backend.auth.util.JwtUtil;
import az.qrfood.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing Admin users.
 */
@RestController
@RequestMapping("${admin}")
@Log4j2
@Tag(name = "Admin Management", description = "API endpoints for managing admin users")
public class AdminController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AdminController(UserService userService, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Impersonate a user by generating a new JWT token with their rights.
     * <p>
     * This endpoint allows a SUPER_ADMIN to log in on behalf of another user.
     * It generates a new JWT token with the selected user's rights and an additional
     * "impersonatedBy" claim to track who initiated the impersonation.
     * </p>
     *
     * @param userId        The ID of the user to impersonate.
     * @param authentication The current user's authentication.
     * @return ResponseEntity with a JSON containing the new token and user ID.
     */
    @Operation(summary = "Impersonate a user", description = "Allows a SUPER_ADMIN to log in on behalf of another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Impersonation successful"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to impersonate"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/impersonate/{userId}")
    @PreAuthorize("@authz.hasAnyRole(authentication)")
    public ResponseEntity<?> impersonateUser(@PathVariable Long userId, Authentication authentication) {
        // Load the user to impersonate by ID
        UserDetails userToImpersonate = userDetailsService.loadUserById(userId);

        // Get the name of the user who is initiating the impersonation
        String impersonatedBy = authentication.getName();

        // Generate a new JWT token with the impersonated user's rights and the impersonatedBy claim
        String token = jwtUtil.generateImpersonationToken(userToImpersonate, impersonatedBy);

        // Log the impersonation action
        log.info("User {} impersonated user {}", impersonatedBy, userToImpersonate.getUsername());

        // Create the response with the new token and user ID
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", userId);

        return ResponseEntity.ok(response);
    }


}
