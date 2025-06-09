package com.restaurant.ordersystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.model.*;
import com.restaurant.ordersystem.repository.*;
import com.restaurant.ordersystem.util.RestaurantHoursUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for the OrderService class using Mockito for mocking dependencies.
 *
 * Mocking is a technique used in unit testing where real objects are replaced with simulated ones
 * that mimic the behavior of the real objects in a controlled way. This approach provides several benefits:
 *
 * 1. Isolation: Tests focus on OrderService behavior without depending on actual implementations of repositories and services
 * 2. Speed: Tests run faster without actual database operations or external service calls
 * 3. Reliability: Tests don't depend on external systems that might be unavailable or inconsistent
 * 4. Control: Allows testing specific scenarios including edge cases and error conditions
 *
 * In these tests, Mockito is used to:
 * - Create mock objects with @Mock annotations
 * - Configure mock behavior with when().thenReturn() statements
 * - Verify interactions with verify() statements
 */

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private ReferralRepository referralRepository;

    @Mock
    private RestaurantWorkingHoursRepository restaurantWorkingHoursRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private DiscountService discountService;

    @Mock
    private DynamoDBService dynamoDBService;

    @Mock
    private RestaurantHoursUtil restaurantHoursUtil;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDTO orderRequest;
    private Customer customer;
    private Restaurant restaurant;
    private Cart cart;
    private List<CartItem> cartItems;
    private MenuItem menuItem;
    private Variant variant;
    private Category category;
    private SubCategory subCategory;

    @BeforeEach
    void setUp() {
        // Setup customer
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setFullName("John Doe");
        customer.setEmail("john@example.com");

        // Setup restaurant
        restaurant = new Restaurant();
        restaurant.setRestaurantId(1);
        restaurant.setName("Test Restaurant");

        // Setup category
        category = new Category();
        category.setCategoryId(1);
        category.setName("Main Course");

        // Setup subcategory
        subCategory = new SubCategory();
        subCategory.setSubCategoryId(1);
        subCategory.setName("Pizza");
        subCategory.setCategory(category);

        // Setup menu item
        menuItem = new MenuItem();
        menuItem.setItemId(1);
        menuItem.setName("Margherita Pizza");
        menuItem.setCategory(category);
        menuItem.setSubCategory(subCategory);
        menuItem.setPrice(BigDecimal.valueOf(10.99));

        // Setup variant
        variant = new Variant();
        variant.setVariantId(1);
        variant.setVariantName("Large");
        variant.setItem(menuItem);
        variant.setPrice(BigDecimal.valueOf(2.00));

        // Setup cart items
        cartItems = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setMenuItem(menuItem);
        cartItem.setVariant(variant);
        cartItem.setQuantity(2);
        cartItem.setPrice(BigDecimal.valueOf(12.99));
        cartItem.setSubtotal(BigDecimal.valueOf(25.98));
        cartItems.add(cartItem);

        // Setup cart
        cart = new Cart();
        cart.setCartId(1);
        cart.setCustomer(customer);
        cart.setCartItems(cartItems);
        cart.setTotalAmount(BigDecimal.valueOf(25.98));
        cart.setStatus("ACTIVE");

        // Setup order request
        orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1);
        orderRequest.setRestaurantId(1);
        orderRequest.setPaymentMethod("Pay Online");
        orderRequest.setOrderDate(LocalDateTime.now());
        orderRequest.setDeliveryDate(LocalDateTime.now().plusHours(2));
    }

    /**
     * Test case for placing a valid order.
     *
     * This test demonstrates the basic mocking approach:
     * 1. Mock repositories to return test data instead of querying a database
     * 2. Mock services to return predefined values instead of making actual service calls
     * 3. Use argument matchers (any(), anyInt(), eq()) to handle different parameter scenarios
     * 4. Use thenAnswer() to provide dynamic responses based on input parameters
     */
    @Test
    void testPlaceOrder_ValidRequest_ReturnsOrderResponse() {
        // Arrange - Configure mock objects to return test data
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(cartRepository.findByCustomerAndStatus(customer, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(cartItems);
        when(paymentService.createPayment(anyInt(), any(BigDecimal.class), anyString())).thenReturn("payment123");

        // Use thenAnswer to dynamically set the order ID when save is called
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId("order123");
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Set delivery date to a Tuesday at noon (restaurant is open)
        LocalDateTime deliveryDate = LocalDateTime.now().with(DayOfWeek.TUESDAY).withHour(12).withMinute(0);
        orderRequest.setDeliveryDate(deliveryDate);

        // Mock restaurant hours util to return true (restaurant is open)
        when(restaurantHoursUtil.isRestaurantOpen(eq(restaurant), any(LocalDateTime.class))).thenReturn(true);

        // Act
        OrderResponseDTO response = orderService.placeOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals("order123", response.getOrderId());
        assertEquals(customer.getCustomerId(), response.getCustomerId());
        assertEquals(restaurant.getRestaurantId(), response.getRestaurantId());
        assertEquals("payment123", response.getPaymentId());
        assertEquals("Pay Online", response.getPaymentMethod());
        assertEquals("Paid", response.getPaymentStatus());
        assertEquals("Received", response.getOrderStatus());
        assertEquals(cart.getTotalAmount(), response.getTotalPrice());
        assertEquals(1, response.getOrderItems().size());

        // Verify interactions
        verify(customerRepository).findById(1);
        verify(restaurantRepository).findById(1);
        verify(cartRepository).findByCustomerAndStatus(customer, "ACTIVE");
        verify(cartItemRepository).findByCart(cart);
        verify(paymentService).createPayment(anyInt(), any(BigDecimal.class), anyString());
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(dynamoDBService).saveOrder(any(Order.class), eq(customer), eq(restaurant), anyList());
        verify(restaurantHoursUtil).isRestaurantOpen(eq(restaurant), any(LocalDateTime.class));
    }

    @Test
    void testPlaceOrder_WithCoupon_AppliesDiscount() {
        // Arrange
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(cartRepository.findByCustomerAndStatus(customer, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(cartItems);
        when(paymentService.createPayment(anyInt(), any(BigDecimal.class), anyString())).thenReturn("payment123");
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId("order123");
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Set delivery date to a Tuesday at noon (restaurant is open)
        LocalDateTime deliveryDate = LocalDateTime.now().with(DayOfWeek.TUESDAY).withHour(12).withMinute(0);
        orderRequest.setDeliveryDate(deliveryDate);
        orderRequest.setCouponCode("DISCOUNT10");

        // Mock restaurant hours util to return true (restaurant is open)
        when(restaurantHoursUtil.isRestaurantOpen(eq(restaurant), any(LocalDateTime.class))).thenReturn(true);

        // Mock coupon validation and discount calculation
        Coupon coupon = new Coupon();
        coupon.setCouponId(1);
        coupon.setCouponCode("DISCOUNT10");
        coupon.setStatus(Coupon.CouponStatus.Active);

        when(discountService.validateCoupon("DISCOUNT10")).thenReturn(coupon);
        when(discountService.applyCouponDiscount(eq(coupon), any(BigDecimal.class))).thenReturn(BigDecimal.valueOf(2.60));

        // Act
        OrderResponseDTO response = orderService.placeOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals("order123", response.getOrderId());
        assertEquals(customer.getCustomerId(), response.getCustomerId());
        assertEquals(restaurant.getRestaurantId(), response.getRestaurantId());
        assertEquals("payment123", response.getPaymentId());
        assertEquals("Pay Online", response.getPaymentMethod());
        assertEquals("Paid", response.getPaymentStatus());
        assertEquals("Received", response.getOrderStatus());
        assertEquals(cart.getTotalAmount(), response.getTotalPrice());
        assertEquals(BigDecimal.valueOf(2.60), response.getDiscountValue());
        assertEquals(cart.getTotalAmount().subtract(BigDecimal.valueOf(2.60)), response.getFinalPrice());
        assertEquals("DISCOUNT10", response.getCouponCode());

        // Verify interactions
        verify(discountService).validateCoupon("DISCOUNT10");
        verify(discountService).applyCouponDiscount(eq(coupon), any(BigDecimal.class));
        verify(restaurantHoursUtil).isRestaurantOpen(eq(restaurant), any(LocalDateTime.class));
    }

    /**
     * Test case for attempting to place an order when the restaurant is closed.
     *
     * This test demonstrates how mocking can be used to test error conditions:
     * 1. Mock the RestaurantHoursUtil to simulate a restaurant being closed
     * 2. Mock multiple related method calls to create a complete test scenario
     * 3. Test exception handling by verifying that the expected exception is thrown
     *
     * This approach allows testing error handling without needing to set up complex
     * data conditions in a real database or waiting for specific times of day.
     */
    @Test
    void testPlaceOrder_RestaurantClosed_ThrowsException() {
        // Arrange - Basic repository mocks
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));

        // Set delivery date to a Monday (restaurant is closed)
        LocalDateTime deliveryDate = LocalDateTime.now().with(DayOfWeek.MONDAY).withHour(12).withMinute(0);
        orderRequest.setDeliveryDate(deliveryDate);

        // Mock restaurant hours util to return false (restaurant is closed)
        // This is the key mock that triggers the error condition we want to test
        when(restaurantHoursUtil.isRestaurantOpen(eq(restaurant), any(LocalDateTime.class))).thenReturn(false);

        // Mock working hours for error message - return empty list to simulate no custom hours
        List<RestaurantWorkingHours> workingHours = new ArrayList<>();
        when(restaurantHoursUtil.getWorkingHoursForDay(eq(restaurant), eq(DayOfWeek.MONDAY))).thenReturn(workingHours);

        // Mock default hours for error message - return empty list to simulate restaurant closed on Monday
        List<RestaurantHoursUtil.TimeRange> defaultHours = new ArrayList<>();
        when(restaurantHoursUtil.getDefaultWorkingHours(eq(DayOfWeek.MONDAY))).thenReturn(defaultHours);

        // Act & Assert
        InvalidOrderException exception = assertThrows(InvalidOrderException.class, () -> {
            orderService.placeOrder(orderRequest);
        });

        assertEquals("Restaurant is closed on MONDAYs", exception.getMessage());
    }

    @Test
    void testPlaceOrder_WithCustomWorkingHours_Success() {
        // Arrange
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(restaurantRepository.findById(1)).thenReturn(Optional.of(restaurant));
        when(cartRepository.findByCustomerAndStatus(customer, "ACTIVE")).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCart(cart)).thenReturn(cartItems);
        when(paymentService.createPayment(anyInt(), any(BigDecimal.class), anyString())).thenReturn("payment123");
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId("order123");
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Set delivery date to a Wednesday at 3 PM (custom hours)
        LocalDateTime deliveryDate = LocalDateTime.now().with(DayOfWeek.WEDNESDAY).withHour(15).withMinute(0);
        orderRequest.setDeliveryDate(deliveryDate);

        // Mock restaurant hours util to return true (restaurant is open)
        when(restaurantHoursUtil.isRestaurantOpen(eq(restaurant), any(LocalDateTime.class))).thenReturn(true);

        // Act
        OrderResponseDTO response = orderService.placeOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals("order123", response.getOrderId());

        // Verify interactions
        verify(restaurantHoursUtil).isRestaurantOpen(eq(restaurant), any(LocalDateTime.class));
    }


}
