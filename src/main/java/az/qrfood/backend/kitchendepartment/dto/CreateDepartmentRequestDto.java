package az.qrfood.backend.kitchendepartment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing the request to create a new Kitchen Department.
 * It contains all the necessary information provided by the client.
 */
@Data
@NoArgsConstructor
public class CreateDepartmentRequestDto {
    /**
     * The desired name for the new department (e.g., "Hot Kitchen", "Bar").
     * This field is expected to be non-null and not empty.
     */
    @NotBlank
    private String name;

    /**
     * The unique identifier (ID) of the restaurant to which this department will belong.
     * This is used to establish the relationship in the database.
     */
    @NotNull
    private Long restaurantId;
}