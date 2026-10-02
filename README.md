# E-Commerce Order Management Backend

A production-style REST API backend for an e-commerce application built with **Java, Spring Boot, Spring Security, PostgreSQL, Spring Data JPA, and JWT authentication**.

The project implements core e-commerce backend functionality including user authentication, role-based authorization, category and product management, shopping cart management, order processing, simulated payments, and inventory management.
##  📚Swagger/OpenAPI
    - All APIs Document
    - Interactive API testing
    - run and test in
- http://localhost:8080/swagger-ui/index.html
---

## 📌 Project Overview

This project is designed to demonstrate practical backend development using the Spring Boot ecosystem.

The backend follows a layered architecture and provides REST APIs for managing:

- Users and authentication
- Categories
- Products
- Shopping carts
- Orders
- Payments
- Inventory

The application uses **JWT-based stateless authentication** and supports two roles:

- `CUSTOMER`
- `ADMIN`

---

# 🚀 Features

## 🔐 Authentication & Authorization

- User registration
- User login
- JWT-based authentication
- BCrypt password hashing
- Stateless authentication
- Role-based authorization
- CUSTOMER and ADMIN roles
- Protected REST APIs
- Admin-only operations

## 📂 Category Management

- Create category
- View all categories
- View category by ID
- Update category
- Deactivate category
- Duplicate category validation

## 📦 Product Management

- Create products
- View products
- View product by ID
- Update products
- Deactivate products
- Search products by name
- Filter products by category
- Pagination
- Sorting
- Stock quantity management
- Product status management

## 🛒 Cart Management

- Automatically create customer cart
- Add products to cart
- Update cart item quantity
- Remove cart items
- Clear cart
- Calculate cart total
- Product availability validation
- Stock validation
- Duplicate product handling

## 🧾 Order Management

- Create orders from cart
- View customer orders
- View individual orders
- Admin order management
- Order status management
- Order cancellation
- Stock reduction during order creation
- Stock restoration during cancellation
- Purchase-time price snapshot
- Transactional order processing

## 💳 Payment Management

Payments are simulated and do not connect to a real payment gateway.

- View order payment
- Process successful payment
- Simulate failed payment
- Payment status tracking
- Transaction reference generation
- Customer payment access
- Admin payment access

## 📊 Inventory Management

- View product inventory
- Update product stock
- Stock validation
- Optimistic locking using JPA `@Version`

## 🛡️ Validation & Exception Handling

- Jakarta Bean Validation
- Request validation
- Custom exceptions
- Global exception handling
- Structured error responses
- Business-rule validation

---

# 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 25 | Programming language |
| Spring Boot 4.1.1 | Backend framework |
| Spring Web | REST API development |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| Spring Security | Authentication & authorization |
| JWT | Stateless authentication |
| PostgreSQL 17 | Relational database |
| Maven | Build and dependency management |
| Lombok | Boilerplate reduction |
| Jakarta Validation | Request validation |

---

# 🏗️ Architecture

The application follows a layered architecture:

```text
                    Client
                      │
                      ▼
               REST Controller
                      │
                      ▼
                   Service
                      │
                      ▼
                 Repository
                      │
                      ▼
                 PostgreSQL
```

Security flow:

```text
Client
  │
  │ JWT
  ▼
JwtAuthenticationFilter
  │
  ▼
Spring Security
  │
  ▼
Controller
```

Order processing flow:

```text
Customer
   │
   ▼
Cart
   │
   ▼
Order
   │
   ├── Order Items
   │
   ├── Inventory
   │
   └── Payment
```

---

# 📁 Project Structure

