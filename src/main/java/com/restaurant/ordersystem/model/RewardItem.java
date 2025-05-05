package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rewardItemId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private MenuItem item;
    
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
