# task-manager-adapter-out-memory

Outbound adapter implementing the `TaskRepository` port with an in-memory `ConcurrentHashMap` — no persistence, data is lost on restart. Intended as the reference outbound adapter; a persistent adapter (e.g. backed by a database) can be added later as a sibling module without changing `domain`, `application`, or `adapter-in-rest`.
