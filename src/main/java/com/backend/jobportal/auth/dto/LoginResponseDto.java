package com.backend.jobportal.auth.dto;

import com.backend.jobportal.user.dto.UserDto;

public record LoginResponseDto(String message, UserDto userDto, String jwtToken) {
}
