package com.restaurant.ordersystem.dto;

import java.util.List;

public class AddToCartResponseDTO {

    private Integer cartId;
    private List<Integer> cartItemIds;
    private String message;
    private String status;

    // No-args constructor
    public AddToCartResponseDTO() {
    }

    // All-args constructor
    public AddToCartResponseDTO(Integer cartId, List<Integer> cartItemIds, String message, String status) {
        this.cartId = cartId;
        this.cartItemIds = cartItemIds;
        this.message = message;
        this.status = status;
    }

    // Manual getters and setters

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public List<Integer> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(List<Integer> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
