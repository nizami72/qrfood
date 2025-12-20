# QR Food Order System

A comprehensive, QR code-based food ordering system designed for restaurants and eateries. This application allows
customers to scan a QR code at their table to instantly view the menu and place an order. It also provides a full suite
of tools for restaurant staff to manage menus, track orders in real-time, and streamline the entire dining experience.


## Оглавление
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Quickstart with Docker (Recommended)](#quickstart-with-docker-recommended)
   - [Manual Installation](#manual-installation)
   - [Default Admin Account](#default-admin-account)
- [Contributing](#contributing)
- [License](#license)
- [Hetzner Remote Command etc](#hetzner-remote-command-etc)
   - [Remote ssh](#remote-ssh)
   - [See Logs Quickly](#see-logs-quickly)
   - [MC show filesystem local and remote](#mc-show-filesystem-local-and-remote)
- [MySQL](#mysql)
   - [Drop and Recreate Database](#drop-and-recreate-database)
   - [Open Tunnel for MySQL](#open-tunnel-for-mysql)
   - [Downlosd Log Files](#downlosd-log-files)
- [Статусы заказа и блюд](#статусы-заказа-и-блюд)
   - [Статусы блюда](#статусы-блюда)
   - [Статусы заказа](#статусы-заказа)
   - [Взаимосвязь статусов](#взаимосвязь-статусов)
- [Mailhog](#mailhog)
- [Nginx](#nginx)
    - [Nginx Configuration Files](#nginx-configuration-files)
    - [Nginx Main Configuration File](#nginx-main-configuration-file)
    - [Reload nginx](#reload-nginx)


## Key Features

- **Dynamic QR Code Generation**: Create and manage unique QR codes for each table.
- **Digital Menu Management**: Easily build, update, and organize your menu with categories, items, and descriptions.
- **Real-time Order Processing**: A live dashboard to receive and manage customer orders as they come in.
- **Secure Role-Based Access**: A secure admin panel for restaurant staff with distinct roles (Admin, Kitchen, Waiter,
  etc.).
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

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/qrfood.git
   cd qrfood
   ```

2. **Launch the application:**
   ```bash
   docker-compose up --build
   ```

3. **Access the services:**
    - **Frontend:** [http://localhost:5173](http://localhost:5173)
    - **Backend API:** [http://localhost:8081](http://localhost:8081)
    - **Swagger API Docs:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)

### Manual Installation

For detailed instructions on setting up the backend and frontend services manually, please refer to the
`MANUAL_SETUP.md` guide. *(I can create this file for you next)*.

### Default Admin Account

- **Username:** `admin`
- **Password:** `admin`

> **Note**: For security, please change the default admin password immediately after your first login.

## Contributing

Contributions are welcome! Please feel free to submit a pull request.

## License

This project is licensed under the MIT License.

## Hetzner Remote Command etc

### Remote ssh

SSH

``` 
ssh -i ~/.ssh/key2 root@157.180.16.28
```

### See Logs Quickly

SSH

``` 
ssh -i ~/.ssh/key2 root@157.180.16.28
```

Expose Logs

```
tail -f -n 500 /home/qrfood/logs/info.log
```
### MC show filesystem local and remote

```
mc sh://hetzner/ /home/nizami/
```

## MySQL

### Drop and Recreate Database

To drop and recreate the database on Hetzner run the command below, it runs the local script on a remote machine

```
ssh -i ~/.ssh/key2 root@157.180.16.28 'bash -s' < ~/Dropbox/projects/Java/qrfood/wiki/shell/drop_qrfood.sh
```

### Open Tunnel for MySQL

1. ````
   ssh -i ~/.ssh/key2 -L 3307:127.0.0.1:3306 root@157.180.16.28 -N
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

## Mailhog

Run Mailhog to test login emails flow
```bash
    docker run -d -p 1025:1025 -p 8025:8025 --name mailhog mailhog/mailhog
```
then open [Mailhog](http://localhost:8025)  

## Nginx

On a remote server, your Nginx configuration is split into two main parts: the main config file and the site-specific config file (which is where your Spring Boot configuration will be).

Here are the most common locations, starting with the one you likely need.

### Nginx Configuration Files
Spring Boot App Configuration (What You Probably Want)
   Your Spring Boot application is configured as a reverse proxy. This specific configuration is almost never placed in the main nginx.conf file. Instead, it's in its own file located in one of these two directories, depending on your server's operating system:

On Ubuntu/Debian:

sites-available: /etc/nginx/sites-available/your-app-name

sites-enabled: /etc/nginx/sites-enabled/your-app-name

How it works: You create your config file in sites-available (e.g., /etc/nginx/sites-available/qrfood). To activate it, you create a symbolic link (shortcut) to it in the sites-enabled directory.

On CentOS/Red Hat/Other OS:

conf.d: /etc/nginx/conf.d/your-app-name.conf

How it works: Any file in this directory that ends with .conf is automatically loaded by Nginx. This is a simpler approach.

Your Spring Boot config file (e.g., /etc/nginx/sites-available/qrfood) will contain the location block that forwards traffic to your app, something like this:

Nginx

server {
listen 80;
server_name your_domain.com;

    location / {
        proxy_pass http://localhost:8080; # Assumes Spring Boot runs on port 8080
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

### Nginx Main Configuration File
There are two main Nginx configuration files:
Main Nginx Configuration File
   This is the main file that loads all other configurations (including your Spring Boot file from above). You usually don't edit this file directly to add a new site.

Default location: /etc/nginx/nginx.conf

This file is what contains the include directives that load your app's config:

It will have a line like include /etc/nginx/sites-enabled/*; (for Ubuntu/Debian)

Or it will have include /etc/nginx/conf.d/*.conf; (for CentOS/RHEL)

How to Find the Exact Location
If you're still not sure, you can run this command on your server. It will tell you exactly which main configuration file Nginx is using:

Bash

sudo nginx -t
The output will test your configuration and show you the path:

nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful

### Reload nginx

To apply your changes to Nginx, you need to reload the configuration without restarting the service. This is generally safer and faster, especially for production environments. Use the following command:

Bash
```
ssh -i /home/nizami/.ssh/key2 root@157.180.16.28
```
```
sudo nginx -s reload
```

This command will tell Nginx to reload its configuration without interrupting existing connections.