# 2. Store tasks as GitHub Issues + Projects v2 items

Date: 2026-08-15

## Status

Accepted

## Context

`task-manager` needs a storage adapter backed by GitHub instead of memory, so tasks are visible and manageable through GitHub itself (Issues, labels, and a Projects v2 board), as detailed in [docs/task-manager-github-adapter.md](../../../docs/task-manager-github-adapter.md). GitHub Issues have no native date or number fields, and Projects v2 (which does have those field types) is only reachable through the GraphQL v4 API — there is no REST equivalent for its custom fields.

## Decision

- `GitHubTaskRepository` (module `adapter-out-github`) talks to GitHub exclusively through the GraphQL API, for both issue/label operations and Projects v2 item operations, so the adapter has a single HTTP surface (`GitHubGraphQlClient`) instead of mixing REST and GraphQL.
- Title and markdown description map to the issue title/body. Labels map to issue labels. Status maps to a mutually-exclusive `status:<value>` label. Start date, end date and budget map to custom fields (`Start date`, `End date`, `Budget`) on the Projects v2 item linked to the issue; the operator is expected to have created that board and those fields out of band.
- Every managed issue also carries a fixed `task-manager` marker label, so `findAll`/`searchByLabels` (implemented via GitHub's `search` API with repeated `label:"..."` terms, which is natively AND-semantics) never pick up unrelated issues on the same repository.
- `save` decides create vs. update by checking whether `task.id()` resolves to an existing GitHub issue node (`node(id: ...)`). Application-layer use cases (e.g. `CreateTask`) generate a throwaway client-side id before the first save; the adapter discards it and returns a `Task` carrying the GitHub-assigned issue node id instead, which callers must use afterwards.
- `deleteById` closes the issue (`state: CLOSED`) rather than deleting it, because the GitHub API cannot delete issues without special/enterprise permissions.
- Writes are sequential and not transactional: an update issues its issue/label mutations first, then its Projects v2 field mutations. A partial failure (e.g. labels updated but a field update fails) can leave a task's GitHub-visible fields briefly inconsistent; `save`/`update` do not attempt automatic rollback. This is accepted for now given the intended use (a single operator's project board, not a high-concurrency system) and re-running the failed operation will converge to the correct state, since every mutation is idempotent given the same target values.
- `InMemoryTaskRepository` remains the default adapter (local dev, and the Cucumber suite), so `mvn verify` stays hermetic. `GitHubTaskRepository` is wired in `bootstrap`'s `RepositoryConfiguration` only under the `github` Spring profile.

## Consequences

- The domain and application layers are unaffected by this choice — `TaskRepository` stays the only port they see, per the existing hexagonal boundary ([ADR 1](0001-hexagonal-architecture.md)).
- Every read (`findAll`, `findById`, `searchByLabels`) makes a live GitHub API call; there is no local cache or read model. This is a known, accepted rate-limit and latency tradeoff for a demo/example service and would need revisiting for higher-traffic use.
- Because status/labels and dates/budget live on two different GitHub objects (the Issue and the Projects v2 item), a task can — in the event of a partial write failure — temporarily show inconsistent data across the two. There is no distributed transaction to prevent this; it is a deliberate simplification documented here so future maintainers don't assume atomicity that doesn't exist.
- `deleteById` is a visible behavior change from the in-memory adapter: "deleted" tasks still exist as closed GitHub issues rather than disappearing entirely.
