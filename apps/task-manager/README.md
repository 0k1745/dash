# task-manager

Backend service managing tasks: list, create, complete and delete them, backed by an in-memory store (no persistence — data is lost on restart). Consumed by the `web/features/task-manager` frontend package.

## Why

Serves as the reference implementation of the hexagonal Maven module split described in [apps/README.md](../README.md): `domain` and `application` stay free of Spring, `adapter-in-rest` and `adapter-out-memory` are swappable adapters, and `bootstrap` is the only module that assembles and runs the service.

## Modules

- `domain/` — `Task` entity, `TaskRepository` port.
- `application/` — use cases: `ListTasks`, `CreateTask`, `CompleteTask`, `DeleteTask`.
- `adapter-in-rest/` — REST controller and DTOs.
- `adapter-out-memory/` — `InMemoryTaskRepository`.
- `bootstrap/` — Spring Boot application, wiring, `Dockerfile`, Cucumber integration tests.

## API

| Method | Path              | Description          |
|--------|-------------------|-----------------------|
| GET    | `/api/tasks`      | List all tasks        |
| POST   | `/api/tasks`      | Create a task          |
| PATCH  | `/api/tasks/{id}` | Complete a task (`{"completed": true}`) |
| DELETE | `/api/tasks/{id}` | Delete a task          |

## Commands

```bash
mvn -pl task-manager/bootstrap -am spring-boot:run   # run locally (from apps/)
mvn -pl task-manager/bootstrap -am verify             # unit + Cucumber integration tests
```

## Docker

Build from `apps/`:

```bash
cd apps
docker build -f task-manager/bootstrap/Dockerfile -t analaizer-task-manager .
```
