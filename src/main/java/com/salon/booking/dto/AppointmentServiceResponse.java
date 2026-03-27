package com.salon.booking.dto;

public record AppointmentServiceResponse(
        Long id,
        String name,
        double price,
        int duration
) {
}
