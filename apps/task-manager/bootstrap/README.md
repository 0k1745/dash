# task-manager-bootstrap

The Spring Boot application: `TaskManagerApplication` entry point, `@Configuration` classes wiring the use cases to the in-memory adapter, `application.yml`, the `Dockerfile`, and the Cucumber integration tests (`src/test/resources/features`, `src/test/java/.../cucumber`) that exercise the service end-to-end over HTTP. The only module producing the runnable jar and the Docker image.
