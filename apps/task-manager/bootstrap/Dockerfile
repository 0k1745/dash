FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml pom.xml
COPY task-manager/pom.xml task-manager/pom.xml
COPY task-manager/domain/pom.xml task-manager/domain/pom.xml
COPY task-manager/application/pom.xml task-manager/application/pom.xml
COPY task-manager/adapter-in-rest/pom.xml task-manager/adapter-in-rest/pom.xml
COPY task-manager/adapter-out-memory/pom.xml task-manager/adapter-out-memory/pom.xml
COPY task-manager/bootstrap/pom.xml task-manager/bootstrap/pom.xml
RUN mvn -pl task-manager/bootstrap -am -DskipTests dependency:go-offline

COPY task-manager/domain task-manager/domain
COPY task-manager/application task-manager/application
COPY task-manager/adapter-in-rest task-manager/adapter-in-rest
COPY task-manager/adapter-out-memory task-manager/adapter-out-memory
COPY task-manager/bootstrap task-manager/bootstrap
RUN mvn -pl task-manager/bootstrap -am -DskipTests package

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /workspace/task-manager/bootstrap/target/task-manager.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
