package com.backend.jobportal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class PathConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of("/api/swagger-ui.html",
                "/swagger-ui/**",
                "/api/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui.html",
                "/webjars/**"
                );
    }

    @Bean(name = "securedPaths")
    public List<String> securedPaths() {
        return List.of("/api/**");
    }

    @Bean(name = "regexPaths")
    public List<String> regexPaths() {
        return List.of(".*public$");
    }

    @Bean(name = "adminPaths")
    public List<String> adminPaths() {
        return List.of("/api/contacts/admin"
                ,"/api/contacts/sort/admin"
                ,"/api/contacts/page/admin"
                ,"/api/contacts/{id}/status/admin");
    }
}
