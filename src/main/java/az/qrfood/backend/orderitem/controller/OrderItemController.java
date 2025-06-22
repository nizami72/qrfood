package az.qrfood.backend.orderitem.controller;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.orderitem.service.OrderItemService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * REST controller for managing order items.
 */
@Log4j2
@RestController
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    /**
     * GET all order items.
     *
     * @return list of order items
     */
    @GetMapping("${order.item}")
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
    @PostMapping("${order.item.order.id}")
    public ResponseEntity<OrderItemDTO> postOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        log.debug("REST request to create OrderItem : {}", orderItemDTO);
        OrderItemDTO result = orderItemService.createOrderItem(orderItemDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * PUT to update an existing order item.
     *
     * @param orderItemId the ID of the order item to update
     * @param orderItemDTO the order item to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated order item
     */
    @PutMapping("${order.item.id}")
    public ResponseEntity<OrderItemDTO> putOrderItem(
            @PathVariable Long orderItemId,
            @RequestBody OrderItemDTO orderItemDTO) {
        log.debug("REST request to update OrderItem : {}", orderItemId);
        return ResponseEntity.ok(orderItemService.updateOrderItem(orderItemId, orderItemDTO));
    }

    /**
     * DELETE an order item.
     *
     * @param orderItemId the ID of the order item to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("${order.item.id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        log.debug("REST request to delete OrderItem : {}", orderItemId);
        orderItemService.deleteOrderItem(orderItemId);
        return ResponseEntity.ok().build();
    }
}