package az.qrfood.backend.client.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for client device responses.
 * <p>
 * This DTO is used to send client device information back to the client,
 * typically after a creation or retrieval operation. It provides a simplified
 * view of the {@link az.qrfood.backend.client.entity.ClientDevice} entity.
 * </p>
 */
@Setter
@Getter
public class ClientDeviceResponseDto {
    /**
     * The unique identifier of the client device.
     */
    private Long id;

    /**
     * The universally unique identifier (UUID) of the client device.
     */
    private String uuid;
}
