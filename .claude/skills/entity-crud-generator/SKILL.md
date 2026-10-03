---
name: entity-crud-generator
description: Generates DTO, Repository, Service (interface + impl), and Controller classes for a Spring Boot JPA entity in this project, following its domain-package layout, validation, relationship-mapping, role-suffixed-path, and security conventions. Use when the user adds a new entity class or asks to scaffold CRUD layers for an entity.
---
# Entity CRUD Generator

## Before generating anything
Read these in full before writing any code. The templates are the authoritative style reference;
the live `job` domain (`src/main/java/com/backend/jobportal/job/`) follows the same style and can be
used to confirm a detail the templates don't show.
- `templates/example/Example.java` — reference entity (with a relationship)
- `templates/example/dto/ExampleDto.java`
- `templates/example/repository/ExampleRepository.java`
- `templates/example/service/IExampleService.java`
- `templates/example/service/impl/ExampleServiceImpl.java`
- `templates/example/controller/ExampleController.java`

Also read:
- The target entity in `com.backend.jobportal.entity` (and any entity it references).
- `security/PathConfig.java` — to know which role-path list new endpoints belong in.
- `job-portal-ui/src/config/api.js` — the frontend may already have placeholder paths for this domain; note mismatches.

## What to generate
For an entity `Example`:
1. **DTO** `ExampleDto` — a `record`.
2. **Repository** `ExampleRepository` — `extends JpaRepository<Example, Long>`.
3. **Service** `IExampleService` (interface) + `ExampleServiceImpl`.
4. **Controller** `ExampleController` — endpoints under `/examples`.

Don't generate the entity itself — it already exists in the shared `com.backend.jobportal.entity`
package and must extend `BaseEntity` (which supplies `created_at`/`created_by`/`updated_at`/`updated_by`).
If the entity redeclares audit columns or doesn't extend `BaseEntity`, point that out.

## Package placement
Packages are organized by **business domain, not by role**. Decide where the new domain goes:
- **Top-level** `com.backend.jobportal.<domain>` when the entity is its own concept: its own
  lifecycle/status flow, used by several roles, or linking several domains
  (e.g. `jobapplication` links `user` and `job`; `job`, `company`, `contact`).
- **Nested** `com.backend.jobportal.<parent>.<feature>` when it's owned by and meaningless without
  a parent (e.g. `user.profile`, `user.auth`).
- **Never** create a mutual package dependency: e.g. `job` already depends on `user`, so nothing
  under `user` may depend on `job`.
- Always give the domain its own subpackages `{controller, dto, repository, service, service.impl}` —
  never drop classes flat into a parent's existing `controller/`/`service/` folders.
- Package names are lowercase with no separators (`jobapplication`, not `jobApplication`/`job_application`).

If placement is ambiguous, state the choice and the reason in your response.

## DTO rules
- Java `record`, field order mirrors the entity.
- Add `jakarta.validation` constraints derived from the entity: `@NotNull`/`nullable = false` on a
  String → `@NotBlank`, other types → `@NotNull`; `@Size`/`length` → `@Size(max = ...)`;
  numeric bounds → `@Min`/`@DecimalMin`/`@DecimalMax`. Every constraint gets a human-readable `message`.
