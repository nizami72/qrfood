package az.qrfood.backend.order.controller;

import static az.qrfood.backend.client.controller.ClientDeviceController.DEVICE;

import az.qrfood.backend.client.service.ClientDeviceService;
import az.qrfood.backend.eatery.repository.EateryRepository;
import az.qrfood.backend.order.OrderStatus;
import az.qrfood.backend.order.dto.ClientDeviceDto;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.entity.Order;
import az.qrfood.backend.order.mapper.OrderMapper;
import az.qrfood.backend.order.service.OrderService;
import az.qrfood.backend.service.WebSocketService;
import az.qrfood.backend.table.entity.TableStatus;
import az.qrfood.backend.table.repository.TableRepository;
import az.qrfood.backend.table.service.TableService;
import az.qrfood.backend.user.UserUtils;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing orders within eateries.
 * <p>
 * This controller provides API endpoints for creating, retrieving, updating,
 * and deleting orders. It also handles order status updates and integrates
 * with client device management for cookie-based sessions.
 * </p>
 */
@Log4j2
@RestController
@Tag(name = "Order Management", description = "API endpoints for managing orders in eateries")
public class OrderController {

    //<editor-fold desc="Fields">
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final ClientDeviceService clientDeviceService;
    private final WebSocketService webSocketService;
    private final TableService tableService;
    private final EateryRepository eateryRepository;
    private final TableRepository tableRepository;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Constructs the OrderController with necessary service and mapper dependencies.
     *
     * @param orderService        The service for handling order business logic.
     * @param orderMapper         The mapper for converting between Order entities and DTOs.
     * @param clientDeviceService The service for managing client devices and cookies.
     * @param webSocketService    The service for sending WebSocket notifications.
     */
    public OrderController(OrderService orderService, OrderMapper orderMapper, ClientDeviceService clientDeviceService,
                           WebSocketService webSocketService, TableService tableService, EateryRepository eateryRepository, TableRepository tableRepository) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.clientDeviceService = clientDeviceService;
        this.webSocketService = webSocketService;
        this.tableService = tableService;
        this.eateryRepository = eateryRepository;
        this.tableRepository = tableRepository;
    }
    //</editor-fold>

    /**
     * Retrieves a list of all orders filtered by their status.
     *
     * @param status The status to filter orders by (e.g., "CREATED", "PREPARING", "READY").
     * @return A {@link ResponseEntity} containing a list of {@link OrderDto} objects
     * that match the specified status.
     */
    @Operation(summary = "Get all orders by status", description = "Retrieves a list of all orders with the specified status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "400", description = "Invalid status value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping("${order.status.auth}")
    public ResponseEntity<List<OrderDto>> getOrdersByStatus(@PathVariable("status") String status) {
        log.debug("GET all order by status [{}]", status);
        return ResponseEntity.ok(orderService.getAllOrdersByStatus(status));
    }

    /**
     * Retrieves a list of all orders for a specific eatery.
     *
     * @param eateryId The ID of the eatery to retrieve orders for.
     * @return A {@link ResponseEntity} containing a list of {@link OrderDto} objects
     * associated with the specified eatery.
     */
    @Operation(summary = "Get orders by eatery ID", description = "Retrieves a list of all orders for the specified eatery", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'WAITER', 'CASHIER')")
    @GetMapping("${orders}")
    public ResponseEntity<List<OrderDto>> getOrdersByEateryId(@PathVariable Long eateryId, Principal principal) {
        log.debug("REST request to get Orders for eatery ID: {}", eateryId);
        Set<Role> roles = UserUtils.getCurrentUserRoles();
        if(roles.size() == 1 && roles.contains(Role.WAITER)){
            Long waiterId = ((User)((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();
            return ResponseEntity.ok(orderService.getOrdersByWaiterId(waiterId));
        }
        return ResponseEntity.ok(orderService.getOrdersByEateryId(eateryId));
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderId The ID of the order to retrieve.
     * @return A {@link ResponseEntity} with status 200 (OK) and the {@link OrderDto} in the body,
     * or status 404 (Not Found) if the order does not exist.
     */
    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by its ID", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the order"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${order.id}")
    // [[getOrderById]]
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long orderId) {
        log.debug("REST request to get Order : {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    /**
     * Creates a new order for a specific eatery table.
     * <p>
     * This endpoint also handles the creation of a client device cookie if one
     * does not already exist, associating the order with the client device.
     * </p>
     *
     * @param response The {@link HttpServletResponse} to add the client device cookie.
     * @param eateryId The ID of the eatery where the order is placed.
     * @param orderDto The {@link OrderDto} containing the details of the order items.
     * @return A {@link ResponseEntity} with status 200 (OK) and the newly created {@link OrderDto} in the body.
     */
    @Operation(summary = "Create a new order", description = "Creates a new order for a specific table with the provided items", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Table not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("${order.post}")
    // [[postOrder]]
    public ResponseEntity<OrderDto> postOrder(HttpServletResponse response,
                                              @PathVariable Long eateryId,
                                              @RequestBody OrderDto orderDto,
                                              @CookieValue(value = DEVICE, required = false) String deviceUuid
    ) {
        log.debug("REST request to create Order for table ID: {}", eateryId);

        Order order = orderService.createOrder(orderDto);
        tableService.updateTableStatus(orderDto.getTableId(), TableStatus.BUSY);

        Cookie cookie = clientDeviceService.resolveCookieUuid(deviceUuid, order);
        response.addCookie(cookie);

        // Send WebSocket notification about the new order
        webSocketService.notifyNewOrder(String.valueOf(eateryId));

        return ResponseEntity.ok(orderMapper.toDto(order));
    }

    /**
     * Updates an existing order.
     *
     * @param orderId  The ID of the order to update.
     * @param orderDTO The {@link OrderDto} containing the updated order data.
     * @return A {@link ResponseEntity} with status 200 (OK) and the updated {@link OrderDto} in the body.
     */
    @Operation(summary = "Update an existing order", description = "Updates an order with the specified ID using the provided data", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'KITCHEN_ADMIN', 'CASHIER', 'WAITER')")
    @PutMapping("${order.id.put}")
    public ResponseEntity<OrderDto> updateOrder(
            @PathVariable Long eateryId,
            @PathVariable Long orderId,
            @RequestBody OrderDto orderDTO
    ) {
        log.debug("REST request to update Order : {}", orderId);

        Set<Role> authorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(roleStr -> Role.valueOf(roleStr.replace("ROLE_", "")))
                .collect(Collectors.toSet());

        // Get the updated order
        OrderDto updatedOrder = orderService.updateOrder(orderId, orderDTO, authorities);

        // Send WebSocket notification about the updated order
        webSocketService.notifyOrderUpdate(String.valueOf(eateryId), orderId, 
            orderDTO.getStatus() != null ? orderDTO.getStatus().toString() : null);

        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param orderId The ID of the order to delete.
     * @return A {@link ResponseEntity} with status 200 (OK) upon successful deletion.
     */
    @Operation(summary = "Delete an order", description = "Deletes an order with the specified ID", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("@authz.hasAnyRole(authentication, 'EATERY_ADMIN', 'WAITER')")
    @DeleteMapping("${order.id.delete}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        // Retrieve the order entity to extract eatery ID before deletion
        Order order = orderService.getOrderEntityById(orderId);
        Long eateryId = order.getTable().getEatery().getId();

        // Perform deletion
        orderService.deleteOrder(orderId);

        // Notify via WebSocket so other clients update in real-time
        webSocketService.notifyOrderDeleted(String.valueOf(eateryId), orderId);

        return ResponseEntity.ok().build();
    }

    /**
     * Adds dishes to an existing order that has not been paid yet.
     *
     * @param orderId  The ID of the order to add dishes to.
     * @param orderDTO The {@link OrderDto} containing the new dishes to add.
     * @return A {@link ResponseEntity} with status 200 (OK) and the updated {@link OrderDto} in the body.
     */
    @Operation(summary = "Add dishes to an existing order", description = "Adds dishes to an order that has not been paid yet and updates its status to PREPARING", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dishes added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("${order.id.add-dishes}")
    public ResponseEntity<OrderDto> addDishesToOrder(@PathVariable Long eateryId, @PathVariable Long orderId,
            @RequestBody OrderDto orderDTO
    ) {
        log.debug("REST request to add dishes to Order : {}", orderId);

        // Get the updated order
        OrderDto updatedOrder = orderService.addDishesToOrder(orderId, orderDTO);

        // Send WebSocket notification about the updated order
        webSocketService.notifyOrderUpdate(String.valueOf(eateryId), orderId, OrderStatus.PREPARING.toString());

        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Retrieves the list of order with status "CREATED" for a specific eatery and device.
     * <p>
     * This endpoint is used by the client application to determine whether to show
     * the order decision page or the menu page when a user scans a QR code.
     * </p>
     *
     * @param eateryId   The ID of the eatery to check orders for.
     * @param deviceUuid The UUID of the client device from the cookie.
     * @return A {@link ResponseEntity} containing a list of {@link OrderDto} objects
     * with status "CREATED" for the specified eatery and device.
     */
    @Operation(summary = "Check for created orders", description = "Checks if there are any orders with status 'CREATED' for a specific eatery and device", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${order.status}")
    public ResponseEntity<List<OrderDto>> getOrdersByEateryIdDeviceUuid(
            @PathVariable Long eateryId,
            @CookieValue(value = DEVICE, required = false) String deviceUuid
    ) {
        log.debug("REST request to check for created orders for eatery ID: {} and device UUID: {}",
                eateryId, deviceUuid);

        // If no device UUID is provided
        if (deviceUuid == null || deviceUuid.isEmpty()) {
            String error = String.format("Device UUID for eatery [%s] is null, unable to provide orders for" +
                    " unknown device", eateryId);
            log.debug(error);
            throw new EntityNotFoundException(error);
        }

        // Get orders with the status "CREATED" for the specified eatery and device
        List<OrderDto> orders = orderService.getClientOrders(eateryId, deviceUuid);

        return ResponseEntity.ok(orders);
    }

    /**
     * Retrieves the list of order with status "CREATED" for a specific eatery and device.
     * <p>
     * This endpoint is used by the client application to determine whether to show
     * the order decision page or the menu page when a user scans a QR code.
     * </p>
     *
     * @param eateryId   The ID of the eatery to check orders for.
     * @param deviceUuid The UUID of the client device from the cookie.
     * @return A {@link ResponseEntity} containing a list of {@link OrderDto} objects
     * with status "CREATED" for the specified eatery and device.
     */
    @Operation(summary = "Check for created orders", description = "Checks if there are any orders with status" +
            " 'CREATED' for a specific eatery and device", tags = {"Order Management"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of orders"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${api.eatery.order.status.created}")
    public ResponseEntity<ClientDeviceDto> getOrdersByEateryIdDeviceUuid(
            @PathVariable Long eateryId,
            @PathVariable Long tableId,
            @CookieValue(value = DEVICE, required = false) String deviceUuid
    ) {
        log.debug("Check if a client device [{}] has created orders in eatery ID: [{}]", deviceUuid, eateryId);

        // If no device UUID is provided
        if (deviceUuid == null || deviceUuid.isEmpty()) {
            String error = String.format("Device UUID for eatery [%s] is null, unable to provide orders for" +
                    " unknown device", eateryId);
            log.debug(error);
            throw new EntityNotFoundException(error);
        }

        // Get orders with the status "CREATED" for the specified eatery and device
        boolean hasOrders  = !orderService.getClientOrders(eateryId, deviceUuid).isEmpty();
        String eateryName = eateryRepository.findById(eateryId).orElseThrow().getName();
        String tableName = tableRepository.findById(tableId).orElseThrow().getTableNumber();

        return ResponseEntity.ok(new ClientDeviceDto(eateryId, eateryName, tableId, tableName, hasOrders));
    }

}
