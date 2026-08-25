# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Build/run tools are the Maven wrapper (no need for a local Maven install):

```
./mvnw compile              # compile
./mvnw test                 # run all tests
./mvnw test -Dtest=ClassName#methodName   # run a single test
./mvnw spring-boot:run       # run the app locally
./mvnw package                # build the jar
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Architecture

- Spring Boot 4.1.1, Java 21, Maven single-module project.
- Base package: `com.backend.jobportal`. Entry point: `JobportalApplication`.
- Feature packages are organized by domain, each with its own `controller` subpackage (e.g. `com.backend.jobportal.company.controller`). Follow this pattern for new domains (`entity`, `repository`, `service`, `controller` subpackages per feature) rather than layering by type at the top level.
- `com.backend.jobportal.config.web.WebConfig` implements `WebMvcConfigurer` and centralizes MVC configuration:
  - API versioning is configured via `configureApiVersioning`, using a media-type parameter (`application/vnd.backend+json;v=`) with supported versions `1.0`, `2.0`, `3.0`. Controller methods opt into a version via `@GetMapping(version = "1.0")` (etc.) rather than separate URL paths.
  - All controller paths are prefixed with `/api` via `configurePathMatch`/`addPathPrefix`, so `@RequestMapping` values on controllers (e.g. `/company`) should be written without the `/api` prefix — it is added globally.
- Data layer: Spring Data JPA with an H2 in-memory database (`spring-boot-h2console`, `h2` runtime dependency) — no external DB setup needed for local development.
- `spring-boot-devtools` is enabled for auto-restart during development.
