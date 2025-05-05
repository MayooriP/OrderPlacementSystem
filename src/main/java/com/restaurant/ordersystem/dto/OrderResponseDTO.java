package com.restaurant.ordersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    
    private String orderId;
    private Integer customerId;
    private String customerName;
    private Integer restaurantId;
    private String restaurantName;
    private String paymentId;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private String orderStatus;
    private BigDecimal totalPrice;
    private BigDecimal discountValue;
    private BigDecimal finalPrice;
    private String couponCode;
    private String pickupInstructions;
    private List<OrderItemDTO> orderItems;
}
