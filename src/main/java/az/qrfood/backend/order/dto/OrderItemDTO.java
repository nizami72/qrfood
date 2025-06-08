package az.qrfood.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO {

    @Override
    public String toString() {
        return "{\"OrderItemDTO\":\n{"
                + "        \"id\":\"" + id + "\""
                + ",         \"dishItemId\":\"" + dishId + "\""
                + ",         \"orderItemId\":\"" + orderItemId + "\""
                + ",         \"name\":\"" + name + "\""
                + ",         \"quantity\":\"" + quantity + "\""
                + ",         \"note\":\"" + note + "\""
                + "\n}\n}";
    }

    private Long id;
    private Long dishId;
    private Long orderItemId;
    private String name;
    private Integer quantity;
    private String note;
    private BigDecimal price;
}
