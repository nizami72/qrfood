package az.qrfood.backend.order.repository;

import az.qrfood.backend.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for the {@link OrderItem} entity.
 * <p>
 * This interface provides standard CRUD operations for {@link OrderItem} entities
 * and supports custom query methods for retrieving order items by their associated order ID.
 * </p>
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * Retrieves a list of {@link OrderItem} entities associated with a specific order ID.
     *
     * @param orderId The ID of the order to retrieve items for.
     * @return A list of {@link OrderItem} entities belonging to the specified order.
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Checks if there are any {@link OrderItem} entities associated with a specific dish ID.
     *
     * @param dishId The ID of the dish to check.
     * @return true if there are any order items referencing the specified dish, false otherwise.
     */
    boolean existsByDishEntityId(Long dishId);
}
