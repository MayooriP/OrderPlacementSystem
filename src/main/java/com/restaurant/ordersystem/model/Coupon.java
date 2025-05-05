package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;
    
    @Column(nullable = false, unique = true)
    private String couponCode;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    private CouponStatus status;
    
    @Column(nullable = false)
    private Integer couponDiscountPercentage;
    
    @Column(nullable = false)
    private Integer maxAmount;
    
    @Column(nullable = false)
    private String couponName;
    
    @Column(nullable = false)
    private Float minOrderValue;
    
    @Column(nullable = false)
    private String discountType;
    
    private Integer limitPerUser;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String imageUrl;
    
    public enum CouponStatus {
        Active, Inactive
    }
}
