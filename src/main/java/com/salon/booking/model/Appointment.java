package com.salon.booking.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;


@Entity // information to Spring "make this class a table in the database"
@Table(name = "appointments")
@Data // Lombok annotation to generate getters, setters, and other utility methods
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing primary key
    private Long id;
    private String clientName;
    private LocalDateTime appointmentTime;
    private String status;

    @ManyToOne // Many appointments can be associated with one service
    @JoinColumn(name = "service_id") // Foreign key column in the appointments table
    private Service service;
}   
