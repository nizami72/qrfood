package az.qrfood.backend.useraccess.dto;

import az.qrfood.backend.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for returning UserAccess data to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessResponse {

    /**
     * The ID of the user access record.
     */
    private Long id;

    /**
     * The ID of the user.
     */
    private Long userId;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The ID of the eatery.
     */
    private Long eateryId;

    /**
     * The name of the eatery.
     */
    private String eateryName;

    /**
     * The role assigned to the user for the eatery.
     */
    private Role role;
}