```text
ecommerce/
│
├── Database/
│   ├── ecommerce.sql
│   └── ecommerce.pdf
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── nishanth/
│       │           └── ecommerce/
│       │               │
│       │               ├── config/
│       │               │
│       │               ├── controller/
│       │               │
│       │               ├── dto/
│       │               │
│       │               ├── entity/
│       │               │
│       │               ├── exception/
│       │               │
│       │               ├── repository/
│       │               │
│       │               ├── security/
│       │               │
│       │               └── service/
│       │
│       └── resources/
│           └── application.properties
│
├── .gitignore
├── .gitattributes
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

# 🗄️ Database Design

The application uses **PostgreSQL**.

## Main Tables

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

## Relationships

```text
User
 │
 ├────────────── Cart
 │
 └────────────── Orders
                       │
                       ├──────── Order Items
                       │
                       └──────── Payment


Category
 │
 └────────────── Products
                       │
                       ├──────── Cart Items
                       │
                       └──────── Order Items
```

## Important Database Concepts

### Product Version

Products use optimistic locking:

```java
@Version
private Long version;
```

This helps protect inventory updates from concurrent modifications.

### Order Item Price

`OrderItem` stores the purchase-time `unitPrice`.

This means the order retains the price that existed when the order was created, even if the product price changes later.

---

# ⚙️ Prerequisites

Before running the project, install:

- Java 25
- PostgreSQL 17+
- Git
- IntelliJ IDEA

Verify Java:

```bash
java -version
```

Verify PostgreSQL:

```bash
psql --version
```

Verify Git:

```bash
git --version
```

---

# 🚀 Setup & Installation

## 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
```

Move into the project:

```bash
cd ecommerce-order-management-backend
```

---

# 🗄️ 2. Create PostgreSQL Database

Open PostgreSQL:

```bash
psql -U postgres
```

Create the database:

```sql
CREATE DATABASE ecommerce_db;
```

Verify:

```sql
\l
```

Exit:

```sql
\q
```

The application expects:

```text
Host: localhost
Port: 5432
Database: ecommerce_db
```

---

# 🔐 3. Configure Environment Variables

Sensitive configuration is **not stored directly in the source code**.

The application uses the following environment variables:

```text
DB_PASSWORD
JWT_SECRET
```

The `application.properties` file contains:

```properties
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
```

## IntelliJ IDEA Setup

Open:

```text
Run
→ Edit Configurations
→ EcommerceApplication
→ Environment variables
```

Add:

```text
DB_PASSWORD=your_postgresql_password
JWT_SECRET=your_jwt_secret
```

Example:

```text
DB_PASSWORD=********
JWT_SECRET=********************************
```

Do not commit these values to GitHub.

---

# 📝 4. Application Configuration

The application uses:

```properties
spring.application.name=ecommerce

spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce_db
spring.datasource.username=nishanthm
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

jwt.secret=${JWT_SECRET}
jwt.expiration=3600000
```

### Configuration

| Property | Description |
|---|---|
| `spring.datasource.url` | PostgreSQL connection URL |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password from environment |
| `spring.jpa.hibernate.ddl-auto` | Updates database schema |
| `spring.jpa.show-sql` | Displays SQL queries |
| `jwt.secret` | JWT signing secret |
| `jwt.expiration` | JWT expiration time |

---

# ▶️ 5. Start the Application

## Option 1 — IntelliJ IDEA

Open:

```text
src/main/java/com/nishanth/ecommerce/EcommerceApplication.java
```

Run:

```text
EcommerceApplication
```

## Option 2 — Maven Wrapper

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

Successful startup should display:

```text
Started EcommerceApplication
```

---

# 🗃️ Database Schema Generation

The project currently uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Therefore, Hibernate automatically creates or updates the database tables based on the JPA entities.

You do not need to manually create every table.

Database reference files are available in:

```text
Database/
├── ecommerce.sql
└── ecommerce.pdf
```

---

# 🔑 Authentication

The application uses JWT-based stateless authentication.

## Authentication Flow

```text
Register
   ↓
Login
   ↓
Password Verification
   ↓
JWT Generation
   ↓
Client Stores JWT
   ↓
Client Sends JWT
   ↓
JwtAuthenticationFilter
   ↓
Spring Security
   ↓
Protected API
```

