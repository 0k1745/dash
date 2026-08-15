package com.analaizer.taskmanager.adapter.out.github;

import com.analaizer.taskmanager.domain.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Runs against a stubbed GraphQL server (WireMock) instead of the real
// GitHub API, per docs/task-manager-github-adapter.md's test strategy.
class GitHubTaskRepositoryTest {

    private WireMockServer server;
    private GitHubTaskRepository repository;

    private static final GitHubTaskManagerProperties PROPERTIES =
            new GitHubTaskManagerProperties("test-token", "octocat", "hello-world", 1);

    @BeforeEach
    void startServer() {
        server = new WireMockServer(0);
        server.start();

        stubContextResolution();

        GitHubGraphQlClient client = new GitHubGraphQlClient(
                HttpClient.newHttpClient(), new ObjectMapper(), "test-token",
                URI.create(server.baseUrl() + "/graphql"));
        GitHubProjectContext context = new GitHubProjectContext(client, PROPERTIES);
        repository = new GitHubTaskRepository(client, context, PROPERTIES);
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void findsAllTasksFromSearchResults() {
        stubGraphQl("search(query", """
                {
                  "data": {
                    "search": {
                      "nodes": [
                        {
                          "id": "issue-1",
                          "title": "Write the ADR",
                          "body": "Describe the decision",
                          "state": "OPEN",
                          "labels": { "nodes": [
                            { "id": "l1", "name": "task-manager" },
                            { "id": "l2", "name": "status:todo" }
                          ] },
                          "projectItems": { "nodes": [
                            {
                              "id": "item-1",
                              "project": { "id": "project-1" },
                              "fieldValues": { "nodes": [
                                { "date": "2026-01-01", "field": { "name": "Start date" } },
                                { "date": "2026-01-31", "field": { "name": "End date" } }
                              ] }
                            }
                          ] }
                        }
                      ]
                    }
                  }
                }
                """);

        List<Task> tasks = repository.findAll();

        assertEquals(1, tasks.size());
        Task task = tasks.get(0);
        assertEquals("issue-1", task.id());
        assertEquals("Write the ADR", task.title());
        assertTrue(task.labels().isEmpty());
    }

    @Test
    void findByIdReturnsEmptyWhenIssueIsMissing() {
        stubGraphQl("node(id", """
                { "data": { "node": null } }
                """);

        Optional<Task> found = repository.findById("missing");

        assertTrue(found.isEmpty());
    }

    @Test
    void deleteByIdClosesTheIssue() {
        stubGraphQl("updateIssue(input", """
                { "data": { "updateIssue": { "issue": { "id": "issue-1" } } } }
                """);

        repository.deleteById("issue-1");

        server.verify(postRequestedFor(urlEqualTo("/graphql")).withRequestBody(containing("CLOSED")));
    }

    private void stubContextResolution() {
        stubGraphQl("repository(owner", """
                {
                  "data": {
                    "repository": { "id": "repo-1" },
                    "organization": {
                      "projectV2": {
                        "id": "project-1",
                        "fields": { "nodes": [
                          { "id": "field-start", "name": "Start date" },
                          { "id": "field-end", "name": "End date" },
                          { "id": "field-budget", "name": "Budget" }
                        ] }
                      }
                    },
                    "user": { "projectV2": null }
                  }
                }
                """);
    }

    private void stubGraphQl(String bodyContains, String responseBody) {
        server.stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(containing(bodyContains))
                .willReturn(okJson(responseBody)));
    }
}
