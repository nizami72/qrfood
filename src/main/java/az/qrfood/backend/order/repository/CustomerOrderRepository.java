package az.qrfood.backend.order.repository;

import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Order entity.
 */
@Repository
public interface CustomerOrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by status.
     *
     * @param status the status
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by eatery ID.
     *
     * @param eateryId the ID of the eatery
     * @return list of orders for the specified eatery
     */
    @Query("SELECT o FROM Order o WHERE o.table.eatery.id = :eateryId")
    List<Order> findByTableEateryId(@Param("eateryId") Long eateryId);
}
