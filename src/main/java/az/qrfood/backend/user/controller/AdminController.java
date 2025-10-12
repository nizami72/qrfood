package az.qrfood.backend.user.controller;

import az.qrfood.backend.auth.service.CustomUserDetailsService;
import az.qrfood.backend.auth.util.JwtUtil;
import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dish.service.DishService;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.service.OrderService;
import az.qrfood.backend.orderitem.service.OrderItemService;
import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.service.TableService;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;
import az.qrfood.backend.tableassignment.service.TableAssignmentService;
import az.qrfood.backend.user.dto.UserResponse;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for managing Admin users.
 */
@RestController
@Log4j2
@Tag(name = "Admin Management", description = "API endpoints for managing admin users")
public class AdminController {

    //<editor-fold desc="Fields">
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final CategoryService categoryService;
    private final DishService dishService;
    private final UserService userService;
    private final EateryService eateryService;
    private final TableService tableService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final TableAssignmentService tableAssignmentService;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    public AdminController(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil,
                           CategoryService categoryService, DishService dishService,
                           UserService userService, EateryService eateryService, TableService tableService,
                           OrderService orderService, OrderItemService orderItemService,
                           TableAssignmentService tableAssignmentService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.categoryService = categoryService;
        this.dishService = dishService;
        this.userService = userService;
        this.eateryService = eateryService;
        this.tableService = tableService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.tableAssignmentService = tableAssignmentService;
    }
    //</editor-fold>

