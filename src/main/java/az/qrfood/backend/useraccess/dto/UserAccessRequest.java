package az.qrfood.backend.useraccess.dto;

import az.qrfood.backend.user.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating a UserAccess entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessRequest {

    /**
     * The ID of the user.
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * The ID of the eatery.
     */
    @NotNull(message = "Eatery ID is required")
    private Long eateryId;

    /**
     * The role to assign to the user for the eatery.
     */
    @NotNull(message = "Role is required")
    private Role role;
}