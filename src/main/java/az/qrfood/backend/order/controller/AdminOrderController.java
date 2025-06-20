package az.qrfood.backend.order.controller;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Admin Order Management", description = "API endpoints for administrative operations on orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Get all orders by status.
     *
     * @param status the status to filter orders by
     * @return list of orders with the specified status
     */
    @Operation(summary = "Get all orders by status", description = "Retrieves a list of all orders with the specified status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/status/{status}")
    public List<OrderDto> getOrdersByStatus(@PathVariable OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    /**
     * Update order status (for example: NEW → IN_PROGRESS).
     *
     * @param orderId the ID of the order to update
     * @param status the new status value
     * @return the updated order
     */
    @Operation(summary = "Update order status", description = "Updates the status of an order with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {
        OrderDto updated = orderService.updateOrderStatus(orderId, status.toUpperCase());
        return ResponseEntity.ok(updated);
    }
}
