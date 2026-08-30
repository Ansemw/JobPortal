---
name: entity-crud-generator
description: Generates DTO, Controller, Service, and Repository classes for a new Spring Boot JPA entity, following this project's layered architecture conventions. Use when the user adds a new entity class or asks to scaffold CRUD layers for an entity.
---
## Before generating anything
Read the following files in full before writing any code. These are the authoritative style
reference — do not use any other file in the project as a style reference, even if it looks similar:
- `templates/example/Example.java`
- `templates/example/dto/ExampleDto.java`
- `templates/example/repository/ExampleRepository.java`
- `templates/example/service/IExampleService.java`
- `templates/example/service/impl/ExampleServiceImpl.java`
- `templates/example/controller/ExampleController.java`
- 
# Entity CRUD Generator

When the user gives you a new `@Entity` class (or points you to one), generate the following files following the patterns and folder structure in `templates/`. Assuming the entity class is called Example and referring to the file `templates/example/Example.java`:

1. **DTO** (`ExampleDto.java`) — create a record class mirroring the entity fields.
2. **Repository** (`ExampleRepository.java`) — extends `JpaRepository<Example, Long>`.
3. **Service** (`IExampleService.java` + `ExampleServiceImpl.java`) — interface + impl.
4. **Controller** (`ExampleController.java`) — REST endpoints under `/examples`.

## Conventions
- Package structure: `com.backend.jobportal.example.{dto,repository,service,service.impl,controller}`
- Use constructor injection, not field injection.
- Follow naming exactly as in templates folder.

## When the entity has fields not covered by the template (e.g. relationships, enums, embedded types)
Generate the DTO/Service/Controller for all standard fields as normal. For fields not covered by
the template pattern, skip them in the generated code but list them explicitly at the end of your
response as "Fields requiring manual review" so the user can decide how to handle them.

## Reference files
See  `templates/example/Example.java`,`templates/example/dto/ExampleDto.java`, `templates/example/repository/ExampleRepository.java`, `templates/example/service/IExampleService.java`, `templates/example/service/impl/ExampleServiceImpl.java`, `templates/example/controller/ExampleController.java` for exact style.