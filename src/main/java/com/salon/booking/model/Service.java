package com.salon.booking.model;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "services")
@Data
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id; // Long can be null, which is useful for new services that haven't been saved to the database yet
    private String name;
    private String description;
    private double price;
    private int duration; // Duration in minutes
}
