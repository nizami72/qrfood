package az.qrfood.backend.orderitem.controller;

import az.qrfood.backend.order.OrderItemStatus;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.orderitem.service.OrderItemService;
import az.qrfood.backend.service.WebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST controller for managing order items.
 */
@Log4j2
@RestController
@Tag(name = "Order Item Management", description = "API endpoints for managing individual items within orders")
public class OrderItemController {

    private final OrderItemService orderItemService;
    private final WebSocketService webSocketService;

    public OrderItemController(OrderItemService orderItemService, WebSocketService webSocketService) {
        this.orderItemService = orderItemService;
        this.webSocketService = webSocketService;
    }

    /**
     * GET all order items.
     *
     * @return list of order items
     */
    @Operation(summary = "Get all order items", description = "Retrieves a list of all order items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of order items"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @GetMapping("${order.item}")
    // [[getAllOrderItems]]
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
        log.debug("REST request to get all OrderItems");
        return ResponseEntity.ok(orderItemService.getAllOrderItems());
    }

    /**
     * GET order items by order ID.
     *
     * @param orderId the ID of the order
     * @return list of order items for the specified order
     */
    @Operation(summary = "Get order items by order ID", description = "Retrieves a list of order items for a specific order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of order items"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping("${order.item.order.id}")
    public ResponseEntity<List<OrderItemDTO>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        log.debug("REST request to get OrderItems for order ID: {}", orderId);
        return ResponseEntity.ok(orderItemService.getOrderItemsByOrderId(orderId));
    }

    /**
     * GET order item by ID.
     *
     * @param orderItemId the ID of the order item to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the order item, or with status 404 (Not Found)
     */
    @Operation(summary = "Get order item by ID", description = "Retrieves a specific order item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the order item"),
            @ApiResponse(responseCode = "404", description = "Order item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping("${order.item.id}")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable Long orderItemId) {
        log.debug("REST request to get OrderItem : {}", orderItemId);
        return ResponseEntity.ok(orderItemService.getOrderItemById(orderItemId));
    }

    /**
     * POST a new order item.
     *
     * @param orderItemDTO the order item to create
     * @return the ResponseEntity with status 201 (Created) and with body the new order item
     */
    @Operation(summary = "Create a new order item", description = "Creates a new order item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order item created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PostMapping("${order.item.order.id}")
    public ResponseEntity<OrderItemDTO> postOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        log.debug("REST request to create OrderItem : {}", orderItemDTO);
        OrderItemDTO result = orderItemService.createOrderItem(orderItemDTO);

        // Get the order item entity to extract the eatery ID
        OrderItem orderItem = orderItemService.getOrderItemEntityById(result.getId());
        Long orderId = orderItem.getOrder().getId();
        Long eateryId = orderItem.getOrder().getTable().getEatery().getId();

        // Send WebSocket notification about the updated order
        webSocketService.notifyOrderUpdate(String.valueOf(eateryId), orderId, null);

        return ResponseEntity.ok(result);
    }

    /**
     * PUT to update an existing order item.
     *
     * @param orderItemId the ID of the order item to update
     * @param orderItemDTO the order item to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated order item
     */
    @Operation(summary = "Update an existing order item", description = "Updates an existing order item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order item updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @PutMapping("${order.item.id}")
    public ResponseEntity<OrderItemDTO> putOrderItem(
            @PathVariable Long orderItemId,
            @RequestBody OrderItemDTO orderItemDTO) {
        log.debug("REST request to update OrderItem : {}", orderItemId);

        // Get the order item entity before update to extract the eatery ID and order ID
        OrderItem orderItem = orderItemService.getOrderItemEntityById(orderItemId);
        Long orderId = orderItem.getOrder().getId();
        Long eateryId = orderItem.getOrder().getTable().getEatery().getId();

        // Update the order item
        OrderItemDTO result = orderItemService.updateOrderItem(orderItemId, orderItemDTO);

        // Send WebSocket notification about the updated order
        webSocketService.notifyOrderUpdate(String.valueOf(eateryId), orderId, null);

        return ResponseEntity.ok(result);
    }

    /**
     * PUT to update the status of an existing order item.
     *
     * @param orderItemId the ID of the order item to update
     * @param status the new status for the order item
     * @return the ResponseEntity with status 200 (OK) and with body the updated order item
     */
    @Operation(summary = "Update the status of an existing order item", description = "Updates the status of an existing order item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order item status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "404", description = "Order item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER')")
    @PutMapping("${order.item.id}/status/{status}")
    public ResponseEntity<OrderItemDTO> updateOrderItemStatus(
            @PathVariable Long orderItemId,
            @PathVariable OrderItemStatus status) {
        log.debug("REST request to update OrderItem status : {}, {}", orderItemId, status);

        // Get the order item entity before update to extract the eatery ID and order ID
        OrderItem orderItem = orderItemService.getOrderItemEntityById(orderItemId);
        Long orderId = orderItem.getOrder().getId();
        Long eateryId = orderItem.getOrder().getTable().getEatery().getId();

        // Update the order item status
        OrderItemDTO result = orderItemService.updateOrderItemStatus(orderItemId, status);

        // Send WebSocket notification about the updated order
        webSocketService.notifyOrderUpdate(String.valueOf(eateryId), orderId, null);

        return ResponseEntity.ok(result);
    }

    /**
     * DELETE an order item.
     *
     * @param orderItemId the ID of the order item to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @Operation(summary = "Delete an order item", description = "Deletes an order item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order item not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN')")
    @DeleteMapping("${order.item.id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        log.debug("Deleting OrderItem [{}]", orderItemId);

        // Get the order item entity before deletion to extract the eatery ID and order ID
        OrderItem orderItem = orderItemService.getOrderItemEntityById(orderItemId);
        Long orderId = orderItem.getOrder().getId();
        Long eateryId = orderItem.getOrder().getTable().getEatery().getId();

        // Delete the order item
        orderItemService.deleteOrderItem(orderItemId);

        // Send WebSocket notification about the updated order
        webSocketService.notifyOrderUpdate(String.valueOf(eateryId), orderId, null);

        return ResponseEntity.ok().build();
    }
}
