package com.backend.jobportal.security;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JobPortalSecurityConfig {

@Qualifier( "publicPaths")
private final List<String> publicPaths;

@Qualifier("securedPaths")
private final List<String> securedPaths;

@Qualifier("regexPaths")
private final List<String> regexPaths;
    @Bean
    SecurityFilterChain customSecurityFilterChain(HttpSecurity http) {

        return http.csrf(csrfConfigurer -> csrfConfigurer.disable())
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(requests->{
                    regexPaths.forEach(path -> requests.requestMatchers(RegexRequestMatcher.regexMatcher(path)).permitAll());
                    publicPaths.forEach(path -> requests.requestMatchers(path).permitAll());
                    securedPaths.forEach(path -> requests.requestMatchers(path).authenticated());

                })
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
                .httpBasic(withDefaults())
                .build();


        /*return http.csrf(csrfConfigurer -> csrfConfigurer.disable())
                .authorizeHttpRequests((requests) -> requests
                       /* .requestMatchers("/api/companies/public").permitAll()
                        .requestMatchers("/api/contacts/public").permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMaetchr(".*public$")).permitAll()
                        .requestMatchers("/api/swagger-ui.html",
                                          "/swagger-ui/**",
                                            "/api/v3/api-docs/**",
                                            "/swagger-resources/**",
                                            "/swagger-ui.html",
                                            "/webjars/**").permitAll())
                .formLogin(formLoginConfigurer -> formLoginConfigurer.disable())
                .httpBasic(withDefaults())
                .build();*/

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
