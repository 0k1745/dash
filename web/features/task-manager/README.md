# task-manager (feature)

Frontend feature package for managing tasks: list, create, change status, add/remove labels, search by label(s) and delete them. Consumed by `web/shell` and backed by the `apps/task-manager` service.

## Why

Kept as a hexagonal package so the task list view and its business rules (e.g. validating a new task before it's sent to the backend) can be developed and tested without React or a running backend, and so the HTTP integration with `apps/task-manager` can change without touching the UI.

## Structure

- `src/domain/` — `Task`/`TaskStatus` types and the `TaskRepository` port.
- `src/application/` — use cases: `listTasks`, `createTask`, `changeTaskStatus`, `addLabel`, `removeLabel`, `searchTasksByLabels`.
- `src/infrastructure/` — `HttpTaskRepository`, the adapter calling the `apps/task-manager` REST API.
- `src/ui/` — `TaskListPage`/`TaskForm`/`TaskItem` and the exported route consumed by `shell/`.
