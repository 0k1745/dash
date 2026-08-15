# apps

Backend services of the platform: Java 25, Spring Boot 4, built with Maven. Each service is split into hexagonal Maven modules so architectural boundaries are enforced at compile time, not just by convention.

## Why

A pure Maven-module split (`domain` / `application` / `adapter-in-*` / `adapter-out-*` / `bootstrap`) means `domain` physically cannot depend on Spring or any I/O library — the build fails if someone tries. This is stricter than a single-module package-based hexagonal layout and keeps the architecture honest as the codebase grows.

## Services

- `task-manager/` — manages tasks in memory, exposes a REST API, reference implementation of the hexagonal Maven layout.

## Commands

```bash
mvn -q -pl task-manager/bootstrap -am verify   # build + unit + Cucumber integration tests for task-manager
```

See [AGENTS.md](AGENTS.md) for conventions when adding a new service.
