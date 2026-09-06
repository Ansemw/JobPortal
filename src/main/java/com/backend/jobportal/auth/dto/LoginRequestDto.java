package com.backend.jobportal.auth.dto;

public record LoginRequestDto(
        String userName,
        String password
) {
}
