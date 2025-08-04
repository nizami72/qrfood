package az.qrfood.backend.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for token refresh requests.
 * <p>
 * This DTO encapsulates the eatery ID provided by the user
 * when requesting a token refresh, typically when switching
 * between different eateries.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {

    /**
     * The ID of the eatery that the user wishes to switch to.
     * This is used to scope the user's session to a specific restaurant.
     */
    @NotNull(message = "Eatery ID is required")
    @Min(value = 1, message = "Eatery id must be greater than 0")
    @Max(value = 99999, message = "Eatery id must be less than 99999")
    private Long eateryId;
}