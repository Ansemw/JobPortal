package com.backend.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@EnableCaching
@EnableConfigurationProperties
public class JobportalApplication {

	// Starts the Spring Boot application.
	public static void main(String[] args) {
		SpringApplication.run(JobportalApplication.class, args);
	}

}
