package com.restaurant.ordersystem.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.restaurant.ordersystem.config.CaseInsensitiveEnumConverter;

@JsonDeserialize(converter = OrderStatus.OrderStatusConverter.class)
public enum OrderStatus {
    Placed,
    Received,
    Preparing,
    ReadyToPickup,
    OrderCompleted,
    Cancelled;

    public static class OrderStatusConverter extends CaseInsensitiveEnumConverter<OrderStatus> {
        public OrderStatusConverter() {
            super(OrderStatus.class);
        }
    }
}
