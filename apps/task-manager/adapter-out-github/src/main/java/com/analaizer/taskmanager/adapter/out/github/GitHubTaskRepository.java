package com.analaizer.taskmanager.adapter.out.github;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import com.analaizer.taskmanager.domain.TaskStatus;
import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

// Stores tasks as GitHub issues (title/body/labels) on the configured
// repository, and their dates/budget as custom fields on a linked Projects
// v2 board item. Managed issues are scoped with a fixed "task-manager" label
// so search/listing never picks up unrelated issues on the repository.
//
// `save` decides create vs. update by asking GitHub whether task.id() names
// an existing issue node: application use cases (e.g. CreateTask) pass in a
// client-generated id for brand-new tasks, and this adapter replaces it with
// the GitHub-assigned issue node id on the Task it returns.
//
// `deleteById` closes the issue rather than deleting it: the GitHub API
// cannot delete issues without special/enterprise permissions.
public class GitHubTaskRepository implements TaskRepository {

    private static final String MARKER_LABEL = "task-manager";
    private static final String START_DATE_FIELD = "Start date";
    private static final String END_DATE_FIELD = "End date";
    private static final String BUDGET_FIELD = "Budget";

    private static final String ISSUE_FIELDS = """
            id
            title
            body
            state
            labels(first: 50) { nodes { id name } }
            projectItems(first: 10) {
              nodes {
                id
                project { id }
                fieldValues(first: 20) {
                  nodes {
                    ... on ProjectV2ItemFieldDateValue { date field { ... on ProjectV2FieldCommon { name } } }
                    ... on ProjectV2ItemFieldNumberValue { number field { ... on ProjectV2FieldCommon { name } } }
                  }
                }
              }
            }
            """;

    private final GitHubGraphQlClient client;
    private final GitHubProjectContext context;
    private final GitHubTaskManagerProperties properties;

    public GitHubTaskRepository(GitHubGraphQlClient client, GitHubProjectContext context, GitHubTaskManagerProperties properties) {
        this.client = client;
        this.context = context;
        this.properties = properties;
    }

    @Override
    public List<Task> findAll() {
        return search(List.of());
    }

    @Override
    public Optional<Task> findById(String id) {
        String query = "query($id: ID!) { node(id: $id) { ... on Issue { " + ISSUE_FIELDS + " } } }";
        JsonNode data = client.execute(query, Map.of("id", id));
        JsonNode issue = data.path("node");
        if (issue.isMissingNode() || issue.isNull()) {
            return Optional.empty();
        }
        return Optional.of(toTask(issue));
    }

    @Override
    public List<Task> searchByLabels(Set<String> labels) {
        return search(List.copyOf(labels));
    }

    @Override
    public Task save(Task task) {
        Optional<Task> existing = looksLikeGitHubId(task.id()) ? findById(task.id()) : Optional.empty();
        if (existing.isPresent()) {
            return update(existing.get(), task);
        }
        return create(task);
    }

    @Override
    public void deleteById(String id) {
        String mutation = "mutation($id: ID!) { updateIssue(input: { id: $id, state: CLOSED }) { issue { id } } }";
        client.execute(mutation, Map.of("id", id));
    }

    private List<Task> search(List<String> requiredLabels) {
        StringJoiner searchQuery = new StringJoiner(" ");
        searchQuery.add("repo:" + properties.owner() + "/" + properties.repo());
        searchQuery.add("is:issue");
        searchQuery.add("label:\"" + MARKER_LABEL + "\"");
        for (String label : requiredLabels) {
            searchQuery.add("label:\"" + label + "\"");
        }
        String query = "query($searchQuery: String!) { search(query: $searchQuery, type: ISSUE, first: 100) { nodes { ... on Issue { "
                + ISSUE_FIELDS + " } } } }";
        JsonNode data = client.execute(query, Map.of("searchQuery", searchQuery.toString()));
        List<Task> tasks = new ArrayList<>();
        for (JsonNode issue : data.path("search").path("nodes")) {
            tasks.add(toTask(issue));
        }
        return tasks;
    }

