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