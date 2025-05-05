package com.restaurant.ordersystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import com.restaurant.ordersystem.exception.InvalidOrderException;
import com.restaurant.ordersystem.exception.ResourceNotFoundException;
import com.restaurant.ordersystem.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponse;

    @BeforeEach
    void setUp() {
        // Setup order request
        orderRequest = new OrderRequestDTO();
        orderRequest.setCustomerId(1);
        orderRequest.setRestaurantId(1);
        orderRequest.setPaymentMethod("Pay Online");
        orderRequest.setOrderDate(LocalDateTime.now());
        orderRequest.setDeliveryDate(LocalDateTime.now().plusHours(1));

        // Setup order response
        orderResponse = new OrderResponseDTO();
        orderResponse.setOrderId("order123");
        orderResponse.setCustomerId(1);
        orderResponse.setCustomerName("John Doe");
        orderResponse.setRestaurantId(1);
        orderResponse.setRestaurantName("Test Restaurant");
        orderResponse.setPaymentId("payment123");
        orderResponse.setPaymentMethod("Pay Online");
        orderResponse.setPaymentStatus("Paid");
        orderResponse.setOrderDate(LocalDateTime.now());
        orderResponse.setDeliveryDate(LocalDateTime.now().plusHours(1));
        orderResponse.setOrderStatus("Received");
        orderResponse.setTotalPrice(BigDecimal.valueOf(50.0));
        orderResponse.setDiscountValue(BigDecimal.ZERO);
        orderResponse.setFinalPrice(BigDecimal.valueOf(50.0));
    }

    @Test
    void testPlaceOrder() throws Exception {
        when(orderService.placeOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", is("order123")))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.restaurantId", is(1)))
                .andExpect(jsonPath("$.paymentMethod", is("Pay Online")))
                .andExpect(jsonPath("$.orderStatus", is("Received")));

        verify(orderService, times(1)).placeOrder(any(OrderRequestDTO.class));
    }

    @Test
    void testGetAllOrders() throws Exception {
        List<OrderResponseDTO> orders = Arrays.asList(orderResponse);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId", is("order123")))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].restaurantId", is(1)));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void testGetOrderById() throws Exception {
        String orderId = "order123";
        when(orderService.getOrderById(orderId)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("order123")))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.restaurantId", is(1)));

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        String orderId = "nonexistent";
        when(orderService.getOrderById(orderId)).thenThrow(new ResourceNotFoundException("Order", "id", orderId));

        mockMvc.perform(get("/api/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    void testGetOrdersByCustomerId() throws Exception {
        Integer customerId = 1;
        List<OrderResponseDTO> orders = Arrays.asList(orderResponse);
        when(orderService.getOrdersByCustomerId(customerId)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId", is("order123")))
                .andExpect(jsonPath("$[0].customerId", is(1)));

        verify(orderService, times(1)).getOrdersByCustomerId(customerId);
    }

    @Test
    void testCancelOrder() throws Exception {
        String orderId = "order123";
        when(orderService.cancelOrder(orderId)).thenReturn(orderResponse);

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("order123")))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.restaurantId", is(1)));

        verify(orderService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testCancelOrderInvalidOrder() throws Exception {
        String orderId = "order123";
        when(orderService.cancelOrder(orderId)).thenThrow(new InvalidOrderException("Cannot cancel a completed order"));

        mockMvc.perform(put("/api/orders/{orderId}/cancel", orderId))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).cancelOrder(orderId);
    }
}
