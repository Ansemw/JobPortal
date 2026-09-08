package com.backend.jobportal.user.auth.dto;

import com.backend.jobportal.user.dto.UserDto;

public record LoginResponseDto(String message, UserDto userDto, String jwtToken) {
}
