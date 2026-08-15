# web

The main web UI of the platform: a single-page React application (the "shell") that hosts a sidebar for navigating between the different applications, each implemented as an independent hexagonal feature package.

## Why

- **Hexagonal architecture per feature**: each feature (e.g. `task-manager`) isolates its domain and use cases (`domain/`, `application/`) from React and HTTP concerns (`ui/`, `infrastructure/`). This keeps business rules testable without a browser or a running backend, and makes it possible to swap the backend integration (e.g. HTTP adapter vs. an in-memory adapter for tests) without touching the domain or the UI logic.
- **pnpm workspace**: `web/` is a workspace so new applications can be added as new packages under `features/` without duplicating tooling, while `shell/` remains the single deployable entry point.

## Structure

- `shell/` — the deployable web application: routing, layout, sidebar, and composition of the feature packages. Produces the Docker image for this module.
- `features/task-manager/` — the Task Manager feature, following hexagonal architecture, consumed by `shell/`.

## Commands

```bash
pnpm install
pnpm --filter shell dev        # start the dev server
pnpm --filter shell build      # production build
pnpm --filter shell test:e2e   # Playwright integration tests
```

See [AGENTS.md](AGENTS.md) for conventions when adding a new feature package.
