# Restaurant Order System - Project Documentation

## Project Overview
The Restaurant Order System is a comprehensive Spring Boot application that manages the entire order placement process for restaurants. It provides a robust API for handling customers, menu items, cart functionality, order processing, payment handling, and various discount mechanisms.

## Technology Stack
- **Backend Framework**: Spring Boot 2.7.18
- **Database**: MySQL 8
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Java Version**: 11
- **Cloud Services**: AWS (DynamoDB, S3, SQS)
- **Additional Libraries**:
  - Lombok for reducing boilerplate code
  - Hibernate Types for JSON support
  - Spring Validation for request validation

## Project Structure

### Directory Structure

```
OrderPlacementSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── restaurant/
│   │   │           └── ordersystem/
│   │   │               ├── config/                 # Configuration classes
│   │   │               │   ├── HibernateConfig.java
│   │   │               │   └── JacksonConfig.java
│   │   │               ├── controller/             # REST API controllers
│   │   │               │   ├── CartController.java
│   │   │               │   ├── CategoryController.java
│   │   │               │   ├── CustomerController.java
│   │   │               │   ├── HomeController.java
│   │   │               │   ├── MenuItemController.java
│   │   │               │   └── OrderController.java
│   │   │               ├── dto/                    # Data Transfer Objects
│   │   │               │   ├── CartDTO.java
│   │   │               │   ├── CartItemDTO.java
│   │   │               │   ├── ErrorResponseDTO.java
│   │   │               │   ├── OrderItemDTO.java
│   │   │               │   ├── OrderRequestDTO.java
│   │   │               │   └── OrderResponseDTO.java
│   │   │               ├── exception/              # Custom exceptions
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── InvalidCouponException.java
│   │   │               │   ├── InvalidOrderException.java
│   │   │               │   ├── JsonProcessingException.java
│   │   │               │   └── ResourceNotFoundException.java
│   │   │               ├── model/                  # Entity classes
│   │   │               │   ├── Cart.java
│   │   │               │   ├── CartItem.java
│   │   │               │   ├── Category.java
│   │   │               │   ├── Coupon.java
│   │   │               │   ├── Customer.java
│   │   │               │   ├── MenuItem.java
│   │   │               │   ├── Order.java
│   │   │               │   ├── OrderItem.java
│   │   │               │   ├── Referral.java
│   │   │               │   ├── Restaurant.java
│   │   │               │   ├── RestaurantWorkingHours.java
│   │   │               │   ├── SubCategory.java
│   │   │               │   ├── Variant.java
│   │   │               │   └── Voucher.java
│   │   │               ├── repository/             # Data access interfaces
│   │   │               │   ├── CartItemRepository.java
│   │   │               │   ├── CartRepository.java
│   │   │               │   ├── CategoryRepository.java
│   │   │               │   ├── CouponRepository.java
│   │   │               │   ├── CustomerRepository.java
│   │   │               │   ├── MenuItemRepository.java
│   │   │               │   ├── OrderItemRepository.java
│   │   │               │   ├── OrderRepository.java
│   │   │               │   ├── ReferralRepository.java
│   │   │               │   ├── RestaurantRepository.java
│   │   │               │   ├── RestaurantWorkingHoursRepository.java
│   │   │               │   ├── SubCategoryRepository.java
│   │   │               │   ├── VariantRepository.java
│   │   │               │   └── VoucherRepository.java
│   │   │               ├── service/                # Business logic
│   │   │               │   ├── CartService.java
│   │   │               │   ├── DiscountService.java
│   │   │               │   ├── DynamoDBService.java
│   │   │               │   ├── OrderService.java
│   │   │               │   ├── PaymentService.java
│   │   │               │   └── RestaurantService.java
│   │   │               ├── util/                   # Utility classes
│   │   │               │   └── RestaurantHoursUtil.java
│   │   │               └── OrderSystemApplication.java  # Main application class
│   │   └── resources/
│   │       └── application.properties              # Application configuration
│   └── test/
│       └── java/
│           └── com/
│               └── restaurant/
│                   └── ordersystem/
│                       ├── controller/             # Controller tests
│                       │   └── OrderControllerTest.java
│                       └── service/                # Service tests
│                           └── OrderServiceTest.java
├── .github/                                        # GitHub workflows
│   └── workflows/
│       └── maven.yml
├── .gitignore                                      # Git ignore file
├── database_schema.txt                             # Database schema documentation
├── lombok.config                                   # Lombok configuration
└── pom.xml                                         # Maven project file
```

