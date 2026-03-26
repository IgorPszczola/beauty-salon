package com.salon.booking.controller;

public record AppointmentServiceResponse(
        Long id,
        String name,
        double price,
        int duration
) {
}
