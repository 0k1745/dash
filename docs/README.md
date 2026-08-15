# Docs

Index of feature documentation for this repository.

Documentation that only concerns a single module belongs in that module's own `README.md` and `ADR/` directory. This folder is for documentation that describes a feature end-to-end, across modules (for example, a feature that spans a backend service in `apps/` and a feature package in `web/features/`).

## Adding a new feature doc

Create one Markdown file per feature (e.g. `docs/task-manager.md`), written in English, covering:

- a `Status:` line (`Draft` → `Doc approved` → `Backend in progress` → `Backend done` → `Frontend in progress` → `Done`), kept up to date as the feature progresses — see the "Contribution workflow" section of the root [AGENTS.md](../AGENTS.md),
- the purpose of the feature and the problem it solves,
- which modules implement it (`apps/...`, `web/features/...`),
- the main flows/use cases,
- links to the relevant ADRs.
