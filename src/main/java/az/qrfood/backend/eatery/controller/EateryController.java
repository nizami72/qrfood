package az.qrfood.backend.eatery.controller;

import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.UserProfileService;
import az.qrfood.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for managing eatery-related operations.
 * <p>
 * This controller provides API endpoints for retrieving, creating, updating,
 * and deleting eatery information. It leverages Spring Security for access control
 * and integrates with Swagger for API documentation.
 * </p>
 */
@Log4j2
@RestController
@Tag(name = "Eatery Management", description = "API endpoints for managing eateries")
public class EateryController {

    private final EateryService eateryService;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;
    private final UserService userService;

    /**
     * Constructs an EateryController with necessary service and repository dependencies.
     *
     * @param eateryService      The service for handling eatery business logic.
     * @param userRepository     The repository for user data.
     * @param userProfileService The service for managing user profiles.
     * @param userService        The service for user-related operations.
     */
    public EateryController(EateryService eateryService, UserRepository userRepository,
                            UserProfileService userProfileService, UserService userService) {
        this.eateryService = eateryService;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
        this.userService = userService;
    }

    /**
     * Retrieves a list of all registered eateries.
     * This endpoint requires the 'EATERY_ADMIN' role.
     *
     * @return A {@link ResponseEntity} containing a list of {@link EateryDto} objects.
     */
    @Operation(summary = "Get all eateries", description = "Retrieves a list of all registered eateries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of eateries"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication)")
    @GetMapping("${eatery}")
    public ResponseEntity<List<EateryDto>> getAllRestaurants() {
        return ResponseEntity.ok(eateryService.getAllRestaurants());
    }

    /**
     * Retrieves all eateries owned by a specific user profile.
     * This endpoint requires the 'EATERY_ADMIN' role.
     *
     * @param userProfileId The ID of the user profile whose eateries are to be retrieved.
     * @return A {@link ResponseEntity} containing a list of {@link EateryDto} objects
     *         owned by the specified user.
     */
    @Operation(summary = "Get eateries by owner ID", description = "Retrieves all eateries owned by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of eateries for the owner"),
            @ApiResponse(responseCode = "404", description = "Owner not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${eatery.owner}")
    public ResponseEntity<List<EateryDto>> getEateriesByOwnerId(@PathVariable("ownerId") Long userProfileId) {
        log.debug("Request to get all eateries of owner with profile ID [{}]", userProfileId);
        return ResponseEntity.ok(eateryService.findEateriesByUserProfileId(userProfileId));
    }


    /**
     * Retrieves a specific eatery by its ID.
     *
     * @param id The ID of the eatery to retrieve.
     * @return A {@link ResponseEntity} containing the {@link EateryDto} of the found eatery.
     */
    @Operation(summary = "Get eatery by ID", description = "Retrieves a specific eatery by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the eatery"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${eatery.id}")
    public ResponseEntity<EateryDto> getEateryById(@PathVariable("eateryId") Long id) {
        log.debug("Request to get Eatery : {}", id);
        return ResponseEntity.ok(eateryService.getEateryById(id));
    }

    /**
     * Creates a new eatery with the provided data.
     * This endpoint requires the 'EATERY_ADMIN' role.
     *
     * @param eateryDto   The {@link EateryDto} containing the data for the new eatery.
     * @param userDetails The authenticated user's details, used to associate the eatery with the user profile.
     * @return A {@link ResponseEntity} containing the ID of the newly created eatery.
     */
    @Operation(summary = "Create a new eatery", description = "Creates a new eatery with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eatery created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PostMapping(value = "${eatery}", consumes = "application/json")
    public ResponseEntity<Long> createRestaurant(@RequestBody EateryDto eateryDto,
                                                 @AuthenticationPrincipal UserDetails userDetails
    ) {
        log.debug("Request to create eatery [{}]", eateryDto);
        Long eateryId = eateryService.createEatery(eateryDto);
        userProfileService.addRestaurantToProfile(userDetails, eateryId);
        return ResponseEntity.ok(eateryId);
    }

    /**
     * Deletes an eatery by its ID.
     * This endpoint requires 'EATERY_ADMIN' role.
     *
     * @param id The ID of the eatery to delete.
     * @return A {@link ResponseEntity} containing the ID of the deleted eatery.
     */
    @Operation(summary = "Delete an eatery", description = "Deletes an eatery with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eatery deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @DeleteMapping("${eatery.id}")
    public ResponseEntity<Long> deleteEatery(@PathVariable("eateryId") Long id) {
        return ResponseEntity.ok(eateryService.deleteEatery(id));
    }

    /**
     * Updates an existing eatery with new information.
     * This endpoint requires 'EATERY_ADMIN' role.
     *
     * @param id        The ID of the eatery to update.
     * @param eateryDTO The {@link EateryDto} containing the updated eatery data.
     * @return A {@link ResponseEntity} containing the ID of the updated eatery.
     */
    @Operation(summary = "Update an existing eatery", description = "Updates an eatery with the specified ID using the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eatery updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PutMapping(value = "${eatery.id}", consumes = "application/json")
    public ResponseEntity<Long> updateEatery(@PathVariable("eateryId") Long id, @RequestBody EateryDto eateryDTO) {
        log.debug("Request to update eatery with ID [{}]: {}", id, eateryDTO);
        return ResponseEntity.ok(eateryService.updateEatery(id, eateryDTO));
    }
}
