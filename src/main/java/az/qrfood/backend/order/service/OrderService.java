package az.qrfood.backend.order.service;

import az.qrfood.backend.common.exception.OrderNotFoundException;
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
import az.qrfood.backend.table.entity.TableStatus;
import az.qrfood.backend.table.repository.TableRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service for managing orders.
 * <p>
 * This class encapsulates the business logic for creating, retrieving, updating,
 * and deleting customer orders. It interacts with various repositories to manage
 * order data, order items, dishes, and tables.
 * </p>
 */
@Log4j2
@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;

    /**
     * Constructs an OrderService with necessary dependencies.
     *
     * @param orderRepository     The repository for customer orders.
     * @param orderItemRepository The repository for order items.
     * @param dishRepository      The repository for dish entities.
     * @param tableRepository     The repository for table entities.
     * @param orderMapper         The mapper for converting between Order entities and DTOs.
     */
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
     * Retrieves all orders in the system.
     *
     * @return A list of {@link OrderDto} representing all orders.
     */
    public List<OrderDto> getAllOrders() {
        log.debug("Request to get all Orders");
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toDtoList(orders);
    }


    /**
     * Retrieves all orders filtered by their status.
     *
     * @param status The status string to filter orders by (e.g., "CREATED", "PREPARING").
     * @return A list of {@link OrderDto} representing orders with the specified status.
     * @throws RuntimeException if the provided status string is invalid.
     */
    public List<OrderDto> getAllOrdersByStatus(String status) {
        OrderStatus ts;
        try {
            ts = OrderStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("There is no such status " + status);
        }
        List<Order> orders = orderRepository.findByStatus(ts);
        return orderMapper.toDtoList(orders);
    }

    /**
     * Retrieves orders associated with a specific eatery ID.
     *
     * @param eateryId The ID of the eatery.
     * @return A list of {@link OrderDto} representing orders for the specified eatery.
     */
    public List<OrderDto> getOrdersByEateryId(Long eateryId) {
        List<Order> orders = orderRepository.findByTableEateryId(eateryId);
        return orderMapper.toDtoList(orders);
    }

    /**
     * Retrieves a single order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return An {@link OrderDto} representing the found order.
     * @throws OrderNotFoundException if the order with the given ID is not found.
     */
    public OrderDto getOrderById(Long id) {
        log.debug("Request to get Order : {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));
        return orderMapper.toDto(order);
    }

    /**
     * Creates a new order based on the provided DTO.
     * <p>
     * This method handles the creation of the order entity, its associated order items,
     * and links it to a specific table. It also calculates the price for each order item.
     * </p>
     *
     * @param orderDto The {@link OrderDto} containing the details for the new order.
     * @return The newly created {@link Order} entity.
     * @throws RuntimeException if the specified table or dish is not found.
     */
    @Transactional
    public Order createOrder(OrderDto orderDto) {
        Long tableId = orderDto.getTableId();

        TableInEatery table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id " + tableId));

        Order order = new Order();
        order.setTable(table);
        order.setNote(orderDto.getNote());
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());
        order = orderRepository.save(order);

        List<OrderItemDTO> orderDtoItems = orderDto.getItems();
        if(orderDtoItems != null && !orderDtoItems.isEmpty()) {
            for (OrderItemDTO dto : orderDto.getItems()) {
                DishEntity dish = dishRepository.findById(dto.getDishId())
                        .orElseThrow(() -> new RuntimeException("Dish not found with id " + dto.getDishId()));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setDishEntity(dish);
                orderItem.setQuantity(dto.getQuantity());
                orderItem.setNote(dto.getNote());
                order.getItems().add(orderItem);
                // Calculate price at order time
                if (dish.getPrice() != null) {
                    BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()));
                    orderItem.setPriceAtOrder(itemTotal);
                }
            }
        }

        // Refresh the order to get the items
        order = orderRepository.findById(order.getId()).orElse(order);
        return order;
    }

    /**
     * Updates an existing order with new information.
     * <p>
     * This method allows updating the order's status and notes.
     * </p>
     *
     * @param id       The ID of the order to update.
     * @param orderDTO The {@link OrderDto} containing the updated order data.
     * @return The updated {@link OrderDto}.
     * @throws RuntimeException if the order with the given ID is not found.
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
     * Updates the status of an order.
     *
     * @param id        The ID of the order to update.
     * @param newStatus The new status string for the order.
     * @return The updated {@link OrderDto}.
     * @throws RuntimeException if the order with the given ID is not found or if the new status is invalid.
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
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     */
    @Transactional
    public void deleteOrder(Long id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
    }

    /**
     * Retrieves orders filtered by their {@link OrderStatus} enum value.
     *
     * @param status The {@link OrderStatus} enum value to filter orders by.
     * @return A list of {@link OrderDto} representing orders with the specified status.
     */
    public List<OrderDto> getOrdersByStatus(OrderStatus status) {
        log.debug("Request to get Orders by status : {}", status);
        List<Order> orders = orderRepository.findByStatus(status);
        return orderMapper.toDtoList(orders);
    }
}
