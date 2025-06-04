package az.qrfood.backend.order.controller;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderStatusUpdateDTO;
import az.qrfood.backend.order.service.OrderService;
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
 * REST controller for managing orders.
 */
@Log4j2
@RestController
@RequestMapping("${segment.api.orders}")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET all orders.
     *
     * @return list of orders
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        log.debug("REST request to get all Orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * GET orders by eatery ID.
     *
     * @param eateryId the ID of the eatery
     * @return list of orders for the specified eatery
     */
    @GetMapping("${component.eatery}/{eateryId}")
    public ResponseEntity<List<OrderDto>> getOrdersByEateryId(@PathVariable Long eateryId) {
        log.debug("REST request to get Orders for eatery ID: {}", eateryId);
        return ResponseEntity.ok(orderService.getOrdersByEateryId(eateryId));
    }

    /**
     * GET order by ID.
     *
     * @param id the ID of the order to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the order, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        log.debug("REST request to get Order : {}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * POST a new order for a specific eatery table.
     *
     * @param tableId the ID of the table
     * @param orderDto the list of order items
     * @return the ResponseEntity with status 201 (Created) and with body the new order
     */
    @PostMapping("/{tableId}")
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable Long tableId,
            @RequestBody OrderDto orderDto
    ) {
        log.debug("REST request to create Order for table ID: {}", tableId);
        orderDto.setTableId(tableId);
        OrderDto result = orderService.createOrder(orderDto);
        return ResponseEntity.ok(result);
    }

    /**
     * Update an existing order.
     *
     * @param id the ID of the order to update
     * @param orderDTO the order to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated order
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable Long id,
            @RequestBody OrderDto orderDTO
    ) {
        log.debug("REST request to update Order : {}", id);
        return ResponseEntity.ok(orderService.updateOrder(id, orderDTO));
    }

    /**
     * Update the status of an order.
     *
     * @param id the ID of the order to update
     * @param statusDTO the status update DTO
     * @return the ResponseEntity with status 200 (OK) and with body the updated order
     */
    @PutMapping("/{id}/status")
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
     * @param id the ID of the order to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }
}
