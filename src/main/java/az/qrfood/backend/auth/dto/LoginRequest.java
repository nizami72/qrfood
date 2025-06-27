package az.qrfood.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user login requests.
 * <p>
 * This DTO encapsulates the credentials (email and password) provided by the user
 * during a login attempt, and optionally includes the ID of the active eatery
 * the user wishes to associate with the session.
 * </p>
 */
@Data // Lombok annotation for getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok annotation for a no-argument constructor
@AllArgsConstructor // Lombok annotation for a constructor with all arguments
public class LoginRequest {
    /**
     * The email address of the user attempting to log in.
     * This serves as the username for authentication.
     */
    private String email;

    /**
     * The password of the user attempting to log in.
     */
    private String password;

    /**
     * The ID of the eatery that the user is currently interacting with or wishes to select.
     * This is optional and can be used to scope the user's session to a specific restaurant.
     */
    private Long eateryId;
}
