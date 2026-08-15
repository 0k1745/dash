# 1. Use a hexagonal architecture per feature, hosted by a single shell

Date: 2026-08-15

## Status

Accepted

## Context

The web UI needs to grow to host several applications over time, reachable from a single sidebar, while keeping each application's business logic testable and independent from React and from how the backend is reached (REST today, potentially something else later). We also want a single deployable frontend artifact rather than one Docker image per feature.

## Decision

We organize `web/` as a pnpm workspace with:

- one deployable package, `shell/`, responsible for routing, layout, the sidebar, and wiring concrete infrastructure adapters into each feature;
- one package per application under `features/<name>/`, each internally split into `domain/`, `application/`, `infrastructure/`, and `ui/`, following hexagonal architecture: `domain` and `application` have no dependency on React or HTTP, `infrastructure` implements the ports defined by `domain`, and `ui` depends only on `application`.

Only `shell/` produces a Docker image.

## Consequences

- Business rules in `domain`/`application` can be unit-tested without a browser or a running backend.
- Swapping a feature's backend integration (e.g. mocking it for tests) only requires a new `infrastructure` adapter, not changes to `domain`, `application`, or `ui`.
- Adding a new application means adding a new package and registering it in the shell, without introducing a new deployable artifact.
- The workspace setup (pnpm) adds a small amount of tooling overhead compared to a single-package app, which is accepted in exchange for this modularity.
