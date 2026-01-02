package az.qrfood.backend.user.service;

import az.qrfood.backend.category.dto.CategoryDto;
import az.qrfood.backend.category.service.CategoryService;
import az.qrfood.backend.dish.dto.DishDto;
import az.qrfood.backend.dish.service.DishService;
import az.qrfood.backend.eatery.dto.EateryDto;
import az.qrfood.backend.eatery.entity.Eatery;
import az.qrfood.backend.eatery.service.EateryService;
import az.qrfood.backend.mail.service.NotificationLogService;
import az.qrfood.backend.order.dto.OrderDto;
import az.qrfood.backend.order.dto.OrderItemDTO;
import az.qrfood.backend.order.service.OrderService;
import az.qrfood.backend.orderitem.service.OrderItemService;
import az.qrfood.backend.table.dto.TableDto;
import az.qrfood.backend.table.service.TableService;
import az.qrfood.backend.tableassignment.dto.TableAssignmentDto;
import az.qrfood.backend.tableassignment.service.TableAssignmentService;
import az.qrfood.backend.user.entity.Role;
import az.qrfood.backend.user.entity.User;
import az.qrfood.backend.user.entity.UserProfile;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class AdminService {

    private final EateryService eateryService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final TableService tableService;
    private final TableAssignmentService tableAssignmentService;
    private final CategoryService categoryService;
    private final DishService dishService;
    private final UserService userService;
    private final UserProfileService userProfileService;
    private final NotificationLogService notificationLogService;

    public AdminService(EateryService eateryService, OrderService orderService, OrderItemService orderItemService, TableService tableService, TableAssignmentService tableAssignmentService, CategoryService categoryService, DishService dishService, UserService userService, UserProfileService userProfileService, NotificationLogService notificationLogService) {
        this.eateryService = eateryService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.tableService = tableService;
        this.tableAssignmentService = tableAssignmentService;
        this.categoryService = categoryService;
        this.dishService = dishService;
        this.userService = userService;
        this.userProfileService = userProfileService;
        this.notificationLogService = notificationLogService;
    }


    public void deleteEatery(Long eateryId) {
        log.info("Deleting eatery with ID: {} and all related data", eateryId);

        // Get eatery details
        EateryDto eatery = eateryService.getEateryById(eateryId);

        // DELETE all order items for the eatery
        List<OrderDto> orders = orderService.getOrdersByEateryId(eateryId);
        for (OrderDto order : orders) {
            List<OrderItemDTO> orderItems = order.getItems();
            for (OrderItemDTO item : orderItems) {
                orderItemService.deleteOrderItem(item.getId());
            }
        }

        // DELETE all orders for the eatery
        for (OrderDto order : orders) {
            orderService.deleteOrder(order.getId());
        }
        log.info("Deleted {} orders for eatery ID: {}", orders.size(), eateryId);

        // DELETE all tables and tables assigment for the eatery
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

        // DELETE all dishes in each category
        List<CategoryDto> categories = categoryService.findAllCategoryForEatery(eateryId);
        for (CategoryDto category : categories) {
            // Get and delete all dishes in the category
            List<DishDto> dishes = dishService.getAllDishesInCategory(category.getCategoryId());
            for (DishDto dish : dishes) {
                dishService.deleteDishItemById(category.getCategoryId(), dish.getDishId());
            }
            log.info("Deleted {} dishes in category ID: {}", dishes.size(), category.getCategoryId());
        }

        // DELETE all categories after all dishes have been deleted
        for (CategoryDto category : categories) {
            categoryService.deleteCategory(category.getCategoryId());
        }
        log.info("Deleted {} categories for eatery ID: {}", categories.size(), eateryId);

        // DELETE all users for the eatery
        List<User> users = userService.findAllUsers(eateryId);

        for (User user : users) {
            Optional<UserProfile> userProfileOp = userProfileService.findProfileByUser(user);
            if (userProfileOp.isEmpty()) continue;
            UserProfile userProfile = userProfileOp.get();
            List<Eatery> e = userProfile.getEateries();
            if (e.size() == 1) {
                userService.deleteUser(user.getId());
            } else {
                userProfileService.removeRestaurantFromProfile(userProfile, eateryId);
            }
        }

        // delete all notification logs of deleted eatery admin
        Optional<User> user = users.stream().filter(u -> u.getRoles().contains(Role.EATERY_ADMIN)).findFirst();
        user.ifPresent(value -> notificationLogService.deleteByEateryId(value.getUsername()));


        log.info("Deleted [{}] users for eatery ID [{}] ", users.size(), eateryId);

        // Finally, delete the eatery
        Long deletedEateryId = eateryService.deleteEatery(eateryId);
        log.info("Deleted eatery ID: {}", deletedEateryId);

    }
}