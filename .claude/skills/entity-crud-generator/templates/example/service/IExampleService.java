package com.backend.jobportal.example.service;

import com.backend.jobportal.example.dto.ExampleDto;

import java.util.List;

public interface IExampleService {

    // Returns every example in the system as a list of DTOs.
    public List<ExampleDto> getAllExamples();

    // Returns a single example by id.
    public ExampleDto getExampleById(Long id);

    // Creates a new example for the logged-in employer's own company, returning the saved example.
    public ExampleDto createExample(ExampleDto exampleDto, String email);

    // Updates an example's status, scoped to the logged-in employer's own company, returning the updated example.
    public ExampleDto updateExampleStatus(Long id, String status, String email);
}
