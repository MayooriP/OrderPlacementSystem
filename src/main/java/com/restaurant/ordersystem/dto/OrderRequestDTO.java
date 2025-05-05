package com.restaurant.ordersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;
    
    @NotNull(message = "Payment method is required")
    private String paymentMethod;
    
    private String couponCode;
    
    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;
    
    @NotNull(message = "Delivery date is required")
    private LocalDateTime deliveryDate;
    
    private String pickupInstructions;
}
