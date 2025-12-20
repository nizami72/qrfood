package az.qrfood.backend.table.controller;

import az.qrfood.backend.constant.ApiRoutes;
import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.entity.TableStatus;
import az.qrfood.backend.table.service.TableService;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;
import az.qrfood.backend.tableassignment.service.TableAssignmentService;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import az.qrfood.backend.user.repository.UserRepository;
import az.qrfood.backend.user.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Log4j2
@Tag(name = "Table Management", description = "API endpoints for managing tables in eateries")
public class TableController {

    private final TableService tableService;
    private final TableAssignmentService tableAssignmentService;
    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    public TableController(TableService tableService, TableAssignmentService tableAssignmentService,
                          UserRepository userRepository, UserProfileService userProfileService) {
        this.tableService = tableService;
        this.tableAssignmentService = tableAssignmentService;
        this.userRepository = userRepository;
        this.userProfileService = userProfileService;
    }

    /**
     * GET all tables for a specific eatery.
     * If the user is a waiter, only returns the tables that this waiter is assigned to.
     */
    @Operation(summary = "Get all tables for an eatery", description = "Retrieves a list of all tables for the specified eatery. If the user is a waiter, only returns the tables that this waiter is assigned to.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tables"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(ApiRoutes.TABLE)
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication,#eateryId, 'EATERY_ADMIN', 'WAITER', 'KITCHEN_ADMIN', 'CASHIER')")
    public ResponseEntity<List<TableDto>> getTables(@PathVariable Long eateryId) {
        try {
            // Get the current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Check if the user has the WAITER role
            boolean isWaiter = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("WAITER"));

            if (isWaiter) {
                // Get the waiter's ID
                Optional<User> userOptional = userRepository.findByUsername(username);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    Optional<UserProfile> userProfileOptional = userProfileService.findProfileByUser(user);
                    if (userProfileOptional.isPresent()) {
                        UserProfile userProfile = userProfileOptional.get();
                        Long waiterId = userProfile.getId();

                        // Get all tables assigned to this waiter
                        List<TableAssignmentDto> assignments = tableAssignmentService.getTableAssignmentsByWaiterId(waiterId);

                        // Extract the table IDs from the assignments
                        List<Long> assignedTableIds = assignments.stream()
                                .map(TableAssignmentDto::getTableId)
                                .collect(Collectors.toList());

                        // Get all tables for the eatery
                        List<TableDto> allTables = tableService.listTablesForEatery(eateryId);

                        // Filter the tables to only include those assigned to the waiter
                        List<TableDto> assignedTables = allTables.stream()
                                .filter(table -> assignedTableIds.contains(table.id()))
                                .collect(Collectors.toList());

                        return ResponseEntity.ok(assignedTables);
                    }
                }
                // If we couldn't get the waiter's ID, return an empty list
                return ResponseEntity.ok(List.of());
            } else {
                // For non-waiter users, return all tables to the eatery
                return ResponseEntity.ok(tableService.listTablesForEatery(eateryId));
            }
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET a specific table by id
     */
    @Operation(summary = "Get a table by ID", description = "Retrieves a specific table by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the table"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication,#eateryId, 'EATERY_ADMIN', 'WAITER', 'KITCHEN_ADMIN', 'CASHIER')")
    @GetMapping(ApiRoutes.TABLE_BY_ID)
    public ResponseEntity<TableDto> getTable(@PathVariable Long tableId) {
        return tableService.findById(tableId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST a new table
     */
    @Operation(summary = "Create a new table", description = "Creates a new table with the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Table created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication,#eateryId, 'EATERY_ADMIN')")
    @PostMapping(ApiRoutes.TABLE)
    public ResponseEntity<TableDto> postTable(@PathVariable Long eateryId, @RequestBody TableDto tableDto) {
        try {
            TableDto createdTable = tableService.createTable(tableDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTable);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT updated information for a specific table
     */
    @Operation(summary = "Update an existing table", description = "Updates a table with the specified ID using the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Table updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication, #eateryId, 'EATERY_ADMIN')")
    @PutMapping(ApiRoutes.TABLE_BY_ID)
    public ResponseEntity<TableDto> putTable(@PathVariable Long eateryId, @PathVariable Long tableId, @RequestBody TableDto tableDto) {
        try {
            TableDto updatedTable = tableService.updateTable(tableId, tableDto);
            return ResponseEntity.ok(updatedTable);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE a specific table by id
     */
    @Operation(summary = "Delete a table", description = "Deletes a table with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Table deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRoleAndAccess(authentication,#eateryId, 'EATERY_ADMIN')")
    @DeleteMapping(ApiRoutes.TABLE_BY_ID)
    public ResponseEntity<Void> deleteTable(@PathVariable Long tableId) {
        try {
            tableService.updateTableStatus(tableId, TableStatus.ARCHIVED);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
