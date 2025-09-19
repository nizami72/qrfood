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
 * Service Implementation for managing {@link OrderItem} entities.
 * <p>
 * This class provides the concrete implementation of the {@link OrderItemService} interface,
 * handling business logic for order items, including CRUD operations and interactions
 * with related entities like {@link Order} and {@link DishEntity}.
 * </p>
 */
@Log4j2
@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderItemMapper orderItemMapper;

    /**
     * Constructs an OrderItemServiceImpl with necessary dependencies.
     *
     * @param orderItemRepository The repository for order items.
     * @param orderRepository     The repository for customer orders.
     * @param dishRepository      The repository for dish entities.
     * @param orderItemMapper     The mapper for converting between OrderItem entities and DTOs.
     */
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository,
                               CustomerOrderRepository orderRepository,
                               DishRepository dishRepository,
                               OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.orderItemMapper = orderItemMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderItemDTO> getAllOrderItems() {
        log.debug("Request to get all OrderItems");
        return orderItemMapper.toDtoList(orderItemRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        log.debug("Request to get OrderItems for Order ID: {}", orderId);
        // Verify order exists
        orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItemMapper.toDtoList(orderItems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderItemDTO getOrderItemById(Long id) {
        log.debug("Request to get OrderItem : {}", id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id " + id));
        return orderItemMapper.toDto(orderItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO) {
        log.debug("Request to create OrderItem : {}", orderItemDTO);

        // Validate order exists
        Order order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id " + orderItemDTO.getOrderId()));

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

    /**
     * {@inheritDoc}
     */
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

        if (orderItemDTO.getStatus() != null && orderItemDTO.getStatus() != orderItem.getStatus()) {
            updateOrderItemStatus(orderItem, orderItemDTO.getStatus());
        }

        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);
        return orderItemMapper.toDto(updatedOrderItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        log.debug("Request to delete OrderItem [{}]", id);
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id " + id));
        orderItemRepository.delete(orderItem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderItem getOrderItemEntityById(Long id) {
        log.debug("Request to get OrderItem entity : {}", id);
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found with id " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OrderItemDTO updateOrderItemStatus(OrderItem orderItem, az.qrfood.backend.order.OrderStatus status) {
        log.debug("Request to update OrderItem [{}] to new status [{}]", orderItem.getId(), status);

        orderItem.setStatus(status);
        OrderItem updatedOrderItem = orderItemRepository.save(orderItem);

        // Update the parent order's status based on the statuses of all its order items
        updateParentOrderStatus(orderItem.getOrder());

        return orderItemMapper.toDto(updatedOrderItem);
    }

    /**
     * Updates the status of an order based on the statuses of its order items.
     * <p>
     * The rules for determining the order status are:
     * - If all order items are CREATED → order CREATED
     * - If there is at least one order item PREPARING → order IN_PROGRESS
     * - If all order items are READY → order READY_FOR_PICKUP
     * - If all order items are SERVED → order SERVED
     * - If the order is closed and paid → PAID
     * - If the order is canceled → CANCELLED
     * </p>
     *
     * @param order The order to update.
     */
    private void updateParentOrderStatus(Order order) {
        log.debug("Updating order status for order ID: {}", order.getId());

        // If the order is already PAID or CANCELLED, don't change its status
        if (order.getStatus() == az.qrfood.backend.order.OrderStatus.PAID || 
            order.getStatus() == az.qrfood.backend.order.OrderStatus.CANCELLED) {
            return;
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        if (orderItems.isEmpty()) {
            return;
        }

        // Check if there is at least one order item with PREPARING status
        boolean hasPreparingItem = orderItems.stream()
                .anyMatch(item -> item.getStatus() == az.qrfood.backend.order.OrderStatus.PREPARING);

        if (hasPreparingItem) {
            order.setStatus(az.qrfood.backend.order.OrderStatus.PREPARING);
            orderRepository.save(order);
            return;
        }

        // Check if all order items have the same status
        boolean allCreated = orderItems.stream()
                .allMatch(item -> item.getStatus() == az.qrfood.backend.order.OrderStatus.CREATED);

        boolean allReady = orderItems.stream()
                .allMatch(item -> item.getStatus() == az.qrfood.backend.order.OrderStatus.READY);

        boolean allServed = orderItems.stream()
                .allMatch(item -> item.getStatus() == az.qrfood.backend.order.OrderStatus.SERVED);

        if (allCreated) {
            order.setStatus(az.qrfood.backend.order.OrderStatus.CREATED);
        } else if (allReady) {
            order.setStatus(az.qrfood.backend.order.OrderStatus.READY);
        } else if (allServed) {
            order.setStatus(az.qrfood.backend.order.OrderStatus.SERVED);
        }

        orderRepository.save(order);
    }
}
