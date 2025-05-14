package az.qrfood.backend.order.repository;

import az.qrfood.backend.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findByStatus(OrderStatus status);
}
