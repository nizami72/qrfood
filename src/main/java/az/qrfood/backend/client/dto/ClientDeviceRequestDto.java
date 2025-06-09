package az.qrfood.backend.client.dto;

import az.qrfood.backend.order.dto.OrderDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@Builder
public class ClientDeviceRequestDto {

    private String uuid;
    private List<OrderDto> orderDtoList;

}