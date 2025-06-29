# QR Food Order System

A comprehensive, QR code-based food ordering system designed for restaurants and eateries. This application allows customers to scan a QR code at their table to instantly view the menu and place an order. It also provides a full suite of tools for restaurant staff to manage menus, track orders in real-time, and streamline the entire dining experience.

## Key Features

- **Dynamic QR Code Generation**: Create and manage unique QR codes for each table.
- **Digital Menu Management**: Easily build, update, and organize your menu with categories, items, and descriptions.
- **Real-time Order Processing**: A live dashboard to receive and manage customer orders as they come in.
- **Secure Role-Based Access**: A secure admin panel for restaurant staff with distinct roles (Admin, Kitchen, Waiter, etc.).
- **Responsive Design**: A seamless experience on any device, for both customers and staff.

## Technology Stack

| Area      | Technology                                       |
|-----------|--------------------------------------------------|
| **Backend**   | Java 21, Spring Boot 3.2, Spring Security (JWT)  |
|           | Spring Data JPA, MariaDB, WebSockets             |
| **Frontend**  | React 18, Vite, React Router, Tailwind CSS       |
| **DevOps**    | Docker, Docker Compose, Maven                    |
| **API Docs**  | SpringDoc OpenAPI (Swagger)                      |

## Getting Started

### Prerequisites
- Java 21
- Node.js & npm
- Docker & Docker Compose

### Quickstart with Docker (Recommended)
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/qrfood.git
    cd qrfood
    ```

2.  **Launch the application:**
    ```bash
    docker-compose up --build
    ```

3.  **Access the services:**
    - **Frontend:** [http://localhost:5173](http://localhost:5173)
    - **Backend API:** [http://localhost:8081](http://localhost:8081)
    - **Swagger API Docs:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

### Manual Installation
For detailed instructions on setting up the backend and frontend services manually, please refer to the `MANUAL_SETUP.md` guide. *(I can create this file for you next)*.

### Default Admin Account
-   **Username:** `admin`
-   **Password:** `admin`

> **Note**: For security, please change the default admin password immediately after your first login.

## Contributing
Contributions are welcome! Please feel free to submit a pull request.

## License
This project is licensed under the MIT License.