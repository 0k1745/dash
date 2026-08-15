package com.analaizer.taskmanager.adapter.out.github;

// Read from the four env vars documented in docs/task-manager-github-adapter.md:
// GITHUB_TASK_MANAGER_TOKEN / _OWNER / _REPO / _PROJECT_NUMBER. Nothing here is
// hardcoded to a specific owner/repo/project so the adapter can target any
// GitHub Issues + Projects v2 board an operator has configured.
public record GitHubTaskManagerProperties(String token, String owner, String repo, int projectNumber) {

    public GitHubTaskManagerProperties {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("GITHUB_TASK_MANAGER_TOKEN must be set");
        }
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("GITHUB_TASK_MANAGER_OWNER must be set");
        }
        if (repo == null || repo.isBlank()) {
            throw new IllegalArgumentException("GITHUB_TASK_MANAGER_REPO must be set");
        }
    }
}
