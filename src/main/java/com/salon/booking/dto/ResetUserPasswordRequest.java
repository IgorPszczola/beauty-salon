package com.salon.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetUserPasswordRequest {

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must have 6-100 characters")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
