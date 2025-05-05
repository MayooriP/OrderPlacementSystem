# Order Placement System

A Spring Boot-based backend service that allows customers to place food orders from a restaurant.

## Features

- Order validation
- Coupon/voucher/referral discounts
- Payment creation
- Order item tracking
- Integration with AWS DynamoDB

## Requirements

- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Setup

1. Clone the repository
2. Configure MySQL database in `application.properties`
3. Run the application

## Database Configuration

The application uses MySQL as the database. You can configure the database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_system?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Running the Application

```bash
mvn spring-boot:run
```

## API Endpoints

### Place Order

```
POST /api/orders
```

Request Body:
```json
{
  "customerId": 1,
  "restaurantId": 1,
  "paymentMethod": "Pay Online",
  "couponCode": "WELCOME10",
  "orderDate": "2023-05-01T12:00:00",
  "deliveryDate": "2023-05-01T13:00:00",
  "pickupInstructions": "Leave at the door"
}
```

Response:
```json
{
  "orderId": "550e8400-e29b-41d4-a716-446655440000",
  "customerId": 1,
  "customerName": "John Doe",
  "restaurantId": 1,
  "restaurantName": "Restaurant Name",
  "paymentId": "550e8400-e29b-41d4-a716-446655440001",
  "paymentMethod": "Pay Online",
  "paymentStatus": "Paid",
  "orderDate": "2023-05-01T12:00:00",
  "deliveryDate": "2023-05-01T13:00:00",
  "orderStatus": "Received",
  "totalPrice": 50.00,
  "discountValue": 5.00,
  "finalPrice": 45.00,
  "couponCode": "WELCOME10",
  "pickupInstructions": "Leave at the door",
  "orderItems": [
    {
      "itemId": 1,
      "itemName": "Burger",
      "variantId": 1,
      "variantName": "Regular",
      "categoryName": "Fast Food",
      "subCategoryName": "Burgers",
      "price": 10.00,
      "quantity": 2,
      "subtotal": 20.00,
      "specialInstructions": "No onions",
      "isFreeItem": false
    }
  ]
}
```

## AWS DynamoDB Integration

The application integrates with AWS DynamoDB to store order information. Configure the AWS credentials in `application.properties`:

```properties
aws.dynamodb.endpoint=http://localhost:8000
aws.region=us-east-1
aws.accessKey=your_access_key
aws.secretKey=your_secret_key
```

## Business Rules

- A coupon or voucher is valid only if not expired and active.
- A referral code can only be used once per customer.
- Empty cart should reject order placement.
- Payment status depends on method chosen.
- Restaurant works Tue - Sun 11:00 AM - 2:00 PM and 5:00PM to 10:00PM.
