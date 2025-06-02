package az.qrfood.backend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Order entity, designed to match the frontend expectations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private Long tableId;
    private String status;
    private LocalDateTime createdAt;
    private String tableNumber;
    private List<OrderItemDTO> items;
    private String note;
}