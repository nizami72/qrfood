# Architecture Overview

This document provides a detailed overview of the technical architecture of the QR Food Order System.

## System Components

The application is composed of three main components:

1.  **Backend Service:** A Java Spring Boot application that serves the REST API, handles business logic, and manages the database.
2.  **Frontend Application:** A single-page application (SPA) built with React that provides the user interface for both customers and restaurant staff.
3.  **Database:** A MariaDB database that stores all application data, including menus, orders, users, and restaurant information.

These services are designed to be run in Docker containers for consistency and ease of deployment.

## Role-Permission Matrix

The system uses a role-based access control (RBAC) model to manage user permissions. The following table outlines the permissions for each role:

| Role / Entity      | <span style="color:red"> Eatery</span> | Category | Dish | Tables | Orders | Users | Receipts |
|--------------------|----------------------------------------|----------|------|--------|--------|-------|----------|
| **CLIENT**         | R                                      | R        | R    | R      | C, R   | –     | R        |
| **EATERY\_ADMIN**  | RCU                                    | RCUD     | RCUD | RCUD   | RCUD   | RCUD  | R        |
| **KITCHEN\_ADMIN** | –                                      | –        | R    | –      | R, U   | –     | –        |
| **WAITER**         | –                                      | –        | –    | –      | R, U   | –     | –        |
| **CASHIER**        | –                                      | –        | –    | –      | R      | –     | R, U     |
| **SUPER_ADMIN**    | RCUD                                   | RCUD     | RCUD | RCUD   | RCUD   | RCUD  | RCUD     |

**Legend:**
*   **C:** Create
*   **R:** Read
*   **U:** Update
*   **D:** Delete

## Development Roadmap

This section contains a list of future enhancements and tasks to be completed.

- [ ] Finish order-decision page navigation buttons and translation.
- [ ] Draw a complete flow diagram of page interactions.
- [ ] Decide on a unified location for all order management pages.
- [ ] Finalize a consistent header design for all pages.
- [ ] Implement the header across all pages.
- [ ] Resolve ambiguity for users with the same email belonging to different eateries.

## Cookie Flow
The UUID cookie is installed on the device when a client has confirmed its order.
