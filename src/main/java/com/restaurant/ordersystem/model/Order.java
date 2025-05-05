package com.restaurant.ordersystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @Column(length = 36)
    private String orderId;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    
    @Column(length = 36)
    private String paymentId;
    
    @Column(nullable = false)
    private LocalDateTime orderDate;
    
    @Column(nullable = false)
    private LocalDateTime deliveryDate;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
    
    private String cookingInstructions;
    
    private String pickupInstructions;
    
    private LocalDateTime lastModifiedDateTime;
    
    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String statusHistory;
    
    private String squareOrderId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @PrePersist
    public void generateOrderId() {
        if (this.orderId == null) {
            this.orderId = UUID.randomUUID().toString();
        }
    }
    
    public enum OrderStatus {
        Received, Preparing, ReadyToPickup, OrderCompleted, Cancelled
    }
}