    private Task create(Task task) {
        String createMutation = "mutation($repositoryId: ID!, $title: String!, $body: String) { "
                + "createIssue(input: { repositoryId: $repositoryId, title: $title, body: $body }) { issue { id } } }";
        JsonNode created = client.execute(createMutation, Map.of(
                "repositoryId", context.repositoryId(),
                "title", task.title(),
                "body", task.description()
        ));
        String issueId = created.path("createIssue").path("issue").path("id").asText();

        Set<String> labelIds = new HashSet<>();
        labelIds.add(ensureLabelId(MARKER_LABEL));
        labelIds.add(ensureLabelId(GitHubStatusLabels.labelFor(task.status())));
        for (String label : task.labels()) {
            labelIds.add(ensureLabelId(label));
        }
        addLabels(issueId, labelIds);

        String itemId = addToProject(issueId);
        setDateField(itemId, START_DATE_FIELD, task.startDate());
        setDateField(itemId, END_DATE_FIELD, task.endDate());
        task.budget().ifPresent(budget -> setNumberField(itemId, BUDGET_FIELD, budget));

        return findById(issueId).orElseThrow();
    }

    private Task update(Task before, Task after) {
        if (!before.title().equals(after.title()) || !before.description().equals(after.description())) {
            String mutation = "mutation($id: ID!, $title: String!, $body: String) { "
                    + "updateIssue(input: { id: $id, title: $title, body: $body }) { issue { id } } }";
            client.execute(mutation, Map.of("id", before.id(), "title", after.title(), "body", after.description()));
        }

        if (before.status() != after.status()) {
            removeLabels(before.id(), Set.of(GitHubStatusLabels.labelFor(before.status())));
            addLabels(before.id(), Set.of(ensureLabelId(GitHubStatusLabels.labelFor(after.status()))));
        }

        Set<String> addedLabels = new HashSet<>(after.labels());
        addedLabels.removeAll(before.labels());
        if (!addedLabels.isEmpty()) {
            Set<String> ids = new HashSet<>();
            for (String label : addedLabels) {
                ids.add(ensureLabelId(label));
            }
            addLabels(before.id(), ids);
        }

        Set<String> removedLabels = new HashSet<>(before.labels());
        removedLabels.removeAll(after.labels());
        if (!removedLabels.isEmpty()) {
            removeLabels(before.id(), removedLabels);
        }

        if (!before.startDate().equals(after.startDate()) || !before.endDate().equals(after.endDate())
                || !before.budget().equals(after.budget())) {
            String itemId = projectItemId(before.id()).orElseGet(() -> addToProject(before.id()));
            setDateField(itemId, START_DATE_FIELD, after.startDate());
            setDateField(itemId, END_DATE_FIELD, after.endDate());
            after.budget().ifPresent(budget -> setNumberField(itemId, BUDGET_FIELD, budget));
        }

        return findById(before.id()).orElseThrow();
    }

    private Optional<String> projectItemId(String issueId) {
        String query = "query($id: ID!) { node(id: $id) { ... on Issue { "
                + "projectItems(first: 10) { nodes { id project { id } } } } } }";
        JsonNode data = client.execute(query, Map.of("id", issueId));
        for (JsonNode item : data.path("node").path("projectItems").path("nodes")) {
            if (item.path("project").path("id").asText("").equals(context.projectId())) {
                return Optional.of(item.path("id").asText());
            }
        }
        return Optional.empty();
    }

    private String addToProject(String issueId) {
        String mutation = "mutation($projectId: ID!, $contentId: ID!) { "
                + "addProjectV2ItemById(input: { projectId: $projectId, contentId: $contentId }) { item { id } } }";
        JsonNode result = client.execute(mutation, Map.of("projectId", context.projectId(), "contentId", issueId));
        return result.path("addProjectV2ItemById").path("item").path("id").asText();
    }

    private void setDateField(String itemId, String fieldName, LocalDate value) {
        String mutation = "mutation($projectId: ID!, $itemId: ID!, $fieldId: ID!, $date: Date!) { "
                + "updateProjectV2ItemFieldValue(input: { projectId: $projectId, itemId: $itemId, fieldId: $fieldId, value: { date: $date } }) { projectV2Item { id } } }";
        client.execute(mutation, Map.of(
                "projectId", context.projectId(),
                "itemId", itemId,
                "fieldId", context.fieldId(fieldName),
                "date", value.toString()
        ));
    }

