package com.restaurant.ordersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    
    private Integer cartId;
    private Integer customerId;
    private List<CartItemDTO> cartItems;
    private BigDecimal totalAmount;
    private String status;
}
