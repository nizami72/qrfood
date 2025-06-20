package az.qrfood.backend.eatery.controller;

import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Log4j2
@RestController
@Tag(name = "Eatery Management", description = "API endpoints for managing eateries")
public class EateryController {

    private final EateryService eateryService;

    public EateryController(EateryService eateryService) {
        this.eateryService = eateryService;
    }

    /**
     * GET all eateries.
     *
     * @return list of eatery
     */
    @Operation(summary = "Get all eateries", description = "Retrieves a list of all registered eateries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of eateries"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${eatery}")
    public ResponseEntity<List<EateryDto>> getAllRestaurants() {
        return ResponseEntity.ok(eateryService.getAllRestaurants());
    }

    /**
     * GET all eateries owned by a specific user.
     *
     * @param userProfileId the ID of the eatery-responsible person
     * @return list of eateries owned by the specified user
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
     * GET eatery by id.
     *
     * @param id eatery ID
     * @return the eatery with the specified ID
     */
    @Operation(summary = "Get eatery by ID", description = "Retrieves a specific eatery by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the eatery"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${eatery.id}")
    public ResponseEntity<EateryDto> getEateryById(@PathVariable("id") Long id) {
        log.debug("Request to get Eatery : {}", id);
        return ResponseEntity.ok(eateryService.getEateryById(id));
    }

    /**
     * POST a new eatery.
     *
     * @param eateryDto created eatery data
     * @return ID of created eatery
     */
    @Operation(summary = "Create a new eatery", description = "Creates a new eatery with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eatery created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PostMapping(value="${eatery}", consumes = "application/json")
    public ResponseEntity<Long> createRestaurant(@RequestBody EateryDto eateryDto) {
        log.debug("Request to create eatery [{}]", eateryDto);
        return ResponseEntity.ok(eateryService.createEatery(eateryDto));
    }

    /**
     * DELETE the eatery by ID.
     *
     * @param id deleted eatery ID
     * @return ID of the deleted eatery
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
     * UPDATE an existing eatery.
     *
     * @param id        The ID of the eatery to update
     * @param eateryDTO The updated eatery data
     * @return The ID of the updated eatery
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
