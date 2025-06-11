package com.restaurant.ordersystem.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.restaurant.ordersystem.config.CaseInsensitiveEnumConverter;

@JsonDeserialize(converter = PaymentStatus.PaymentStatusConverter.class)
public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED,
    CANCELLED;

    public static class PaymentStatusConverter extends CaseInsensitiveEnumConverter<PaymentStatus> {
        public PaymentStatusConverter() {
            super(PaymentStatus.class);
        }
    }
}
