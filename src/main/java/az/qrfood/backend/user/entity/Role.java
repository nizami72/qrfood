package az.qrfood.backend.user.entity;

public enum Role {
    SUPER_ADMIN,        // system admin, manages everything
    EATERY_ADMIN,       // admin of a specific restaurant, manages the menu, users, etc.
    KITCHEN_ADMIN,      // kitchen admin, sees and can edit only orders
    WAITER,             // waiter can change the status of orders
    CASHIER             // cashier sees receipts and completes payment
}
