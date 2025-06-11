package com.restaurant.ordersystem.controller;

import com.restaurant.ordersystem.dto.OrderRequestDTO;
import com.restaurant.ordersystem.dto.OrderResponseDTO;
import com.restaurant.ordersystem.dto.PaymentStatusUpdateDTO;
import com.restaurant.ordersystem.model.Payment;
import com.restaurant.ordersystem.model.PaymentStatus;
import com.restaurant.ordersystem.service.OrderService;
import com.restaurant.ordersystem.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService; 
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        logger.info("Received order placement request for customer ID: {}", orderRequest.getCustomerId());
        OrderResponseDTO response = orderService.placeOrder(orderRequest);
        logger.info("Order placed successfully with order ID: {}", response.getOrderId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        logger.info("Retrieving all orders");
        List<OrderResponseDTO> orders = orderService.getAllOrders();
        logger.info("Retrieved {} orders", orders.size());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String orderId) {
        logger.info("Retrieving order with ID: {}", orderId);
        OrderResponseDTO order = orderService.getOrderById(orderId);
        logger.info("Retrieved order with ID: {}", orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByCustomerId(@PathVariable Integer customerId) {
        logger.info("Retrieving orders for customer ID: {}", customerId);
        List<OrderResponseDTO> orders = orderService.getOrdersByCustomerId(customerId);
        logger.info("Retrieved {} orders for customer ID: {}", orders.size(), customerId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByRestaurantId(@PathVariable Integer restaurantId) {
        logger.info("Retrieving orders for restaurant ID: {}", restaurantId);
        List<OrderResponseDTO> orders = orderService.getOrdersByRestaurantId(restaurantId);
        logger.info("Retrieved {} orders for restaurant ID: {}", orders.size(), restaurantId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("Retrieving orders between {} and {}", startDate, endDate);
        List<OrderResponseDTO> orders = orderService.getOrdersByDateRange(startDate, endDate);
        logger.info("Retrieved {} orders in date range", orders.size());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

@PutMapping("/{orderId}/payment/status")
public ResponseEntity<String> updatePaymentStatusByOrderId(
        @PathVariable String orderId,
        @RequestBody PaymentStatusUpdateDTO statusUpdateDTO) {
    
    logger.info("Updating payment status for order ID: {}", orderId);
    
    // Get the payment associated with this order
    Payment payment = paymentService.getPaymentByOrderId(orderId);
    if (payment == null) {
        return new ResponseEntity<>("Payment not found for order", HttpStatus.NOT_FOUND);
    }

    PaymentStatus newStatus = statusUpdateDTO.getPaymentStatus();
    payment.setStatus(newStatus);
    payment.setLastModifiedDateTime(LocalDateTime.now());
    paymentService.savePayment(payment);

    logger.info("Payment status updated successfully for order ID: {}", orderId);
    return new ResponseEntity<>("Payment status updated successfully", HttpStatus.OK);
}



    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable String orderId) {
        logger.info("Cancelling order with ID: {}", orderId);
        OrderResponseDTO cancelledOrder = orderService.cancelOrder(orderId);
        logger.info("Order with ID: {} has been cancelled", orderId);
        return new ResponseEntity<>(cancelledOrder, HttpStatus.OK);
    }
}
