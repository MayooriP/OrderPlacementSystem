package com.restaurant.ordersystem.config;

import com.fasterxml.jackson.databind.util.StdConverter;

public class CaseInsensitiveEnumConverter<T extends Enum<T>> extends StdConverter<String, T> {
    private final Class<T> enumType;

    public CaseInsensitiveEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T convert(String value) {
        if (value == null) return null;

        for (T constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(value)) {
                return constant;
            }
        }
        throw new IllegalArgumentException("Invalid value for enum " + enumType.getSimpleName() + ": " + value);
    }
}
