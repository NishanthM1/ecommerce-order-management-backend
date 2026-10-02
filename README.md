# E-Commerce Order Management Backend

A production-style REST API backend built with **Java and Spring Boot** for managing users, products, categories, carts, orders, payments, and inventory.

The project demonstrates real-world backend concepts including **JWT authentication, role-based authorization, PostgreSQL, JPA/Hibernate, Redis caching, transactions, optimistic locking, validation, Docker, and Swagger/OpenAPI**.

#### Swagger/OpenApi Test UI:
- `http://localhost:8080/swagger-ui/index.html` 
- this link works when **your application is running locally**; 

---

## 🚀 Tech Stack

| Technology | Usage |
|---|---|
| Java 25 | Backend programming |
| Spring Boot 4.1.1 | Application framework |
| Spring Security | Authentication & authorization |
| JWT | Stateless authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM |
| PostgreSQL 17 | Relational database |
| Redis 8 | Caching |
| Maven | Build & dependency management |
| Docker | Containerization |
| Docker Compose | Multi-container setup |
| Swagger / OpenAPI | API documentation & testing |
| Lombok | Boilerplate reduction |

---

## ✨ Features

### 🔐 Authentication & Security

- User registration
- User login
- JWT-based authentication
- BCrypt password hashing
- Role-based authorization
- CUSTOMER and ADMIN roles
- Protected REST APIs
- JWT validation filter
- Global authentication and authorization handling

### 👤 User Management

- View own profile
- Update profile
- Change password
- Customer/Admin role management

### 📦 Product Management

- Create products
- Update products
- Deactivate products
- View product details
- Product search
- Category filtering
- Price filtering
- Product status filtering
- Pagination
- Sorting
- Stock management

### 🗂️ Category Management

- Create categories
- Update categories
- View categories
- Deactivate categories
- Duplicate category validation

### 🛒 Cart Management

- Create/retrieve customer cart
- Add products to cart
- Update item quantities
- Remove cart items
- Clear cart
- Calculate cart total
- Validate product availability
- Validate stock availability

### 📋 Order Management

- Create orders from cart
- Store order item price snapshots
- Deduct product stock
- View customer orders
- View individual orders
- Admin order management
- Cancel orders
- Restore stock after cancellation

### 🔄 Order Lifecycle

```text
PLACED
   ↓
CONFIRMED
   ↓
PROCESSING
   ↓
SHIPPED
   ↓
DELIVERED
```

Orders can also be cancelled where permitted:

```text
PLACED → CANCELLED
```

### 💳 Payment Management

Simulated payment system for backend demonstration.

- Payment status tracking
- PENDING
- SUCCESS
- FAILED
- Transaction references
- Customer payment access
- Admin payment access
- Payment ownership validation
- Duplicate payment protection

> No real payment gateway is connected. Payments are simulated.

### 📊 Inventory Management

- View product inventory
- Update stock
- Increase/decrease stock
- Low-stock detection
- Stock validation
- Optimistic locking
- Concurrent stock update protection

Uses JPA `@Version` for optimistic locking.

### ⚡ Redis Caching

Redis is used for product caching.

Features include:

- Product response caching
- Cache lookup using product ID
- Cache eviction after updates
- Cache eviction after deactivation
- 10-minute cache TTL

Example cache key:

```text
products::1
```

### 🧩 Backend Engineering

- Layered architecture
- DTO-based API design
- Service layer
- Repository layer
- Bean validation
- Global exception handling
- Custom exceptions
- Transaction management
- Pagination
- Sorting
- Redis caching
- Optimistic locking
- Role-based access control

---

# 🏗️ Architecture

```text
                    Client
                      |
                      v
              REST Controllers
                      |
                      v
                  Services
                 /        \
                v          v
        Repositories      Redis
              |
              v
          PostgreSQL
```

### Security Flow

```text
Client
  |
  | JWT
  v
JwtAuthenticationFilter
  |
  v
Spring Security
  |
  v
Role Authorization
  |
  v
Protected REST API
```

---

# 📁 Project Structure

```text
ecommerce-order-management-backend
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── nishanth
│   │   │           └── ecommerce
│   │   │               ├── config
│   │   │               ├── controller
│   │   │               ├── dto
│   │   │               ├── entity
│   │   │               ├── exception
│   │   │               ├── repository
│   │   │               ├── security
│   │   │               └── service
│   │   │
│   │   └── resources
│   │       └── application.properties
│   │
│   └── test
│
├── Database
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .gitignore
└── README.md
```

---

# 🗄️ Database

The application uses **PostgreSQL**.

### Main Tables

```text
users
categories
products
carts
cart_items
orders
order_items
payments
```

### Relationships

```text
User
 ├── Cart
 └── Orders

Category
 └── Products

Cart
 └── Cart Items
       └── Products

Order
 ├── Order Items
 │      └── Products
 └── Payment
```

### Product Optimistic Locking

The `products` table uses a version field:

```java
@Version
private Long version;
```

This helps prevent conflicting concurrent stock updates.

---

# ⚙️ Requirements

To run the application locally, install:

- Java 25
- Maven
- PostgreSQL 17+
- Redis 8+
- Git

For containerized execution:

- Docker Desktop

---

