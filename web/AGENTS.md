# AGENTS.md — web

Instructions for AI coding agents working in `web/`.

## Architecture

Each application is a hexagonal feature package under `features/<name>/`:

- `src/domain/` — types and rules with no dependency on React, HTTP, or any framework.
- `src/application/` — use cases (plain functions/classes) orchestrating the domain, depending on domain-defined ports (interfaces), not on concrete adapters.
- `src/infrastructure/` — adapters implementing the ports (e.g. `HttpTaskRepository` calling the backend REST API).
- `src/ui/` — React components and the route(s) exposed to `shell/`. UI code depends on `application/`, never directly on `infrastructure/`.

`shell/` is the only deployable package: it wires the sidebar, the router, and imports each feature's UI entry point, providing the concrete infrastructure adapters (e.g. the API base URL) via composition, not by having features reach into `shell/`.

## Adding a new application

1. Create `web/features/<name>/` following the `task-manager` package as a reference (same `domain/application/infrastructure/ui` split, same `package.json` conventions).
2. Add a `README.md` to the new feature package explaining its purpose.
3. Register the new package's route and sidebar entry in `shell/src/ui/layout`.
4. Add `<name>` to `pnpm-workspace.yaml` if it lives outside `features/*` (usually not needed, the glob already covers it).

## Testing

- Unit-test `domain/` and `application/` in isolation (no DOM, no network).
- Integration-test `shell/` with Playwright (`shell/tests/`), stubbing HTTP calls at the network boundary so tests exercise the real UI → application → infrastructure wiring without requiring the backend to be running.

## Conventions

- TypeScript strict mode, no `any` without justification.
- Comments and documentation in English.
- Run `pnpm --filter <package> lint` and `pnpm --filter <package> typecheck` before considering a change done.
