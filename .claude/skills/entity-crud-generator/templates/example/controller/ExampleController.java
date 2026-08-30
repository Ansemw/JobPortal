package com.backend.jobportal.Example.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
public class ExampleController {

    private final IExampleService ExampleService;

    /*@Autowired
    public ExampleController(IExampleService ExampleService) {
        this.ExampleService = ExampleService;
    }*/
    // Handles GET requests for the list of all companies and returns them as DTOs.

    // Handles GET requests for the list of all contacts and returns them as DTOs.
    @GetMapping(version = "1.0")
    public ResponseEntity<List<ExampleDto>> getAllExamples() {
        List<ExampleDto> examples = ExampleService.getAllExamples();
        return ResponseEntity.ok().body(examples);
    }
}
