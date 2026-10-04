package com.backend.jobportal.security.util;

import com.backend.jobportal.constants.ApplicationConstant;
import com.backend.jobportal.entity.JobPortalUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@PropertySource(value = "classpath:jwt.properties")
public class JwtUtil {

    private final Environment env;

    @Value("${jwt.issuer: PORTAL}")
    private String jwtIssuer;

    @Value("${jwt.subject: JWT}")
    private String jwtSubject;

    @Value("${jwt.expiration.hours:1}")
    private int jwtExpirationHours;

    @Value("${jwt.prod.expiration.hours:1}")
    private int jwtProdExpirationHours;


    public String generateJwtToken(Authentication authentication) {
        String jwtToken = null;
        int expirationHrs=jwtExpirationHours;
        List<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains("prod")) {
            expirationHrs = jwtProdExpirationHours;
        }
        String secret = env.getProperty(ApplicationConstant.JWT_SECRET_KEY, ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);

        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        var fetchedUser = (JobPortalUser)authentication.getPrincipal();

        jwtToken = Jwts.builder().issuer(jwtIssuer).subject(jwtSubject)
                .claim("name", fetchedUser.getName())
                .claim("email", fetchedUser.getEmail())
                .claim("mobileNumber", fetchedUser.getMobileNumber())
                .claim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new java.util.Date())
                .expiration(new java.util.Date((new java.util.Date()).getTime() + expirationHrs * 60 * 60 * 1000))
                .signWith(secretKey).compact();
        return jwtToken;
    }
}