### Main Components

#### 1. Models (Entity Classes)
- **Customer**: Stores customer information
- **Restaurant**: Contains restaurant details
- **Category/SubCategory**: Organizes menu items hierarchically
- **MenuItem**: Represents food and beverage items
- **Variant**: Represents variations of menu items (e.g., sizes, options)
- **Cart/CartItem**: Manages shopping cart functionality
- **Order/OrderItem**: Handles order processing and tracking
- **Coupon/Voucher/Referral**: Implements discount mechanisms
- **RestaurantWorkingHours**: Defines restaurant operating hours

#### 2. Controllers
- **HomeController**: Provides API overview and documentation
- **CustomerController**: Manages customer data
- **CategoryController**: Handles menu categories
- **MenuItemController**: Manages menu items
- **CartController**: Handles shopping cart operations
- **OrderController**: Processes and manages orders

#### 3. Services
- **OrderService**: Core business logic for order processing
- **CartService**: Shopping cart management
- **PaymentService**: Payment processing
- **DiscountService**: Applies various discount types
- **DynamoDBService**: AWS DynamoDB integration for order storage
- **RestaurantService**: Restaurant management and availability checking

#### 4. Repositories (Data Access)
JPA repositories for each entity to handle database operations:
- **CustomerRepository**: Customer data access
- **RestaurantRepository**: Restaurant data access
- **CategoryRepository/SubCategoryRepository**: Menu organization
- **MenuItemRepository/VariantRepository**: Menu items and variants
- **CartRepository/CartItemRepository**: Shopping cart operations
- **OrderRepository/OrderItemRepository**: Order processing
- **CouponRepository/VoucherRepository/ReferralRepository**: Discount mechanisms

#### 5. DTOs (Data Transfer Objects)
- **OrderRequestDTO/OrderResponseDTO**: For order operations
- **CartDTO/CartItemDTO**: For cart operations
- **ErrorResponseDTO**: For standardized error responses

#### 6. Exception Handling
- **GlobalExceptionHandler**: Centralized exception handling
- Custom exceptions: ResourceNotFoundException, InvalidOrderException, InvalidCouponException

#### 7. Configuration
- **HibernateConfig**: Database ORM configuration
- **JacksonConfig**: JSON serialization/deserialization configuration

#### 8. Utilities
- **RestaurantHoursUtil**: Helper methods for checking restaurant availability

## Database Schema

### Key Tables
1. **customers**: Stores customer information
   - Primary fields: customer_id, full_name, email, status

2. **restaurants**: Restaurant information
   - Primary fields: restaurant_id, name, description, address, phone_number, email

3. **categories/subcategories**: Menu organization
   - Primary fields: category_id/subcategory_id, name, description

4. **menu_items**: Food and beverage items
   - Primary fields: item_id, name, description, category_id, subcategory_id, price

5. **variants**: Item variations
   - Primary fields: variant_id, item_id, variant_name, price

6. **carts/cart_items**: Shopping cart functionality
   - Primary fields: cart_id, customer_id, total_amount, status

7. **orders/order_items**: Order processing and history
   - Primary fields: order_id, customer_id, restaurant_id, payment_id, status

8. **coupons/vouchers/referrals**: Discount mechanisms
   - Primary fields: coupon_id, coupon_code, discount_type, discount_value

## API Endpoints

### Home
- `GET /`: API overview and available endpoints

### Customers
- `GET /api/customers`: Get all customers
- `GET /api/customers/{id}`: Get customer by ID
- `POST /api/customers`: Create new customer
- `PUT /api/customers/{id}`: Update customer
- `DELETE /api/customers/{id}`: Delete customer

### Categories
- `GET /api/categories`: Get all categories
- `GET /api/categories/{id}`: Get category by ID
- `POST /api/categories`: Create new category
- `PUT /api/categories/{id}`: Update category
- `DELETE /api/categories/{id}`: Delete category

### Menu Items
- `GET /api/menu-items`: Get all menu items
- `GET /api/menu-items/{id}`: Get menu item by ID
- `POST /api/menu-items`: Create new menu item
- `PUT /api/menu-items/{id}`: Update menu item
- `DELETE /api/menu-items/{id}`: Delete menu item

