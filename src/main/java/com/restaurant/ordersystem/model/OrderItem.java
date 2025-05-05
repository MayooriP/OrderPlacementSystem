package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    private MenuItem menuItem;
    
    @ManyToOne
    @JoinColumn(name = "variant_id")
    private Variant variant;
    
    private String itemName;
    
    private String categoryName;
    
    private String subCategoryName;
    
    private String variantName;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private BigDecimal subtotal;
    
    private String specialInstructions;
    
    private Boolean isFreeItem;
}
