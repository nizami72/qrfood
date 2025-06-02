package az.qrfood.backend.order.controller;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Получить все заказы по статусу.
     */
    @GetMapping("/status/{status}")
    public List<OrderDto> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    /**
     * Изменить статус заказа (например: NEW → IN_PROGRESS).
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        OrderDto updated = orderService.updateOrderStatus(orderId, status.toUpperCase());
        return ResponseEntity.ok(updated);
    }
}
