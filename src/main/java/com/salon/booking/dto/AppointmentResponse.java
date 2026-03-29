package com.salon.booking.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String clientName,
        LocalDateTime appointmentTime,
        String status,
        String artist,
        AppointmentServiceResponse service
) {
}