    private void setNumberField(String itemId, String fieldName, BigDecimal value) {
        String mutation = "mutation($projectId: ID!, $itemId: ID!, $fieldId: ID!, $number: Float!) { "
                + "updateProjectV2ItemFieldValue(input: { projectId: $projectId, itemId: $itemId, fieldId: $fieldId, value: { number: $number } }) { projectV2Item { id } } }";
        client.execute(mutation, Map.of(
                "projectId", context.projectId(),
                "itemId", itemId,
                "fieldId", context.fieldId(fieldName),
                "number", value.doubleValue()
        ));
    }

    private void addLabels(String issueId, Set<String> labelIds) {
        String mutation = "mutation($labelableId: ID!, $labelIds: [ID!]!) { "
                + "addLabelsToLabelable(input: { labelableId: $labelableId, labelIds: $labelIds }) { clientMutationId } }";
        client.execute(mutation, Map.of("labelableId", issueId, "labelIds", List.copyOf(labelIds)));
    }

    private void removeLabels(String issueId, Set<String> labelNames) {
        Set<String> ids = new HashSet<>();
        for (String name : labelNames) {
            ensureLabelId(name);
            ids.add(labelId(name));
        }
        String mutation = "mutation($labelableId: ID!, $labelIds: [ID!]!) { "
                + "removeLabelsFromLabelable(input: { labelableId: $labelableId, labelIds: $labelIds }) { clientMutationId } }";
        client.execute(mutation, Map.of("labelableId", issueId, "labelIds", List.copyOf(ids)));
    }

    private final Map<String, String> labelIdCache = new java.util.concurrent.ConcurrentHashMap<>();

    private String labelId(String name) {
        return labelIdCache.get(name);
    }

    private String ensureLabelId(String name) {
        return labelIdCache.computeIfAbsent(name, this::findOrCreateLabel);
    }

    private String findOrCreateLabel(String name) {
        String findQuery = "query($owner: String!, $repo: String!, $name: String!) { "
                + "repository(owner: $owner, name: $repo) { label(name: $name) { id } } }";
        JsonNode found = client.execute(findQuery, Map.of("owner", properties.owner(), "repo", properties.repo(), "name", name));
        JsonNode label = found.path("repository").path("label");
        if (!label.isMissingNode() && !label.isNull()) {
            return label.path("id").asText();
        }

        String createMutation = "mutation($repositoryId: ID!, $name: String!, $color: String!) { "
                + "createLabel(input: { repositoryId: $repositoryId, name: $name, color: $color }) { label { id } } }";
        JsonNode created = client.execute(createMutation, Map.of(
                "repositoryId", context.repositoryId(),
                "name", name,
                "color", "ededed"
        ));
        return created.path("createLabel").path("label").path("id").asText();
    }

    private Task toTask(JsonNode issue) {
        String id = issue.path("id").asText();
        String title = issue.path("title").asText();
        String description = issue.path("body").asText("");

        Set<String> labels = new HashSet<>();
        TaskStatus status = TaskStatus.TODO;
        for (JsonNode label : issue.path("labels").path("nodes")) {
            String name = label.path("name").asText();
            if (name.equals(MARKER_LABEL)) {
                continue;
            }
            if (GitHubStatusLabels.isStatusLabel(name)) {
                status = GitHubStatusLabels.statusFor(name);
            } else {
                labels.add(name);
            }
        }

        LocalDate startDate = null;
        LocalDate endDate = null;
        BigDecimal budget = null;
        for (JsonNode item : issue.path("projectItems").path("nodes")) {
            if (!item.path("project").path("id").asText("").equals(context.projectId())) {
                continue;
            }
            for (JsonNode fieldValue : item.path("fieldValues").path("nodes")) {
                String fieldName = fieldValue.path("field").path("name").asText("");
                if (fieldName.equals(START_DATE_FIELD) && fieldValue.hasNonNull("date")) {
                    startDate = LocalDate.parse(fieldValue.path("date").asText());
                } else if (fieldName.equals(END_DATE_FIELD) && fieldValue.hasNonNull("date")) {
                    endDate = LocalDate.parse(fieldValue.path("date").asText());
                } else if (fieldName.equals(BUDGET_FIELD) && fieldValue.hasNonNull("number")) {
                    budget = BigDecimal.valueOf(fieldValue.path("number").asDouble());
                }
            }
        }
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = startDate;
        }

        return new Task(id, title, description, startDate, endDate, labels, status, budget);
    }

    private boolean looksLikeGitHubId(String id) {
        return id != null && !id.isBlank() && !id.contains("-");
    }
}
