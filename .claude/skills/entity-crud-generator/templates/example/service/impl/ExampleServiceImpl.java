package com.backend.jobportal.example.service.impl;

import com.backend.jobportal.entity.Company;
import com.backend.jobportal.entity.Example;
import com.backend.jobportal.entity.JobPortalUser;
import com.backend.jobportal.example.dto.ExampleDto;
import com.backend.jobportal.example.repository.ExampleRepository;
import com.backend.jobportal.example.service.IExampleService;
import com.backend.jobportal.user.repository.JobPortalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExampleServiceImpl implements IExampleService {

    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "CLOSED", "DRAFT");

    private final ExampleRepository exampleRepository;
    private final JobPortalUserRepository jobPortalUserRepository;

    @Override
    public List<ExampleDto> getAllExamples() {
        List<Example> examples = exampleRepository.findAll();
        List<ExampleDto> dto = examples.stream().map(this::transformToDto).toList();
        return dto;
    }

    @Override
    public ExampleDto getExampleById(Long id) {
        Example example = exampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Example not found"));
        return transformToDto(example);
    }

    @Override
    @Transactional
    // If a cached payload in CaffeineCacheConfig embeds this entity, evict it here, e.g.
    // @CacheEvict(value = "companiesPublic", allEntries = true)
    public ExampleDto createExample(ExampleDto exampleDto, String email) {
        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        Company company = employer.getCompany();
        if (company == null) {
            throw new RuntimeException("You must be associated with a company to create an example");
        }

        Example example = transformDtoToExample(exampleDto);
        example.setCompany(company);
        example.setStatus("DRAFT");
        Example savedExample = exampleRepository.save(example);
        return transformToDto(savedExample);
    }

    @Override
    @Transactional
    public ExampleDto updateExampleStatus(Long id, String status, String email) {
        if (status == null || !VALID_STATUSES.contains(status)) {
            throw new RuntimeException("Status must be one of ACTIVE, CLOSED, DRAFT");
        }

        Example example = exampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Example not found"));

        JobPortalUser employer = jobPortalUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        if (employer.getCompany() == null || example.getCompany() == null
                || !example.getCompany().getId().equals(employer.getCompany().getId())) {
            throw new RuntimeException("You are not authorized to update this example");
        }

        int rowsAffected = exampleRepository.updateStatusById(id, status, email);
        if (rowsAffected == 0) {
            throw new RuntimeException("Example not found");
        }

        Example updatedExample = exampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Example not found"));
        return transformToDto(updatedExample);
    }

    // Copies the writable fields of an ExampleDto into a brand-new Example entity. company/status
    // are set separately by createExample; id and the audit columns are DB-generated/audit-managed.
    private Example transformDtoToExample(ExampleDto exampleDto) {
        Example example = new Example();
        example.setName(exampleDto.name());
        example.setWebsite(exampleDto.website());
        example.setRating(exampleDto.rating());
        example.setDescription(exampleDto.description());
        example.setDeadline(exampleDto.deadline());
        return example;
    }

    private ExampleDto transformToDto(Example example) {
        return new ExampleDto(
                example.getId(),
                example.getCompany() != null ? example.getCompany().getId() : null,
                example.getCompany() != null ? example.getCompany().getName() : null,
                example.getName(),
                example.getWebsite(),
                example.getRating(),
                example.getDescription(),
                example.getDeadline(),
                example.getStatus(),
                example.getCreatedAt()
        );
    }
}
