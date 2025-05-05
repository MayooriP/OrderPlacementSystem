package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @Column(length = 36)
    private String paymentId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    @Column(nullable = false)
    private String paymentMethod;
    
    private LocalDateTime paymentDate;
    
    private String transactionId;
    
    @PrePersist
    public void generatePaymentId() {
        if (this.paymentId == null) {
            this.paymentId = UUID.randomUUID().toString();
        }
    }
    
    public enum PaymentStatus {
        Pending, Paid, Failed, Refunded
    }
}
