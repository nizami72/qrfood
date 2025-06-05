package az.qrfood.backend.orderitem.service;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.OrderItem;
import java.util.List;

/**
 * Service Interface for managing OrderItem.
 */
public interface OrderItemService {

    /**
     * Get all order items.
     *
     * @return the list of order items
     */
    List<OrderItemDTO> getAllOrderItems();

    /**
     * Get order items by order ID.
     *
     * @param orderId the ID of the order
     * @return the list of order items for the specified order
     */
    List<OrderItemDTO> getOrderItemsByOrderId(Long orderId);

    /**
     * Get order item by ID.
     *
     * @param id the ID of the order item
     * @return the order item
     */
    OrderItemDTO getOrderItemById(Long id);

    /**
     * Create a new order item.
     *
     * @param orderItemDTO the order item to create
     * @return the created order item
     */
    OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO);

    /**
     * Update an existing order item.
     *
     * @param id the ID of the order item to update
     * @param orderItemDTO the order item to update
     * @return the updated order item
     */
    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);

    /**
     * Delete an order item.
     *
     * @param id the ID of the order item to delete
     */
    void deleteOrderItem(Long id);
}