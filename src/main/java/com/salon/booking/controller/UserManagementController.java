package com.salon.booking.controller;

import com.salon.booking.dto.CreateUserRequest;
import com.salon.booking.dto.ResetUserPasswordRequest;
import com.salon.booking.dto.UpdateUserRoleRequest;
import com.salon.booking.dto.UserSummaryResponse;
import com.salon.booking.model.AppUser;
import com.salon.booking.model.UserRole;
import com.salon.booking.repository.AppUserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserManagementController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<UserSummaryResponse> getUsers() {
        return appUserRepository.findAll().stream()
                .map(user -> new UserSummaryResponse(user.getId(), user.getUsername(), user.getRole().name()))
                .sorted((a, b) -> a.username().compareToIgnoreCase(b.username()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<UserSummaryResponse> createUser(@Valid @RequestBody CreateUserRequest request,
                                                          Authentication authentication) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new FieldValidationException("username", "Username already exists");
        }

        ensureCanManageRole(authentication, request.getRole());

        AppUser user = new AppUser();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        AppUser saved = appUserRepository.save(user);
        return ResponseEntity.ok(new UserSummaryResponse(saved.getId(), saved.getUsername(), saved.getRole().name()));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserSummaryResponse> updateRole(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateUserRoleRequest request,
                                                          Authentication authentication) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new FieldValidationException("id", "User not found"));

        ensureCanManageRole(authentication, request.getRole());

        if (authentication.getName().equals(user.getUsername()) && user.getRole() != request.getRole()) {
            throw new FieldValidationException("role", "You cannot change your own role");
        }

        user.setRole(request.getRole());
        AppUser updated = appUserRepository.save(user);

        return ResponseEntity.ok(new UserSummaryResponse(updated.getId(), updated.getUsername(), updated.getRole().name()));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id,
                                              @Valid @RequestBody ResetUserPasswordRequest request,
                                              Authentication authentication) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new FieldValidationException("id", "User not found"));

        if (authentication.getName().equals(user.getUsername()) && user.getRole() == UserRole.CUSTOMER) {
            throw new FieldValidationException("password", "Customer cannot reset own password here");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        appUserRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    private void ensureCanManageRole(Authentication authentication, UserRole targetRole) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (targetRole == UserRole.ADMIN || targetRole == UserRole.OWNER) {
            if (!isAdmin) {
                throw new FieldValidationException("role", "Only ADMIN can assign ADMIN or OWNER role");
            }
        }
    }
}
