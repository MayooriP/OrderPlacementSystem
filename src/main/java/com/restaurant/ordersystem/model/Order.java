package com.restaurant.ordersystem.model;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@TypeDef(name = "json", typeClass = JsonStringType.class)
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
    @Column(columnDefinition = "enum('Received','Preparing','Ready To Pickup','Order Completed','Cancelled')")
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(length = 80)
    private String cookingInstructions;

    @Column(length = 80)
    private String pickupInstructions;

    private LocalDateTime lastModifiedDateTime;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private String statusHistory;

    @Column(length = 255)
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
        Placed,Received, Preparing, ReadyToPickup, OrderCompleted, Cancelled
    }

    // Manually added getters and setters
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public String getCookingInstructions() {
        return cookingInstructions;
    }

    public void setCookingInstructions(String cookingInstructions) {
        this.cookingInstructions = cookingInstructions;
    }

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }

    public LocalDateTime getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public void setLastModifiedDateTime(LocalDateTime lastModifiedDateTime) {
        this.lastModifiedDateTime = lastModifiedDateTime;
    }

    public String getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(String statusHistory) {
        this.statusHistory = statusHistory;
    }

    public String getSquareOrderId() {
        return squareOrderId;
    }

    public void setSquareOrderId(String squareOrderId) {
        this.squareOrderId = squareOrderId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
