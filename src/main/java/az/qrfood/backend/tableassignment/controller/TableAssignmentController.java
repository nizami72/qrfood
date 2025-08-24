package az.qrfood.backend.tableassignment.controller;

import az.qrfood.backend.tableassignment.dto.CreateTableAssignmentDto;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;
import az.qrfood.backend.tableassignment.service.TableAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing table assignments to waiters.
 * <p>
 * This controller provides endpoints for creating, retrieving, and deleting
 * table assignments between waiters and tables in a restaurant.
 * </p>
 */
@RestController
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Table Assignment Management", description = "API endpoints for managing table assignments to waiters")
public class TableAssignmentController {

    private final TableAssignmentService tableAssignmentService;

    /**
     * Creates a new table assignment.
     *
     * @param eateryId  The ID of the eatery.
     * @param createDto The DTO containing the data for the new table assignment.
     * @return The created table assignment.
     */
    @Operation(summary = "Create a new table assignment", description = "Assigns a table to a waiter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Table assignment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Waiter or table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("${table.assignment}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<TableAssignmentDto> createTableAssignment(
            @PathVariable Long eateryId,
            @Valid @RequestBody CreateTableAssignmentDto createDto) {
        try {
            TableAssignmentDto createdAssignment = tableAssignmentService.createTableAssignment(createDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAssignment);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all table assignments for a specific eatery.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of table assignments.
     */
    @Operation(summary = "Get all table assignments for an eatery", description = "Retrieves all table assignments for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of table assignments"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${table.assignment}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'CASHIER', 'WAITER')")
    public ResponseEntity<List<TableAssignmentDto>> getAllTableAssignments(@PathVariable Long eateryId) {
        try {
            List<TableAssignmentDto> assignments = tableAssignmentService.getAllTableAssignments(eateryId);
            return ResponseEntity.ok(assignments);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a table assignment by its ID.
     *
     * @param eateryId     The ID of the eatery.
     * @param assignmentId The ID of the table assignment.
     * @return The table assignment.
     */
    @Operation(summary = "Get a table assignment by ID", description = "Retrieves a specific table assignment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the table assignment"),
            @ApiResponse(responseCode = "404", description = "Table assignment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${table.assignment.id}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER')")
    public ResponseEntity<TableAssignmentDto> getTableAssignmentById(
            @PathVariable Long eateryId,
            @PathVariable Long assignmentId) {
        try {
            TableAssignmentDto assignment = tableAssignmentService.getTableAssignmentById(assignmentId);
            return ResponseEntity.ok(assignment);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all table assignments for a specific waiter.
     *
     * @param eateryId The ID of the eatery.
     * @param waiterId The ID of the waiter.
     * @return A list of table assignments.
     */
    @Operation(summary = "Get all table assignments for a waiter", description = "Retrieves all table assignments for the specified waiter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of table assignments"),
            @ApiResponse(responseCode = "404", description = "Waiter not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${table.assignment.waiter}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER')")
    public ResponseEntity<List<TableAssignmentDto>> getTableAssignmentsByWaiterId(
            @PathVariable Long eateryId,
            @PathVariable Long waiterId) {
        try {
            List<TableAssignmentDto> assignments = tableAssignmentService.getTableAssignmentsByWaiterId(waiterId);
            return ResponseEntity.ok(assignments);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all table assignments for a specific table.
     *
     * @param eateryId The ID of the eatery.
     * @param tableId  The ID of the table.
     * @return A list of table assignments.
     */
    @Operation(summary = "Get all table assignments for a table", description = "Retrieves all table assignments for the specified table")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of table assignments"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${table.assignment.table}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER')")
    public ResponseEntity<List<TableAssignmentDto>> getTableAssignmentsByTableId(
            @PathVariable Long eateryId,
            @PathVariable Long tableId) {
        try {
            List<TableAssignmentDto> assignments = tableAssignmentService.getTableAssignmentsByTableId(tableId);
            return ResponseEntity.ok(assignments);
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a table assignment by its ID.
     *
     * @param eateryId     The ID of the eatery.
     * @param assignmentId The ID of the table assignment.
     * @return No content if successful, not found if the table assignment doesn't exist.
     */
    @Operation(summary = "Delete a table assignment", description = "Deletes a specific table assignment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Table assignment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Table assignment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("${table.assignment.id}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<Void> deleteTableAssignment(
            @PathVariable Long eateryId,
            @PathVariable Long assignmentId) {
        try {
            tableAssignmentService.deleteTableAssignment(assignmentId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
