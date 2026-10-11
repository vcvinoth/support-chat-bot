---
name: springboot-api
description: Conventions for the Java 21 / Spring Boot 4 backend in backend/. Use when adding or changing REST endpoints, services, configuration, error handling, or backend tests.
---

# Spring Boot API

## Before coding
- Check `backend/pom.xml` for Spring Boot and Spring AI versions. Verify any Spring/Spring AI class or method exists in that version (docs or jar sources) — never guess.
- Ask before adding a dependency.

## Structure
`com.example.supportchat` (adjust to the scaffolded package):
- `controller/` — thin REST controllers, request/response DTOs as Java `record`s.
- `service/` — business logic; constructor injection only.
- `config/` — `@Configuration` beans and `@ConfigurationProperties`.
- `exception/` — custom exceptions + one `@RestControllerAdvice` returning `ProblemDetail`.

## Conventions
- All endpoints under `/api`. Follow the contract in `docs/ARCHITECTURE.md`; update it if the contract changes.
- Validate input (`jakarta.validation`), reject non-PDF uploads and enforce max file size.
- Config in `application.yml`; secrets only from env vars (`ANTHROPIC_API_KEY`).
- No database, no persistence layer.
- Log at INFO for key events, never log document content or the API key.

## Tests
- Unit tests for services; `@WebMvcTest` (or the Boot 4 equivalent) for controllers.
- Mock the Claude call; tests must not require network or an API key.

## Done when
- `./mvnw test` passes.
- Run `change-explainer` and update `docs/PROGRESS.md`.
