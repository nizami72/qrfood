package az.qrfood.backend.client.service;


import az.qrfood.backend.client.dto.ClientDeviceRequestDto;
import az.qrfood.backend.client.dto.ClientDeviceResponseDto;
import az.qrfood.backend.client.entity.ClientDevice;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between {@link ClientDevice} entities and their
 * corresponding Data Transfer Objects (DTOs).
 * <p>
 * This class facilitates the transformation of data between the service layer
 * and the presentation layer, ensuring that only necessary information is exposed.
 * </p>
 */
@Component
public class ClientDeviceMapper {

    /**
     * Converts a {@link ClientDeviceRequestDto} to a {@link ClientDevice} entity.
     * This method is typically used when creating a new client device from incoming request data.
     *
     * @param dto The {@link ClientDeviceRequestDto} to convert.
     * @return A new {@link ClientDevice} entity populated with data from the DTO.
     */
    public ClientDevice toEntity(ClientDeviceRequestDto dto) {
        ClientDevice device = new ClientDevice();
        device.setUuid(dto.getUuid());
        return device;
    }

    /**
     * Converts a {@link ClientDevice} entity to a {@link ClientDeviceResponseDto}.
     * This method is typically used when sending client device data back to the client.
     *
     * @param device The {@link ClientDevice} entity to convert.
     * @return A new {@link ClientDeviceResponseDto} populated with data from the entity.
     */
    public ClientDeviceResponseDto toDto(ClientDevice device) {
        ClientDeviceResponseDto dto = new ClientDeviceResponseDto();
        dto.setId(device.getId());
        dto.setUuid(device.getUuid());
        return dto;
    }

    /**
     * Updates an existing {@link ClientDevice} entity with data from a {@link ClientDeviceRequestDto}.
     * This method is typically used when modifying an existing client device's properties.
     *
     * @param device The existing {@link ClientDevice} entity to update.
     * @param dto    The {@link ClientDeviceRequestDto} containing the updated data.
     */
    public void updateEntity(ClientDevice device, ClientDeviceRequestDto dto) {
        device.setUuid(dto.getUuid());
    }
}
