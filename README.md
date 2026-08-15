# Analaizer

Monorepo hosting the web frontend and the backend services of the platform.

## Structure

- `web/` — the main web UI (React, hexagonal architecture, pnpm workspace). See [web/README.md](web/README.md).
- `apps/` — backend services (Java 25, Spring Boot 4, Maven, hexagonal architecture). See [apps/README.md](apps/README.md).
- `docs/` — documentation for features that span one or more modules. See [docs/README.md](docs/README.md).

## Running everything locally

`docker-compose.yml` at the repository root builds and runs every deployable module together on a shared Docker network:

```
docker compose up --build
```

- `task-manager` (backend) → http://localhost:8080
- `web-shell` (frontend) → http://localhost:5173

## Conventions

- All code comments and documentation are written in English.
- Every deployable module has its own `README.md` explaining why it exists, an `ADR/` folder with its architectural decisions (Michael Nygard format), an `AGENTS.md` with instructions for AI coding agents, and produces a Docker image.
- Both the frontend and the backend follow a hexagonal architecture: domain and application logic are isolated from frameworks and I/O, which live in adapters.

See the root [AGENTS.md](AGENTS.md) for instructions when working on this repository with an AI coding agent.
