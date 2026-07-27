---
적용: 항상
---

# E-Commerce API - AI Context

## 1. Project Overview
This project is an intermediate backend project to build a RESTful API for an e-commerce platform.

- It is an API-first backend project, not a frontend project.
- It should be tested primarily with Postman, curl, HTTP client files, or automated tests.
- It uses HTTP requests and JSON responses.
- It stores users, products, carts, orders, and payments in a database.
- It includes user authentication and a minimal admin role.
- It includes cart, checkout, payment, and inventory-related business logic.
- It is intended to strengthen Java/Spring Boot backend skills, not to expand into many new stacks.

Primary stack for this project:
- Java
- Spring Boot
- Spring Data JPA
- Relational database, preferably MySQL or PostgreSQL
- REST API

Do not suggest rewriting this project in Kotlin, Node.js, Go, React, or another stack unless explicitly requested.

---

## 2. Source Requirement Summary
The referenced project is an E-Commerce API project. Its goal is to build an API for an e-commerce platform with:

- JWT authentication
- CRUD operations
- external service interaction such as payment gateway integration
- a more complex data model involving products, shopping carts, and related concepts
- user signup and login
- adding products to cart
- removing products from cart
- viewing and searching products
- checkout and payment
- admin-only product, price, and inventory management

For this project, treat the official requirement as a guide, but keep implementation scope controlled.

---

## 3. Main Learning Focus
This project has only **two primary learning goals**.

### Learning Goal 1 - E-Commerce Domain Modeling and Transactional Flow
Learn how to model and implement a logic-heavy backend domain involving:

- users
- products
- inventory
- carts
- cart items
- orders
- order items
- payments
- order/payment status transitions
- price snapshots
- stock validation
- transaction boundaries

The most important backend learning point is not simply CRUD.  
The important point is whether the system preserves correctness during checkout.

### Learning Goal 2 - Production-Minded API Design with Auth, Admin, and External Integration
Learn how to build a backend API that includes:

- authentication
- minimal authorization
- admin-only product/inventory management
- REST API structure
- DTOs
- validation
- exception handling
- pagination and basic search
- one payment integration boundary
- minimal tests for high-risk business flows

Anything not serving these two goals should be treated as secondary.

---

## 4. Must Scope
The following features are mandatory for this project.

### 4.1 Authentication
- User registration
- User login
- Token-based authentication
- Authenticated access to cart, checkout, orders
- Minimal role distinction:
  - `USER`
  - `ADMIN`

Keep role management simple.  
Do not build an advanced permission system.

### 4.2 Product API
- Public product list API
- Product detail API
- Basic keyword search
- Pagination
- Admin-only product creation
- Admin-only product update
- Admin-only product deletion or deactivation
- Admin-only price and inventory update

Search should remain basic.  
Do not add Elasticsearch or advanced search ranking in the Must scope.

### 4.3 Cart API
- Add product to cart
- Update cart item quantity
- Remove product from cart
- View current user's cart
- Prevent invalid quantity
- Prevent adding unavailable or inactive products

A user should have only their own cart.

### 4.4 Checkout and Order API
- Checkout current cart
- Validate product availability and stock
- Create order from cart items
- Store order item price snapshot
- Decrease or reserve stock safely within a transaction
- Clear cart after successful order creation
- Allow user to view own orders
- Allow user to view own order detail

### 4.5 Payment Flow
Implement one simple payment flow.

Minimum acceptable approach:
- Create a payment record for an order
- Use a mock payment gateway or sandbox adapter
- Record payment status:
  - `PENDING`
  - `SUCCESS`
  - `FAILED`
- Handle payment success/failure clearly
- Update order status based on payment result

Important:
- Do not process real money.
- Do not store real card data.
- Use fake/sandbox/mock payment only.

Actual Stripe sandbox integration is `Nice to have`, not required for first completion.

### 4.6 Database and Constraints
Use explicit relational modeling.

At minimum, model:
- User
- Product
- Cart
- CartItem
- Order
- OrderItem
- Payment

Recommended constraints:
- unique email for users
- unique product per cart item within a cart
- non-negative price
- non-negative stock
- valid status values
- foreign key relationships where appropriate

### 4.7 API Quality Basics
- Consistent request/response DTOs
- Request validation
- Consistent exception handling
- Appropriate HTTP status codes
- Pagination for product list
- Ownership checks for cart and order access
- Admin-only checks for product and inventory management

### 4.8 Minimal Critical Tests
Testing should not expand endlessly, but this project is logic-heavy.  
At least minimal tests or clear manual verification should cover:

- adding item to cart
- checkout with valid stock
- checkout with insufficient stock
- price snapshot behavior
- payment success/failure status transition
- user cannot access another user's cart/order
- non-admin cannot create/update products

Broad test coverage is optional, but critical business flow verification is Must.

---

## 5. Suggested Endpoint Set
The endpoint design may change, but keep the API close to this structure.

### Auth
- `POST /auth/register`
- `POST /auth/login`

### Products
- `GET /products?page=0&size=20&keyword=...`
- `GET /products/{productId}`

### Admin Products
- `POST /admin/products`
- `PUT /admin/products/{productId}`
- `PATCH /admin/products/{productId}/inventory`
- `DELETE /admin/products/{productId}`

### Cart
- `GET /cart`
- `POST /cart/items`
- `PATCH /cart/items/{cartItemId}`
- `DELETE /cart/items/{cartItemId}`

