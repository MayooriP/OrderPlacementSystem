package com.restaurant.ordersystem.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.restaurant.ordersystem.config.CaseInsensitiveEnumConverter;

@JsonDeserialize(converter = PaymentMethod.PaymentMethodConverter.class)
public enum PaymentMethod {
    CASH,
    UPI;

    public static class PaymentMethodConverter extends CaseInsensitiveEnumConverter<PaymentMethod> {
        public PaymentMethodConverter() {
            super(PaymentMethod.class);
        }
    }
}
