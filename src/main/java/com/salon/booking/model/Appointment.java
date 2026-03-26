package com.salon.booking.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;


@Entity // information to Spring "make this class a table in the database"
@Table(name = "appointments")
@Data // Lombok annotation to generate getters, setters, and other utility methods
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
    private Long id;

    @NotBlank(message = "Client name is required")
    @Size(max = 120, message = "Client name must be at most 120 characters")
    private String clientName;

    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentTime;

    @NotBlank(message = "Status is required")
    @Size(max = 60, message = "Status must be at most 60 characters")
    private String status;

    @ManyToOne // Many appointments can be associated with one service
    @JoinColumn(name = "service_id") // Foreign key column in the appointments table
    @NotNull(message = "Service selection is required")
    private Service service;
}   
