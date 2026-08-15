# 1. Keep feature composition (adapters + sidebar registration) in the shell

Date: 2026-08-15

## Status

Accepted

## Context

Feature packages (e.g. `task-manager`) must not know how they are configured (backend URLs) or how they are exposed (routing, sidebar), otherwise they would depend on the shell and lose their independence.

## Decision

Each feature package exports a route descriptor and a factory function (e.g. `taskManagerRoute`, `createTaskManagerElement`) from a single `src/ui/route.tsx` entry point, along with the concrete adapters it offers (e.g. `HttpTaskRepository`). `web/shell/src/applications.tsx` is the only place that imports these, constructs adapters with environment-specific configuration, and registers the resulting `{ path, label, element }` entries consumed by the router and the sidebar.

## Consequences

- Feature packages have no dependency on the shell and can be unit-tested with an in-memory `TaskRepository` instead of `HttpTaskRepository`.
- Adding an application requires one addition to `applications.tsx`, not changes scattered across routing and layout code.
- The shell's `applications.tsx` grows with the number of applications; if this becomes unwieldy, splitting registration per application (e.g. one file per app) is a straightforward follow-up.
