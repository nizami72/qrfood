package az.qrfood.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO {

    private Long id;
    private Long dishItemId;
    private String name;
    private Integer quantity;
    private String note;
}
