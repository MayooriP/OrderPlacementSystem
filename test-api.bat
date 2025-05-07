@echo off
echo ===================================================
echo    Restaurant Order System - API Test Script
echo ===================================================
echo.

set BASE_URL=http://localhost:8081

echo Checking if application is running...
curl --silent %BASE_URL% >nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Application is not running or not accessible at %BASE_URL%
    echo Please start the application using run-project.bat first.
    pause
    exit /b 1
)

echo Application is running at %BASE_URL%
echo.
echo Starting automatic API tests...
echo.

echo ===================================================
echo 1. Testing Home Endpoint
echo ===================================================
echo GET %BASE_URL%/
curl -v %BASE_URL%/
echo.
echo.

echo ===================================================
echo 2. Testing Categories Endpoint
echo ===================================================
echo GET %BASE_URL%/api/categories
curl -v %BASE_URL%/api/categories
echo.
echo.

echo ===================================================
echo 3. Testing Menu Items Endpoint
echo ===================================================
echo GET %BASE_URL%/api/menu-items
curl -v %BASE_URL%/api/menu-items
echo.
echo.

echo ===================================================
echo 4. Testing Customers Endpoint
echo ===================================================
echo GET %BASE_URL%/api/customers
curl -v %BASE_URL%/api/customers
echo.
echo.

echo ===================================================
echo 5. Testing Orders Endpoint
echo ===================================================
echo GET %BASE_URL%/api/orders
curl -v %BASE_URL%/api/orders
echo.
echo.

echo ===================================================
echo 6. Testing Create Customer
echo ===================================================
echo POST %BASE_URL%/api/customers
curl -v -X POST %BASE_URL%/api/customers -H "Content-Type: application/json" -d "{\"fullName\":\"Test Customer\",\"email\":\"test@example.com\",\"status\":\"ACTIVE\"}"
echo.
echo.

echo ===================================================
echo 7. Testing Create Category
echo ===================================================
echo POST %BASE_URL%/api/categories
curl -v -X POST %BASE_URL%/api/categories -H "Content-Type: application/json" -d "{\"name\":\"Test Category\",\"description\":\"Test category description\",\"status\":\"ACTIVE\"}"
echo.
echo.

REM Get the first category ID for creating a menu item
echo Getting a category ID for menu item creation...
for /f "tokens=*" %%a in ('curl --silent %BASE_URL%/api/categories ^| findstr /C:"categoryId"') do (
    set category_line=%%a
    goto found_category
)
:found_category
echo %category_line%
for /f "tokens=2 delims=:," %%b in ("%category_line%") do set category_id=%%b
echo Using category ID: %category_id%

echo ===================================================
echo 8. Testing Create Menu Item
echo ===================================================
echo POST %BASE_URL%/api/menu-items
curl -v -X POST %BASE_URL%/api/menu-items -H "Content-Type: application/json" -d "{\"name\":\"Test Item\",\"description\":\"Test item description\",\"price\":9.99,\"categoryId\":%category_id%,\"available\":true,\"status\":\"ACTIVE\"}"
echo.
echo.

REM Get the first customer ID for placing an order
echo Getting a customer ID for order placement...
for /f "tokens=*" %%a in ('curl --silent %BASE_URL%/api/customers ^| findstr /C:"customerId"') do (
    set customer_line=%%a
    goto found_customer
)
:found_customer
echo %customer_line%
for /f "tokens=2 delims=:," %%b in ("%customer_line%") do set customer_id=%%b
echo Using customer ID: %customer_id%

REM Get the first restaurant ID for placing an order
echo Getting a restaurant ID for order placement...
for /f "tokens=*" %%a in ('curl --silent %BASE_URL%/api/restaurants ^| findstr /C:"restaurantId"') do (
    set restaurant_line=%%a
    goto found_restaurant
)
:found_restaurant
echo %restaurant_line%
for /f "tokens=2 delims=:," %%b in ("%restaurant_line%") do set restaurant_id=%%b
echo Using restaurant ID: %restaurant_id%

echo ===================================================
echo 9. Testing Place Order
echo ===================================================
echo POST %BASE_URL%/api/orders
curl -v -X POST %BASE_URL%/api/orders -H "Content-Type: application/json" -d "{\"customerId\":%customer_id%,\"restaurantId\":%restaurant_id%,\"paymentMethod\":\"Pay Online\",\"orderDate\":\"2023-06-15T14:30:00\",\"deliveryDate\":\"2023-06-15T15:30:00\",\"pickupInstructions\":\"Test order\"}"
echo.
echo.

echo ===================================================
echo API Testing Complete
echo ===================================================
echo.
echo Press any key to exit...
pause > nul
