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
public class UserResponse {

    public UserResponse(Long id, String username, String name, Set<String> roles, boolean hasProfile) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.roles = roles;
        this.hasProfile = hasProfile;
    }

    public UserResponse(Long id, String username, String name, Set<String> roles, boolean hasProfile, String phone) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.roles = roles;
        this.hasProfile = hasProfile;
        this.phone = phone;
    }

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

    /**
     * The phone number of the user.
     */
    private String phone;
}
