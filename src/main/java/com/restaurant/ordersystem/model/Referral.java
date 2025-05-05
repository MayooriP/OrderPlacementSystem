package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Referral {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer referralId;
    
    @Column(nullable = false, unique = true)
    private String referralCode;
    
    @ManyToOne
    @JoinColumn(name = "referrer_id", nullable = false)
    private Customer referrer;
    
    @ManyToOne
    @JoinColumn(name = "referred_id")
    private Customer referred;
    
    private Boolean isUsed;
    
    private LocalDateTime usedDate;
    
    private LocalDateTime createdDateTime;
    
    @Enumerated(EnumType.STRING)
    private ReferralStatus status;
    
    public enum ReferralStatus {
        Active, Used, Expired
    }
}
