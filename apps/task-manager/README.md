# task-manager

Backend service managing tasks: list, create, change status, add/remove labels, search by labels, and delete them.

Two storage adapters are available:

- `InMemoryTaskRepository` (default) — no persistence, data lost on restart. Used for local dev and the Cucumber suite.
- `GitHubTaskRepository` — stores tasks as GitHub Issues + Projects v2 items. Activated with the `github` Spring profile; see [docs/task-manager-github-adapter.md](../../docs/task-manager-github-adapter.md) and [ADR 2](ADR/0002-github-issues-projects-v2-storage.md) for the field mapping, configuration and known limitations.

Consumed by the `web/features/task-manager` frontend package.

## Why

Serves as the reference implementation of the hexagonal Maven module split described in [apps/README.md](../README.md): `domain` and `application` stay free of Spring, `adapter-in-rest` and `adapter-out-*` are swappable adapters, and `bootstrap` is the only module that assembles and runs the service.

## Modules

- `domain/` — `Task` entity, `TaskStatus` enum, `TaskRepository` port.
- `application/` — use cases: `ListTasks`, `CreateTask`, `ChangeTaskStatus`, `AddLabel`, `RemoveLabel`, `SearchTasksByLabels`, `DeleteTask`.
- `adapter-in-rest/` — REST controller and DTOs.
- `adapter-out-memory/` — `InMemoryTaskRepository`.
- `adapter-out-github/` — `GitHubTaskRepository`, backed by the GitHub GraphQL API.
- `bootstrap/` — Spring Boot application, wiring, `Dockerfile`, Cucumber integration tests.

## API

| Method | Path                          | Description                                    |
|--------|-------------------------------|-------------------------------------------------|
| GET    | `/api/tasks`                  | List all tasks (optional `?labels=a,b` filter, AND semantics) |
| POST   | `/api/tasks`                  | Create a task (`title`, `description`, `startDate`, `endDate`, optional `budget`) |
| PATCH  | `/api/tasks/{id}/status`      | Change status (`{"status": "TODO\|ANALYSIS\|IN_PROGRESS\|DONE"}`) |
| POST   | `/api/tasks/{id}/labels`      | Add a label (`{"label": "..."}`)                |
| DELETE | `/api/tasks/{id}/labels/{label}` | Remove a label                               |
| DELETE | `/api/tasks/{id}`             | Delete a task                                   |

## Commands

```bash
mvn -pl task-manager/bootstrap -am spring-boot:run  # run locally (from apps/), in-memory adapter
mvn -pl task-manager/bootstrap -am verify            # unit + Cucumber integration tests
```

To run against GitHub instead of memory, set the four env vars documented in [docs/task-manager-github-adapter.md](../../docs/task-manager-github-adapter.md) and activate the `github` Spring profile (e.g. `SPRING_PROFILES_ACTIVE=github`).

## Docker

Build from `apps/`:

```bash
cd apps
docker build -f task-manager/bootstrap/Dockerfile -t analaizer-task-manager .
```
