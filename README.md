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

## Hetzner Remote Command etc

### See Logs Quickly

SSH
``` 
ssh -i /home/nizami/.ssh/key2 root@157.180.16.28
```

Expose Logs
```
tail -f -n 500 /home/qrfood/logs/info.log
```


### Drop and Recreate Database

To drop and recreate the database on Hetzner run the command below, it runs the local script on a remote machine
```
ssh -i /home/nizami/.ssh/key2 root@157.180.16.28 'bash -s' < /home/nizami/Dropbox/projects/Java/qrfood/wiki/shell/drop_qrfood.sh
```

### Open Tunnel for MySQL

1. ````
   ssh -i /home/nizami/.ssh/key2 -L 3307:127.0.0.1:3306 root@157.180.16.28 -N
   ````
2. ````
   docker container stop myadmi
   docker container rm myadmin
   docker run --name myadmin -d \
   --network="host" \
   -e PMA_HOST=127.0.0.1 \
   -e PMA_PORT=3307 \
   phpmyadmin/phpmyadmin
   ````
4. Then open [PHP MyAdmin](http://localhost)

### Downlosd Log Files

## Статусы заказа и блюд

### Статусы блюда
| Статус блюда  | Описание                                   |
|---------------|---------------------------------------------|
| `CREATED`     | Блюдо добавлено в заказ, но готовка не началась |
| `PREPARING`   | Блюдо в процессе приготовления              |
| `READY`       | Блюдо готово к выдаче                       |
| `SERVED`      | Блюдо выдано клиенту                        |
| `DELETED`     | Блюдо удалено из заказа                     |

---

### Статусы заказа
| Статус заказа       | Описание                                                                 |
|---------------------|---------------------------------------------------------------------------|
| `CREATED`           | Заказ создан, но кухня ещё не приступила к готовке                        |
| `PREPARING`       | В заказе есть блюда в статусе `PREPARING`                                |
| `READY_FOR_PICKUP`  | Все блюда заказа находятся в статусе `READY` (заказ полностью готов)      |
| `SERVED`            | Все блюда заказа выданы клиенту                                           |
| `PAID`              | Заказ полностью оплачен                                                  |
| `CANCELLED`         | Заказ отменён (по инициативе клиента или ресторана)                       |

---

### Взаимосвязь статусов

- Если все блюда `CREATED` → заказ `CREATED`
- Если есть хотя бы одно `PREPARING` → заказ `PREPARING`
- Если все блюда `READY` → заказ `READY_FOR_PICKUP`
- Если все блюда `SERVED` → заказ `SERVED`
- Если заказ закрыт и оплачен → `PAID`
- Если заказ отменён → `CANCELLED`  
