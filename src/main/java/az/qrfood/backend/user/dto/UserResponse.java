package az.qrfood.backend.user.dto;

import az.qrfood.backend.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for returning User data to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * The ID of the user.
     */
    private Long id;

    /**
     * The username of the user that is unique mail.
     */
    private String username;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The roles assigned to the user.
     */
    private Set<String> roles;

    /**
     * Flag indicating if the user has a profile.
     */
    private boolean hasProfile;
}