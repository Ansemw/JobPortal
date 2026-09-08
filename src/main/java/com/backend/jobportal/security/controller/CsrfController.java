package com.backend.jobportal.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/csrf-token")
public class CsrfController {

    @GetMapping("/public")
    public CsrfToken getCsrfToken(HttpServletRequest request) {

        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }
}
