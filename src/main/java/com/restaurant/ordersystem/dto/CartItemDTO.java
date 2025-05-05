package com.restaurant.ordersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    
    private Integer cartItemId;
    private Integer menuItemId;
    private String menuItemName;
    private Integer variantId;
    private String variantName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String specialInstructions;
}
