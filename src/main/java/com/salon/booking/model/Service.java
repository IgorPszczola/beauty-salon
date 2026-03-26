package com.salon.booking.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "services")
@Data
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id; // Long can be null, which is useful for new services that haven't been saved to the database yet

    @NotBlank(message = "Service name is required")
    @Size(max = 120, message = "Service name must be at most 120 characters")
    private String name;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    private double price;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int duration; // Duration in minutes
}
