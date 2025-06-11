package com.restaurant.ordersystem.dto;

public class CartItemRequestDTO {

    private Integer menuItemId;
    private Integer quantity;
    private String specialInstructions;
    // Optional field
    private Integer variantId;

    // No-args constructor
    public CartItemRequestDTO() {
    }

    // All-args constructor
    public CartItemRequestDTO(Integer menuItemId, Integer quantity, String specialInstructions, Integer variantId) {
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
        this.variantId = variantId;
    }

    // Manual getters and setters
    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }
}
