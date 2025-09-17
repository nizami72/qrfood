## Navigation

- [AdminController](#admincontroller)
- [Alive](#alive)
- [AuthController](#authcontroller)
- [CategoryController](#categorycontroller)
- [ClientDeviceController](#clientdevicecontroller)
- [CommonDishController](#commondishcontroller)
- [DishController](#dishcontroller)
- [EateryController](#eaterycontroller)
- [FrontendLogController](#frontendlogcontroller)
- [FrontendPathConfig](#frontendpathconfig)
- [ImageController](#imagecontroller)
- [OrderController](#ordercontroller)
- [OrderItemController](#orderitemcontroller)
- [QrController](#qrcontroller)
- [TableAssignmentController](#tableassignmentcontroller)
- [TableController](#tablecontroller)
- [UserController](#usercontroller)
### AdminController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteEatery(Long)](../../src/main/java/az/qrfood/backend/user/controller/AdminController.java) | `public` | `/api/admin/eatery/{eateryId}` |
| [GET getEateryDetails(Long)](../../src/main/java/az/qrfood/backend/user/controller/AdminController.java) | `public` | `/api/admin/eatery/{eateryId}/details` |
| [GET getEateryResources(Long)](../../src/main/java/az/qrfood/backend/user/controller/AdminController.java) | `public` | `/api/admin/eatery/{eateryId}/resources` |
| [POST impersonateUser(Long, Authentication)](../../src/main/java/az/qrfood/backend/user/controller/AdminController.java) | `public` | `/api/admin/impersonate/{userId}` |

###### [🔝](#navigation)

---

### Alive

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET alive()](../../src/main/java/az/qrfood/backend/common/controller/Alive.java) | `public` | `/ui/alive` |

###### [🔝](#navigation)

---

### AuthController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET logout(HttpServletResponse)](../../src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/logout` |
| [GET status()](../../src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/status` |
| [POST login(LoginRequest, HttpServletResponse)](../../src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/login` |
| [POST recreateTokenOnEateryChange(RecreateTokenOnEateryChangeRequest)](../../src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/recreate-token-on-eatery-change` |
| [POST refreshToken(HttpServletRequest, HttpServletResponse)](../../src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/refresh-token` |

###### [🔝](#navigation)

---

### CategoryController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteCategory(Long)](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}` |
| [GET getCategoryById(Long)](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER, SUPER_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}` |
| [GET getCommonCategories()](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/category/common` |
| [GET getEateryCategories(Long)](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/category` |
| [POST createDishCategory(Long, CategoryDto, MultipartFile)](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category` |
| [POST createDishCategoryNoImage(Long, CategoryDto)](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/predefined` |
| [PUT updateCategory(Long, CategoryDto, MultipartFile)](../../src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}` |

###### [🔝](#navigation)

---

### ClientDeviceController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE delete(Long)](../../src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/{id}` |
| [GET eateryCategories(Long)](../../src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/eatery/{eateryId}` |
| [GET eateryCategories(Long, Long)](../../src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/eatery/{eateryId}/table/{tableId}` |
| [GET getAll()](../../src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client` |
| [GET getById(Long)](../../src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/{id}` |
| [PUT update(Long, ClientDeviceRequestDto)](../../src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/{id}` |

###### [🔝](#navigation)

---

### CommonDishController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getCommonDishesForCategory(String, String)](../../src/main/java/az/qrfood/backend/dish/controller/CommonDishController.java) | `EATERY_ADMIN` | `/api/dish/common/{categoryName}` |
| [POST createDishesFromTemplates(Long, List)](../../src/main/java/az/qrfood/backend/dish/controller/CommonDishController.java) | `EATERY_ADMIN` | `/api/dish/common/{categoryId}` |

###### [🔝](#navigation)

---

### DishController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteDishItemById(Long, Long)](../../src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}` |
| [GET getDish(Long, Long)](../../src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER` | `/api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}` |
| [GET getDishes(Long)](../../src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/category/{categoryId}/dish` |
| [POST createDish(Long, DishDto, MultipartFile)](../../src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}/dish` |
| [PUT putDish(Long, Long, DishDto, MultipartFile)](../../src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}` |

###### [🔝](#navigation)

---

### EateryController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteEatery(Long)](../../src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}` |
| [GET getAllRestaurants()](../../src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `public` | `/api/eatery` |
| [GET getEateriesByOwnerId(Long)](../../src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN, WAITER, KITCHEN_ADMIN, CASHIER` | `/api/eatery/owner/{ownerId}` |
| [GET getEateryById(Long)](../../src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}` |
| [POST createRestaurant(EateryDto, UserDetails)](../../src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery` |
| [PUT updateEatery(Long, EateryDto)](../../src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}` |

###### [🔝](#navigation)

---

### FrontendLogController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [POST logFrontendMessage(FrontendLogDTO, HttpServletRequest)](../../src/main/java/az/qrfood/backend/log/controller/FrontendLogController.java) | `public` | `/api/logs/frontend` |

###### [🔝](#navigation)

---

### FrontendPathConfig

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getImagePaths()](../../src/main/java/az/qrfood/backend/common/controller/FrontendPathConfig.java) | `public` | `/api/config/image-paths` |

###### [🔝](#navigation)

---

### ImageController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getCategoryImageN(String, String, String, HttpServletResponse)](../../src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/eatery/{eateryId}/category/{categoryId}/file/{fileName}` |
| [GET getDishImageN(String, String, String, HttpServletResponse)](../../src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/eatery/{eateryId}/dish/{dishId}/file/{fileName}` |
| [GET getEateryImageN(String, String, HttpServletResponse)](../../src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/eatery/{eateryId}/file/{fileName}` |
| [GET getPredefinedCatImage(String, HttpServletResponse)](../../src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/predefined/category/{fileName}` |
| [GET getPredefinedDishImage(String, HttpServletResponse)](../../src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/predefined/dish/{fileName}` |

###### [🔝](#navigation)

---

### OrderController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteOrder(Long)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, WAITER` | `/api/eatery/{eateryId}/order/{orderId}/delete` |
| [GET getOrderById(Long)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `public` | `/api/eatery/{eateryId}/order/{orderId}` |
| [GET getOrdersByEateryId(Long, Principal)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/orders` |
| [GET getOrdersByEateryIdAndTableId(Long, String)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `public` | `/api/eatery/{eateryId}/order/status/created` |
| [GET getOrdersByStatus(String, String)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order/status/{status}` |
| [POST postOrder(HttpServletResponse, Long, OrderDto, String)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `public` | `/api/eatery/{eateryId}/order/post` |
| [PUT updateOrder(Long, Long, OrderDto)](../../src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, CASHIER, WAITER` | `/api/eatery/{eateryId}/order/{orderId}/put` |

###### [🔝](#navigation)

---

### OrderItemController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteOrderItem(Long)](../../src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order-item/{orderItemId}` |
| [GET getAllOrderItems()](../../src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order-item` |
| [GET getOrderItemById(Long)](../../src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order-item/{orderItemId}` |
| [GET getOrderItemsByOrderId(Long)](../../src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order-item/order/{orderId}` |
| [POST postOrderItem(OrderItemDTO)](../../src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN, WAITER` | `/api/eatery/{eateryId}/order-item/order/{orderId}` |
| [PUT putOrderItem(Long, OrderItemDTO)](../../src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN, WAITER` | `/api/eatery/{eateryId}/order-item/{orderItemId}` |

###### [🔝](#navigation)

---

### QrController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getQrImage(Long, Integer)](../../src/main/java/az/qrfood/backend/qr/controller/QrController.java) | `EATERY_ADMIN` | `/api/qrcode/eatery/{eateryId}/table/{tableId}` |

###### [🔝](#navigation)

---

### TableAssignmentController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteTableAssignment(Long, Long)](../../src/main/java/az/qrfood/backend/tableassignment/controller/TableAssignmentController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table-assignment/{assignmentId}` |
| [GET getAllTableAssignments(Long)](../../src/main/java/az/qrfood/backend/tableassignment/controller/TableAssignmentController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, CASHIER, WAITER` | `/api/eatery/{eateryId}/table-assignment` |
| [GET getTableAssignmentById(Long, Long)](../../src/main/java/az/qrfood/backend/tableassignment/controller/TableAssignmentController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER` | `/api/eatery/{eateryId}/table-assignment/{assignmentId}` |
| [GET getTableAssignmentsByTableId(Long, Long)](../../src/main/java/az/qrfood/backend/tableassignment/controller/TableAssignmentController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER` | `/api/eatery/{eateryId}/table-assignment/table/{tableId}` |
| [GET getTableAssignmentsByWaiterId(Long, Long)](../../src/main/java/az/qrfood/backend/tableassignment/controller/TableAssignmentController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER` | `/api/eatery/{eateryId}/table-assignment/waiter/{waiterId}` |
| [POST createTableAssignment(Long, CreateTableAssignmentDto)](../../src/main/java/az/qrfood/backend/tableassignment/controller/TableAssignmentController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table-assignment` |

###### [🔝](#navigation)

---

### TableController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteTable(Long)](../../src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table/{tableId}` |
| [GET getTable(Long)](../../src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN, WAITER, KITCHEN_ADMIN, CASHIER` | `/api/eatery/{eateryId}/table/{tableId}` |
| [GET getTables(Long)](../../src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN, WAITER, KITCHEN_ADMIN, CASHIER` | `/api/eatery/{eateryId}/table` |
| [POST createTable(TableDto)](../../src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table` |
| [PUT updateTable(Long, TableDto)](../../src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table/{tableId}` |

###### [🔝](#navigation)

---

### UserController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteUser(Long, Long)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/{userId}` |
| [GET getAllEateryUsers(Long)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN, CASHIER, WAITER, KITCHEN_ADMIN` | `/api/eatery/{eateryId}/user` |
| [GET getAllUsers()](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/eatery/{eateryId}/user/users` |
| [GET getAllUsersFromAllEateries()](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/user` |
| [GET getUserById(Long)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/{userId}` |
| [GET getUserByUsername(String)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/name/{userName}` |
| [POST deleteUserByName(String)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/user/{id}` |
| [POST postEateryAdminUser(RegisterRequest)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/admin/eatery` |
| [POST registerEateryStaff(RegisterRequest, Long)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/register/general` |
| [PUT putUser(Long, UserRequest)](../../src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/{userId}` |

###### [🔝](#navigation)

---

