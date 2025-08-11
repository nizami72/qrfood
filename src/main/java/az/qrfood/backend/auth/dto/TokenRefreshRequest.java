package az.qrfood.backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for a token refresh request.
 * <p>
 * This DTO carries the refresh token that will be used to generate a new access token.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshRequest {
    
    /**
     * The refresh token used to obtain a new access token.
     */
    @NotBlank
    private String refreshToken;
}