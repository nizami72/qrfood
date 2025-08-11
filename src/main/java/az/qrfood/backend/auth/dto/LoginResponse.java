package az.qrfood.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for a successful authentication response.
 * <p>
 * This DTO carries the JWT access token, refresh token, the authenticated user's ID, and the ID
 * of the currently active eatery. The client will use this information for
 * subsequent authenticated requests and to manage the user's session context.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    /**
     * The JSON Web Token (JWT) issued upon successful authentication.
     * This token must be included in subsequent requests for authentication.
     */
    private String jwt;

    /**
     * The refresh token used to obtain new access tokens without re-authentication.
     */
    private String refreshToken;

    /**
     * The unique identifier of the authenticated user.
     */
    private Long userId;

    /**
     * The ID of the eatery that the user is currently associated with.
     * This can be null if no specific eatery is selected or applicable.
     */
    private Long eateryId;

    /**
     * Constructor for backward compatibility, initializing only the JWT.
     *
     * @param jwt The JSON Web Token.
     */
    public LoginResponse(String jwt) {
        this.jwt = jwt;
    }

    /**
     * Constructor for backward compatibility, initializing the JWT and user ID.
     *
     * @param jwt    The JSON Web Token.
     * @param userId The unique identifier of the authenticated user.
     */
    public LoginResponse(String jwt, Long userId) {
        this.jwt = jwt;
        this.userId = userId;
    }

    /**
     * Constructor for backward compatibility, initializing the JWT, user ID, and eatery ID.
     *
     * @param jwt      The JSON Web Token.
     * @param userId   The unique identifier of the authenticated user.
     * @param eateryId The ID of the eatery that the user is currently associated with.
     */
    public LoginResponse(String jwt, Long userId, Long eateryId) {
        this.jwt = jwt;
        this.userId = userId;
        this.eateryId = eateryId;
    }
}
