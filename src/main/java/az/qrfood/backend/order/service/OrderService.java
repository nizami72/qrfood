package az.qrfood.backend.order.service;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.menu.entity.MenuItem;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.repository.CustomerOrderRepository;
import az.qrfood.backend.menu.repository.MenuItemRepository;
import az.qrfood.backend.order.repository.OrderItemRepository;
import az.qrfood.backend.qr.repository.QrRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final QrRepository tableQRRepository;

    public OrderService(CustomerOrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        MenuItemRepository menuItemRepository,
                        QrRepository tableQRRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.tableQRRepository = tableQRRepository;
    }

    /**
     * Создаёт заказ на стол, включая позиции.
     */
    @Transactional
    public Order createOrder(Long tableId, List<OrderItemDTO> itemsDto) {
//        QrCode table = tableQRRepository.findById(tableId)
//                .orElseThrow(() -> new RuntimeException("Table not found"));

        Order order = new Order();
//        order.setTable(table);
        order.setStatus(OrderStatus.NEW);

        BigDecimal totalPrice = BigDecimal.ZERO;
        order = orderRepository.save(order);

        for (OrderItemDTO dto : itemsDto) {
            MenuItem item = menuItemRepository.findById(dto.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setMenuItem(item);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setNote(dto.getNote());
//            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
//            orderItem.setPrice(itemTotal);
//            totalPrice = totalPrice.add(itemTotal);

            orderItemRepository.save(orderItem);
        }

//        order.setTotalPrice(totalPrice);
//        return orderRepository.save(order);
        return null;
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
//        return orderRepository.findByStatus(status);
        return null;
    }

    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.NEW);
        return orderRepository.save(order);
    }
}
