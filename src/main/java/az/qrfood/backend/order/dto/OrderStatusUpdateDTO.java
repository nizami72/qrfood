package az.qrfood.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for updating the status of an order.
 * <p>
 * This DTO is used to carry the new status value when changing an order's state.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateDTO {
    /**
     * The new status for the order. This should correspond to a value in {@link az.qrfood.backend.order.OrderStatus}.
     */
    private String status;
}
