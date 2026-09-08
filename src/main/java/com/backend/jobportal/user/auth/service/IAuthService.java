package com.backend.jobportal.user.auth.service;

import com.backend.jobportal.user.auth.dto.RegisterRequestDto;

import java.util.Map;

public interface IAuthService {

    public Map<String, String> apiRegister(RegisterRequestDto registerRequestDto);
}