    /**
     * Retrieve all resources (categories, dishes, users) for a specific eatery.
     * <p>
     * This endpoint returns a JSON document listing all resource IDs that belong to the specified eatery,
     * including categories, dishes within those categories, and users associated with the eatery.
     * </p>
     *
     * @param eateryId The ID of the eatery to retrieve resources for.
     * @return ResponseEntity with a JSON containing the resource IDs.
     */
    @Operation(summary = "Get all resources for an eatery", description = "Returns all resource IDs (categories, dishes, users) that belong to the specified eatery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to access this eatery"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${admin.resources}")
    @PreAuthorize("@authz.isSuperAdmin(authentication)")
    public ResponseEntity<?> getEateryResources(@PathVariable Long eateryId) {
        log.info("Retrieving all resources for eatery with ID: {}", eateryId);

        // Get all categories for the eatery
        List<CategoryDto> categories = categoryService.findAllCategoryForEatery(eateryId);

        // Create the response map
        Map<String, Object> response = new HashMap<>();

        // Add category IDs to the response
        List<Long> categoryIds = categories.stream()
                .map(CategoryDto::getCategoryId)
                .collect(Collectors.toList());
        response.put("categories", categoryIds);

        // For each category, get all dishes and add their IDs to the response
        Map<Long, List<Long>> dishesInCategories = new HashMap<>();
        for (CategoryDto category : categories) {
            List<DishDto> dishes = dishService.getAllDishesInCategory(category.getCategoryId());
            List<Long> dishIds = dishes.stream()
                    .map(DishDto::getDishId)
                    .collect(Collectors.toList());
            dishesInCategories.put(category.getCategoryId(), dishIds);
        }
        response.put("dishes", dishesInCategories);

        // Get all users for the eatery and add their IDs to the response
        List<UserResponse> users = userService.getAllUsers(eateryId);
        List<Long> userIds = users.stream()
                .map(UserResponse::getId)
                .collect(Collectors.toList());
        response.put("users", userIds);

        // get all tables
        List<TableDto> d = tableService.listTablesForEatery(eateryId);
        List<Long> tableIds = d.stream()
                .map(TableDto::id)
                .collect(Collectors.toList());
        response.put("tables", tableIds);

        return ResponseEntity.ok(response);
    }

    /**
     * Get detailed information about an eatery including all its resources.
     * <p>
     * This endpoint returns a JSON document with detailed information about the eatery,
     * including its basic details and all resources that belong to it such as tables,
     * categories, and dishes within those categories.
     * </p>
     *
     * @param eateryId The ID of the eatery to retrieve details for.
     * @return ResponseEntity with a JSON containing the eatery details and resources.
     */
    @Operation(summary = "Get eatery details with resources", description = "Returns detailed information about an eatery including all its resources")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eatery details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to access this eatery"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("${admin.eatery.details}")
    @PreAuthorize("@authz.isSuperAdmin(authentication)")
    public ResponseEntity<?> getEateryDetails(@PathVariable Long eateryId) {
        log.info("Retrieving detailed information for eatery with ID: {}", eateryId);

        // Get the eatery details
        EateryDto eatery = eateryService.getEateryById(eateryId);
        List<Map<String, Object>> categoryList = fetchCategories(eateryId);

        // Create the response map
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", eatery.getId());
        response.put("ownerMailid", eatery.getOwnerMail());
        response.put("categories", categoryList);
        response.put("tableIds", eatery.getTableIds());
        response.put("orders", fetchOrderWithOrderItems(eateryId));
        response.put("users", userService.getAllUsers(eateryId).stream()
                .map(UserResponse::getId)
                .collect(Collectors.toCollection(ArrayList::new))
        );
        response.put("name", eatery.getName());
        response.put("address", eatery.getAddress());
        response.put("phones", eatery.getPhones());
        response.put("numberOfTables", eatery.getNumberOfTables());
        response.put("geoLat", eatery.getGeoLat());
        response.put("geoLng", eatery.getGeoLng());
        response.put("ownerProfileId", eatery.getOwnerProfileId());

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes an eatery and all related data.
     * <p>
     * This endpoint deletes an eatery and all its related data including
     * - All users (except eatery admin if there is another eatery related to them)
     * - All tables and table assignments
     * - All orders
     * - All categories and dishes
     * - All category translations
     * </p>
     *
     * @param eateryId The ID of the eatery to delete.
     * @return ResponseEntity with a success message.
     */
    @Operation(summary = "Delete eatery with all related data", description = "Deletes an eatery and all its related data including users, tables, orders, categories, dishes, and category translations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eatery deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Eatery not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to delete this eatery"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("${admin.eatery}")
    @PreAuthorize("@authz.isSuperAdmin(authentication)")
//    @Transactional
    public ResponseEntity<?> deleteEatery(@PathVariable Long eateryId) {
        log.info("Deleting eatery with ID: {} and all related data", eateryId);

        // Get eatery details
        EateryDto eatery = eateryService.getEateryById(eateryId);

        // Delete all order items for the eatery
        List<OrderDto> orders = orderService.getOrdersByEateryId(eateryId);
        for (OrderDto order : orders) {
            List<OrderItemDTO> orderItems = order.getItems();
            for (OrderItemDTO item : orderItems) {
                orderItemService.deleteOrderItem(item.getId());
            }
        }

        // Now delete all orders for the eatery
        for (OrderDto order : orders) {
            orderService.deleteOrder(order.getId());
        }
        log.info("Deleted {} orders for eatery ID: {}", orders.size(), eateryId);

        // Delete all tables and tables assigment for the eatery
        List<TableDto> tables = tableService.listTablesForEatery(eateryId);
        for (TableDto table : tables) {
            List<TableAssignmentDto> tableAssignments =
                    tableAssignmentService.getTableAssignmentsByTableId(table.id());
            for (TableAssignmentDto tableAssignment : tableAssignments) {
                log.debug("Deleting teble assigment [{}]", tableAssignment.getId());
                tableAssignmentService.deleteTableAssignment(tableAssignment.getId());
            }
            log.debug("Deleting table [{}]", table.id());
            tableService.deleteTable(table.id());
        }
        log.info("Deleted {} tables for eatery ID: {}", tables.size(), eateryId);

        // Delete all dishes in each category
        List<CategoryDto> categories = categoryService.findAllCategoryForEatery(eateryId);
        for (CategoryDto category : categories) {
            // Get and delete all dishes in the category
            List<DishDto> dishes = dishService.getAllDishesInCategory(category.getCategoryId());
            for (DishDto dish : dishes) {
                dishService.deleteDishItemById(category.getCategoryId(), dish.getDishId());
            }
            log.info("Deleted {} dishes in category ID: {}", dishes.size(), category.getCategoryId());
        }

        // Delete all categories after all dishes have been deleted
        for (CategoryDto category : categories) {
            categoryService.deleteCategory(category.getCategoryId());
        }
        log.info("Deleted {} categories for eatery ID: {}", categories.size(), eateryId);

        // Delete all users for the eatery
        List<UserResponse> users = userService.getAllUsers(eateryId);
        for (UserResponse user : users) {
            userService.deleteUser(user.getId());
        }
        log.info("Deleted {} users for eatery ID: {}", users.size(), eateryId);

        // Finally, delete the eatery
        eateryService.deleteEatery(eateryId);
        log.info("Deleted eatery ID: {}", eateryId);

        // Create the response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Eatery and all related data deleted successfully");
        response.put("eateryId", eateryId);

        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves a list of eateries associated with a specific user based on the provided admin username.
     *
     * @param admin the username of the admin whose associated eateries are to be retrieved
     * @return a ResponseEntity containing a list of EateryDto objects representing the eateries associated with the user,
     *         or a NOT_FOUND response if the user does not have the required roles
     */
    @GetMapping("${admin.eatery.admin.eateries}")
    @PreAuthorize("@authz.isSuperAdmin(authentication)")
    public ResponseEntity<List<EateryDto>> getEateryBelongToUser(@PathVariable String admin) {

        UserResponse user = userService.getUserByUsername(admin);
        Set<String> roles = user.getRoles();
        if (roles.isEmpty() || !roles.contains(Role.EATERY_ADMIN.name())) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        List<EateryDto> eateryDtos = eateryService.findEateriesByUserProfileId(user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eateryDtos);

    }


    private List<Map<String, Object>> fetchCategories(Long eateryId) {
        // Get all categories for the eatery
        List<CategoryDto> categories = categoryService.findAllCategoryForEatery(eateryId);

        // Create a list to hold category details with their dishes
        List<Map<String, Object>> categoryList = new ArrayList<>();

        // For each category, get all dishes and add them to the category
        for (CategoryDto category : categories) {
            Map<String, Object> categoryMap = new LinkedHashMap<>();
            categoryMap.put("categoryId", category.getCategoryId());

            // Get all dishes for this category
            List<DishDto> dishes = dishService.getAllDishesInCategory(category.getCategoryId());

            // Extract dish IDs
            List<Long> dishIds = dishes.stream()
                    .map(DishDto::getDishId)
                    .collect(Collectors.toList());

            categoryMap.put("dishId", dishIds);
            categoryList.add(categoryMap);
        }
        return categoryList;
    }

    private List<OrderDto> fetchOrderWithOrderItems(Long eateryId) {
        return orderService.getOrdersByEateryId(eateryId);
    }

}
