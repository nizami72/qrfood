package az.qrfood.backend.tableassignment.service;

import az.qrfood.backend.tableassignment.dto.CreateTableAssignmentDto;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;

import java.util.List;

/**
 * Service interface for table assignment operations.
 * <p>
 * This interface defines the operations that can be performed on table assignments,
 * such as creating, retrieving, and deleting assignments.
 * </p>
 */
public interface TableAssignmentService {

    /**
     * Creates a new table assignment.
     *
     * @param createDto The DTO containing the data for the new table assignment.
     * @return The DTO representing the created table assignment.
     */
    TableAssignmentDto createTableAssignment(CreateTableAssignmentDto createDto);

    /**
     * Retrieves a table assignment by its ID.
     *
     * @param id The ID of the table assignment to retrieve.
     * @return The DTO representing the retrieved table assignment.
     */
    TableAssignmentDto getTableAssignmentById(Long id);

    /**
     * Retrieves all table assignments for a specific waiter.
     *
     * @param waiterId The ID of the waiter for whom to retrieve table assignments.
     * @return A list of DTOs representing the retrieved table assignments.
     */
    List<TableAssignmentDto> getTableAssignmentsByWaiterId(Long waiterId);

    /**
     * Retrieves all table assignments for a specific table.
     *
     * @param tableId The ID of the table for which to retrieve assignments.
     * @return A list of DTOs representing the retrieved table assignments.
     */
    List<TableAssignmentDto> getTableAssignmentsByTableId(Long tableId);

    /**
     * Deletes a table assignment by its ID.
     *
     * @param id The ID of the table assignment to delete.
     */
    void deleteTableAssignment(Long id);
}