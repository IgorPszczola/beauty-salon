package com.salon.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {
    @NotBlank(message = "Client name is required")
    @Size(max = 120, message = "Client name must be at most 120 characters")
    private String clientName;

    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentTime;

    @NotBlank(message = "Status is required")
    @Size(max = 60, message = "Status must be at most 60 characters")
    private String status;

    @NotNull(message = "Service selection is required")
    private Long serviceId;

    @Size(max = 120, message = "Artist must be at most 120 characters")
    private String artist;
}
