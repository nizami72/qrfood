package az.qrfood.backend.tableassignment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for creating a new table assignment.
 * <p>
 * This class is used to transfer the data required to create a new table assignment
 * from the client to the server.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTableAssignmentDto {

    /**
     * The ID of the waiter to whom the table will be assigned.
     * This field is required.
     */
    @NotNull(message = "Waiter ID is required")
    private Long waiterId;

    /**
     * The ID of the table that will be assigned to the waiter.
     * This field is required.
     */
    @NotNull(message = "Table ID is required")
    private Long tableId;
}