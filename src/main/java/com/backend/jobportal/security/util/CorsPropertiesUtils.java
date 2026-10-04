package com.backend.jobportal.security.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsPropertiesUtils {

    // Origins (e.g. the frontend URLs) permitted to make cross-origin requests
    private List<String> allowedOrigins ;

    // HTTP methods permitted on cross-origin requests ("*" allows all)
    private List<String> allowedMethods ;

    // Request headers permitted on cross-origin requests ("*" allows all)
    private List<String> allowedHeaders ;

    // Whether the browser may send credentials (cookies, Authorization header) with cross-origin requests
    private boolean allowCredentials ;

    // How long (in seconds) browsers may cache the preflight (OPTIONS) response
    private long maxAge ;

}
