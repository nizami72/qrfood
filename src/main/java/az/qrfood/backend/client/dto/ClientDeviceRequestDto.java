package az.qrfood.backend.client.dto;

import az.qrfood.backend.order.dto.OrderDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 * Data Transfer Object (DTO) for client device requests.
 * <p>
 * This DTO is used to carry data from the client when creating or updating
 * a {@link az.qrfood.backend.client.entity.ClientDevice} record.
 * It includes the device's UUID and an optional list of associated orders.
 * </p>
 */
@Setter
@Getter
@Builder
public class ClientDeviceRequestDto {

    /**
     * The universally unique identifier (UUID) of the client device.
     * This is a mandatory field for identifying the device.
     */
    private String uuid;

    /**
     * An optional list of {@link OrderDto} objects associated with this client device.
     * This can be used to link orders to a device during creation or update.
     */
    private List<OrderDto> orderDtoList;

}
