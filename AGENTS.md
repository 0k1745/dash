# AGENTS.md — Repository root

Instructions for AI coding agents working anywhere in this repository.

## Layout

- `web/` — React frontend, pnpm workspace. Has its own [web/AGENTS.md](web/AGENTS.md).
- `apps/` — Java/Spring Boot backend services, Maven multi-module. Has its own [apps/AGENTS.md](apps/AGENTS.md).
- `docs/` — cross-module feature documentation.

Always read the `AGENTS.md` of the module you are working in before making changes; it takes precedence over these root-level instructions for anything specific to that module.

## General conventions

- Write all code comments, commit messages, README files, ADRs and other documentation in English, regardless of the language used in the conversation with the user.
- Commit messages must follow the [Conventional Commits](https://www.conventionalcommits.org/) format: `<type>[optional scope]: <description>` (e.g. `feat(task-manager): add delete task endpoint`, `fix(shell): correct sidebar active link`). Common types: `feat`, `fix`, `docs`, `refactor`, `test`, `chore`, `build`.
- Every deployable module (a web app, a backend service) must have: a `README.md` explaining why it exists, an `ADR/` directory recording its architectural decisions (Michael Nygard format: Title, Status, Context, Decision, Consequences), an `AGENTS.md`, and a `Dockerfile`.
- Follow hexagonal architecture in both the frontend and the backend: keep domain and application logic free of framework and I/O concerns; put those in adapters.
- When adding a new module (a new backend service under `apps/`, or a new feature package under `web/features/`), scaffold it with the same README/ADR/AGENTS.md/Dockerfile conventions as the existing `task-manager` module, which serves as the reference implementation.
- When a decision worth recording is made (a new dependency, a structural choice, a trade-off), add an ADR in the relevant module's `ADR/` directory instead of only explaining it in the conversation.

## Contribution workflow

- Never commit directly to `main`. Every change goes through a feature branch and a pull request.
- For any new feature, follow this sequence, one pull request per step, each merged before the next starts:
  1. Write or update the feature's documentation (in `docs/` for cross-module features, or the relevant module's `README.md` for module-local concerns) in an initial/draft state, and open a PR for the documentation alone.
  2. Once the documentation PR is merged, implement the backend in its own PR (or a small sequence of PRs if the backend change is large — e.g. domain model first, then the adapter).
  3. Once the backend PR(s) are merged, implement the frontend in a separate PR.
  - Do not mix documentation, backend, and frontend changes in the same PR.
- Every feature doc must carry an explicit `Status:` line, one of: `Draft` → `Doc approved` → `Backend in progress` → `Backend done` → `Frontend in progress` → `Done`. Update it as the feature moves through the process.
- Keep PRs small enough to review in one pass; split a step further by layer or concern when it would otherwise still be large.