For protected APIs, include:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

# 👥 User Roles

The application supports:

```text
CUSTOMER
ADMIN
```

## CUSTOMER

Customers can:

- View categories
- View products
- Search products
- Manage their cart
- Create orders
- View their orders
- Cancel eligible orders
- Process payments for their orders

## ADMIN

Admins can:

- Create/update/deactivate categories
- Create/update/deactivate products
- Manage inventory
- View all orders
- Update order status
- View all payments

---

# 🌐 API Documentation

Base URL:

```text
http://localhost:8080
```

### Access Levels

| Symbol | Meaning |
|---|---|
| 🌍 Public | Authentication not required |
| 🔐 Authenticated | Valid JWT required |
| 👤 CUSTOMER | Customer JWT required |
| 👑 ADMIN | Admin JWT required |

---

# 🔐 Authentication APIs

## Register

```http
POST /api/auth/register
```

### Access

🌍 Public

### Description

Creates a new customer account.

### Request

```json
{
  "name": "Nishanth",
  "email": "nishanth@example.com",
  "password": "password123"
}
```

### Response

```json
{
  "id": 1,
  "name": "Nishanth",
  "email": "nishanth@example.com",
  "role": "CUSTOMER"
}
```

---

## Login

```http
POST /api/auth/login
```

### Access

🌍 Public

### Description

Authenticates a user and returns a JWT.

### Request

```json
{
  "email": "nishanth@example.com",
  "password": "password123"
}
```

### Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "name": "Nishanth",
  "email": "nishanth@example.com",
  "role": "CUSTOMER"
}
```

---

## Get Current User

```http
GET /api/auth/me
```

### Access

🔐 Authenticated

### Header

```http
Authorization: Bearer <JWT_TOKEN>
```

### Description

Returns information related to the authenticated user.

---

## Admin Test

```http
GET /api/auth/admin-test
```

### Access

👑 ADMIN

### Description

Used to verify ADMIN role-based authorization.

---

# 📂 Category APIs

## Create Category

```http
POST /api/categories
```

### Access

👑 ADMIN

### Request

```json
{
  "name": "Electronics",
  "description": "Electronic products"
}
```

---

## Get All Categories

```http
GET /api/categories
```

### Access

🔐 Authenticated

### Description

Returns all categories.

---

## Get Category

```http
GET /api/categories/{id}
```

### Access

🔐 Authenticated

### Example

```http
GET /api/categories/1
```

---

## Update Category

```http
PUT /api/categories/{id}
```

### Access

👑 ADMIN

### Example

```http
PUT /api/categories/1
```

### Request

```json
{
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

---

## Deactivate Category

```http
PATCH /api/categories/{id}/deactivate
```

### Access

👑 ADMIN

### Example

```http
PATCH /api/categories/1/deactivate
```

---

# 📦 Product APIs

## Create Product

```http
POST /api/products
```

### Access

👑 ADMIN

### Request

```json
{
  "name": "Wireless Headphones",
  "description": "Bluetooth wireless headphones",
  "price": 2499.00,
  "stockQuantity": 50,
  "categoryId": 1
}
```

---

## Get Product

```http
GET /api/products/{id}
```

### Access

🔐 Authenticated

### Example

```http
GET /api/products/1
```

---

## Get Products

```http
GET /api/products
```

### Access

🔐 Authenticated

### Supported Query Parameters

```text
search
categoryId
page
size
sortBy
direction
```

### Get all

```http
GET /api/products
```

### Search

```http
GET /api/products?search=headphones
```

### Filter by category

```http
GET /api/products?categoryId=1
```

### Pagination

```http
GET /api/products?page=0&size=10
```

### Sorting

```http
GET /api/products?sortBy=price&direction=asc
```

### Combined search

```http
GET /api/products?search=phone&categoryId=1&page=0&size=10&sortBy=price&direction=asc
```

---

## Update Product

```http
PUT /api/products/{id}
```

### Access

👑 ADMIN

---

## Deactivate Product

```http
PATCH /api/products/{id}/deactivate
```

### Access

👑 ADMIN

---

# 🛒 Cart APIs

All cart APIs require:

👤 `CUSTOMER`

---

## Get Cart

```http
GET /api/cart
```

### Description

Returns the authenticated customer's cart.

---

## Add Product to Cart

```http
POST /api/cart/items
```

### Request

```json
{
  "productId": 1,
  "quantity": 2
}
```

### Description

Adds a product to the cart.

If the product already exists, the quantity is increased.

---

## Update Cart Item

```http
PUT /api/cart/items/{productId}?quantity=3
```

### Example

```http
PUT /api/cart/items/1?quantity=3
```

---

## Remove Cart Item

```http
DELETE /api/cart/items/{productId}
```

### Example

```http
DELETE /api/cart/items/1
```

---

## Clear Cart

```http
DELETE /api/cart
```

---

# 🧾 Order APIs

## Create Order

```http
POST /api/orders
```

### Access

👤 CUSTOMER

### Description

Creates an order from the authenticated customer's cart.

### Processing Flow

```text
Cart
 ↓
Check Cart
 ↓
Validate Products
 ↓
Validate Stock
 ↓
Create Order
 ↓
Create Order Items
 ↓
Snapshot Product Price
 ↓
Reduce Stock
 ↓
Create Pending Payment
 ↓
Clear Cart
```

No request body is required.

---

## Get My Orders

```http
GET /api/orders
```

### Access

👤 CUSTOMER

### Pagination

```http
GET /api/orders?page=0&size=10
```

Returns only orders belonging to the authenticated customer.

---

## Get Order

```http
GET /api/orders/{id}
```

### Access

👤 CUSTOMER

### Example

```http
GET /api/orders/1
```

Customers can only access their own orders.

---

## Get All Orders

```http
GET /api/orders/admin
```

### Access

👑 ADMIN

### Description

Returns orders across customers.

---

## Update Order Status

```http
PATCH /api/orders/{id}/status?status=CONFIRMED
```

### Access

👑 ADMIN

### Supported Statuses

```text
PLACED
CONFIRMED
PROCESSING
SHIPPED
DELIVERED
CANCELLED
```

### Normal Order Lifecycle

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

---

## Cancel Order

```http
PATCH /api/orders/{id}/cancel
```

### Access

👤 CUSTOMER

### Description

Allows a customer to cancel an eligible order.

When an eligible order is cancelled, the associated stock is restored.

---

# 💳 Payment APIs

Payments are simulated for this project.

No real payment gateway is connected.

---

## Get Order Payment

```http
GET /api/payments/order/{orderId}
```

### Access

👤 CUSTOMER

### Example

```http
GET /api/payments/order/1
```

---

## Process Payment

```http
POST /api/payments/order/{orderId}/pay
```

### Access

👤 CUSTOMER

### Description

Processes a simulated successful payment.

Payment status becomes:

```text
SUCCESS
```

A transaction reference is generated.

Example:

```text
TXN-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
```

---

## Fail Payment

```http
POST /api/payments/order/{orderId}/fail
```

### Access

👤 CUSTOMER

### Description

Simulates a failed payment.

Payment status becomes:

```text
FAILED
```

---

## Get All Payments

```http
GET /api/payments/admin
```

### Access

👑 ADMIN

---

# 📊 Inventory APIs

Inventory management is restricted to administrators.

---

## Get Inventory

```http
GET /api/inventory/{productId}
```

### Access

👑 ADMIN

### Example

```http
GET /api/inventory/1
```

Returns:

- Product ID
- Product name
- Stock quantity
- Version
- Product status

---

## Update Inventory

```http
PUT /api/inventory/{productId}
```

### Access

👑 ADMIN

### Request

```json
{
  "stockQuantity": 100
}
```

---

# 🔄 Complete E-Commerce Flow

A typical customer workflow is:

```text
1. Register
      ↓
2. Login
      ↓
3. Receive JWT
      ↓
4. Browse Products
      ↓
5. Add Product to Cart
      ↓
6. View Cart
      ↓
7. Create Order
      ↓
8. Stock Validation
      ↓
9. Stock Reduction
      ↓
10. Payment Created
      ↓
11. Process Payment
      ↓
12. Admin Confirms Order
      ↓
13. Processing
      ↓
14. Shipped
      ↓
15. Delivered
```

---

# 🧪 Recommended API Testing Sequence

For testing the complete application:

### Step 1 — Start PostgreSQL

Make sure PostgreSQL is running.

### Step 2 — Start Spring Boot

```bash
./mvnw spring-boot:run
```

### Step 3 — Register Customer

```http
POST /api/auth/register
```

### Step 4 — Login Customer

```http
POST /api/auth/login
```

Copy the JWT token.

### Step 5 — Login as Admin

Use the development admin account configured by the application.

Copy the admin JWT.

### Step 6 — Create Category

```http
POST /api/categories
```

Use the ADMIN token.

### Step 7 — Create Product

```http
POST /api/products
```

Use the ADMIN token.

### Step 8 — Browse Products

```http
GET /api/products
```

Use the CUSTOMER token.

### Step 9 — Add Product to Cart

```http
POST /api/cart/items
```

### Step 10 — View Cart

```http
GET /api/cart
```

### Step 11 — Create Order

```http
POST /api/orders
```

### Step 12 — View Payment

```http
GET /api/payments/order/{orderId}
```

### Step 13 — Process Payment

```http
POST /api/payments/order/{orderId}/pay
```

### Step 14 — Admin Updates Order

```http
PATCH /api/orders/{id}/status?status=CONFIRMED
```

### Step 15 — Continue Order Lifecycle

```text
CONFIRMED
    ↓
PROCESSING
    ↓
SHIPPED
    ↓
DELIVERED
```

---

# 📋 Complete API Reference

| Module | Method | Endpoint | Access |
|---|---|---|---|
| Auth | POST | `/api/auth/register` | 🌍 Public |
| Auth | POST | `/api/auth/login` | 🌍 Public |
| Auth | GET | `/api/auth/me` | 🔐 Authenticated |
| Auth | GET | `/api/auth/admin-test` | 👑 ADMIN |
| Category | POST | `/api/categories` | 👑 ADMIN |
| Category | GET | `/api/categories` | 🔐 Authenticated |
| Category | GET | `/api/categories/{id}` | 🔐 Authenticated |
| Category | PUT | `/api/categories/{id}` | 👑 ADMIN |
| Category | PATCH | `/api/categories/{id}/deactivate` | 👑 ADMIN |
| Product | POST | `/api/products` | 👑 ADMIN |
| Product | GET | `/api/products` | 🔐 Authenticated |
| Product | GET | `/api/products/{id}` | 🔐 Authenticated |
| Product | PUT | `/api/products/{id}` | 👑 ADMIN |
| Product | PATCH | `/api/products/{id}/deactivate` | 👑 ADMIN |
| Cart | GET | `/api/cart` | 👤 CUSTOMER |
| Cart | POST | `/api/cart/items` | 👤 CUSTOMER |
| Cart | PUT | `/api/cart/items/{productId}` | 👤 CUSTOMER |
| Cart | DELETE | `/api/cart/items/{productId}` | 👤 CUSTOMER |
| Cart | DELETE | `/api/cart` | 👤 CUSTOMER |
| Order | POST | `/api/orders` | 👤 CUSTOMER |
| Order | GET | `/api/orders` | 👤 CUSTOMER |
| Order | GET | `/api/orders/{id}` | 👤 CUSTOMER |
| Order | GET | `/api/orders/admin` | 👑 ADMIN |
| Order | PATCH | `/api/orders/{id}/status` | 👑 ADMIN |
| Order | PATCH | `/api/orders/{id}/cancel` | 👤 CUSTOMER |
| Payment | GET | `/api/payments/order/{orderId}` | 👤 CUSTOMER |
| Payment | POST | `/api/payments/order/{orderId}/pay` | 👤 CUSTOMER |
| Payment | POST | `/api/payments/order/{orderId}/fail` | 👤 CUSTOMER |
| Payment | GET | `/api/payments/admin` | 👑 ADMIN |
| Inventory | GET | `/api/inventory/{productId}` | 👑 ADMIN |
| Inventory | PUT | `/api/inventory/{productId}` | 👑 ADMIN |

---

# 🛡️ Security

The project uses Spring Security and JWT authentication.

### Password Security

Passwords are hashed using BCrypt before being stored.

### JWT Security

JWT tokens contain authentication information and are signed using a secret key supplied through an environment variable.

### Role-Based Authorization

Administrative endpoints are protected using role-based authorization.

### Sensitive Configuration

The following values are never stored in the repository:

```text
DB_PASSWORD
JWT_SECRET
```

The repository `.gitignore` excludes local secret files and generated files.

---

# 📄 Example Error Response

The application uses a common error response structure.

Example:

```json
{
  "status": 400,
  "message": "Product stock cannot be negative"
}
```

Validation errors are also handled centrally through the global exception handler.

---

# 📈 Development Status

## ✅ Completed

- [x] Spring Boot project setup
- [x] PostgreSQL integration
- [x] JPA entities
- [x] Entity relationships
- [x] Repository layer
- [x] Service layer
- [x] Controller layer
- [x] DTO layer
- [x] Request validation
- [x] JWT authentication
- [x] BCrypt password hashing
- [x] Role-based authorization
- [x] Customer/Admin roles
- [x] Category management
- [x] Product management
- [x] Product search
- [x] Category filtering
- [x] Pagination
- [x] Sorting
- [x] Cart management
- [x] Order management
- [x] Order status lifecycle
- [x] Order cancellation
- [x] Simulated payments
- [x] Inventory management
- [x] Optimistic locking
- [x] Transactional operations
- [x] Custom exceptions
- [x] Global exception handling
- [x] Environment-based secret configuration

## 🔜 Planned

- [ ] Swagger / OpenAPI
- [ ] JUnit testing
- [ ] Mockito unit testing
- [ ] Integration testing
- [ ] Advanced JWT security hardening
- [ ] Redis caching
- [ ] Advanced product filtering
- [ ] Docker
- [ ] Docker Compose
- [ ] AI Product Assistant
- [ ] Production deployment
- [ ] Final production configuration

---

# 🗺️ Future Architecture

Planned improvements:

```text
                    Client
                      │
                      ▼
               Spring Boot API
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
       Security     Services     Cache
          │           │           │
          │           ▼         Redis
          │       Repositories
          │           │
          └───────────┼──────────
                      ▼
                  PostgreSQL
```

Future AI integration:

```text
User
  ↓
AI Product Assistant
  ↓
Structured Search Filters
  ↓
Backend Validation
  ↓
Product Search Service
  ↓
PostgreSQL
```

The AI layer will not directly access or modify the database.

---

# 📁 Database Reference Files

The repository includes:

```text
Database/
├── ecommerce.sql
└── ecommerce.pdf
```

These files contain the database schema/reference information used during development.

---

# 🎯 Project Objective

This project is being developed as a placement-focused backend project to demonstrate practical Java backend development and production-oriented design.

The project demonstrates:

- Java
- Spring Boot
- REST API development
- Spring Security
- JWT authentication
- PostgreSQL
- JPA / Hibernate
- Entity relationships
- Layered architecture
- Dependency Injection
- Transactions
- Validation
- Exception handling
- Pagination
- Sorting
- Inventory management
- Optimistic locking
- Order processing
- Payment workflow
- Role-based authorization

---

# 👨‍💻 Author

**Nishanth M**

B.E. Computer Science Engineering
Bengaluru, Karnataka, India
