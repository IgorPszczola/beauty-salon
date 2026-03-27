package com.salon.booking.controller;

import com.salon.booking.dto.CreateServiceRequest;
import com.salon.booking.dto.UpdateServiceRequest;
import com.salon.booking.dto.ServiceResponse;
import com.salon.booking.model.Service;
import com.salon.booking.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // informs Spring that this class will handle HTTP requests and return JSON responses
@RequestMapping("/api/services")
@CrossOrigin(origins = "*") // Allow requests from any origin (for development purposes)
public class ServiceController {
    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository){
        this.serviceRepository = serviceRepository;
    }

    @GetMapping // maps HTTP GET requests to this method
    public List<ServiceResponse> getAllService(){
        return serviceRepository.findAll().stream()
                .map(this::toServiceResponse)
                .toList();
    }

    @PostMapping
    public ServiceResponse createService(@Valid @RequestBody CreateServiceRequest request){
        Service service = new Service();
        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDuration(request.getDuration());

        Service savedService = serviceRepository.save(service);
        return toServiceResponse(savedService);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id, @Valid @RequestBody UpdateServiceRequest request){
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new FieldValidationException("id", "Service not found"));

        service.setName(request.getName());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setDuration(request.getDuration());

        Service updatedService = serviceRepository.save(service);
        return ResponseEntity.ok(toServiceResponse(updatedService));
    }

    private ServiceResponse toServiceResponse(Service service) {
        return new ServiceResponse(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDuration()
        );
    }
}
