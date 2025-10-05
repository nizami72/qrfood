package az.qrfood.backend.kitchendepartment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing a Kitchen Department.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KitchenDepartmentDto {
    /**
     * The unique identifier for the department.
     */
    private Long id;

    /**
     * The name of the department (e.g., "Hot Kitchen", "Bar").
     */
    private String name;
}