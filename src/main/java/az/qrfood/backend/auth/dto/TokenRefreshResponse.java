package az.qrfood.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for a token refresh response.
 * <p>
 * This DTO carries the new access token and the refresh token that was used.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {

    /**
     * Constructor that takes only the access token and eatery ID.
     * Used when the refresh token is stored in a cookie.
     * 
     * @param accessToken The new access token.
     * @param eateryId The ID of the eatery associated with this token.
     */
    public TokenRefreshResponse(String accessToken, Long eateryId) {
        this.accessToken = accessToken;
        this.eateryId = eateryId;
    }

    /**
     * The new access token generated from the refresh token.
     */
    private String accessToken;

    /**
     * The refresh token that was used to generate the new access token.
     */
    private String refreshToken;

    /**
     * The ID of the eatery associated with this token, if applicable.
     */
    private Long eateryId;
}
