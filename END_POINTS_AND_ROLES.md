### OrderItemController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteOrderItem(Long)](src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order-item/{orderItemId}` |
| [GET getAllOrderItems()](src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order-item` |
| [GET getOrderItemById(Long)](src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order-item/{orderItemId}` |
| [GET getOrderItemsByOrderId(Long)](src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order-item/order/{orderId}` |
| [POST postOrderItem(OrderItemDTO)](src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order-item/order/{orderId}` |
| [PUT putOrderItem(Long, OrderItemDTO)](src/main/java/az/qrfood/backend/orderitem/controller/OrderItemController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order-item/{orderItemId}` |

---

### DishController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteDishItemById(Long, Long)](src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}` |
| [GET getDish(Long, Long)](src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER` | `/api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}` |
| [GET getDishes(Long)](src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/category/{categoryId}/dish` |
| [POST createDish(Long, DishDto, MultipartFile)](src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}/dish` |
| [PUT putDish(Long, Long, DishDto, MultipartFile)](src/main/java/az/qrfood/backend/dish/controller/DishController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}` |

---

### ImageController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getCategoryImage(String, String, HttpServletResponse)](src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/category/{id}/{fileName}` |
| [GET getDishImage(String, String, HttpServletResponse)](src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/dish/{id}/{fileName}` |
| [GET getEateryImage(String, String, HttpServletResponse)](src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/api/eatery/{id}/{fileName}` |
| [GET getPredefinedCatImage(String, HttpServletResponse)](src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/predefined/category/{fileName}` |
| [GET getPredefinedDishImage(String, HttpServletResponse)](src/main/java/az/qrfood/backend/image/controller/ImageController.java) | `public` | `/api/image/predefined/dish/{fileName}` |

---

### FrontendLogController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [POST logFrontendMessage(FrontendLogDTO, HttpServletRequest)](src/main/java/az/qrfood/backend/log/controller/FrontendLogController.java) | `public` | `/api/logs/frontend` |

---

### EateryController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteEatery(Long)](src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}` |
| [GET getAllRestaurants()](src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `public` | `/api/eatery` |
| [GET getEateriesByOwnerId(Long)](src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN, WAITER, KITCHEN_ADMIN, CASHIER` | `/api/eatery/owner/{ownerId}` |
| [GET getEateryById(Long)](src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}` |
| [POST createRestaurant(EateryDto, UserDetails)](src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery` |
| [PUT updateEatery(Long, EateryDto)](src/main/java/az/qrfood/backend/eatery/controller/EateryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}` |

---

### AuthController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET status()](src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/status` |
| [POST login(LoginRequest)](src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/login` |
| [POST logout()](src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/logout` |
| [POST refreshToken(Map)](src/main/java/az/qrfood/backend/auth/controller/AuthController.java) | `public` | `/api/auth/refresh-token` |

---

### TableController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteTable(Long)](src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table/{tableId}` |
| [GET getTable(Long)](src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN, WAITER, KITCHEN_ADMIN, CASHIER` | `/api/eatery/{eateryId}/table/{tableId}` |
| [GET getTables(Long)](src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN, WAITER, KITCHEN_ADMIN, CASHIER` | `/api/eatery/{eateryId}/table` |
| [POST createTable(TableDto)](src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table` |
| [PUT updateTable(Long, TableDto)](src/main/java/az/qrfood/backend/table/controller/TableController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/table/{tableId}` |

---

### QrController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getQrImage(Long, Integer)](src/main/java/az/qrfood/backend/qr/controller/QrController.java) | `EATERY_ADMIN` | `/api/qrcode/api/eatery/{eatery}/table/{table}` |

---

### Alive

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET alive()](src/main/java/az/qrfood/backend/common/controller/Alive.java) | `public` | `/ui/alive` |

---

### OrderController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteOrder(Long)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/order/{orderId}` |
| [GET getAllOrders(String, String)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order/status/{status}` |
| [GET getOrderById(Long)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `public` | `/api/eatery/{eateryId}/order/{orderId}` |
| [GET getOrdersByEateryId(Long)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/order` |
| [GET getOrdersByEateryIdAndStatusCreated(Long, String)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `public` | `/api/eatery/{eateryId}/order/status/created` |
| [POST postOrder(HttpServletResponse, Long, OrderDto, String)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `public` | `/api/eatery/{eateryId}/order/post` |
| [PUT updateOrder(Long, OrderDto)](src/main/java/az/qrfood/backend/order/controller/OrderController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, CASHIER` | `/api/eatery/{eateryId}/order/{orderId}` |

---

### CommonDishController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getCommonDishesForCategory(String, String)](src/main/java/az/qrfood/backend/dish/controller/CommonDishController.java) | `EATERY_ADMIN` | `/api/dish/common/{categoryName}` |
| [POST createDishesFromTemplates(Long, List)](src/main/java/az/qrfood/backend/dish/controller/CommonDishController.java) | `EATERY_ADMIN` | `/api/dish/common/{categoryId}` |

---

### ClientDeviceController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE delete(Long)](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/{id}` |
| [GET eateryCategories(Long)](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/eatery/{eateryId}` |
| [GET eateryCategories(Long, Long)](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/eatery/{eateryId}/table/{tableId}` |
| [GET getAll()](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client` |
| [GET getById(Long)](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/{id}` |
| [POST create(ClientDeviceRequestDto)](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client` |
| [PUT update(Long, ClientDeviceRequestDto)](src/main/java/az/qrfood/backend/client/controller/ClientDeviceController.java) | `public` | `/api/client/{id}` |

---

### CategoryController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteCategory(Long)](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}` |
| [GET getCategoryById(Long)](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER, SUPER_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}` |
| [GET getCommonCategories()](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/category/common` |
| [GET getEateryCategories(Long)](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN, KITCHEN_ADMIN, WAITER, CASHIER` | `/api/eatery/{eateryId}/category` |
| [POST createDishCategory(Long, CategoryDto, MultipartFile)](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category` |
| [POST createDishCategoryNoImage(Long, CategoryDto)](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/predefined` |
| [PUT updateCategory(Long, CategoryDto, MultipartFile)](src/main/java/az/qrfood/backend/category/controller/CategoryController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/category/{categoryId}` |

---

### UserController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [DELETE deleteUser(Long, Long)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/{userId}` |
| [GET getAllEateryUsers(Long)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user` |
| [GET getAllUsers()](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/eatery/{eateryId}/user/users` |
| [GET getAllUsersFromAllEateries()](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/user` |
| [GET getUserById(Long)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/{userId}` |
| [GET getUserByUsername(String)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/name/{userName}` |
| [POST deleteUserByName(String)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `SUPER_ADMIN` | `/api/user/{id}` |
| [POST postEateryAdminUser(RegisterRequest)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `public` | `/api/admin/eatery` |
| [POST registerEateryStaff(RegisterRequest, Long)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/register/general` |
| [PUT putUser(Long, UserRequest)](src/main/java/az/qrfood/backend/user/controller/UserController.java) | `EATERY_ADMIN` | `/api/eatery/{eateryId}/user/{userId}` |

---

### FrontendPathConfig

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [GET getImagePaths()](src/main/java/az/qrfood/backend/common/controller/FrontendPathConfig.java) | `public` | `/api/config/image-paths` |

---

### AdminController

| Method | Role(s) | URL Path |
|--------|---------|----------|
| [POST impersonateUser(Long, Authentication)](src/main/java/az/qrfood/backend/user/controller/AdminController.java) | `public` | `/api/admin/impersonate/{userId}` |

---

