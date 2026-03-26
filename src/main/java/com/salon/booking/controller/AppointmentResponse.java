package com.salon.booking.controller;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String clientName,
        LocalDateTime appointmentTime,
        String status,
        AppointmentServiceResponse service
) {
}
