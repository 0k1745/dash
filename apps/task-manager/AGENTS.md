# AGENTS.md — task-manager

- Keep `domain` and `application` free of Spring/Jakarta imports; the Maven module boundaries will fail the build if this is violated by an accidental dependency.
- New use cases go in `application/`, one class per use case, constructor-injected with `TaskRepository`.
- Wiring (which adapter implements `TaskRepository`, which use cases exist as beans) lives only in `bootstrap`'s `@Configuration` classes (`RepositoryConfiguration`, `UseCaseConfiguration`), never inside the adapters themselves.
- When changing the REST API, update both the Cucumber feature file (`bootstrap/src/test/resources/features/task-manager.feature`) and the `web/features/task-manager` frontend package so the network contract stays in sync.
- Run `mvn -pl task-manager/bootstrap -am verify` (from `apps/`) before considering a change done.
