# AGENTS.md — task-manager

- Keep `domain` and `application` free of Spring/Jakarta imports; the Maven module boundaries will fail the build if this is violated by an accidental dependency.
- New use cases go in `application/`, one class per use case, constructor-injected with `TaskRepository`.
- Wiring (which adapter implements `TaskRepository`, which use cases exist as beans) lives only in `bootstrap`'s `@Configuration` classes (`RepositoryConfiguration`, `UseCaseConfiguration`), never inside adapters themselves. Adapter selection between `InMemoryTaskRepository` and `GitHubTaskRepository` is a Spring profile (`github`), set in `RepositoryConfiguration`.
- The `GitHubTaskRepository` adapter (module `adapter-out-github`) talks to GitHub exclusively through the GraphQL v4 API — see [ADR 2](ADR/0002-github-issues-projects-v2-storage.md) for why, and the field mapping table in [docs/task-manager-github-adapter.md](../../docs/task-manager-github-adapter.md). Don't mix in REST calls to `api.github.com` for issues/labels; keep everything on the single `GitHubGraphQlClient` surface.
- If changing the REST API, update both the Cucumber feature file (`bootstrap/src/test/resources/features/task-manager.feature`) and the `web/features/task-manager` frontend package so the network contract stays in sync.
- Run `mvn -pl task-manager/bootstrap -am verify` (from `apps/`) before considering a change done. This must stay hermetic (no live GitHub calls) — any new `GitHubTaskRepository` test goes through a fake GraphQL server (e.g. WireMock), not the real API.
