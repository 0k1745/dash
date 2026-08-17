package com.analaizer.taskmanager.adapter.out.github;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;

// Resolves and caches the handful of GitHub node ids this adapter needs on
// every call: the repository id, the Projects v2 board id (owned by either a
// user or an organization) and the id of each custom field on that board.
// Resolved lazily on first use and cached for the lifetime of the adapter.
public class GitHubProjectContext {

    private static final String QUERY = """
            query($owner: String!, $repo: String!, $number: Int!) {
              repository(owner: $owner, name: $repo) { id }
              organization(login: $owner) {
                projectV2(number: $number) {
                  id
                  fields(first: 20) {
                    nodes { ... on ProjectV2FieldCommon { id name } }
                  }
                }
              }
              user(login: $owner) {
                projectV2(number: $number) {
                  id
                  fields(first: 20) {
                    nodes { ... on ProjectV2FieldCommon { id name } }
                  }
                }
              }
            }
            """;

    private final GitHubGraphQlClient client;
    private final GitHubTaskManagerProperties properties;

    private String repositoryId;
    private String projectId;
    private final Map<String, String> fieldIdsByName = new HashMap<>();

    public GitHubProjectContext(final GitHubGraphQlClient client, final GitHubTaskManagerProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public synchronized String repositoryId() {
        resolveIfNeeded();
        return repositoryId;
    }

    public synchronized String projectId() {
        resolveIfNeeded();
        return projectId;
    }

    public synchronized String fieldId(final String fieldName) {
        resolveIfNeeded();
        final String fieldId = fieldIdsByName.get(fieldName);
        if (fieldId == null) {
            throw new GitHubGraphQlException("Projects v2 board is missing the expected \"" + fieldName + "\" field");
        }
        return fieldId;
    }

    private void resolveIfNeeded() {
        if (repositoryId != null && projectId != null) {
            return;
        }
        final JsonNode data = client.execute(QUERY, Map.of(
                "owner", properties.owner(),
                "repo", properties.repo(),
                "number", properties.projectNumber()
        ));
        repositoryId = data.path("repository").path("id").asText();

        JsonNode projectV2 = data.path("organization").path("projectV2");
        if (projectV2.isMissingNode() || projectV2.isNull()) {
            projectV2 = data.path("user").path("projectV2");
        }
        if (projectV2.isMissingNode() || projectV2.isNull()) {
            throw new GitHubGraphQlException("No Projects v2 board #" + properties.projectNumber()
                    + " found for owner " + properties.owner());
        }
        projectId = projectV2.path("id").asText();
        for (final JsonNode field : projectV2.path("fields").path("nodes")) {
            final String name = field.path("name").asText(null);
            final String id = field.path("id").asText(null);
            if (name != null && id != null) {
                fieldIdsByName.put(name, id);
            }
        }
    }
}
