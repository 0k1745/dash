# 1. Split task-manager into hexagonal Maven modules

Date: 2026-08-15

## Status

Accepted

## Context

We want the hexagonal architecture rule ("domain and application logic must not depend on frameworks or I/O") to be enforced rather than just documented, so it survives contributors who are unfamiliar with the convention.

## Decision

`task-manager` is split into five Maven modules instead of one module with hexagonal packages: `domain`, `application`, `adapter-in-rest`, `adapter-out-memory`, `bootstrap`. Each has its own `pom.xml` with explicit dependencies: `domain` depends on nothing but the JDK and JUnit; `application` depends only on `domain`; the adapters depend on `application`/`domain` plus the framework they need; `bootstrap` depends on everything and is the only module with `spring-boot-maven-plugin`, producing the runnable jar and the Docker image.

## Consequences

- A developer cannot accidentally add a Spring import to `domain` or `application` — the module simply has no such dependency declared, and the build fails if one is added without also changing the `pom.xml`, which is a visible, reviewable change.
- Swapping `adapter-out-memory` for a persistent store later means adding a new adapter module and changing one `@Bean` in `bootstrap`'s `RepositoryConfiguration`, without touching `domain`, `application`, or `adapter-in-rest`.
- The service has five `pom.xml` files instead of one, and inter-module dependencies must be declared explicitly; this is accepted as the cost of compile-time-enforced boundaries.
