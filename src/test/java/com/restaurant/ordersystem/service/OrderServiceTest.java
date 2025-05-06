package com.restaurant.ordersystem.service;

import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Test
    void testGetOrderById() {
        // Given
        String orderId = "order123";

        // When
        OrderResponseDTO result = orderService.getOrderById(orderId);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        assertEquals(1, result.getCustomerId());
        assertEquals(1, result.getRestaurantId());
    }

    @Test
    void testGetAllOrders() {
        // When
        List<OrderResponseDTO> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetOrdersByCustomerId() {
        // Given
        Integer customerId = 1;

        // When
        List<OrderResponseDTO> result = orderService.getOrdersByCustomerId(customerId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customerId, result.get(0).getCustomerId());
    }

    @Test
    void testPlaceOrder() {
        // Given
        OrderRequestDTO orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1);
        orderRequest.setRestaurantId(1);
        orderRequest.setPaymentMethod("CREDIT_CARD");
        orderRequest.setOrderDate(LocalDateTime.now());
        orderRequest.setDeliveryDate(LocalDateTime.now().plusHours(1));

        // When
        OrderResponseDTO result = orderService.placeOrder(orderRequest);

        // Then
        assertNotNull(result);
        assertEquals(orderRequest.getCustomerId(), result.getCustomerId());
        assertEquals(orderRequest.getRestaurantId(), result.getRestaurantId());
        assertEquals(orderRequest.getPaymentMethod(), result.getPaymentMethod());
        assertEquals("Pending", result.getPaymentStatus());
        assertEquals("Received", result.getOrderStatus());
        assertEquals(BigDecimal.valueOf(100.00), result.getTotalPrice());
    }
}
