package az.qrfood.backend.orderitem.service;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.OrderItem;
import java.util.List;

/**
 * Service interface for managing {@link OrderItem} entities.
 * <p>
 * This interface defines the contract for business operations related to order items,
 * including CRUD operations and retrieval based on various criteria.
 * </p>
 */
public interface OrderItemService {

    /**
     * Retrieves a list of all order items in the system.
     *
     * @return A list of {@link OrderItemDTO} representing all order items.
     */
    List<OrderItemDTO> getAllOrderItems();

    /**
     * Retrieves a list of order items associated with a specific order ID.
     *
     * @param orderId The ID of the order for which to retrieve items.
     * @return A list of {@link OrderItemDTO} representing the order items for the specified order.
     */
    List<OrderItemDTO> getOrderItemsByOrderId(Long orderId);

    /**
     * Retrieves a single order item by its unique identifier.
     *
     * @param id The ID of the order item to retrieve.
     * @return An {@link OrderItemDTO} representing the found order item.
     */
    OrderItemDTO getOrderItemById(Long id);

    /**
     * Creates a new order item.
     *
     * @param orderItemDTO The {@link OrderItemDTO} containing the data for the new order item.
     * @return The newly created {@link OrderItemDTO}.
     */
    OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO);

    /**
     * Updates an existing order item.
     *
     * @param id           The ID of the order item to update.
     * @param orderItemDTO The {@link OrderItemDTO} containing the updated order item data.
     * @return The updated {@link OrderItemDTO}.
     */
    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);

    /**
     * Deletes an order item by its unique identifier.
     *
     * @param id The ID of the order item to delete.
     */
    void deleteOrderItem(Long id);

    /**
     * Retrieves a single order item entity by its unique identifier.
     *
     * @param id The ID of the order item to retrieve.
     * @return The {@link OrderItem} entity.
     */
    OrderItem getOrderItemEntityById(Long id);

    /**
     * Updates the status of an existing order item.
     *
     * @param status The new status for the order item.
     * @return The updated {@link OrderItemDTO}.
     */
    OrderItemDTO updateOrderItemStatus(OrderItem orderItem, az.qrfood.backend.order.OrderItemStatus status);
}
