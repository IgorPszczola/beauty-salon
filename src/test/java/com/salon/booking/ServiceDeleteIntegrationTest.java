package com.salon.booking;

import com.salon.booking.model.Appointment;
import com.salon.booking.model.AppUser;
import com.salon.booking.model.Service;
import com.salon.booking.repository.AppUserRepository;
import com.salon.booking.repository.AppointmentRepository;
import com.salon.booking.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResponseErrorHandler;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ServiceDeleteIntegrationTest {

    private final RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @BeforeEach
    void cleanDatabase() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(java.net.URI url,
                                    org.springframework.http.HttpMethod method,
                                    org.springframework.http.client.ClientHttpResponse response) {
                // no-op: assertions in tests verify status codes explicitly
            }
        });
        appointmentRepository.deleteAll();
        serviceRepository.deleteAll();
        enableUser("admin");
        enableUser("owner");
        enableUser("staff");
        enableUser("customer");
    }

    @Test
    void adminCanDeleteService() throws Exception {
        Service service = createService("Admin Service");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(serviceRepository.existsById(service.getId()));
    }

    @Test
    void ownerCanDeleteService() throws Exception {
        Service service = createService("Owner Service");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("owner", "owner123");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(serviceRepository.existsById(service.getId()));
    }

    @Test
    void staffGetsForbiddenWhenDeletingService() throws Exception {
        Service service = createService("Staff Forbidden Service");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("staff", "staff123");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        assertTrue(serviceRepository.existsById(service.getId()));
    }

    @Test
    void customerGetsForbiddenWhenDeletingService() throws Exception {
        Service service = createService("Customer Forbidden Service");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("customer", "customer123");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        assertTrue(serviceRepository.existsById(service.getId()));
    }

    @Test
    void deletingServiceLinkedToAppointmentReturnsBadRequest() throws Exception {
        Service service = createService("Linked Service");

        Appointment appointment = new Appointment();
        appointment.setClientName("Client A");
        appointment.setAppointmentTime(LocalDateTime.now().plusDays(1));
        appointment.setStatus("NEW");
        appointment.setService(service);
        appointmentRepository.save(appointment);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        Map<?, ?> errors = body == null ? null : (Map<?, ?>) body.get("errors");
        assertEquals("Cannot delete service linked to existing appointments", errors == null ? null : errors.get("id"));

        assertTrue(serviceRepository.existsById(service.getId()));
    }

    @Test
    void deletingNonExistingServiceReturnsNotFound() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/999999"),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deletingServiceWithoutAuthenticationReturnsUnauthorized() {
        Service service = createService("Auth Required Service");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            HttpEntity.EMPTY,
            Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(serviceRepository.existsById(service.getId()));
    }

    @Test
    void disabledAdminCannotDeleteService() {
        Service service = createService("Disabled Admin Service");
        AppUser admin = appUserRepository.findByUsername("admin").orElseThrow();
        admin.setEnabled(false);
        appUserRepository.save(admin);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("admin", "admin123");

        ResponseEntity<Void> response = restTemplate.exchange(
            baseUrl("/api/services/" + service.getId()),
            HttpMethod.DELETE,
            new HttpEntity<>(headers),
            Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(serviceRepository.existsById(service.getId()));
    }

    private Service createService(String name) {
        Service service = new Service();
        service.setName(name);
        service.setDescription("Integration test service");
        service.setPrice(120.0);
        service.setDuration(60);
        return serviceRepository.save(service);
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }

    private void enableUser(String username) {
        appUserRepository.findByUsername(username).ifPresent(user -> {
            user.setEnabled(true);
            appUserRepository.save(user);
        });
    }
}
