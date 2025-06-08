package az.qrfood.backend.orderitem.service;

import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.order.repository.CustomerOrderRepository;
import az.qrfood.backend.order.repository.OrderItemRepository;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.dish.repository.DishRepository;
import az.qrfood.backend.orderitem.mapper.OrderItemMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service Implementation for managing OrderItem.
 */
@Log4j2
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderItemMapper orderItemMapper;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                               CustomerOrderRepository orderRepository,
                               DishRepository dishRepository,
                               OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        log.debug("Request to get all OrderItems");
        return orderItemMapper.toDtoList(orderItemRepository.findAll());
    }

    @Override
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        log.debug("Request to get OrderItems for Order ID: {}", orderId);
        // Verify order exists
        orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItemMapper.toDtoList(orderItems);
    }

    @Override
    public OrderItemDTO getOrderItemById(Long id) {
        log.debug("Request to get OrderItem : {}", id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id " + id));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    @Transactional
    public OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO) {
        log.debug("Request to create OrderItem : {}", orderItemDTO);

        // Validate order exists
        Order order = orderRepository.findById(orderItemDTO.getOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderItemDTO.getOrderItemId()));

        // Validate dish exists
        DishEntity dish = dishRepository.findById(orderItemDTO.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found with id " + orderItemDTO.getDishId()));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setDishEntity(dish);
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setNote(orderItemDTO.getNote());
        orderItem.setPriceAtOrder(dish.getPrice());

        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toDto(savedOrderItem);
    }

    @Override
    @Transactional
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO) {
        log.debug("Request to update OrderItem : {}", orderItemDTO);

        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id " + id));

        // Update only allowed fields
        if (orderItemDTO.getQuantity() != null) {
            orderItem.setQuantity(orderItemDTO.getQuantity());
        }

        if (orderItemDTO.getNote() != null) {
            orderItem.setNote(orderItemDTO.getNote());
        }

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toDto(updatedOrderItem);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        log.debug("Request to delete OrderItem : {}", id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id " + id));
        orderItemRepository.delete(orderItem);
    }

}
