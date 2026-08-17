package com.analaizer.taskmanager.bootstrap;

import com.analaizer.taskmanager.adapter.out.github.GitHubGraphQlClient;
import com.analaizer.taskmanager.adapter.out.github.GitHubProjectContext;
import com.analaizer.taskmanager.adapter.out.github.GitHubTaskManagerProperties;
import com.analaizer.taskmanager.adapter.out.github.GitHubTaskRepository;
import com.analaizer.taskmanager.adapter.out.memory.InMemoryTaskRepository;
import com.analaizer.taskmanager.domain.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RepositoryConfiguration {

    // Default adapter: used for local dev and by the Cucumber suite so
    // mvn verify stays hermetic (no live GitHub calls).
    @Bean
    @Profile("!github")
    public TaskRepository inMemoryTaskRepository() {
        return new InMemoryTaskRepository();
    }

    // Backed by GitHub Issues + Projects v2. Activated with the "github"
    // Spring profile once GITHUB_TASK_MANAGER_TOKEN/OWNER/REPO/PROJECT_NUMBER
    // are configured (see docs/task-manager-github-adapter.md).
    @Bean
    @Profile("github")
    public TaskRepository gitHubTaskRepository(
            @Value("${github.task-manager.token}") final String token,
            @Value("${github.task-manager.owner}") final String owner,
            @Value("${github.task-manager.repo}") final String repo,
            @Value("${github.task-manager.project-number}") final int projectNumber
    ) {
        final GitHubTaskManagerProperties properties = new GitHubTaskManagerProperties(token, owner, repo, projectNumber);
        final GitHubGraphQlClient client = new GitHubGraphQlClient(HttpClient.newHttpClient(), new ObjectMapper(), token);
        final GitHubProjectContext context = new GitHubProjectContext(client, properties);
        return new GitHubTaskRepository(client, context, properties);
    }
}
