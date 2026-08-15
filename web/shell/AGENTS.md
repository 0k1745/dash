# AGENTS.md — shell

- `src/applications.tsx` is the composition root: it constructs each feature's infrastructure adapters and registers `{ path, label, element }` entries. Register new applications there, not inside `App.tsx` or `Sidebar.tsx` directly.
- `src/ui/layout/` contains only layout concerns (`Shell`, `Sidebar`); it must stay free of feature-specific logic.
- Feature packages are consumed by their exported route module (see `task-manager`'s `src/ui/route.tsx`) — never import a feature's internal `domain`/`application`/`infrastructure` files directly from the shell.
- Run `pnpm lint`, `pnpm typecheck` and `pnpm test:e2e` before considering a change to this package done.
