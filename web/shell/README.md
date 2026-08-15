# shell

The deployable web application: routing, layout, the sidebar, and the composition root that wires each feature package (currently `task-manager`) with its infrastructure adapters.

## Why

Kept separate from the feature packages so there is a single deployable frontend artifact and a single place (`src/applications.tsx`) responsible for configuring adapters (e.g. backend base URLs) and registering sidebar entries, while features stay unaware of how they are hosted.

## Commands

```bash
pnpm dev              # dev server
pnpm build            # production build (tsc -b && vite build)
pnpm preview           # preview the production build
pnpm test:e2e          # Playwright integration tests
```

## Configuration

- `VITE_TASK_MANAGER_API_URL` — base URL of the `apps/task-manager` backend (defaults to `http://localhost:8080`).

## Docker

Build from the `web/` directory, since the image needs the workspace files and the feature packages:

```bash
cd web
docker build -f shell/Dockerfile -t analaizer-web-shell .
```
