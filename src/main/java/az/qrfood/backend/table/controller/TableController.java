package az.qrfood.backend.table.controller;

import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Log4j2
@Tag(name = "Table Management", description = "API endpoints for managing tables in eateries")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    /**
     * GET all tables for a specific eatery
     */
    @Operation(summary = "Get all tables for an eatery", description = "Retrieves a list of all tables for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tables"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${table}")
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    public ResponseEntity<List<TableDto>> getTables(@PathVariable Long eateryId) {
        return ResponseEntity.ok(tableService.listTablesForEatery(eateryId));
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
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${table.id}")
    public ResponseEntity<TableDto> getTable(@PathVariable Long eateryId) {
        return tableService.findById(eateryId)
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
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PostMapping("${table}")
    public ResponseEntity<TableDto> createTable(@RequestBody TableDto tableDto) {
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
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PutMapping("${table.id}")
    public ResponseEntity<TableDto> updateTable(@PathVariable Long tableId, @RequestBody TableDto tableDto) {
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
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @DeleteMapping("${table.id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long tableId) {
        try {
            tableService.deleteTable(tableId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
