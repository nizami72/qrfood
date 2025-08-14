package az.qrfood.backend.tableassignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for table assignment operations.
 * <p>
 * This class is used to transfer table assignment data between the client and the server.
 * It contains the essential information about a table assignment.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableAssignmentDto {

    /**
     * The unique identifier for the table assignment.
     */
    private Long id;

    /**
     * The ID of the waiter to whom the table is assigned.
     */
    private Long waiterId;

    /**
     * The ID of the table that is assigned to the waiter.
     */
    private Long tableId;

    /**
     * The timestamp when the assignment was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the assignment was last updated.
     */
    private LocalDateTime updatedAt;
}