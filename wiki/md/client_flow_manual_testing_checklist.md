# Client Flow Manual Testing Checklist

This document provides a comprehensive step-by-step checklist for manual testing of all client flows and backend calls in the QR Food Order System. It covers all actions an end user can make from the app, organized by user role.

- [Client Flow](#client-flow)
- [Eatery Admin Flow](#2-eatery-admin-flow)
- [KITCHEN_ADMIN Flow](#KITCHEN_ADMIN flow)
- [WAITER Flow](#WAITER)
- [CASHIER Flow](#)
- [Cross-Role Testing](#)
- [Edge Cases and Error Handling](#)
- [Performance Testing](#)

## Prerequisites

Before starting the testing process, ensure the following prerequisites are in place:

1. The application is deployed and running (both frontend and backend)
2. The database is properly initialized with test data
3. You have access to accounts with the following roles:
   - CLIENT (no login required, uses QR code scanning)
   - EATERY_ADMIN
   - KITCHEN_ADMIN
   - WAITER
   - CASHIER
4. Test restaurant(s) with categories, dishes, and tables are set up
5. QR codes for tables are generated and accessible
6. Mobile device or emulator for testing client flows
7. Desktop browser for testing admin flows

---

## Client Flow

### 1.1 Initial QR Code Scanning

#### Test Case: First-time QR Code Scan
- **Prerequisites:**
  - Mobile device with camera
  - QR code for a specific table in a restaurant
  - No existing cookie for the client device

- **Steps:**
  1. Open the camera app on the mobile device
  2. Scan the QR code for a table
  3. Open the link from the QR code

- **Expected Results:**
  - The application opens in the browser
  - The menu page loads with categories and dishes for the specific restaurant
  - The table number is displayed
  - No client device cookie is set yet (will be set upon order confirmation)
  - Backend call: GET `${api.client.eatery.table}` with eateryId and tableId parameters

### 1.2 Menu Browsing

#### Test Case: Browse Menu Categories and Dishes
- **Prerequisites:**
  - Successfully scanned QR code and loaded menu page

- **Steps:**
  1. Scroll through the available categories
  2. Click on different category buttons
  3. View dishes within each category

- **Expected Results:**
  - Categories are displayed in a horizontal scrollable list
  - Clicking a category filters the dishes to show only those in the selected category
  - Each dish displays its name, description, price, and an image (if available)
  - Add to cart button (+) is visible for each dish
  - No backend calls are made during menu browsing (data already loaded)

### 1.3 Adding Items to Cart

#### Test Case: Add Items to Cart
- **Prerequisites:**
  - Successfully loaded menu page

- **Steps:**
  1. Click the "+" button on a dish
  2. Add multiple quantities of the same dish by clicking "+" multiple times
  3. Add different dishes to the cart
  4. Click "Add Note" on a dish and enter a special instruction

- **Expected Results:**
  - Dish is added to cart
  - Quantity counter appears next to the dish
  - Cart button appears at the bottom of the screen showing total items and amount
  - Note is saved with the dish
  - No backend calls are made during cart management (handled client-side)

### 1.4 Cart Management

#### Test Case: View and Modify Cart
- **Prerequisites:**
  - Items added to cart

- **Steps:**
  1. Click on the cart button at the bottom of the screen
  2. Review items in the cart
  3. Increase quantity of an item
  4. Decrease quantity of an item
  5. Remove an item completely
  6. Edit note for an item

- **Expected Results:**
  - Cart page displays all added items with quantities and notes
  - Total amount is calculated correctly
  - Items can be modified or removed
  - Changes are reflected in the cart total
  - No backend calls are made during cart management (handled client-side)

### 1.5 Order Placement

#### Test Case: Place an Order
- **Prerequisites:**
  - Items added to cart
  - Reviewed cart contents

- **Steps:**
  1. Click "Proceed to Order" or equivalent button
  2. Review the final order details
  3. Confirm the order

- **Expected Results:**
  - Order confirmation page is displayed
  - Order details are shown with items, quantities, and total amount
  - Success message is displayed
  - A client device cookie is set in the browser
  - Backend calls:
    - POST `${order.post}` with eateryId and order details
    - WebSocket notification is sent to notify staff about the new order

### 1.6 Order Tracking

#### Test Case: Track Order Status
- **Prerequisites:**
  - Successfully placed an order
  - Client device cookie is set

- **Steps:**
  1. Wait for order status updates
  2. Refresh the page or navigate back to the restaurant (by scanning QR code again)

- **Expected Results:**
  - Order status page is displayed instead of menu when scanning the same QR code
  - Current status of the order is shown (CREATED, PREPARING, READY, etc.)
  - Order details are displayed
  - Backend calls:
    - GET `${api.eatery.order.status.created}` to check for existing orders
    - WebSocket connection to receive real-time updates

### 1.7 Additional Order

#### Test Case: Add More Items to Existing Order
- **Prerequisites:**
  - Existing order with status "CREATED"
  - On the order status page

- **Steps:**
  1. Click "Add More Items" or equivalent button
  2. Browse the menu and add more items
  3. Proceed to checkout
  4. Confirm the additional order

- **Expected Results:**
  - Menu page loads with categories and dishes
  - New items can be added to cart
  - New items are added to the existing order
  - Updated order is displayed with all items
  - Backend calls:
    - PUT `${order.id.put}` to update the existing order
    - WebSocket notification is sent about the order update

---

## 2. Eatery Admin Flow

### 2.1 Authentication

#### Test Case: Admin Login
- **Prerequisites:**
  - Admin credentials (email and password)

- **Steps:**
  1. Navigate to the login page
  2. Enter admin email and password
  3. Click "Login" button

- **Expected Results:**
  - Admin is successfully authenticated
  - Admin dashboard is displayed
  - Backend call: POST to authentication endpoint with credentials

### 2.2 Restaurant Management

#### Test Case: View and Edit Restaurant Details
- **Prerequisites:**
  - Logged in as EATERY_ADMIN

- **Steps:**
  1. Navigate to restaurant management section
  2. View current restaurant details
  3. Edit restaurant information (name, address, etc.)
  4. Save changes

- **Expected Results:**
  - Restaurant details are displayed
  - Changes are saved successfully
  - Backend calls:
    - GET to retrieve restaurant details
    - PUT to update restaurant information

### 2.3 Category Management

#### Test Case: Create, Edit, and Delete Categories
- **Prerequisites:**
  - Logged in as EATERY_ADMIN

- **Steps:**
  1. Navigate to category management section
  2. View existing categories
  3. Create a new category with name in multiple languages
  4. Upload an image for the category
  5. Edit an existing category
  6. Delete a category
  7. Reorder categories using drag and drop

- **Expected Results:**
  - Categories are displayed in a list
  - New category is created successfully
  - Category is updated with new information
  - Category is deleted (with confirmation if it contains dishes)
  - Categories are reordered according to drag and drop
  - Backend calls:
    - GET to retrieve categories
    - POST to create a new category
    - PUT to update a category
    - DELETE to remove a category
    - PUT to update category order

### 2.4 Dish Management

#### Test Case: Create, Edit, and Delete Dishes
- **Prerequisites:**
  - Logged in as EATERY_ADMIN
  - At least one category exists

- **Steps:**
  1. Navigate to dish management section
  2. View existing dishes
  3. Create a new dish with name, description, price, and category
  4. Upload an image for the dish
  5. Edit an existing dish
  6. Delete a dish

- **Expected Results:**
  - Dishes are displayed in a list
  - New dish is created successfully
  - Dish is updated with new information
  - Dish is deleted
  - Backend calls:
    - GET to retrieve dishes
    - POST to create a new dish
    - PUT to update a dish
    - DELETE to remove a dish

### 2.5 Table Management

#### Test Case: Create, Edit, and Delete Tables
- **Prerequisites:**
  - Logged in as EATERY_ADMIN

- **Steps:**
  1. Navigate to table management section
  2. View existing tables
  3. Create a new table with number and capacity
  4. Generate QR code for the table
  5. Edit an existing table
  6. Delete a table

- **Expected Results:**
  - Tables are displayed in a list
  - New table is created successfully
  - QR code is generated and can be downloaded
  - Table is updated with new information
  - Table is deleted
  - Backend calls:
    - GET to retrieve tables
    - POST to create a new table
    - GET to generate QR code
    - PUT to update a table
    - DELETE to remove a table

### 2.6 Staff Management

#### Test Case: Create, Edit, and Delete Staff Accounts
- **Prerequisites:**
  - Logged in as EATERY_ADMIN

- **Steps:**
  1. Navigate to staff management section
  2. View existing staff accounts
  3. Create a new staff account with role (KITCHEN_ADMIN, WAITER, CASHIER)
  4. Edit an existing staff account
  5. Delete a staff account

- **Expected Results:**
  - Staff accounts are displayed in a list
  - New staff account is created successfully
  - Staff account is updated with new information
  - Staff account is deleted
  - Backend calls:
    - GET to retrieve staff accounts
    - POST to create a new staff account
    - PUT to update a staff account
    - DELETE to remove a staff account

### 2.7 Order Management

#### Test Case: View and Manage Orders
- **Prerequisites:**
  - Logged in as EATERY_ADMIN
  - At least one order exists

- **Steps:**
  1. Navigate to order management section
  2. View list of orders with different statuses
  3. View details of a specific order
  4. Update order status
  5. Delete an order

- **Expected Results:**
  - Orders are displayed in a list with their statuses
  - Order details show items, quantities, table, and status
  - Order status is updated successfully
  - Order is deleted
  - Backend calls:
    - GET to retrieve orders
    - GET to retrieve specific order details
    - PUT to update order status
    - DELETE to remove an order
    - WebSocket notifications are sent for order updates

---

## 3. KITCHEN_ADMIN Flow

### 3.1 Authentication

#### Test Case: Kitchen Admin Login
- **Prerequisites:**
  - Kitchen admin credentials (email and password)

- **Steps:**
  1. Navigate to the login page
  2. Enter kitchen admin email and password
  3. Click "Login" button

- **Expected Results:**
  - Kitchen admin is successfully authenticated
  - Kitchen dashboard is displayed
  - Backend call: POST to authentication endpoint with credentials

### 3.2 Order Queue Management

#### Test Case: View and Update Order Queue
- **Prerequisites:**
  - Logged in as KITCHEN_ADMIN
  - At least one order with status "CREATED" exists

- **Steps:**
  1. View the order queue
  2. Select an order to view details
  3. Update order status to "PREPARING"
  4. Complete preparation and update status to "READY"

- **Expected Results:**
  - Order queue displays all orders with status "CREATED"
  - Order details show items, quantities, and special instructions
  - Order status is updated successfully
  - Orders move through the workflow (CREATED → PREPARING → READY)
  - Backend calls:
    - GET to retrieve orders by status
    - PUT to update order status
    - WebSocket notifications are sent for order updates

### 3.3 Real-time Updates

#### Test Case: Receive Real-time Order Notifications
- **Prerequisites:**
  - Logged in as KITCHEN_ADMIN
  - Kitchen dashboard is open

- **Steps:**
  1. Have another user (client) place a new order
  2. Observe the kitchen dashboard

- **Expected Results:**
  - New order appears in the queue in real-time
  - Notification or alert is displayed
  - No manual refresh is needed
  - Backend: WebSocket connection receives notifications about new orders

---

## 4. WAITER Flow

### 4.1 Authentication

#### Test Case: Waiter Login
- **Prerequisites:**
  - Waiter credentials (email and password)

- **Steps:**
  1. Navigate to the login page
  2. Enter waiter email and password
  3. Click "Login" button

- **Expected Results:**
  - Waiter is successfully authenticated
  - Waiter dashboard is displayed
  - Backend call: POST to authentication endpoint with credentials

### 4.2 Table Assignment

#### Test Case: View and Manage Assigned Tables
- **Prerequisites:**
  - Logged in as WAITER
  - Tables are assigned to the waiter

- **Steps:**
  1. View assigned tables
  2. Check table status (empty, occupied, etc.)

- **Expected Results:**
  - Assigned tables are displayed with their status
  - Backend call: GET to retrieve assigned tables

### 4.3 Order Delivery

#### Test Case: Deliver Ready Orders
- **Prerequisites:**
  - Logged in as WAITER
  - At least one order with status "READY" exists

- **Steps:**
  1. View orders with status "READY"
  2. Select an order to deliver
  3. Update order status to "DELIVERED" after serving

- **Expected Results:**
  - Ready orders are displayed
  - Order status is updated successfully
  - Backend calls:
    - GET to retrieve orders by status
    - PUT to update order status
    - WebSocket notifications are sent for order updates

### 4.4 Order Assistance

#### Test Case: Assist with Order Issues
- **Prerequisites:**
  - Logged in as WAITER
  - At least one active order exists

- **Steps:**
  1. View active orders for assigned tables
  2. Handle customer requests or issues
  3. Update order if needed (add items, remove items)

- **Expected Results:**
  - Active orders are displayed
  - Order can be updated with new items or changes
  - Backend calls:
    - GET to retrieve orders
    - PUT to update order
    - WebSocket notifications are sent for order updates

---

## 5. CASHIER Flow

### 5.1 Authentication

#### Test Case: Cashier Login
- **Prerequisites:**
  - Cashier credentials (email and password)

- **Steps:**
  1. Navigate to the login page
  2. Enter cashier email and password
  3. Click "Login" button

- **Expected Results:**
  - Cashier is successfully authenticated
  - Cashier dashboard is displayed
  - Backend call: POST to authentication endpoint with credentials

### 5.2 Payment Processing

#### Test Case: Process Payment for Order
- **Prerequisites:**
  - Logged in as CASHIER
  - At least one order with status "DELIVERED" exists

- **Steps:**
  1. View orders ready for payment
  2. Select an order to process payment
  3. Enter payment details (amount, method)
  4. Complete payment
  5. Generate receipt

- **Expected Results:**
  - Orders ready for payment are displayed
  - Payment is processed successfully
  - Order status is updated to "COMPLETED"
  - Receipt is generated
  - Backend calls:
    - GET to retrieve orders by status
    - PUT to update order status and payment information
    - POST to generate receipt
    - WebSocket notifications are sent for order updates

### 5.3 Receipt Management

#### Test Case: View and Print Receipts
- **Prerequisites:**
  - Logged in as CASHIER
  - At least one completed order with receipt exists

- **Steps:**
  1. View list of receipts
  2. Select a receipt to view details
  3. Print or email receipt

- **Expected Results:**
  - Receipts are displayed in a list
  - Receipt details show order items, amounts, taxes, and total
  - Receipt can be printed or emailed
  - Backend calls:
    - GET to retrieve receipts
    - GET to retrieve specific receipt details
    - POST to send receipt by email (if implemented)

---

## 6. Cross-Role Testing

### 6.1 Order Lifecycle

#### Test Case: Complete Order Lifecycle
- **Prerequisites:**
  - Access to all roles (CLIENT, KITCHEN_ADMIN, WAITER, CASHIER)
  - Test restaurant with menu and tables set up

- **Steps:**
  1. As CLIENT: Scan QR code, browse menu, add items to cart, place order
  2. As KITCHEN_ADMIN: Receive order, update status to PREPARING, then to READY
  3. As WAITER: Deliver order, update status to DELIVERED
  4. As CASHIER: Process payment, complete order
  5. As CLIENT: Scan QR code again (should show menu, not order status)

- **Expected Results:**
  - Order flows through all statuses correctly
  - Each role can perform their specific actions
  - Status updates are reflected in real-time for all roles
  - After completion, client device is freed for new orders
  - Backend calls: Various GET, POST, PUT requests as detailed in previous test cases

### 6.2 Concurrent Orders

#### Test Case: Handle Multiple Concurrent Orders
- **Prerequisites:**
  - Access to all roles
  - Multiple client devices or browsers
  - Test restaurant with menu and tables set up

- **Steps:**
  1. As multiple CLIENTs: Place orders from different tables simultaneously
  2. As KITCHEN_ADMIN: Process multiple orders in queue
  3. As WAITER: Deliver multiple orders
  4. As CASHIER: Process payments for multiple orders

- **Expected Results:**
  - All orders are processed correctly without mixing or losing data
  - Kitchen queue shows all orders in correct sequence
  - Orders are associated with the correct tables
  - Backend handles concurrent requests without errors
  - Backend calls: Multiple concurrent API calls are handled correctly

---

## 7. Edge Cases and Error Handling

### 7.1 Network Issues

#### Test Case: Handle Network Disconnection
- **Prerequisites:**
  - Client with items in cart

- **Steps:**
  1. Add items to cart
  2. Disable network connection
  3. Attempt to place order
  4. Re-enable network connection
  5. Try again to place order

- **Expected Results:**
  - Error message is displayed when offline
  - Cart items are preserved
  - Order can be placed successfully after reconnection
  - Backend calls: Appropriate error handling for failed requests

### 7.2 Invalid QR Codes

#### Test Case: Scan Invalid or Expired QR Code
- **Prerequisites:**
  - Invalid or non-existent QR code

- **Steps:**
  1. Scan an invalid QR code
  2. Scan a QR code for a deleted table

- **Expected Results:**
  - Error page is displayed with appropriate message
  - User is prompted to scan a valid QR code
  - Backend calls: GET request returns appropriate error status

### 7.3 Session Handling

#### Test Case: Multiple Devices Same Table
- **Prerequisites:**
  - Multiple client devices
  - QR code for a single table

- **Steps:**
  1. Scan the same table QR code on multiple devices
  2. Place orders from each device

- **Expected Results:**
  - Each device gets its own session
  - Orders are created separately but associated with the same table
  - Backend calls: Each device gets its own client device cookie

---

## 8. Performance Testing

### 8.1 Load Testing

#### Test Case: Handle Peak Load
- **Prerequisites:**
  - Testing tools (e.g., JMeter, Postman)
  - Test environment

- **Steps:**
  1. Simulate multiple concurrent users (10-50)
  2. Execute common operations (menu browsing, order placement)
  3. Monitor system response

- **Expected Results:**
  - System remains responsive under load
  - No errors or timeouts occur
  - Backend calls: Multiple concurrent API calls are handled efficiently

### 8.2 Response Time

#### Test Case: Verify Response Times
- **Prerequisites:**
  - Network monitoring tools
  - Test environment

- **Steps:**
  1. Measure response time for key operations:
     - Menu loading
     - Order placement
     - Order status updates
  2. Compare with acceptable thresholds

- **Expected Results:**
  - Response times are within acceptable limits:
     - Menu loading: < 2 seconds
     - Order placement: < 3 seconds
     - Status updates: < 1 second
  - Backend calls: API responses are timely and efficient