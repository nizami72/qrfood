package az.qrfood.backend.order.repository;

import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the {@link Order} entity.
 * <p>
 * This interface provides standard CRUD operations for {@link Order} entities
 * and supports custom query methods for retrieving orders based on their status
 * or the associated eatery.
 * </p>
 */
@Repository
public interface CustomerOrderRepository extends JpaRepository<Order, Long> {

    /**
     * Retrieves a list of orders with the specified status.
     *
     * @param status The {@link OrderStatus} to filter orders by.
     * @return A list of {@link Order} entities matching the given status.
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Retrieves a list of orders associated with a specific eatery ID.
     * <p>
     * This method uses a custom JPQL query to fetch orders where the table
     * associated with the order belongs to the given eatery ID.
     * </p>
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link Order} entities belonging to the specified eatery.
     */
    @Query("SELECT o FROM Order o WHERE o.table.eatery.id = :eateryId")
    List<Order> findByTableEateryId(@Param("eateryId") Long eateryId);
}