# 🔧 Local Setup

## 1. Clone the Repository

```bash
git clone https://github.com/NishanthM1/ecommerce-order-management-backend.git
```

```bash
cd ecommerce-order-management-backend
```

---

## 2. Create PostgreSQL Database

Open PostgreSQL and run:

```sql
CREATE DATABASE ecommerce_db;
```

---

## 3. Configure Environment Variables

The application uses environment variables for sensitive configuration.

### Required Variables

```text
DB_PASSWORD=your_postgresql_password
JWT_SECRET=your_jwt_secret
```

Do not commit real passwords or JWT secrets to GitHub.

---

# ▶️ Run Locally

You can run the application using IntelliJ IDEA or Maven.

### IntelliJ IDEA

Run:

```text
EcommerceApplication
```

The application starts on:

```text
http://localhost:8080
```

### Maven

```bash
./mvnw spring-boot:run
```

---

# 🐳 Run with Docker

Docker Compose starts:

```text
Spring Boot Application
        +
PostgreSQL
        +
Redis
```

### Start Everything

```bash
docker compose up --build
```

The backend will be available at:

```text
http://localhost:8080
```

### Stop Containers

```bash
docker compose down
```

### Check Running Containers

```bash
docker compose ps
```

---

# 📚 Swagger API Documentation

Swagger UI is included for API documentation and testing.

After starting the application, open:

### 👉 http://localhost:8080/swagger-ui/index.html

Swagger allows you to:

- View all REST APIs
- View request/response models
- Authorize using JWT
- Test endpoints directly
- Test authentication
- Test customer APIs
- Test admin APIs

### Swagger Authentication

1. Register or login using:

```text
POST /api/auth/register
POST /api/auth/login
```

2. Copy the JWT token from the login response.

3. Click **Authorize** in Swagger.

4. Enter:

```text
Bearer YOUR_JWT_TOKEN
```

5. Click **Authorize**.

6. You can now test protected APIs.

---

# 🔗 API Modules

| Module | Base URL |
|---|---|
| Authentication | `/api/auth` |
| Users | `/api/users` |
| Categories | `/api/categories` |
| Products | `/api/products` |
| Cart | `/api/cart` |
| Orders | `/api/orders` |
| Payments | `/api/payments` |
| Inventory | `/api/inventory` |

---

# 🔐 Authorization

The application uses two roles.

### CUSTOMER

Customers can:

- View products
- View categories
- Manage their cart
- Create orders
- View their orders
- Cancel their orders
- Process simulated payments
- View their profile
- Update their profile
- Change their password

### ADMIN

Admins can:

- Manage products
- Manage categories
- Manage inventory
- Manage order statuses
- View all orders
- View all payments
- Access administrative APIs

---

# 🔑 Authentication Header

Protected APIs require a JWT.

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 🧪 Testing

The project includes security integration testing for:

- Authentication
- JWT validation
- Missing JWT
- Invalid JWT
- Customer authorization
- Admin authorization
- HTTP 401 responses
- HTTP 403 responses

API functionality can also be tested manually using Swagger UI.

---

# 🔄 Example Customer Flow

```text
Register
   ↓
Login
   ↓
Receive JWT
   ↓
Authorize Swagger
   ↓
Browse Products
   ↓
Add Product to Cart
   ↓
Create Order
   ↓
Stock Deducted
   ↓
Process Payment
   ↓
Admin Updates Order
   ↓
Order Delivered
```

---

# 🛡️ Security

Security features implemented in the project:

- JWT authentication
- BCrypt password hashing
- Stateless authentication
- Role-based authorization
- Protected endpoints
- Environment-based secrets
- Global authentication error handling
- Global authorization error handling
- Input validation
- Custom exception handling

Sensitive configuration is not stored directly in source code.

---

# 📈 Project Status

## Completed

- Spring Boot backend
- PostgreSQL integration
- JPA/Hibernate
- User authentication
- JWT authentication
- Role-based authorization
- User profile management
- Category management
- Product management
- Product search
- Filtering
- Pagination
- Sorting
- Cart management
- Order management
- Order lifecycle
- Simulated payments
- Inventory management
- Optimistic locking
- Transaction management
- Global exception handling
- Validation
- Redis caching
- Swagger/OpenAPI
- Docker
- Docker Compose
- Security integration tests

## Planned / Future Improvements

- More JUnit and Mockito test coverage
- Advanced JWT hardening
- Production deployment
- AI Product Assistant
- Additional integration tests

---

# 🎯 Project Objective

This project was built as a placement-focused Java backend project to demonstrate practical knowledge of:

- Java
- Spring Boot
- REST APIs
- Spring Security
- JWT
- PostgreSQL
- JPA/Hibernate
- Redis
- Docker
- Transactions
- Validation
- Exception handling
- Pagination
- Caching
- Inventory management
- Order processing
- Role-based authorization

---

# 👨‍💻 Author

**Nishanth M**

B.E. Computer Science Engineering

GitHub:  
https://github.com/NishanthM1

---

## ⭐ Project

If you find this project useful, consider giving the repository a star.



**Swagger UI:**  
`http://localhost:8080/swagger-ui/index.html`

Just remember that this link works when **your application is running locally**; it isn't a public online API yet.