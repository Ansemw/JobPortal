package com.backend.jobportal.exception.dto;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import java.time.LocalDate;

public record ErrorResponseDto(String apiPath, HttpStatus errorCode, String errorMessage, LocalDate errorTime)  {
}
