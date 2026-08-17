package com.analaizer.taskmanager.adapter.out.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

// Thin wrapper around the GitHub GraphQL v4 endpoint. GitHub Projects v2 has
// no REST equivalent, so this adapter drives issues, labels and project
// items through a single GraphQL surface for consistency.
public class GitHubGraphQlClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String token;
    private final URI endpoint;

    public GitHubGraphQlClient(final HttpClient httpClient, final ObjectMapper objectMapper, final String token) {
        this(httpClient, objectMapper, token, URI.create("https://api.github.com/graphql"));
    }

    public GitHubGraphQlClient(final HttpClient httpClient, final ObjectMapper objectMapper, final String token, final URI endpoint) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.token = token;
        this.endpoint = endpoint;
    }

    public JsonNode execute(final String query, final Map<String, Object> variables) {
        try {
            final String body = objectMapper.writeValueAsString(Map.of("query", query, "variables", variables));
            final HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/vnd.github+json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            final HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new GitHubGraphQlException("GitHub GraphQL request failed with HTTP " + response.statusCode() + ": " + response.body());
            }
            final JsonNode root = objectMapper.readTree(response.body());
            if (root.has("errors") && !root.get("errors").isEmpty()) {
                throw new GitHubGraphQlException("GitHub GraphQL request returned errors: " + root.get("errors"));
            }
            return root.get("data");
        } catch (final IOException e) {
            throw new GitHubGraphQlException("Failed to call GitHub GraphQL API: " + e.getMessage());
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new GitHubGraphQlException("Interrupted while calling GitHub GraphQL API");
        }
    }
}
