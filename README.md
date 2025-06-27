# QR Food Order System

A comprehensive QR code-based food ordering system for restaurants and eateries. This application allows customers to scan QR codes at restaurant tables to view menus and place orders, while providing restaurant staff with tools to manage menus, track orders, and streamline the dining experience.

## TODO List
1. Finish order-decision page navigation buttons and translation
2. Draw flow of pages interaction
3. Make a decision where all orders pages should appear
4. Make a decision what should be header for all pages
5. Add headers to all pages
6. Decide what if the same email belomgs to different eateries

## Features

- **QR Code Generation**: Create unique QR codes for each table in your restaurant
- **Digital Menu Management**: Easily create, update, and organize your menu with categories and dishes
- **Real-time Order Processing**: Receive and manage customer orders in real-time
- **User Authentication**: Secure admin panel for restaurant staff
- **Responsive Design**: Works on all devices for both customers and staff

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.2.0
- Spring Security with JWT authentication
- Spring Data JPA
- MariaDB
- RESTful API architecture
- WebSockets for real-time communication

### Frontend
- React 18
- Vite
- React Router
- Tailwind CSS
- Responsive design

### DevOps
- Docker for containerization
- Maven for build automation

## Getting Started

### Prerequisites
- Java 21
- Node.js and npm
- Docker and Docker Compose (optional)
- MariaDB (if not using Docker)

### Installation

#### Using Docker (Recommended)
1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/qrfood.git
   cd qrfood
   ```

2. Start the application using Docker Compose
   ```bash
   docker-compose up
   ```

3. Access the application:
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

#### Manual Setup
1. Clone the repository
   ```bash
   git clone https://github.com/yourusername/qrfood.git
   cd qrfood
   ```

2. Configure the database
   - Create a MariaDB database
   - Update `src/main/resources/application.properties` with your database credentials

3. Build and run the backend
   ```bash
   ./mvnw spring-boot:run
   ```

4. Install frontend dependencies and start the development server
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. Access the application:
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

### Default Admin Account
- Username: `admin`
- Password: `admin`

**Note**: For security reasons, please change the default admin password after the first login.

## Usage
 
### Restaurant Staff
1. Log in to the admin panel
2. Create your restaurant profile
3. Add categories and dishes to your menu
4. Generate QR codes for your tables
5. Print and place QR codes on tables
6. Monitor and manage incoming orders

### Customers
1. Scan the QR code on the table
2. Browse the digital menu
3. Select items and customize as needed
4. Place the order
5. Track order status in real-time

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the [MIT License](LICENSE).

## Run on Mobile Phone

## Cookie Flow
The UUID cookie is installed on the device when a client has confirmed its order 
 





| Role / Entity      | <span style="color:red"> Eatery</span> | Category | Dish | Tables | Orders | Users | Receipts |
|--------------------|----------------------------------------|----------|------|--------|--------|-------|----------|
| **CLIENT**         | R                                      | R        | R    | R      | C, R   | –     | R        |
| **EATERY\_ADMIN**  | RCU                                    | RCUD     | RCUD | RCUD   | RCUD   | RCUD  | R        |
| **KITCHEN\_ADMIN** | –                                      | –        | R    | –      | R, U   | –     | –        |
| **WAITER**         | –                                      | –        | –    | –      | R, U   | –     | –        |
| **CASHIER**        | –                                      | –        | –    | –      | R      | –     | R, U     |
| **SUPER_ADMIN**    | RCUD                                   | RCUD     | RCUD | RCUD   | RCUD   | RCUD  | RCUD     |
