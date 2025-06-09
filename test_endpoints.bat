@echo off
echo ===== TESTING CUSTOMERS ENDPOINTS =====

echo GET all customers
curl -X GET http://localhost:8081/api/customers
echo.

echo GET customer by ID
curl -X GET http://localhost:8081/api/customers/1
echo.

echo POST create new customer
curl -X POST http://localhost:8081/api/customers -H "Content-Type: application/json" -d "{\"fullName\":\"Alice Johnson\",\"email\":\"alice.johnson@example.com\",\"firstName\":\"Alice\",\"dob\":\"1990-05-12\",\"gender\":\"Female\"}"
echo.

echo PUT update customer
curl -X PUT http://localhost:8081/api/customers/2 -H "Content-Type: application/json" -d "{\"fullName\":\"Bob Smith\",\"email\":\"bob.smith@example.com\",\"firstName\":\"Bob\"}"
echo.

echo DELETE customer
curl -X DELETE http://localhost:8081/api/customers/3
echo.

echo ===== TESTING CATEGORIES ENDPOINTS =====

echo GET all categories
curl -X GET http://localhost:8081/api/categories
echo.

echo GET category by ID
curl -X GET http://localhost:8081/api/categories/1
echo.

echo POST create new category
curl -X POST http://localhost:8081/api/categories -H "Content-Type: application/json" -d "{\"name\":\"Pizza\",\"description\":\"All kinds of pizzas\"}"
echo.

echo PUT update category
curl -X PUT http://localhost:8081/api/categories/2 -H "Content-Type: application/json" -d "{\"name\":\"Sushi\",\"description\":\"Authentic Japanese sushi\"}"
echo.

echo DELETE category
curl -X DELETE http://localhost:8081/api/categories/3
echo.

echo ===== TESTING MENU ITEMS ENDPOINTS =====

echo GET all menu items
curl -X GET http://localhost:8081/api/menu-items
echo.

echo GET menu item by ID
curl -X GET http://localhost:8081/api/menu-items/1
echo.

echo POST create new menu item
curl -X POST http://localhost:8081/api/menu-items -H "Content-Type: application/json" -d "{\"name\":\"Margherita Pizza\",\"description\":\"Classic cheese and tomato pizza\",\"price\":9.99,\"category\":{\"categoryId\":1}}"
echo.

echo PUT update menu item
curl -X PUT http://localhost:8081/api/menu-items/3 -H "Content-Type: application/json" -d "{\"name\":\"Chocolate Cake\",\"description\":\"Rich chocolate layered cake\",\"price\":7.00,\"category\":{\"categoryId\":3}}"
echo.

echo DELETE menu item
curl -X DELETE http://localhost:8081/api/menu-items/3
echo.

echo ===== TESTING CART ENDPOINTS =====

echo GET cart by customerId
curl -X GET http://localhost:8081/api/cart/customer/1
echo.

echo POST add item to cart
curl -X POST http://localhost:8081/api/cart/add -H "Content-Type: application/json" -d "{\"customerId\":1,\"menuItemId\":2,\"quantity\":3,\"specialInstructions\":\"Extra cheese\"}"
echo.

echo PUT update cart item
curl -X PUT http://localhost:8081/api/cart/item/5 -H "Content-Type: application/json" -d "{\"quantity\":5,\"specialInstructions\":\"No spice\"}"
echo.

echo DELETE cart item
curl -X DELETE http://localhost:8081/api/cart/item/5
echo.

echo DELETE clear cart by customerId
curl -X DELETE http://localhost:8081/api/cart/customer/1
echo.

echo ===== TESTING ORDERS ENDPOINTS =====

echo POST place order
curl -X POST http://localhost:8081/api/orders -H "Content-Type: application/json" -d "{\"customerId\":1,\"restaurantId\":3,\"paymentMethod\":\"Cash\",\"orderDate\":\"2025-06-09T15:00:00\",\"deliveryDate\":\"2025-06-09T16:00:00\",\"pickupInstructions\":\"Ring bell\",\"couponCode\":null}"
echo.

echo GET all orders
curl -X GET http://localhost:8081/api/orders
echo.

echo GET order by ID
curl -X GET http://localhost:8081/api/orders/ORD1002
echo.

echo GET orders by customer ID
curl -X GET http://localhost:8081/api/orders/customer/1
echo.

echo PUT cancel order
curl -X PUT http://localhost:8081/api/orders/ORD1002/cancel
echo.

pause
