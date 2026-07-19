# AGENTS.md

## Project Overview

This repository is a Spring Boot 4 Maven application using Java 21. It exposes:

- a book CRUD API backed by PostgreSQL and Spring Data JPA
- Caffeine caching for `BookService.findById`
- a Google Cloud Storage read endpoint for file download/inline viewing

## Important Paths

- `src/main/java/com/example/springcaffineimpl/book` - book domain, controller, service, repository, seed data
- `src/main/java/com/example/springcaffineimpl/gcs` - GCS config, properties, service, controller, exception handling
- `src/main/resources/application.properties` - main runtime config
- `src/test/resources/application.properties` - test config using H2
- `pom.xml` - dependencies and build setup

## Runtime Notes

- Main database is PostgreSQL by default.
- Tests use H2 and should remain isolated from local PostgreSQL.
- GCS access depends on `app.gcs.bucket-name` and Google Application Default Credentials.
- `BookDataInitializer` seeds two books only when the table is empty.

## Code Conventions For This Repo

- Prefer small, local changes over broad refactors.
- Keep the current package split between `book` and `gcs` unless there is a strong reason to change it.
- Preserve existing Spring style: constructor injection, simple services, `ProblemDetail` for API errors.
- Avoid introducing extra abstraction layers unless duplication or complexity clearly justifies them.
- When changing cache behavior, check `BookService` annotations and update tests accordingly.

## Verification

- Run `./mvnw test` after meaningful code changes.
- If changing runtime configuration behavior, verify both `src/main/resources/application.properties` and test configuration expectations.
- For GCS-related changes, verify failure handling for missing files remains a `404` via `GcsControllerAdvice`.

## Documentation Expectations

- Keep `README.md` aligned with real endpoints and properties.
- If new endpoints, environment variables, or required services are added, update `README.md` in the same change.