### Cart
- `GET /api/cart/{customerId}`: Get customer's cart
- `POST /api/cart/add`: Add item to cart
- `PUT /api/cart/update`: Update cart item
- `DELETE /api/cart/remove/{cartItemId}`: Remove item from cart
- `DELETE /api/cart/clear/{customerId}`: Clear customer's cart

### Orders
- `POST /api/orders`: Place new order
- `GET /api/orders`: Get all orders
- `GET /api/orders/{orderId}`: Get order by ID
- `GET /api/orders/customer/{customerId}`: Get customer's orders
- `PUT /api/orders/{orderId}/cancel`: Cancel order
- `PUT /api/orders/{orderId}/status`: Update order status

## Error Handling
The application implements a comprehensive error handling strategy:

1. **GlobalExceptionHandler**: Central component that catches and processes all exceptions
2. **Custom Exception Types**:
   - ResourceNotFoundException: When requested resource doesn't exist
   - InvalidOrderException: For order validation failures
   - InvalidCouponException: For coupon validation failures
3. **Standardized Error Responses**: Using ErrorResponseDTO for consistent error format

## AWS Integration
The application integrates with several AWS services:

1. **DynamoDB**: For storing order data as a backup/audit trail
2. **S3**: For storing images and other assets
3. **SQS**: For order event messaging

## Running the Project

### Prerequisites
- Java 11 or higher
- Maven
- MySQL 8.0 or higher
- MySQL Workbench (for database management)

### Database Setup
1. Create a MySQL database named `order_system`
2. Configure database connection in `application.properties`
   - Default username: root
   - Default password: 1234
   - Default port: 3306

### Running the Application
```bash
# Clone the repository
git clone <repository-url>
cd OrderPlacementSystem

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Run with skipping tests
mvn spring-boot:run -DskipTests
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=OrderServiceTest

# Skip tests during build
mvn clean install -DskipTests
```

## API Testing with Postman/cURL

### Example API Requests

#### 1. Get API Overview
```bash
curl -X GET http://localhost:8081/
```

#### 2. Get All Categories
```bash
curl -X GET http://localhost:8081/api/categories
```

#### 3. Get All Menu Items
```bash
curl -X GET http://localhost:8081/api/menu-items
```

#### 4. Place an Order
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "paymentMethod": "Pay Online",
    "orderDate": "2023-06-15T14:30:00",
    "deliveryDate": "2023-06-15T15:30:00",
    "pickupInstructions": "Leave at reception"
  }'
