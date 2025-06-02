package az.qrfood.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for successful authentication response.
 * Contains JWT token and user userId which the client will use for further requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String jwt;
    private Long userId;

    // Constructor with just JWT for backward compatibility
    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }
}
