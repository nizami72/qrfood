package az.qrfood.backend.kitchendepartment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an update request for a Kitchen Department.
 * Currently, supports updating the department name.
 */
@Data
@NoArgsConstructor
public class UpdateDepartmentRequestDto {
    @NotBlank
    private String name;
}
