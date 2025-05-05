package com.restaurant.ordersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    
    private Integer itemId;
    private String itemName;
    private Integer variantId;
    private String variantName;
    private String categoryName;
    private String subCategoryName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private String specialInstructions;
    private Boolean isFreeItem;
}
