package az.qrfood.backend.user.controller;

import az.qrfood.backend.user.dto.RegisterRequest;
import az.qrfood.backend.user.dto.UserRequest;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Admin users.
 */
@RestController
@RequestMapping("${admin}")
@Log4j2
@Tag(name = "Admin Management", description = "API endpoints for managing admin users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new superuser, no eatery created and assigned here.
     *
     * @param request the user request
     * @return the created user response
     */
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping()
    public ResponseEntity<UserResponse> postAdmin(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * POST for registering a new user with a restaurant.
     *
     * @param registerRequest RegisterRequest object containing user and restaurant data.
     * @return ResponseEntity with a success or error message.
     */
    @Operation(summary = "Register a new user with a restaurant", description = "Registers a new user with the provided restaurant data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("${admin.eatery}")
    public ResponseEntity<?> postEateryAdminUser(@RequestBody RegisterRequest registerRequest) {
        return userService.createAdminUser(registerRequest);
    }

    /**
     * Deletion of the superuser is allowed to another superuser only.
     *
     * @param userId the user ID
     * @return no content response
     */
    @Operation(summary = "Delete a superuser", description = "Deletes a superuser with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("${user.id}")
    @PreAuthorize("@authz.hasAnyRole(authentication)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


}