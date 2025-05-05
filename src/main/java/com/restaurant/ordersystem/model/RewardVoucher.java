package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardVoucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rewardVoucherId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    private Integer discountPercentage;
    
    private BigDecimal discountAmount;
    
    private Boolean isUsed;
    
    private LocalDateTime expiryDate;
    
    private LocalDateTime usedDate;
    
    private LocalDateTime createdDateTime;
    
    @Enumerated(EnumType.STRING)
    private RewardStatus status;
    
    public enum RewardStatus {
        Active, Used, Expired
    }
}
