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

    public GitHubTaskRepository(final GitHubGraphQlClient client, final GitHubProjectContext context, final GitHubTaskManagerProperties properties) {
        this.client = client;
        this.context = context;
        this.properties = properties;
    }

    @Override
    public List<Task> findAll() {
        return search(List.of());
    }

    @Override
    public Optional<Task> findById(final String id) {
        final String query = "query($id: ID!) { node(id: $id) { ... on Issue { " + ISSUE_FIELDS + " } } }";
        final JsonNode data = client.execute(query, Map.of("id", id));
        final JsonNode issue = data.path("node");
        if (issue.isMissingNode() || issue.isNull()) {
            return Optional.empty();
        }
        return Optional.of(toTask(issue));
    }

    @Override
    public List<Task> searchByLabels(final Set<String> labels) {
        return search(List.copyOf(labels));
    }

    @Override
    public Task save(final Task task) {
        final Optional<Task> existing = looksLikeGitHubId(task.id()) ? findById(task.id()) : Optional.empty();
        if (existing.isPresent()) {
            return update(existing.get(), task);
        }
        return create(task);
    }

    @Override
    public void deleteById(final String id) {
        final String mutation = "mutation($id: ID!) { updateIssue(input: { id: $id, state: CLOSED }) { issue { id } } }";
        client.execute(mutation, Map.of("id", id));
    }

    private List<Task> search(final List<String> requiredLabels) {
        final StringJoiner searchQuery = new StringJoiner(" ");
        searchQuery.add("repo:" + properties.owner() + "/" + properties.repo());
        searchQuery.add("is:issue");
        searchQuery.add("label:\"" + MARKER_LABEL + "\"");
        for (final String label : requiredLabels) {
            searchQuery.add("label:\"" + label + "\"");
        }
        final String query = "query($searchQuery: String!) { search(query: $searchQuery, type: ISSUE, first: 100) { nodes { ... on Issue { "
                + ISSUE_FIELDS + " } } } }";
        final JsonNode data = client.execute(query, Map.of("searchQuery", searchQuery.toString()));
        final List<Task> tasks = new ArrayList<>();
        for (final JsonNode issue : data.path("search").path("nodes")) {
            tasks.add(toTask(issue));
        }
        return tasks;
    }

    private Task create(final Task task) {
        final String createMutation = "mutation($repositoryId: ID!, $title: String!, $body: String) { "
                + "createIssue(input: { repositoryId: $repositoryId, title: $title, body: $body }) { issue { id } } }";
        final JsonNode created = client.execute(createMutation, Map.of(
                "repositoryId", context.repositoryId(),
                "title", task.title(),
                "body", task.description()
        ));
        final String issueId = created.path("createIssue").path("issue").path("id").asText();

        final Set<String> labelIds = new HashSet<>();
        labelIds.add(ensureLabelId(MARKER_LABEL));
        labelIds.add(ensureLabelId(GitHubStatusLabels.labelFor(task.status())));
        for (final String label : task.labels()) {
            labelIds.add(ensureLabelId(label));
        }
        addLabels(issueId, labelIds);

        final String itemId = addToProject(issueId);
        setDateField(itemId, START_DATE_FIELD, task.startDate());
        setDateField(itemId, END_DATE_FIELD, task.endDate());
        task.budget().ifPresent(budget -> setNumberField(itemId, BUDGET_FIELD, budget));

        return findById(issueId).orElseThrow();
    }

    private Task update(final Task before, final Task after) {
        if (!before.title().equals(after.title()) || !before.description().equals(after.description())) {
            final String mutation = "mutation($id: ID!, $title: String!, $body: String) { "
                    + "updateIssue(input: { id: $id, title: $title, body: $body }) { issue { id } } }";
            client.execute(mutation, Map.of("id", before.id(), "title", after.title(), "body", after.description()));
        }

        if (before.status() != after.status()) {
            removeLabels(before.id(), Set.of(GitHubStatusLabels.labelFor(before.status())));
            addLabels(before.id(), Set.of(ensureLabelId(GitHubStatusLabels.labelFor(after.status()))));
        }

        final Set<String> addedLabels = new HashSet<>(after.labels());
        addedLabels.removeAll(before.labels());
        if (!addedLabels.isEmpty()) {
            final Set<String> ids = new HashSet<>();
            for (final String label : addedLabels) {
                ids.add(ensureLabelId(label));
            }
            addLabels(before.id(), ids);
        }

        final Set<String> removedLabels = new HashSet<>(before.labels());
        removedLabels.removeAll(after.labels());
        if (!removedLabels.isEmpty()) {
            removeLabels(before.id(), removedLabels);
        }

        if (!before.startDate().equals(after.startDate()) || !before.endDate().equals(after.endDate())
                || !before.budget().equals(after.budget())) {
            final String itemId = projectItemId(before.id()).orElseGet(() -> addToProject(before.id()));
            setDateField(itemId, START_DATE_FIELD, after.startDate());
            setDateField(itemId, END_DATE_FIELD, after.endDate());
            after.budget().ifPresent(budget -> setNumberField(itemId, BUDGET_FIELD, budget));
        }

        return findById(before.id()).orElseThrow();
    }

    private Optional<String> projectItemId(final String issueId) {
        final String query = "query($id: ID!) { node(id: $id) { ... on Issue { "
                + "projectItems(first: 10) { nodes { id project { id } } } } } }";
        final JsonNode data = client.execute(query, Map.of("id", issueId));
        for (final JsonNode item : data.path("node").path("projectItems").path("nodes")) {
            if (item.path("project").path("id").asText("").equals(context.projectId())) {
                return Optional.of(item.path("id").asText());
            }
        }
        return Optional.empty();
    }

    private String addToProject(final String issueId) {
        final String mutation = "mutation($projectId: ID!, $contentId: ID!) { "
                + "addProjectV2ItemById(input: { projectId: $projectId, contentId: $contentId }) { item { id } } }";
        final JsonNode result = client.execute(mutation, Map.of("projectId", context.projectId(), "contentId", issueId));
        return result.path("addProjectV2ItemById").path("item").path("id").asText();
    }

    private void setDateField(final String itemId, final String fieldName, final LocalDate value) {
        final String mutation = "mutation($projectId: ID!, $itemId: ID!, $fieldId: ID!, $date: Date!) { "
                + "updateProjectV2ItemFieldValue(input: { projectId: $projectId, itemId: $itemId, fieldId: $fieldId, value: { date: $date } }) { projectV2Item { id } } }";
        client.execute(mutation, Map.of(
                "projectId", context.projectId(),
                "itemId", itemId,
                "fieldId", context.fieldId(fieldName),
                "date", value.toString()
        ));
    }

    private void setNumberField(final String itemId, final String fieldName, final BigDecimal value) {
        final String mutation = "mutation($projectId: ID!, $itemId: ID!, $fieldId: ID!, $number: Float!) { "
                + "updateProjectV2ItemFieldValue(input: { projectId: $projectId, itemId: $itemId, fieldId: $fieldId, value: { number: $number } }) { projectV2Item { id } } }";
        client.execute(mutation, Map.of(
                "projectId", context.projectId(),
                "itemId", itemId,
                "fieldId", context.fieldId(fieldName),
                "number", value.doubleValue()
        ));
    }

    private void addLabels(final String issueId, final Set<String> labelIds) {
        final String mutation = "mutation($labelableId: ID!, $labelIds: [ID!]!) { "
                + "addLabelsToLabelable(input: { labelableId: $labelableId, labelIds: $labelIds }) { clientMutationId } }";
        client.execute(mutation, Map.of("labelableId", issueId, "labelIds", List.copyOf(labelIds)));
    }

    private void removeLabels(final String issueId, final Set<String> labelNames) {
        final Set<String> ids = new HashSet<>();
        for (final String name : labelNames) {
            ensureLabelId(name);
            ids.add(labelId(name));
        }
        final String mutation = "mutation($labelableId: ID!, $labelIds: [ID!]!) { "
                + "removeLabelsFromLabelable(input: { labelableId: $labelableId, labelIds: $labelIds }) { clientMutationId } }";
        client.execute(mutation, Map.of("labelableId", issueId, "labelIds", List.copyOf(ids)));
    }

    private final Map<String, String> labelIdCache = new java.util.concurrent.ConcurrentHashMap<>();

    private String labelId(final String name) {
        return labelIdCache.get(name);
    }

    private String ensureLabelId(final String name) {
        return labelIdCache.computeIfAbsent(name, this::findOrCreateLabel);
    }

    private String findOrCreateLabel(final String name) {
        final String findQuery = "query($owner: String!, $repo: String!, $name: String!) { "
                + "repository(owner: $owner, name: $repo) { label(name: $name) { id } } }";
        final JsonNode found = client.execute(findQuery, Map.of("owner", properties.owner(), "repo", properties.repo(), "name", name));
        final JsonNode label = found.path("repository").path("label");
        if (!label.isMissingNode() && !label.isNull()) {
            return label.path("id").asText();
        }

        final String createMutation = "mutation($repositoryId: ID!, $name: String!, $color: String!) { "
                + "createLabel(input: { repositoryId: $repositoryId, name: $name, color: $color }) { label { id } } }";
        final JsonNode created = client.execute(createMutation, Map.of(
                "repositoryId", context.repositoryId(),
                "name", name,
                "color", "ededed"
        ));
        return created.path("createLabel").path("label").path("id").asText();
    }

    private Task toTask(final JsonNode issue) {
        final String id = issue.path("id").asText();
        final String title = issue.path("title").asText();
        final String description = issue.path("body").asText("");

        final Set<String> labels = new HashSet<>();
        TaskStatus status = TaskStatus.TODO;
        for (final JsonNode label : issue.path("labels").path("nodes")) {
            final String name = label.path("name").asText();
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
        for (final JsonNode item : issue.path("projectItems").path("nodes")) {
            if (!item.path("project").path("id").asText("").equals(context.projectId())) {
                continue;
            }
            for (final JsonNode fieldValue : item.path("fieldValues").path("nodes")) {
                final String fieldName = fieldValue.path("field").path("name").asText("");
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

    private boolean looksLikeGitHubId(final String id) {
        return id != null && !id.isBlank() && !id.contains("-");
    }
}
