package az.qrfood.backend.order.repository;

import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Collection;
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

    /**
     * Finds order items by kitchen department and item statuses.
     * Includes join to dish and its kitchen department to filter efficiently.
     */
    @Query("select oi from OrderItem oi " +
            "join oi.dishEntity d " +
            "join d.kitchenDepartment kd " +
            "where kd.id = :departmentId and oi.status in :statuses")
    List<OrderItem> findByDepartmentAndStatuses(@Param("departmentId") Long departmentId,
                                                @Param("statuses") Collection<OrderStatus> statuses);
}
