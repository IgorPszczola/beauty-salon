package com.salon.booking.dto;

public record UserSummaryResponse(Long id, String username, String role, boolean enabled) {
}
