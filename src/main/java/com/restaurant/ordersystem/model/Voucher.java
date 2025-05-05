package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer voucherId;
    
    @Column(nullable = false, unique = true)
    private String voucherCode;
    
    private String description;
    
    @Enumerated(EnumType.STRING)
    private VoucherStatus status;
    
    private Integer discountPercentage;
    
    private BigDecimal discountAmount;
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    private MenuItem freeItem;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    private LocalDateTime expiryDate;
    
    private Boolean isUsed;
    
    private LocalDateTime usedDate;
    
    private LocalDateTime createdDateTime;
    
    public enum VoucherStatus {
        Active, Inactive, Expired, Used
    }
}
