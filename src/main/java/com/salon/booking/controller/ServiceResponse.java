package com.salon.booking.controller;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        double price,
        int duration
) {
}
