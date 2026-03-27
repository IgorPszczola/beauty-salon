package com.salon.booking.controller;

import com.salon.booking.dto.CreateAppointmentRequest;
import com.salon.booking.dto.UpdateAppointmentRequest;
import com.salon.booking.dto.AppointmentResponse;
import com.salon.booking.dto.AppointmentServiceResponse;
import com.salon.booking.model.Appointment;
import com.salon.booking.model.Service;
import com.salon.booking.repository.AppointmentRepository;
import com.salon.booking.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentController(AppointmentRepository appointmentRepository, ServiceRepository serviceRepository){
        this.appointmentRepository = appointmentRepository;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<AppointmentResponse> getAllAppointments(){
        return appointmentRepository.findAll().stream()
                .map(this::toAppointmentResponse)
                .toList();
    }    

    @PostMapping
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request){
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new FieldValidationException("serviceId", "Selected service does not exist"));

        Appointment appointment = new Appointment();
        appointment.setClientName(request.getClientName());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(request.getStatus());
        appointment.setService(service);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return toAppointmentResponse(savedAppointment);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable Long id, @Valid @RequestBody UpdateAppointmentRequest request){
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new FieldValidationException("id", "Appointment not found"));

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new FieldValidationException("serviceId", "Selected service does not exist"));

        appointment.setClientName(request.getClientName());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(request.getStatus());
        appointment.setService(service);

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.ok(toAppointmentResponse(updatedAppointment));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id){
        if (!appointmentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appointmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private AppointmentResponse toAppointmentResponse(Appointment appointment) {
        AppointmentServiceResponse serviceResponse = null;
        if (appointment.getService() != null) {
            Service service = appointment.getService();
            serviceResponse = new AppointmentServiceResponse(
                    service.getId(),
                    service.getName(),
                    service.getPrice(),
                    service.getDuration()
            );
        }

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getClientName(),
                appointment.getAppointmentTime(),
                appointment.getStatus(),
                serviceResponse
        );
    }
}
