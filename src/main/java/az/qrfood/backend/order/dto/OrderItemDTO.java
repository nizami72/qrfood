package az.qrfood.backend.order.dto;

import az.qrfood.backend.order.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@ToString
public class OrderItemDTO {

    /**
     * The unique identifier for this order item entry.
     */
    private Long id;

    /**
     * The ID of the dish (menu item) being ordered.
     */
    private Long dishId;// todo: orderItemId vs id

    /**
     * The ID of the order itself that this item belongs to., potentially distinct from the dish ID.
     */
    private Long orderId;

    /**
     * The name of the dish.
     */
    private String dishName;
    /**
     * The quantity of the dish ordered.
     */
    private Integer quantity;

    /**
     * Any specific notes or customizations for this order item.
     */
    private String note;

    /**
     * The price of this individual order item (price per-unit * quantity).
     */
    private BigDecimal price;

    /**
     * The current status of this order item.
     */
    private OrderStatus status;
}
