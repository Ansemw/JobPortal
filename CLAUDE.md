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
- Data layer: Spring Data JPA with H2 (`spring-boot-h2console`, `h2` runtime dependency). The datasource is currently a **file-based** H2 DB (`src/main/resources/jobportalDBFile.mv.db`, `AUTO_SERVER=true`), not in-memory — data persists across restarts. The H2 console is enabled at `/h2-console`.
- Schema and seed data live in `src/main/resources/schema.sql` and `data.sql` (e.g. the `companies` table). `spring.sql.init.mode` is currently commented out in `application.properties`, so these scripts are not auto-run by default — enable that property if schema/data initialization on startup is needed.
- Table columns follow an audit convention: `created_at`, `created_by`, `updated_at`, `updated_by` (see `companies` in `schema.sql`) — follow this pattern for new tables/entities.
- `spring-boot-devtools` is enabled for auto-restart during development.