```

## Common Errors and Troubleshooting

### Database Connection Issues
- **Error**: Unable to connect to MySQL database
- **Solution**:
  - Verify MySQL is running
  - Check credentials in application.properties
  - Ensure database exists

### Missing Required Fields
- **Error**: 400 Bad Request with validation errors
- **Solution**: Check request payload for missing required fields

### Resource Not Found
- **Error**: 404 Not Found
- **Solution**: Verify the resource ID exists in the database

### AWS Configuration Issues
- **Error**: AWS service connection failures
- **Solution**:
  - Check AWS credentials
  - Verify region configuration
  - Ensure required AWS services are available

## Project Requirements and Business Rules

### Core Requirements
1. **Order Management System**: A comprehensive system to handle the entire order lifecycle
2. **Customer Management**: Track customer information and order history
3. **Menu Management**: Organize menu items by categories and subcategories
4. **Cart Functionality**: Allow customers to add items to cart before placing orders
5. **Payment Processing**: Support multiple payment methods
6. **Discount System**: Apply various types of discounts (coupons, vouchers, referrals)
7. **Restaurant Availability**: Check restaurant working hours before accepting orders
8. **Order Status Tracking**: Track order status throughout its lifecycle
9. **Data Persistence**: Store order data in both MySQL and DynamoDB for redundancy

### Business Rules

#### Order Processing
1. Customer must exist in the system to place an order
2. Restaurant must exist and be available at the requested delivery time
3. Cart must contain at least one item
4. Order total is calculated based on item prices and quantities
5. Discounts are applied based on applicable coupons or promotions
6. Payment must be processed successfully before order confirmation
7. Order status is tracked from receipt through completion
8. Order cancellation is allowed only for orders in certain statuses

#### Discount Application
1. Coupons have validation rules (expiration date, minimum order value)
2. Only one coupon can be applied per order
3. Discount value is calculated based on discount type (percentage or fixed amount)
4. Free items may be added to order based on specific promotions

#### Restaurant Availability
1. Each restaurant has defined working hours for each day of the week
2. Orders can only be placed during restaurant working hours
3. Special holiday hours can override regular working hours

## Database Tables in Detail

### customers
Stores customer information including personal details and contact information.
- **customer_id**: Primary key, auto-increment
- **user_id**: Reference to user account (if applicable)
- **full_name**: Customer's full name
- **email**: Customer's email address
- **first_name**: Customer's first name
- **dob**: Date of birth
- **gender**: Enum (Male, Female, PREFER_NOT_TO_SAY)
- **status**: Account status

### restaurants
Contains restaurant details including contact information and operational status.
- **restaurant_id**: Primary key, auto-increment
- **name**: Restaurant name
- **description**: Restaurant description
- **address**: Physical address
- **phone_number**: Contact phone
- **email**: Contact email
- **opening_hours**: General hours information
- **logo_url**: URL to restaurant logo
- **status**: Operational status

### restaurant_working_hours
Defines specific working hours for each day of the week.
- **id**: Primary key, auto-increment
- **restaurant_id**: Foreign key to restaurants
- **day_of_week**: Day (0-6, Sunday-Saturday)
- **opening_time**: Opening time for that day
- **closing_time**: Closing time for that day
- **is_closed**: Whether restaurant is closed that day

### categories
Organizes menu items by type.
- **category_id**: Primary key, auto-increment
- **name**: Category name
- **description**: Category description
- **status**: Active/Inactive

### subcategories
Further organizes menu items within main categories.
- **subcategory_id**: Primary key, auto-increment
- **name**: Subcategory name
- **description**: Subcategory description
- **category_id**: Foreign key to categories
- **status**: Active/Inactive

### menu_items
Stores food and beverage items available for ordering.
- **item_id**: Primary key, auto-increment
- **name**: Item name
- **description**: Item description
- **category_id**: Foreign key to categories
- **subcategory_id**: Foreign key to subcategories
- **price**: Base price
- **image_url**: URL to item image
- **available**: Whether item is currently available
- **status**: Active/Inactive

### variants
Stores variations of menu items (e.g., sizes, options).
- **variant_id**: Primary key, auto-increment
- **item_id**: Foreign key to menu_items
- **variant_name**: Name of the variant
- **price**: Additional price for this variant
- **status**: Active/Inactive

### carts
Tracks shopping carts for customers.
- **cart_id**: Primary key, auto-increment
- **customer_id**: Foreign key to customers
- **total_amount**: Total amount of all items
- **status**: Cart status (ACTIVE, COMPLETED)

### cart_items
Stores individual items within a cart.
- **cart_item_id**: Primary key, auto-increment
- **cart_id**: Foreign key to carts
- **item_id**: Foreign key to menu_items
- **variant_id**: Foreign key to variants
- **quantity**: Quantity of this item
- **price**: Price per unit
- **subtotal**: Total price for this item
- **special_instructions**: Special instructions for this item

### orders
Stores finalized customer orders.
- **order_id**: Primary key (UUID)
- **customer_id**: Foreign key to customers
- **restaurant_id**: Foreign key to restaurants
- **payment_id**: Reference to payment
- **order_date**: When order was placed
- **delivery_date**: When order is to be delivered/picked up
- **status**: Order status (Received, Preparing, ReadyToPickup, OrderCompleted, Cancelled)
- **coupon_id**: Foreign key to coupons
- **status_history**: JSON field tracking status changes
- **square_order_id**: External order ID in Square system

### order_items
Stores individual items within an order.
- **order_item_id**: Primary key, auto-increment
- **order_id**: Foreign key to orders
- **item_id**: Foreign key to menu_items
- **variant_id**: Foreign key to variants
- **item_name**: Name of the item (snapshot)
- **category_name**: Category name (snapshot)
- **subcategory_name**: Subcategory name (snapshot)
- **variant_name**: Variant name (snapshot)
- **price**: Price per unit (snapshot)
- **quantity**: Quantity ordered
- **subtotal**: Total price for this item
- **is_free_item**: Whether this is a free item

## Error Handling in Detail

The application implements a comprehensive error handling strategy through the `GlobalExceptionHandler` class, which uses Spring's `@RestControllerAdvice` to centralize exception handling.

### Exception Types

1. **ResourceNotFoundException**
   - Thrown when a requested resource doesn't exist in the database
   - Returns HTTP 404 (Not Found) status
   - Example: Customer with ID 123 not found

2. **InvalidOrderException**
   - Thrown when order validation fails
   - Returns HTTP 400 (Bad Request) status
   - Examples: Empty cart, restaurant closed, invalid delivery time

3. **InvalidCouponException**
   - Thrown when coupon validation fails
   - Returns HTTP 400 (Bad Request) status
   - Examples: Expired coupon, minimum order value not met

4. **MethodArgumentNotValidException**
   - Thrown by Spring when request validation fails
   - Returns HTTP 400 (Bad Request) status with field-level error details
   - Example: Missing required fields in request payload

5. **General Exception**
   - Catches all other unhandled exceptions
   - Returns HTTP 500 (Internal Server Error) status
   - Logs detailed error information for troubleshooting

### Error Response Format

All errors return a standardized `ErrorResponseDTO` with:
- Timestamp: When the error occurred
- Status: HTTP status code
- Error: Error type description
- Message: Detailed error message
- Path: Request path that caused the error

## Potential Manager Questions and Answers

### Technical Questions

1. **Q: How does the system handle concurrent orders?**
   - A: The system uses Spring's transaction management to ensure data consistency. Database locks prevent conflicts when multiple users try to modify the same data simultaneously.

2. **Q: How is data security implemented?**
   - A: Sensitive data like customer emails and phone numbers can be encrypted. The application uses HTTPS for secure communication, and database credentials are stored securely in configuration files.

3. **Q: How does the AWS integration work?**
   - A: The application uses AWS SDK to interact with DynamoDB for order storage, S3 for image storage, and SQS for event messaging. AWS credentials are managed through the default credential provider chain.

4. **Q: How scalable is the system?**
   - A: The application follows a microservices-inspired architecture that can be scaled horizontally. Database connections are pooled, and stateless design allows for load balancing.

5. **Q: How are database migrations handled?**
   - A: The application uses Hibernate's schema update feature (`spring.jpa.hibernate.ddl-auto=update`) to automatically apply schema changes. For production, a dedicated migration tool like Flyway or Liquibase would be recommended.

### Business Questions

1. **Q: How does the system handle peak order times?**
   - A: The system is designed to handle concurrent requests efficiently. For extremely high loads, the application can be scaled horizontally by adding more instances behind a load balancer.

2. **Q: What analytics capabilities does the system provide?**
   - A: The system stores comprehensive order data that can be used for analytics. Future enhancements could include a dedicated reporting module or integration with BI tools.

3. **Q: How does the system integrate with external payment processors?**
   - A: The system has a flexible payment service interface that can be implemented for various payment providers. Currently, it supports basic payment tracking with hooks for external payment processing.

4. **Q: How customizable is the discount system?**
   - A: The discount system supports multiple types of discounts (coupons, vouchers, referrals) with various calculation methods (percentage, fixed amount). New discount types can be added by extending the discount service.

5. **Q: What measures are in place to prevent order errors?**
   - A: The system implements comprehensive validation at multiple levels: request validation, business rule validation, and database constraints. Error handling provides clear feedback to users when issues occur.

## Additional Implementation Details

### Lombok Integration
The project uses Lombok to reduce boilerplate code. Key annotations include:
- `@Data`: Generates getters, setters, equals, hashCode, and toString methods
- `@NoArgsConstructor`/`@AllArgsConstructor`: Generates constructors
- Manual getters and setters are also included for better IDE compatibility

### JSON Handling
- Uses Jackson for JSON serialization/deserialization
- Custom ObjectMapper configuration for handling Java 8 date/time types
- JSON type support for storing complex data like status history

### Logging Strategy
- SLF4J with Logback implementation
- Different log levels for different environments (DEBUG for development, INFO for production)
- File-based logging with rotation for production use

### Testing Approach
- Unit tests for service and controller layers
- Mock-based testing using Mockito
- Integration tests for repository layer
- Test coverage for critical business logic