### Orders and Checkout
- `POST /orders/checkout`
- `GET /orders`
- `GET /orders/{orderId}`

### Payments
- `POST /orders/{orderId}/payments`
- `GET /orders/{orderId}/payments/{paymentId}`

If this endpoint set becomes too large, prioritize the Must business flows rather than adding more endpoints.

---

## 6. Important Domain Rules
The following rules are central to this project.

### Product and Inventory
- A product can be active or inactive.
- Users cannot add inactive products to cart.
- Users cannot checkout products with insufficient stock.
- Stock must never become negative.

### Cart
- Cart is not an order.
- Cart price is not final.
- Cart items should reference products.
- Cart quantity must be positive.

### Order
- Order is created from a cart at checkout time.
- OrderItem must store product name and price snapshot at checkout time.
- Later product price changes must not change existing order item price.
- A user can only view their own orders.

### Payment
- Payment status must be explicit.
- Payment result should update order status.
- Payment failure should not pretend checkout succeeded.
- Real card information must never be stored.

### Transaction
Checkout is the most important transaction boundary.

During checkout, the system should:
1. load cart items
2. validate stock
3. create order
4. create order items with price snapshots
5. decrease or reserve stock
6. clear cart

If the checkout transaction fails, partial changes should not remain.

---

## 7. Scope Control Rules
Always classify new ideas as one of:

- `Must`
- `Nice to have`
- `Out of scope for now`
- `Future learning`

Do not let this project become a full commercial commerce platform.

This project is not for learning every backend topic at once.  
The core is:

- domain modeling
- transaction-safe checkout
- cart/order/payment flow
- basic admin product management
- API quality

---

## 8. Nice to Have
These are optional and should not block project completion.

- Real Stripe sandbox integration
- Payment webhook simulation
- Order cancellation
- Refund flow
- Coupons
- Discounts
- Product categories beyond a simple field
- Product images
- Product reviews
- Wishlist
- Shipping address and delivery tracking
- Swagger/OpenAPI
- Docker
- CI/CD
- AWS deployment
- Redis caching
- Advanced filtering
- Sorting
- Optimistic locking or pessimistic locking experiments
- More comprehensive test coverage
- Admin dashboard frontend
- Simple React client
- Recommendation feature
- Search ranking

---

## 9. Out of Scope for Now
Flag the following as out of scope unless explicitly requested after the Must scope is complete.

- Full frontend application
- Mobile app
- Microservices
- Event-driven architecture
- Kafka or message queues
- Real production payment integration
- Storing card information
- Full refund and dispute handling
- Multi-vendor marketplace
- Seller dashboard
- Warehouse management
- Delivery company integration
- Full observability stack
- Prometheus/Grafana setup
- Kubernetes
- Elasticsearch/OpenSearch
- Recommendation engine
- Machine learning model integration
- Full security hardening beyond project needs
- OAuth/social login
- Complex RBAC permission matrix
- Rewriting the project in Kotlin, Go, Node.js, or another language

---

## 10. Implementation Phases
Follow this order unless there is a strong reason not to.

### Phase 0 - Domain Sketch
Before implementation, clarify:
- entities
- relationships
- order/payment status values
- checkout flow
- transaction boundary

Do not over-design.

### Phase 1 - Auth and Roles
Implement:
- register
- login
- token authentication
- simple `USER` and `ADMIN` roles

### Phase 2 - Product and Admin Product Management
Implement:
- product list/detail
- basic search
- admin product create/update/delete
- admin inventory update

### Phase 3 - Cart
Implement:
- add to cart
- update quantity
- remove item
- view cart

### Phase 4 - Checkout and Order
Implement:
- checkout
- order creation
- price snapshot
- stock decrease/reservation
- order list/detail

### Phase 5 - Payment
Implement:
- mock or sandbox payment adapter
- payment status
- order status transition
- success/failure handling

### Phase 6 - Critical Verification and Documentation
Implement or document:
- critical tests/manual verification
- README
- API examples
- ERD or schema explanation
- design decisions
- known limitations

### Phase 7 - Final Refactoring Pass
Only after all Must features work end-to-end, do one bounded project-level refactoring pass.

---

## 11. Refactoring Policy
During implementation:
- fix only clearly harmful structure, duplication, naming, or responsibility issues
- do not redesign the whole system while core features are incomplete
- do not pursue architectural perfection early

After all Must features are working end-to-end:
- one final project-level refactoring pass is allowed
- time-box the pass
- focus on responsibility split, naming, duplication, transaction clarity, and error response consistency

After that:
- freeze structure unless there is a bug, security issue, transaction issue, or major design flaw

---

## 12. AI Interaction Rules
- Do NOT provide full implementations unless explicitly requested.
- Do NOT take over coding.
- Prefer explanation, reasoning, trade-offs, and review comments.
- If code is necessary, provide minimal snippets only.
- Ask which phase the project is in before giving large design advice.
- If the user proposes scope expansion, classify it clearly.
- If the user starts adding optional features too early, warn about scope creep.
- Keep advice aligned with Java/Spring Boot unless explicitly asked otherwise.

---

## 13. Development Goal
The goal is not to build a production-grade Amazon clone.

The goal is to complete a focused intermediate backend project that demonstrates:

- Spring Boot API implementation
- relational domain modeling
- authenticated user flows
- admin product management
- cart/order/payment business logic
- transaction-safe checkout
- basic payment integration boundary
- API validation and exception handling
- enough reasoning to explain design decisions in interviews

