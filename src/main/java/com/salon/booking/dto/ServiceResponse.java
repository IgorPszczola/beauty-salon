package com.salon.booking.dto;

import com.salon.booking.model.ServiceCategory;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        double price,
        int duration,
        ServiceCategory category
) {
}
