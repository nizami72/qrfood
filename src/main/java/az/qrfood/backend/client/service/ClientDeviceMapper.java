package az.qrfood.backend.client.service;


import az.qrfood.backend.client.dto.ClientDeviceRequestDto;
import az.qrfood.backend.client.dto.ClientDeviceResponseDto;
import az.qrfood.backend.client.entity.ClientDevice;
import org.springframework.stereotype.Component;

// ClientDeviceMapper.java
@Component
public class ClientDeviceMapper {

    public ClientDevice toEntity(ClientDeviceRequestDto dto) {
        ClientDevice device = new ClientDevice();
        device.setUuid(dto.getUuid());
        return device;
    }

    public ClientDeviceResponseDto toDto(ClientDevice device) {
        ClientDeviceResponseDto dto = new ClientDeviceResponseDto();
        dto.setId(device.getId());
        dto.setUuid(device.getUuid());
        return dto;
    }

    public void updateEntity(ClientDevice device, ClientDeviceRequestDto dto) {
        device.setUuid(dto.getUuid());
    }
}
