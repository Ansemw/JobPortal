package com.backend.jobportal.user.auth.dto;

public record LoginRequestDto(
        String userName,
        String password
) {
}
