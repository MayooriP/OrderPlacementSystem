package com.restaurant.ordersystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.model.*;
import com.restaurant.ordersystem.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private RestaurantService restaurantService;

    @Mock
    private CartService cartService;

    @Mock
    private DiscountService discountService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private DynamoDBService dynamoDBService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Restaurant restaurant;
    private Cart cart;
    private OrderRequestDTO orderRequest;
    private Payment payment;
    private Order order;

    @BeforeEach
    void setUp() {
        // Setup customer
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setFullName("John Doe");

        // Setup restaurant
        restaurant = new Restaurant();
        restaurant.setRestaurantId(1);
        restaurant.setName("Test Restaurant");

        // Setup cart
        cart = new Cart();
        cart.setCartId(1);
        cart.setCustomer(customer);
        cart.setTotalAmount(BigDecimal.valueOf(50.0));
        cart.setCartItems(new ArrayList<>());

        // Setup cart items
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(1);
        cartItem.setCart(cart);
        
        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(1);
        menuItem.setName("Test Item");
        menuItem.setPrice(BigDecimal.valueOf(25.0));
        
        Category category = new Category();
        category.setCategoryId(1);
        category.setName("Test Category");
        menuItem.setCategory(category);
        
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(2);
        cartItem.setPrice(BigDecimal.valueOf(25.0));
        cartItem.setSubtotal(BigDecimal.valueOf(50.0));
        
        cart.getCartItems().add(cartItem);

        // Setup payment
        payment = new Payment();
        payment.setPaymentId("payment123");
        payment.setAmount(BigDecimal.valueOf(50.0));
        payment.setStatus(Payment.PaymentStatus.Paid);
        payment.setPaymentMethod("Pay Online");

        // Setup order
        order = new Order();
        order.setOrderId("order123");
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setPaymentId(payment.getPaymentId());
        order.setOrderDate(LocalDateTime.now());
        order.setDeliveryDate(LocalDateTime.now().plusHours(1));
        order.setStatus(Order.OrderStatus.Received);
        order.setOrderItems(new ArrayList<>());

        // Setup order request
        orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1);
        orderRequest.setRestaurantId(1);
        orderRequest.setPaymentMethod("Pay Online");
        orderRequest.setOrderDate(LocalDateTime.now());
        orderRequest.setDeliveryDate(LocalDateTime.now().plusHours(1));
    }

    @Test
    void testGetOrderById() {
        // Given
        String orderId = "order123";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        OrderResponseDTO result = orderService.getOrderById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(customer.getCustomerId(), result.getCustomerId());
        assertEquals(restaurant.getRestaurantId(), result.getRestaurantId());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testGetOrderByIdNotFound() {
        // Given
        String orderId = "nonexistent";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(orderId));
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void testGetAllOrders() {
        // Given
        List<Order> orders = List.of(order);
        when(orderRepository.findAll()).thenReturn(orders);

        // When
        List<OrderResponseDTO> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testGetOrdersByCustomerId() {
        // Given
        Integer customerId = 1;
        List<Order> orders = List.of(order);
        when(customerService.getCustomerById(customerId)).thenReturn(customer);
        when(orderRepository.findByCustomer(customer)).thenReturn(orders);

        // When
        List<OrderResponseDTO> result = orderService.getOrdersByCustomerId(customerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(customerService, times(1)).getCustomerById(customerId);
        verify(orderRepository, times(1)).findByCustomer(customer);
    }

    @Test
    void testCancelOrder() {
        // Given
        String orderId = "order123";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // When
        OrderResponseDTO result = orderService.cancelOrder(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testCancelOrderCompletedOrder() {
        // Given
        String orderId = "order123";
        order.setStatus(Order.OrderStatus.OrderCompleted);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When & Then
        assertThrows(InvalidOrderException.class, () -> orderService.cancelOrder(orderId));
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
