package com.backend.jobportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class JobportalApplication {

	// Starts the Spring Boot application.
	public static void main(String[] args) {
		SpringApplication.run(JobportalApplication.class, args);
	}

}
