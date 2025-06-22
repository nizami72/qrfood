package az.qrfood.backend.order.controller;

import az.qrfood.backend.client.service.ClientDeviceService;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderStatusUpdateDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.mapper.OrderMapper;
import az.qrfood.backend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for managing orders.
 */
@Log4j2
@RestController
@Tag(name = "Order Management", description = "API endpoints for managing orders in eateries")
public class OrderController {

    //<editor-fold desc="Fields">
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final ClientDeviceService clientDeviceService;
    //</editor-fold>

    public OrderController(OrderService orderService, OrderMapper orderMapper, ClientDeviceService clientDeviceService) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.clientDeviceService = clientDeviceService;
    }

    /**
     * GET all orders by status.
     *
     * @param status the status to filter orders by
     * @return list of orders with defined status
     */
    @Operation(summary = "Get all orders by status", description = "Retrieves a list of all orders with the specified status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${order.status}")
    public ResponseEntity<List<OrderDto>> getAllOrders(@PathVariable("status") String status) {
        log.debug("GET all order by status [{}]", status);
        return ResponseEntity.ok(orderService.getAllOrdersByStatus(status));
    }

    /**
     * GET all orders by eatery ID.
     *
     * @param eateryId the ID of the eatery
     * @return list of orders for the specified eatery
     */
    @Operation(summary = "Get orders by eatery ID", description = "Retrieves a list of all orders for the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${order}")
    public ResponseEntity<List<OrderDto>> getOrdersByEateryId(@PathVariable Long eateryId) {
        log.debug("REST request to get Orders for eatery ID: {}", eateryId);
        return ResponseEntity.ok(orderService.getOrdersByEateryId(eateryId));
    }

    /**
     * GET order by ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the order, or with status 404 (Not Found)
     */
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${order.id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        log.debug("REST request to get Order : {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * POST a new order for a specific eatery table.
     *
     * @param response HTTP response to add cookie
     * @param orderDto the list of order items
     * @return the ResponseEntity with status 201 (Created) and with body the new order
     */
    @Operation(summary = "Create a new order", description = "Creates a new order for a specific table with the provided items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("${order}")
    public ResponseEntity<OrderDto> postOrder(HttpServletResponse response, @PathVariable Long eateryId,
                                              @RequestBody OrderDto orderDto
    ) {
        log.debug("REST request to create Order for table ID: {}", eateryId);
        Order order = orderService.createOrder(orderDto);
        Cookie cookie = clientDeviceService.createCookieUuid(order);
        response.addCookie(cookie);
        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    /**
     * Update an existing order.
     *
     * @param orderId the ID of the order to update
     * @param orderDTO the order to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated order
     */
    @Operation(summary = "Update an existing order", description = "Updates an order with the specified ID using the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("${order.id}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable Long orderId,
            @RequestBody OrderDto orderDTO
    ) {
        log.debug("REST request to update Order : {}", orderId);
        return ResponseEntity.ok(orderService.updateOrder(orderId, orderDTO));
    }

    /**
     * Update the status of an order.
     *
     * @param id the ID of the order to update
     * @param statusDTO the status update DTO
     * @return the ResponseEntity with status 200 (OK) and with body the updated order
     */
    @Operation(summary = "Update order status", description = "Updates the status of an order with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @PutMapping("${order.id}")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO statusDTO
    ) {
        log.debug("REST request to update Order status : {}", id);
        return ResponseEntity.ok(orderService.updateOrderStatus(id, statusDTO.getStatus()));
    }

    /**
     * DELETE an order.
     *
     * @param orderId the ID of the order to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Delete an order", description = "Deletes an order with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("${order.id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }
}
