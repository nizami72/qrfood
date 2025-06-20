package az.qrfood.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for successful authentication response.
 * Contains JWT token, user userId, and active eatery ID which the client will use for further requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String jwt;
    private Long userId;
    private Long eateryId;

    // Constructor with just JWT for backward compatibility
    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }

    // Constructor with JWT and userId for backward compatibility
    public LoginResponse(String jwt, Long userId) {
        this.jwt = jwt;
        this.userId = userId;
    }
}
