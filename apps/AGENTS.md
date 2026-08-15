# AGENTS.md — apps

Instructions for AI coding agents working in `apps/`.

## Module split (hexagonal, enforced by Maven)

Each service `apps/<service>/` is a Maven aggregator with:

- `domain/` — entities and ports (interfaces). Must not depend on Spring, Jakarta, or any I/O library. If a change here requires adding such a dependency, it belongs in another module instead.
- `application/` — use cases, depending only on `domain`. One class per use case (e.g. `CreateTask`), constructor-injected with the ports it needs.
- `adapter-in-<kind>/` — inbound adapters (e.g. `adapter-in-rest`), translating an external protocol into calls to `application`.
- `adapter-out-<kind>/` — outbound adapters (e.g. `adapter-out-memory`), implementing a `domain` port.
- `bootstrap/` — the Spring Boot application: `@SpringBootApplication` entry point, `@Configuration` classes wiring use cases to concrete adapters, `application.yml`, the `Dockerfile`, and the Cucumber integration tests.

Only `bootstrap` depends on Spring Boot starters and produces the runnable jar / Docker image.

## Adding a new service

1. Copy the module layout of `task-manager` (same 5-module split, same `pom.xml` parent/dependency wiring).
2. Register the new module in `apps/pom.xml`'s `<modules>`.
3. Add `README.md`, `AGENTS.md`, and `ADR/` at the service root, following `task-manager`'s.

## Testing

- Unit-test `domain` (plain JUnit) and `application` (JUnit + Mockito) in isolation.
- Test `adapter-in-rest` with `@WebMvcTest` and `@MockitoBean` for the use cases.
- Integration-test the whole service through `bootstrap` with Cucumber (`cucumber-java`, `cucumber-spring`, `cucumber-junit-platform-engine`): feature files in `bootstrap/src/test/resources/features`, step definitions under `bootstrap/src/test/java/.../cucumber`, driven through `TestRestTemplate` against a `@SpringBootTest(webEnvironment = RANDOM_PORT)` context.

## Conventions

- Comments and documentation in English.
- `mvn -pl apps/<service>/bootstrap -am verify` must pass before considering a change done.
