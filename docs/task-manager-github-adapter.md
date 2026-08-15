# Task Manager: GitHub-backed storage adapter

Status: Draft

## Purpose

`task-manager` currently stores tasks in memory (`InMemoryTaskRepository`), and its domain model is minimal (`id`, `title`, `completed`). This feature replaces that storage with a real external system — GitHub Issues and Projects (v2) — and enriches the domain model to a realistic task: title, markdown description, start/end date, dynamic labels, a status, and an optional budget estimate.

This is also the reference implementation for outbound adapters backed by a real third-party API in this codebase (as opposed to the in-memory one), and the first feature to go through the doc-first, small-PR contribution workflow described in the root [AGENTS.md](../AGENTS.md#contribution-workflow).

## Modules involved

- `apps/task-manager/domain` — richer `Task` entity, `TaskStatus` enum, `TaskRepository` port (adds label-based search).
- `apps/task-manager/application` — use cases for creating/updating tasks, changing status, adding/removing labels, searching by labels.
- `apps/task-manager/adapter-out-github` (new module) — `GitHubTaskRepository`, implemented against the GitHub GraphQL API.
- `apps/task-manager/adapter-out-memory` — kept as-is (updated to match the new domain shape) and remains the default adapter for local dev and automated tests.
- `apps/task-manager/adapter-in-rest` — DTOs/controller updated for the new fields.
- `apps/task-manager/bootstrap` — reads the GitHub configuration, selects the adapter (Spring profile), wires it.
- `web/features/task-manager` — frontend domain/UI updated to match (separate PR, after the backend is merged).

## Domain model

```
Task
 - id: String                      (GitHub issue node id)
 - title: String                   (required)
 - description: String             (markdown, required — maps to the GitHub issue body)
 - startDate: LocalDate            (required)
 - endDate: LocalDate              (required)
 - labels: Set<String>             (0..N, can be added/removed dynamically after creation)
 - status: TaskStatus              (required — TODO | ANALYSIS | IN_PROGRESS | DONE)
 - budget: Optional<BigDecimal>    (optional estimate, no currency unit for now — see open questions)
```

- `TaskStatus` values map to the French statuses requested: `TODO` = "À faire", `ANALYSIS` = "En cours d'analyse", `IN_PROGRESS` = "En cours", `DONE` = "Terminé".
- Status is realized as mutually-exclusive GitHub labels: `status:todo`, `status:analysis`, `status:in-progress`, `status:done`. Changing status removes the previous status label and adds the new one.
- Start date, end date and budget are stored as custom fields on the GitHub Projects (v2) item linked to the issue (GitHub issues have no native date/number fields).
- Search is multi-criteria by label, with **AND** semantics: a task must carry every label passed to the search to be included in the results.

## Where each field lives on GitHub

| Domain field  | GitHub location                                  |
|---------------|---------------------------------------------------|
| title         | Issue title                                        |
| description   | Issue body (markdown)                              |
| labels        | Issue labels (including the `status:*` label)      |
| status        | Issue label, prefix `status:`                      |
| startDate     | Projects v2 item field (Date)                      |
| endDate       | Projects v2 item field (Date)                      |
| budget        | Projects v2 item field (Number), optional          |

## Configuration

Read by the `bootstrap` module, none hardcoded to a specific owner/repo:

- `GITHUB_TASK_MANAGER_TOKEN` — Personal Access Token with `repo` + `project` scopes.
- `GITHUB_TASK_MANAGER_OWNER` — user or organization that owns the repository and the project board.
- `GITHUB_TASK_MANAGER_REPO` — repository holding the issues/labels.
- `GITHUB_TASK_MANAGER_PROJECT_NUMBER` — the Projects v2 board number within that owner.

The PAT and the Projects v2 board (with its Date/Date/Number custom fields) are created once, out of band, by whoever operates a given environment; this repository does not provision them automatically.

## Adapter selection and test strategy

- `InMemoryTaskRepository` stays the **default** adapter (local dev, and the existing Cucumber suite), so `mvn verify` remains hermetic, fast, and independent of network access or credentials.
- `GitHubTaskRepository` is only wired when a Spring profile (e.g. `github`) is active, which in turn requires the four env vars above to be set.
- A small set of adapter-level tests for `GitHubTaskRepository` run against a fake GraphQL server (e.g. WireMock) rather than the real GitHub API, so they can run in `mvn verify` too. Any test against the real GitHub API is opt-in/manual, not part of the default build.

## Known risks / open questions

- **Two sources of truth**: status/labels live on the Issue, dates/budget live on the Projects v2 item. The adapter must keep both in sync on every write and define what happens if one of the two calls fails partway through (documented in more detail, with the chosen ordering, in the backend PR).
- **No delete on GitHub**: the API cannot truly delete an issue without special/enterprise permissions. `TaskRepository.deleteById` is expected to map to **closing** the issue rather than deleting it — a user-visible behavior change from today's in-memory adapter. To be confirmed before the backend PR starts.
- **No currency for `budget`**: modeled as a plain number for now. Not a blocker for this doc, but should be revisited if multi-currency ever matters.
- **Rate limits**: every list/search hits the GitHub API live; acceptable for a demo/example service, but a known limitation worth keeping in mind if this pattern is reused for a higher-traffic service.

## Planned delivery sequence

1. **This PR** — this document + the contribution workflow instructions in `AGENTS.md`. No production code changes.
2. **Backend PR(s)** — richer domain model and use cases first (with `adapter-out-memory` updated to match, existing tests staying green), then the new `adapter-out-github` module and its wiring, possibly as two separate PRs given the size.
3. **Frontend PR** — `web/features/task-manager` updated to the new fields once the backend is merged.

## Related ADRs

None yet — an ADR documenting the choice of GitHub Issues + Projects v2 as the backing store (over alternatives) will be added alongside the backend PR that implements it, in `apps/task-manager/ADR/`.
