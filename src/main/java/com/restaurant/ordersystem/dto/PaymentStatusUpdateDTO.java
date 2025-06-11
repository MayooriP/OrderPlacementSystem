package com.restaurant.ordersystem.dto;

import com.restaurant.ordersystem.model.PaymentStatus;

public class PaymentStatusUpdateDTO {

    private PaymentStatus paymentStatus;

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
