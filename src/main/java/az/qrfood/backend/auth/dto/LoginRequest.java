package az.qrfood.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    /**
     * The password of the user attempting to log in.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    /**
     * The ID of the eatery that the user is currently interacting with or wishes to select.
     * This is optional and can be used to scope the user's session to a specific restaurant.
     */
    private Long eateryId;

    public LoginRequest(Long eateryId) {
        this.eateryId = eateryId;
    }
}
