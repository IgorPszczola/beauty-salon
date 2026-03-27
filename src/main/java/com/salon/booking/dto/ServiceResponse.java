package com.salon.booking.dto;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        double price,
        int duration
) {
}
