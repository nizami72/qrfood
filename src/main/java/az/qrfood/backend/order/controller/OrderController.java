package az.qrfood.backend.order.controller;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Создание заказа с привязкой к конкретному столику.
     */
    @PostMapping("/create/{tableId}")
    public ResponseEntity<Order> createOrder(
            @PathVariable Long tableId,
            @RequestBody List<OrderItemDTO> orderItems
    ) {
        Order order = orderService.createOrder(tableId, orderItems);
        return ResponseEntity.ok(order);
    }
}
