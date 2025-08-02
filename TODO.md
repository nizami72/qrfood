# TODO LIST

## Modify orhan removal regarding orders and devices entities
## While creating a new user, it is created a new eatery, notified when run Kimber test
## User phone box is not shown if there was no number specified while new user creation
## Cloudflare Turnstile
## Delete all user-related data when a user is deleted if the user has the role eatery-admin
## To secure public API methods from scam and overloading
## Status order
## The table status should be busy if an order is assigned to the table
## Create a new email for qrfood.
## Check is a user allowed to perform operations on resources?

## Interceptors to check eligibility of eaccess by sertain user

| Needed  | Implemented | Tested   | Controller             |
|---------|-------------|----------|------------------------|
| _       | _           | _        | AdminController        |
| _       | _           | _        | AuthController         |
| _       | _           | _        | CategoryController     |
| _       | _           | _        | ClientDeviceController |
| _       | _           | _        | CommonDishController   |
| _       | &check;     | _        | DishController         |
| _       | _           | _        | EateryController       |
| _       | _           | _        | FrontendLogController  |
| _       | _           | _        | FrontendPathConfig     |
| _       | _           | _        | ImageController        |
| _       | _           | _        | OrderController        |
| _       | _           | _        | OrderItemController    |
| _       | _           | _        | QrController           |
| _       | _           | _        | TableController        |
| _       | _           | _        | UserController         |

* &check; /api/eatery/{eateryId}/category/{categoryId}/dish/{dishId}
* &#10007; /api/admin/api/eatery/{eateryId}/user/{userId}
* &#10007; /api/client/eatery/{eateryId}/table/{tableId}
* &#10007; /api/eatery/{eateryId}/category/{categoryId}
* &#10007; /api/eatery/{eateryId}/order-item/order/{orderId}
* &#10007; /api/eatery/{eateryId}/order-item/{orderItemId}
* &#10007; /api/eatery/{eateryId}/order/{orderId}
* &#10007; /api/eatery/{eateryId}/table/{tableId}
* &#10007; /api/eatery/{eateryId}/user/name/{userName}
* &#10007; /api/eatery/{eateryId}/user/{userId}
* &#10007; /api/qrcode/api/eatery/{eatery}/table/{table}