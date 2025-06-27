package az.qrfood.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object (DTO) for {@link az.qrfood.backend.order.entity.Order} entities.
 * <p>
 * This DTO is used to transfer order data between the client and the server,
 * providing a simplified view of the Order entity, including its items, status,
 * and other relevant details. It's designed to align with frontend expectations.
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    /**
     * The unique identifier of the order.
     */
    private Long id;

    /**
     * The ID of the table from which the order was placed.
     */
    private Long tableId;

    /**
     * The current status of the order (e.g., "CREATED", "PREPARING", "READY").
     * This maps to {@link az.qrfood.backend.order.OrderStatus}.
     */
    private String status;

    /**
     * The timestamp when the order was created.
     */
    private LocalDateTime createdAt;

    /**
     * The number or identifier of the table from which the order was placed.
     */
    private String tableNumber;

    /**
     * A list of {@link OrderItemDTO} representing the individual items included in this order.
     */
    private List<OrderItemDTO> items;

    /**
     * Any additional notes or special requests for the order.
     */
    private String note;

    /**
     * The total price of the order.
     */
    private double orderPrice;

}
