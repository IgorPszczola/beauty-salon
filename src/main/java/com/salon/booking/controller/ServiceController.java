package com.salon.booking.controller;

import com.salon.booking.model.Service;
import com.salon.booking.repository.ServiceRepository;

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
    public List<Service> getAllService(){
        return serviceRepository.findAll();
    }

    @PostMapping
    public Service createService(@RequestBody Service service){ // @RequestBody tells Spring to parse the incoming JSON into a Service object
        return serviceRepository.save(service);
    }
}
