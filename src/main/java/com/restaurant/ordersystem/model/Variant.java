package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "variants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Variant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer variantId;
    
    @Column(nullable = false)
    private String variantName;
    
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private MenuItem item;
    
    @Column(nullable = false)
    private String variantType;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Boolean available;
    
    private LocalDateTime createdDateTime;
    
    private String createdBy;
    
    private LocalDateTime lastModifiedDateTime;
    
    private String lastModifiedBy;
    
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String caution;
    
    private String attribute;
    
    private Integer listingOrder;
}
