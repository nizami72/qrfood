package az.qrfood.backend.order.service;

import az.qrfood.backend.client.entity.ClientDevice;
import az.qrfood.backend.client.repository.ClientDeviceRepository;
import az.qrfood.backend.common.Util;
import az.qrfood.backend.common.exception.OrderNotFoundException;
import az.qrfood.backend.common.exception.UnauthorizedStatusChangeException;
import az.qrfood.backend.dish.entity.DishEntity;
import az.qrfood.backend.dish.repository.DishRepository;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.entity.OrderItem;
import az.qrfood.backend.order.mapper.OrderMapper;
import az.qrfood.backend.order.repository.CustomerOrderRepository;
import az.qrfood.backend.order.repository.OrderItemRepository;
import az.qrfood.backend.table.entity.TableInEatery;
import az.qrfood.backend.table.repository.TableRepository;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;
import az.qrfood.backend.tableassignment.service.TableAssignmentService;
import az.qrfood.backend.user.entity.Role;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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

    //<editor-fold desc="Fields">
    private final CustomerOrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishRepository dishRepository;
    private final TableRepository tableRepository;
    private final OrderMapper orderMapper;
    private final ClientDeviceRepository clientDeviceRepository;
    private final TableAssignmentService tableAssignmentService;
    //</editor-fold>

    //<editor-fold desc="Constructor">

    /**
     * Constructs an OrderService with necessary dependencies.
     *
     * @param orderRepository        The repository for customer orders.
     * @param orderItemRepository    The repository for order items.
     * @param dishRepository         The repository for dish entities.
     * @param tableRepository        The repository for table entities.
     * @param orderMapper            The mapper for converting between Order entities and DTOs.
     * @param clientDeviceRepository The repository for client devices.
     * @param tableAssignmentService The service for table assignments.
     */
    public OrderService(CustomerOrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        DishRepository dishRepository,
                        TableRepository tableRepository,
                        OrderMapper orderMapper,
                        ClientDeviceRepository clientDeviceRepository,
                        TableAssignmentService tableAssignmentService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.dishRepository = dishRepository;
        this.tableRepository = tableRepository;
        this.orderMapper = orderMapper;
        this.clientDeviceRepository = clientDeviceRepository;
        this.tableAssignmentService = tableAssignmentService;
    }
    //</editor-fold>

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
     * Retrieves a single order entity by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return The {@link Order} entity.
     * @throws OrderNotFoundException if the order with the given ID is not found.
     */
    public Order getOrderEntityById(Long id) {
        log.debug("Request to get Order entity : {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + id));
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
        log.debug("Request to create new order");
        Long tableId = orderDto.getTableId();

        TableInEatery table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found with id " + tableId));

        Order order = new Order();
        order.setTable(table);
        order.setNote(orderDto.getNote());
        order.setStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());
        order = orderRepository.save(order);

        createOrderItems(orderDto.getItems(), order);

        // Refresh the order to get the items
        order = orderRepository.findById(order.getId()).orElse(order);
        return order;
    }

    private void createOrderItems(List<OrderItemDTO> orderDtoItems, Order order) {
        if (orderDtoItems != null && !orderDtoItems.isEmpty()) {
            for (OrderItemDTO dto : orderDtoItems) {
                DishEntity dish = dishRepository.findById(dto.getDishId())
                        .orElseThrow(() -> new RuntimeException("Error while creation order items, dish not found with id " + dto.getDishId()));

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
    public OrderDto updateOrder(Long id, OrderDto orderDTO, Set<Role> auth) {
        log.debug("Request to update Order : {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

        OrderStatus newStatus = orderDTO.getStatus();

        // Update only allowed fields
        if (newStatus != null
                && newStatus != order.getStatus()
//                && (newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED)
        ) {

            try {
//                if (canUpdateStatus(auth, newStatus)) {
                    OrderStatus oldStatus = order.getStatus();
                    order.setStatus(newStatus);
                    propagateStatusToItemsIfForward(order, oldStatus, newStatus);
//                } else {
//                    throw new UnauthorizedStatusChangeException();
//                }
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
            OrderStatus oldStatus = order.getStatus();
            order.setStatus(status);
            // propagate to order items if moving forward in flow
            propagateStatusToItemsIfForward(order, oldStatus, status);
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
        Order order = orderRepository.findById(id).orElseThrow();
        List<ClientDevice> clientDevices = clientDeviceRepository.findByOrdersId(id);
        for (ClientDevice device : clientDevices) {
            device.getOrders().remove(order);
        }
        clientDeviceRepository.saveAll(clientDevices);
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

    /**
     * Adds dishes to an existing order that has not been paid yet.
     * <p>
     * This method allows adding additional dishes to an order that is in a status other than PAID or CANCELLED.
     * When dishes are added, the order status is updated to PREPARING.
     * </p>
     *
     * @param id       The ID of the order to add dishes to.
     * @param orderDto The {@link OrderDto} containing the new dishes to add.
     * @return The updated {@link OrderDto}.
     * @throws RuntimeException if the order with the given ID is not found or if the order is already paid or cancelled.
     */
    @Transactional
    public OrderDto addDishesToOrder(Long id, OrderDto orderDto) {
        log.debug("Request to add dishes to Order : {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));

        // Check if the order is not paid or cancelled
        if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot add dishes to an order that is already paid or cancelled");
        }

        createOrderItems(orderDto.getItems(), order);

        // Update order status to PREPARING
        OrderStatus currentStatus = order.getStatus();
        if(currentStatus.equals(OrderStatus.READY) || currentStatus.equals(OrderStatus.SERVED)) {
            order.setStatus(OrderStatus.PREPARING);
        }

        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Retrieves orders filtered by eatery ID and device UUID.
     * <p>
     * This method is used to check if a specific device has any orders
     * at a specific eatery. It's primarily used to determine whether to show the order
     * decision page or the menu page when a user scans a QR code.
     * </p>
     *
     * @param eateryId   The ID of the eatery to filter orders by.
     * @param deviceUuid The UUID of the client device to filter orders by.
     * @return A list of {@link OrderDto} representing orders that match all criteria.
     */
    public List<OrderDto> getClientOrders(Long eateryId, String deviceUuid) {

        // Find the client device by UUID
        return clientDeviceRepository.findByUuid(deviceUuid)
                .map(clientDevice -> {
                    // Filter orders by eatery ID and status
                    List<Order> filteredOrders = clientDevice.getOrders().stream()
                            .filter(order ->
                                    order.getTable() != null && order.getTable().getEatery() != null &&
                                            eateryId.equals(order.getTable().getEatery().getId()))
                            .filter(order ->
                                    Util.isNotOlder(order.getCreatedAt(), 1, ChronoUnit.DAYS)).toList();

                    return orderMapper.toDtoList(filteredOrders);
                })
                .orElse(List.of());
    }

    /**
     * Retrieves orders assigned to a specific waiter.
     * <p>
     * This method gets all table assignments for the waiter, extracts the tables,
     * and then retrieves all orders for those tables.
     * </p>
     *
     * @param waiterId The ID of the waiter to get orders for.
     * @return A list of {@link OrderDto} representing orders assigned to the waiter.
     */
    public List<OrderDto> getOrdersByWaiterId(Long waiterId) {
        log.debug("Request to get Orders by waiter ID : {}", waiterId);

        // Get all table assignments for the waiter
        List<TableAssignmentDto> tableAssignments = tableAssignmentService.getTableAssignmentsByWaiterId(waiterId);

        if (tableAssignments.isEmpty()) {
            return List.of();
        }

        // Extract the tables from the assignments
        Set<TableInEatery> tables = tableAssignments.stream()
                .map(assignment -> tableRepository.findById(assignment.getTableId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toSet());

        if (tables.isEmpty()) {
            return List.of();
        }

        // Get all orders for those tables
        List<Order> orders = orderRepository.findByTableIn(tables);

        return orderMapper.toDtoList(orders);
    }

    private void propagateStatusToItemsIfForward(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == null || newStatus == null) return;
        int oldRank = statusRank(oldStatus);
        int newRank = statusRank(newStatus);
        if (newRank <= oldRank) {
            // backward or same: do not touch item statuses
            return;
        }
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        boolean changed = false;
        for (OrderItem item : items) {
            if (statusRank(item.getStatus()) < newRank) {
                item.setStatus(newStatus);
                changed = true;
            }
        }
        if (changed) {
            orderItemRepository.saveAll(items);
        }
    }

    private int statusRank(OrderStatus s) {
        // Define the normal forward flow order
        return switch (s) {
            case CREATED -> 0;
            case PREPARING -> 1;
            case READY -> 2;
            case SERVED -> 3;
            case PAID -> 4;
            case CANCELLED -> 5;
        };
    }

    // NAV allowance to update order status
    private boolean canUpdateStatus(Set<Role> roles, OrderStatus status) {

        if (roles == null || status == null) {
            return false;
        }

        if (roles.contains(Role.SUPER_ADMIN) || roles.contains(Role.EATERY_ADMIN)) return true;

        return switch (status) {
            case CREATED -> roles.contains(Role.WAITER);
            case PREPARING -> roles.contains(Role.KITCHEN_ADMIN);
            case READY -> roles.contains(Role.KITCHEN_ADMIN);
            case SERVED -> roles.contains(Role.WAITER);
            case PAID -> roles.contains(Role.CASHIER);
            case CANCELLED -> roles.contains(Role.WAITER) || roles.contains(Role.CASHIER);
        };
    }

}
