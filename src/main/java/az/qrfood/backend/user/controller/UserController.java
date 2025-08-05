package az.qrfood.backend.user.controller;

import az.qrfood.backend.user.dto.GeneralResponse;
import az.qrfood.backend.user.dto.OnUpdate;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for managing User entities.
 */
@RestController
@Log4j2
@Tag(name = "User Management", description = "API endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users from all eateries.
     *
     * @return list of user responses
     */
    @Operation(summary = "Get all users from all eateries", description = "Retrieves a list of all users across all eateries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${api.user}")
    @PreAuthorize("@authz.hasAnyRole(authentication)")
    // [[getAllUsersFromAllEateries]]
    public ResponseEntity<List<UserResponse>> getAllUsersFromAllEateries() {
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all users.
     *
     * @return list of user responses
     */
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication)")
    @GetMapping("${users}")
    // [[getAllUsers]]
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers();
        return ResponseEntity.ok(responses);
    }


    /**
     * Get all users of the eatery.
     *
     * @return list of user responses
     */
    @Operation(summary = "Get all users of an eatery", description = "Retrieves a list of all users for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of eatery users"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${usr}")
    public ResponseEntity<List<UserResponse>> getAllEateryUsers(@PathVariable Long eateryId) {
        List<UserResponse> responses = userService.getAllUsers(eateryId);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get a user by ID.
     *
     * @param userId the user ID
     * @return the user response
     */
    @Operation(summary = "Get a user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${user.id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a user by username.
     *
     * @param userName the userName
     * @return the user response
     */
    @Operation(summary = "Get a user by username", description = "Retrieves a specific user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${user.n}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String userName) {
        UserResponse response = userService.getUserByUsername(userName);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT a user.
     *
     * @param userId  the user ID
     * @param request the user request
     * @return the updated user response
     */
    @Operation(summary = "Update an existing user", description = "Updates a user with the specified ID using the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("${user.id}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<UserResponse> putUser(
            @PathVariable Long userId,
            @Validated(OnUpdate.class) @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a user.
     *
     * @param userId the user ID
     * @return no content response
     */
    @Operation(summary = "Delete a user", description = "Deletes a user with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("${user.id}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long eateryId, @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }


    /**
     * POST new not admin user and assigns it to an already existing eatery.
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
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PostMapping("${user.general}")
    public ResponseEntity<?> registerEateryStaff(@RequestBody RegisterRequest registerRequest, @PathVariable Long eateryId) {
        return userService.registerEateryStaff(registerRequest, eateryId);
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
    @PostMapping("/api/admin/eatery")
    public ResponseEntity<?> postEateryAdminUser(@RequestBody RegisterRequest registerRequest) {
        return userService.registerAdminAndEatery(registerRequest);
    }

    /**
     * POST to delete a user by ID.
     *
     * @param id the user ID to delete
     * @return no content response
     */
    @Operation(summary = "Delete a user by ID using POST", description = "Deletes a user with the specified ID using POST method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("${usr.delete}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'SUPER_ADMIN')")
    public ResponseEntity<GeneralResponse> deleteUserByName(@PathVariable String id) {
        GeneralResponse g = userService.deleteEateryAdminWithResources(id);
        log.debug("Deleted user with id [{}]", id);
        return ResponseEntity.ok(g);
    }


}
