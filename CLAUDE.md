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

- Spring Boot 4.1.1, Java 21, Maven single-module project. Lombok is available (`@Getter`/`@Setter`/`@RequiredArgsConstructor`, etc.) — prefer it over hand-written boilerplate.
- Base package: `com.backend.jobportal`. Entry point: `JobportalApplication`.
- Feature packages are organized by domain, each with its own subpackages: `controller`, `dto`, `service` (interface, prefixed `I...Service`) and `service.impl` (implementation, suffixed `...ServiceImpl`), `repository` (e.g. `com.backend.jobportal.company.controller`, `company.dto`, `company.service`, `company.service.impl`, `company.repository`). Follow this pattern for new domains rather than layering by type at the top level.
- Entities live in a shared top-level `com.backend.jobportal.entity` package (not per-feature) — e.g. `com.backend.jobportal.entity.Company`.
- Per-domain DTOs (e.g. `CompanyDto`) are Java `record`s living in `<domain>.dto`, mirroring the entity's fields. Service impls map entity → DTO via a private `transaformToDto` helper that calls the record constructor field-by-field; controllers only ever expose DTOs, never entities, in their `ResponseEntity` bodies.
- `com.backend.jobportal.config.web.WebConfig` implements `WebMvcConfigurer` and centralizes MVC configuration:
  - API versioning is configured via `configureApiVersioning`, using a media-type parameter (`application/vnd.backend+json;v=`) with supported versions `1.0`, `2.0`, `3.0`. Controller methods opt into a version via `@GetMapping(version = "1.0")` (etc.) rather than separate URL paths. A request that omits the version parameter/header will not match a versioned mapping — this fails before the handler is reached, which can also make CORS look broken (no `Access-Control-Allow-Origin` header on the failed response) even when CORS itself is configured correctly.
  - All controller paths are prefixed with `/api` via `configurePathMatch`/`addPathPrefix`, so `@RequestMapping` values on controllers (e.g. `/company`) should be written without the `/api` prefix — it is added globally.
  - CORS is configured via `addCorsMappings`, currently allowing `http://localhost:5173` (the local React/Vite dev origin) on `/api/**` with all methods/headers and credentials enabled.
- Data layer: Spring Data JPA with H2 (`spring-boot-h2console`, `h2` runtime dependency). The datasource is currently a **file-based** H2 DB (`src/main/resources/jobportalDBFile.mv.db`, `AUTO_SERVER=true`), not in-memory — data persists across restarts. The H2 console is enabled at `/h2-console`.
- Schema and seed data live in `src/main/resources/schema.sql` and `data.sql` (e.g. the `companies` table). `spring.sql.init.mode` is currently commented out in `application.properties`, so these scripts are not auto-run by default — enable that property if schema/data initialization on startup is needed.
- Table columns follow an audit convention: `created_at`, `created_by`, `updated_at`, `updated_by` (see `companies` in `schema.sql`) — follow this pattern for new tables/entities.
- `spring-boot-devtools` is enabled for auto-restart during development.
