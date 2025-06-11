package com.restaurant.ordersystem.dto;

import java.util.List;

public class AddToCartRequestDTO {

    private Integer customerId;
    private List<CartItemRequestDTO> items;

    // No-args constructor
    public AddToCartRequestDTO() {
    }

    // All-args constructor
    public AddToCartRequestDTO(Integer customerId, List<CartItemRequestDTO> items) {
        this.customerId = customerId;
        this.items = items;
    }

    // Manual getters and setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<CartItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemRequestDTO> items) {
        this.items = items;
    }
}
