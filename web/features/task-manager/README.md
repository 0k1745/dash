# task-manager (feature)

Frontend feature package for managing tasks: list, create and complete them. Consumed by `web/shell` and backed by the `apps/task-manager` service.

## Why

Kept as a hexagonal package so the task list view and its business rules (e.g. "a task can only be completed once") can be developed and tested without React or a running backend, and so the HTTP integration with `apps/task-manager` can change without touching the UI.

## Structure

- `src/domain/` — `Task` type and the `TaskRepository` port.
- `src/application/` — use cases: `listTasks`, `createTask`, `completeTask`.
- `src/infrastructure/` — `HttpTaskRepository`, the adapter calling the `apps/task-manager` REST API.
- `src/ui/` — `TaskListPage` and the exported route consumed by `shell/`.
