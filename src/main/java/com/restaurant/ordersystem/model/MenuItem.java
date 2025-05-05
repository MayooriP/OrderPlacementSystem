package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private SubCategory subCategory;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private String imageUrl;
    
    private Boolean available;
    
    private LocalDateTime createdDateTime;
    
    private String createdBy;
    
    private LocalDateTime lastModifiedDateTime;
    
    private String lastModifiedBy;
    
    private String status;
}
