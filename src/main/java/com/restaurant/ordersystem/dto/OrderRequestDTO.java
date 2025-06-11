package com.restaurant.ordersystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import com.restaurant.ordersystem.model.OrderStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    private String couponCode;

    @NotNull(message = "Order date is required")
    private LocalDateTime orderDate;

    @NotNull(message = "Delivery date is required")
    private LocalDateTime deliveryDate;

    private String pickupInstructions;

    @JsonDeserialize(converter = OrderStatus.OrderStatusConverter.class)
    private OrderStatus status;


    // Manual getters and setters to handle Lombok issues

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
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

    public String getPickupInstructions() {
        return pickupInstructions;
    }

    public void setPickupInstructions(String pickupInstructions) {
        this.pickupInstructions = pickupInstructions;
    }
}
