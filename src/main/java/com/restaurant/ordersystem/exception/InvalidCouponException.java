package com.restaurant.ordersystem.exception;

public class InvalidCouponException extends RuntimeException {
    
    public InvalidCouponException(String message) {
        super(message);
    }
}
