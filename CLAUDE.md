# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Backend build/run tools are the Maven wrapper (no need for a local Maven install):

```
./mvnw compile                              # compile
./mvnw test                                 # run all tests
./mvnw test -Dtest=ClassName#methodName     # run a single test
./mvnw spring-boot:run                      # run the app locally
./mvnw package                              # build the jar
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

The backend requires a MySQL instance (see Database below). Start it with:

```
docker compose up -d      # starts MySQL via compose.yml
```

Frontend (`job-portal-ui/`, separate npm project):

```
npm install
npm run dev        # Vite dev server
npm run build
npm run lint
```

## Architecture

- Spring Boot 4.1.1, Java 21, Maven single-module project. Lombok is available (`@Getter`/`@Setter`/`@RequiredArgsConstructor`, etc.) — prefer it over hand-written boilerplate.
- Base package: `com.backend.jobportal`. Entry point: `JobportalApplication`.
- Feature packages are organized by domain, each with its own subpackages: `controller`, `dto`, `service` (interface, prefixed `I...Service`) and `service.impl` (implementation, suffixed `...ServiceImpl`), `repository` (e.g. `com.backend.jobportal.company.controller`, `company.dto`, `company.service`, `company.service.impl`, `company.repository`). Follow this pattern for new domains rather than layering by type at the top level.
- Entities live in a shared top-level `com.backend.jobportal.entity` package (not per-feature) — e.g. `com.backend.jobportal.entity.Company`, `com.backend.jobportal.entity.Job`. New entities should extend `com.backend.jobportal.entity.BaseEntity`, a `@MappedSuperclass` that supplies the `createdAt`/`createdBy`/`updatedAt`/`updatedBy` audit columns via Spring Data JPA auditing (`@CreatedDate`/`@CreatedBy`/`@LastModifiedDate`/`@LastModifiedBy`). Auditing is enabled app-wide via `@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")` on `JobportalApplication`, backed by `com.backend.jobportal.audit.AuditAwareImpl` (currently a stub `AuditorAware<String>` that always returns `"Anonymous User"` — replace once real authentication exists).
- `Company` and `Job` have a bidirectional one-to-many/many-to-one relationship: `Company.jobs` is `@OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)`, and `Job.company` is the owning side (`@ManyToOne(fetch = FetchType.LAZY, optional = false)` with `@JoinColumn(name = "company_id")`). The `mappedBy` value must match the field name on the owning side exactly, or the relationship silently fails to load — when adding new relationships, add the owning-side (`@ManyToOne`/`@JoinColumn`) field before or together with the `mappedBy` side.
- Per-domain DTOs (e.g. `CompanyDto`, `JobDto`) are Java `record`s living in `<domain>.dto`, mirroring the entity's fields (a DTO on the "many" side of a relationship, like `CompanyDto.jobs`, holds a `List<JobDto>` rather than the entity list). Service impls map entity → DTO via a private `transformXToDto` helper that calls the record constructor field-by-field; controllers only ever expose DTOs, never entities, in their `ResponseEntity` bodies.
- A `com.backend.jobportal.exception.GlobalExceptionHandler` (`@RestControllerAdvice`) centralizes error handling: generic `Exception`/`NullPointerException` map to a `ErrorResponseDto` (`exception.dto`) with HTTP 500; `MethodArgumentNotValidException` (body `@Valid` failures) and `HandlerMethodValidationException` (method-level `@Validated` failures, e.g. on `@RequestParam`/`@PathVariable`) both map to a field-name → message `Map<String, String>` with HTTP 400.
- There is a `entity-crud-generator` Claude Code skill (`.claude/skills/entity-crud-generator/`) that scaffolds the DTO/Repository/Service/Controller layers for a new entity from templates in `templates/example/` — use it (or match its templates' style) when adding a new domain.
- `com.backend.jobportal.config.web.WebConfig` implements `WebMvcConfigurer` and centralizes MVC configuration:
  - API versioning is configured via `configureApiVersioning`, using a media-type parameter (`application/vnd.backend+json;v=`) with supported versions `1.0`, `2.0`, `3.0`. Controller methods opt into a version via `@GetMapping(version = "1.0")` (etc.) rather than separate URL paths. A request that omits the version parameter/header will not match a versioned mapping — this fails before the handler is reached, which can also make CORS look broken (no `Access-Control-Allow-Origin` header on the failed response) even when CORS itself is configured correctly.
  - All controller paths are prefixed with `/api` via `configurePathMatch`/`addPathPrefix`, so `@RequestMapping` values on controllers (e.g. `/company`) should be written without the `/api` prefix — it is added globally.
  - CORS is configured via `addCorsMappings`, currently allowing `http://localhost:5173` (the local React/Vite dev origin) on `/api/**` with all methods/headers and credentials enabled. Note that Spring Security (see below) applies its own separate CORS config which takes effect first on secured paths.
- `com.backend.jobportal.security` holds the Spring Security setup (`spring-boot-starter-security`):
  - `PathConfig` defines three named `List<String>` beans consumed by qualifier — `publicPaths` (exact/ant paths, e.g. Swagger/OpenAPI UI), `securedPaths` (currently `/api/**`), and `regexPaths` (regex-matched paths, e.g. `.*public$` so any endpoint ending in `public` is open). Add new open endpoints to one of these lists rather than editing the filter chain directly.
  - `JobPortalSecurityConfig` wires those beans into the `SecurityFilterChain`: CSRF disabled, its own CORS source (currently duplicating `http://localhost:5173`), regex/public paths permitted, secured paths requiring authentication, HTTP Basic auth enabled (no form login). There is no custom `UserDetailsService`/user store yet, so authenticated requests rely on Spring Boot's default generated-password Basic auth user until real authentication is added (see `AuditAwareImpl` note above, which is still a stub).
- Data layer: Spring Data JPA against MySQL, run via `compose.yml` (`mysql:latest`, db `jobportal`, root/root, port 3306). `spring.datasource.*` in `application.properties` reads `DATABASE_HOST`/`DATABASE_PORT`/`DATABASE_NAME`/`DATABASE_USERNAME`/`DATABASE_PASSWORD` env vars, defaulting to `localhost:3306/jobportal` with `root`/`root`. H2 (file-based, `AUTO_SERVER=true`) config lines are present but commented out — an earlier setup kept for reference, not currently active.
- Schema and seed data live in `src/main/resources/schema.sql` and `data.sql` (e.g. the `companies` table). `spring.sql.init.mode` is currently commented out in `application.properties`, so these scripts are not auto-run by default — enable that property if schema/data initialization on startup is needed.
- Table columns follow an audit convention: `created_at`, `created_by`, `updated_at`, `updated_by` (see `companies` in `schema.sql`) — follow this pattern for new tables/entities.
- `spring-boot-devtools` is enabled for auto-restart during development.
- Frontend (`job-portal-ui/`) is a separate Vite + React 19 project (not a Maven module): Redux Toolkit for state, React Router 7, Tailwind 4, Axios for API calls, Stripe (`@stripe/react-stripe-js`) for payments. Dev server runs on `localhost:5173`, matching the CORS origin configured in `WebConfig`.
