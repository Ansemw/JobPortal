package com.backend.jobportal.example.controller;

import com.backend.jobportal.example.dto.ExampleDto;
import com.backend.jobportal.example.service.IExampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

// Paths are written without /api (added globally by WebConfig). Role-restricted endpoints put the
// role as the LAST path segment (/admin, /employer, /jobseeker, /public) and must also be added to
// the matching list in security/PathConfig — nothing infers access from the suffix automatically.
@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
public class ExampleController {

    private final IExampleService exampleService;

    // Handles GET requests for the list of all examples and returns them as DTOs.
    @GetMapping(version = "1.0")
    public ResponseEntity<List<ExampleDto>> getAllExamples() {
        List<ExampleDto> examples = exampleService.getAllExamples();
        return ResponseEntity.ok().body(examples);
    }

    // Handles GET requests for a single example by id.
    @GetMapping(path = "/{id}", version = "1.0")
    public ResponseEntity<ExampleDto> getExampleById(@PathVariable Long id) {
        ExampleDto example = exampleService.getExampleById(id);
        return ResponseEntity.ok().body(example);
    }

    // Handles POST requests to create a new example for the logged-in employer's own company.
    @PostMapping(path = "/employer", version = "1.0")
    public ResponseEntity<ExampleDto> createExample(@RequestBody @Valid ExampleDto exampleDto, Authentication authentication) {
        ExampleDto savedExample = exampleService.createExample(exampleDto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExample);
    }

    // Handles PATCH requests to update an example's status, returning the updated DTO
    // so the frontend can splice it into local state without a refetch.
    @PatchMapping(path = "/{id}/status/employer", version = "1.0")
    public ResponseEntity<?> updateExampleStatus(@PathVariable Long id, @RequestBody Map<String, String> requestBody, Authentication authentication) {
        String status = requestBody.get("status");

        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Status is required"));
        }
        ExampleDto updatedExample = exampleService.updateExampleStatus(id, status, authentication.getName());
        return ResponseEntity.ok(updatedExample);
    }
}
