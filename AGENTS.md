# AGENTS.md — Repository root

Instructions for AI coding agents working anywhere in this repository.

## Layout

- `web/` — React frontend, pnpm workspace. Has its own [web/AGENTS.md](web/AGENTS.md).
- `apps/` — Java/Spring Boot backend services, Maven multi-module. Has its own [apps/AGENTS.md](apps/AGENTS.md).
- `docs/` — cross-module feature documentation.

Always read the `AGENTS.md` of the module you are working in before making changes; it takes precedence over these root-level instructions for anything specific to that module.

## General conventions

- Write all code comments, commit messages, README files, ADRs and other documentation in English, regardless of the language used in the conversation with the user.
- Every deployable module (a web app, a backend service) must have: a `README.md` explaining why it exists, an `ADR/` directory recording its architectural decisions (Michael Nygard format: Title, Status, Context, Decision, Consequences), an `AGENTS.md`, and a `Dockerfile`.
- Follow hexagonal architecture in both the frontend and the backend: keep domain and application logic free of framework and I/O concerns; put those in adapters.
- When adding a new module (a new backend service under `apps/`, or a new feature package under `web/features/`), scaffold it with the same README/ADR/AGENTS.md/Dockerfile conventions as the existing `task-manager` module, which serves as the reference implementation.
- When a decision worth recording is made (a new dependency, a structural choice, a trade-off), add an ADR in the relevant module's `ADR/` directory instead of only explaining it in the conversation.
