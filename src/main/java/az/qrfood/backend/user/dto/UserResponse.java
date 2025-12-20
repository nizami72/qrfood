package az.qrfood.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * DTO for returning User data to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    /**
     * The ID of the user.
     */
    private Long id;

    private List<Long> eateryIds;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean hasProfile;

    /**
     * The phone number of the user.
     */
    private List<String> phone;

    private String registered;

    private String lastLogin;

}
