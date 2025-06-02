package az.qrfood.backend.order.service;

import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.mapper.OrderMapper;
import az.qrfood.backend.order.repository.CustomerOrderRepository;
import az.qrfood.backend.dish.repository.DishRepository;
import az.qrfood.backend.order.repository.OrderItemRepository;
import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.repository.TableRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service for managing orders.
 */
@Log4j2
@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;

    public OrderService(CustomerOrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        DishRepository dishRepository,
                        TableRepository tableRepository,
                        OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.dishRepository = dishRepository;
        this.tableRepository = tableRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Get all orders.
     *
     * @return list of orders
     */
    public List<OrderDto> getAllOrders() {
        log.debug("Request to get all Orders");
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }

    /**
     * Get orders by eatery ID.
     *
     * @param eateryId the ID of the eatery
     * @return list of orders for the specified eatery
     */
    public List<OrderDto> getOrdersByEateryId(Long eateryId) {
        log.debug("Request to get Orders for eatery ID: {}", eateryId);
        List<Order> orders = orderRepository.findByTableEateryId(eateryId);
        return orderMapper.toDtoList(orders);
    }

    /**
     * Get order by ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order
     */
    public OrderDto getOrderById(Long id) {
        log.debug("Request to get Order : {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        return orderMapper.toDto(order);
    }

    /**
     * Create a new order for a specific table.
     *
     * @param orderDto the list of order items
     * @return the created order
     */
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Long tableId = orderDto.getTableId();
        log.debug("Request to create Order for table ID: {}", tableId);

        TableInEatery table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id " + tableId));

        Order order = new Order();
        order.setTable(table);
        order.setNote(orderDto.getNote());
        order.setStatus(OrderStatus.CREATED);
        order = orderRepository.save(order);

        for (OrderItemDTO dto : orderDto.getItems()) {
            DishEntity dish = dishRepository.findById(dto.getDishItemId())
                    .orElseThrow(() -> new RuntimeException("Dish not found with id " + dto.getDishItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setDishEntity(dish);
            orderItem.setQuantity(dto.getQuantity());
            orderItem.setNote(dto.getNote());

            // Calculate price at order time
            if (dish.getPrice() != null) {
                BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
                orderItem.setPriceAtOrder(itemTotal);
            }

            orderItemRepository.save(orderItem);
        }

        // Refresh the order to get the items
        order = orderRepository.findById(order.getId()).orElse(order);
        return orderMapper.toDto(order);
    }

    /**
     * Update an existing order.
     *
     * @param id the ID of the order to update
     * @param orderDTO the order to update
     * @return the updated order
     */
    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDTO) {
        log.debug("Request to update Order : {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

        // Update only allowed fields
        if (orderDTO.getStatus() != null) {
            try {
                OrderStatus status = OrderStatus.valueOf(orderDTO.getStatus());
                order.setStatus(status);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status: {}", orderDTO.getStatus());
            }
        }

        if (orderDTO.getNote() != null) {
            order.setNote(orderDTO.getNote());
        }

        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Update the status of an order.
     *
     * @param id the ID of the order to update
     * @param newStatus the new status
     * @return the updated order
     */
    @Transactional
    public OrderDto updateOrderStatus(Long id, String newStatus) {
        log.debug("Request to update Order status : {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

        try {
            OrderStatus status = OrderStatus.valueOf(newStatus);
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status: {}", newStatus);
            throw new RuntimeException("Invalid status: " + newStatus);
        }

        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Delete an order.
     *
     * @param id the ID of the order to delete
     */
    @Transactional
    public void deleteOrder(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    /**
     * Get orders by status.
     *
     * @param status the status
     * @return list of orders with the specified status
     */
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        log.debug("Request to get Orders by status : {}", status);
        List<Order> orders = orderRepository.findByStatus(status);
        return orderMapper.toDtoList(orders);
    }
}
