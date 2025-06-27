package az.qrfood.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for individual items within an order.
 * <p>
 * This DTO represents a single dish or product ordered by a customer,
 * including its quantity, price, and any specific notes.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemDTO {

    /**
     * The unique identifier for this order item entry.
     */
    private Long id;

    /**
     * The ID of the dish (menu item) being ordered.
     */
    private Long dishId;

    /**
     * The ID of the order item itself, potentially distinct from the dish ID.
     */
    private Long orderItemId;

    /**
     * The name of the dish.
     */
    private String name;

    /**
     * The quantity of the dish ordered.
     */
    private Integer quantity;

    /**
     * Any specific notes or customizations for this order item.
     */
    private String note;

    /**
     * The price of this individual order item (price per unit * quantity).
     */
    private BigDecimal price;

    /**
     * Provides a string representation of the OrderItemDTO object.
     *
     * @return A formatted string including the order item's details.
     */
    @Override
    public String toString() {
        return "{\"OrderItemDTO\":\n{"
                + "        \"id\":\"" + id + "\""
                + ",         \"dishId\":\"" + dishId + "\""
                + ",         \"orderItemId\":\"" + orderItemId + "\""
                + ",         \"name\":\"" + name + "\""
                + ",         \"quantity\":\"" + quantity + "\""
                + ",         \"note\":\"" + note + "\""
                + "\n}\n}";
    }
}