- No validation on `id`, `createdAt`, `updatedAt`, or relationship-derived fields (they're server-set).
- Enum-like String fields (status, type, category…) get a `@Pattern(regexp = "^(A|B|C)$")`. Tell the
  user the regex must match the option list the UI actually submits.
- Include `createdAt` (read-only) when it's useful to the client; omit `createdBy`/`updatedBy`.

## Relationships
- `@ManyToOne` / `@OneToOne`: flatten to `<rel>Id` plus the few display fields the UI needs
  (see `JobDto.companyId/companyName/companyLogo`). Map null-safely in `transformToDto`
  (`x.getRel() != null ? x.getRel().getId() : null`).
- Use a nested DTO (e.g. `JobDto job`, `ProfileDto userProfile`) only when the client needs the whole
  related object; import that DTO from its own domain package.
- `@OneToMany`: a `List<ChildDto>` in read DTOs only (see `CompanyDto.jobs`); never accept it on writes.
- Relationship fields are never copied from the request body in `transformDtoToX` — the service
  resolves them server-side (e.g. the caller's company, the target job by id).

## Binary (`byte[]`) fields
Include them in the DTO (see `ProfileDto.profilePicture`/`resume`), but flag them in your response:
blobs serialized as base64 in JSON list responses are expensive, so suggest a separate
upload/download endpoint and leaving them out of list DTOs.

## Service conventions
- `@Service`, `@Transactional(readOnly = true)` at class level, `@RequiredArgsConstructor` with
  `private final` fields (constructor injection). Write methods get their own `@Transactional`.
- Every interface method has a one-line `//` comment describing it.
- Private `transformToDto(X)` and, when there's a create/update, `transformDtoToX(XDto)` helpers.
- Resolve the caller from the `email` passed by the controller via
  `JobPortalUserRepository.findByEmail(email)`; scope reads/writes to the caller's own data
  (e.g. employer's company) and reject cross-owner writes.
- Errors: throw a plain `RuntimeException("...")` (project convention; surfaces as 500 through
  `GlobalExceptionHandler`) unless a dedicated exception already exists.
- Server-controlled fields (initial `status`, timestamps like `postedDate`/`appliedAt`, counters) are
  set in the service, ignoring whatever the request body contains.
- Bulk updates use `@Modifying(clearAutomatically = true) @Query` in the repository; the
  `clearAutomatically` is required when the method re-reads the entity to return the DTO.
- Caching: if any cache in `cache/CaffeineCacheConfig` has a payload that embeds this entity
  (e.g. `companiesPublic` embeds jobs), add `@CacheEvict(value = "...", allEntries = true)` on the
  write methods. A new `@Cacheable` name must also be registered in `CaffeineCacheConfig`.

## Controller conventions
- `@RestController`, `@RequestMapping("/<plural-kebab-case>")` (e.g. `/job-applications`), **no** `/api`
  prefix (added globally by `WebConfig`).
- Every mapping declares `version = "1.0"`.
- Role-restricted endpoints put the role as the **last** path segment: `/admin`, `/employer`,
  `/jobseeker`, `/public` (e.g. `/examples/{id}/status/employer`, not `/employer/examples/...`).
- Get the caller via an `Authentication authentication` parameter → `authentication.getName()` (email).
- `@RequestBody @Valid` on DTO bodies; a single-field update may take `Map<String, String>` (see
  `JobController.updateJobStatus`).
- Only DTOs in response bodies, never entities. Create → `201 CREATED` with the saved DTO; updates
  return the updated DTO (the frontend splices it into state without refetching).

## Post-generation checklist (include in your response)
1. Add every new role-suffixed path to the matching list in `security/PathConfig` (`adminPaths`,
   `employerPaths`, or a `jobSeekerPaths` list if one exists — otherwise say it needs creating and
   wiring in `JobPortalSecurityConfig` before the `securedPaths` rule). Use `{var}` placeholders, not `${var}`.
   Paths ending in `public` are already open via the `.*public$` regex.
2. Compare the new paths and HTTP methods against `job-portal-ui/src/config/api.js`; list mismatches.
3. Register any new cache name in `CaffeineCacheConfig`.
4. Update the architecture notes in `CLAUDE.md` for the new domain.
5. Run `mvnw.cmd compile` (Windows) / `./mvnw compile` and report the result.

## Fields requiring manual review
Anything not covered above — `@ManyToMany`, `@Embedded`/`@Embeddable`, `@Enumerated` JPA enums,
`@ElementCollection`, composite keys — generate the rest normally, leave these out, and list them at
the end of your response under **"Fields requiring manual review"** with a suggested approach for each.
