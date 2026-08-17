package com.analaizer.taskmanager.bootstrap.cucumber;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private ResponseEntity<Map> lastResponse;
    private List<Map> lastListResponse;

    @Before
    public void resetTasks() {
        taskRepository.findAll().forEach(task -> taskRepository.deleteById(task.id()));
    }

    @Given("the task list is empty")
    public void theTaskListIsEmpty() {
        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Given("I have created a task titled {string}")
    public void iHaveCreatedATaskTitled(final String title) {
        createTask(title);
    }

    @And("I have changed the status of the task titled {string} to {string}")
    public void iHaveChangedTheStatusOfTheTaskTitledTo(final String title, final String status) {
        changeStatus(title, status);
    }

    @When("I create a task titled {string}")
    public void iCreateATaskTitled(final String title) {
        createTask(title);
    }

    @When("I change the status of the task titled {string} to {string}")
    public void iChangeTheStatusOfTheTaskTitledTo(final String title, final String status) {
        changeStatus(title, status);
    }

    @When("I change the status of the task titled {string} to {string} again")
    public void iChangeTheStatusOfTheTaskTitledToAgain(final String title, final String status) {
        changeStatus(title, status);
    }

    @When("I add the label {string} to the task titled {string}")
    public void iAddTheLabelToTheTaskTitled(final String label, final String title) {
        final Task task = findByTitle(title);
        lastResponse = restTemplate.postForEntity(
                "/api/tasks/" + task.id() + "/labels", Map.of("label", label), Map.class);
    }

    @Then("the task list contains a task titled {string} with status {string}")
    public void theTaskListContainsATaskTitledWithStatus(final String title, final String status) {
        final Task task = findByTitle(title);
        assertThat(task.status().name()).isEqualTo(status);
    }

    @Then("the request fails with a conflict")
    public void theRequestFailsWithAConflict() {
        assertThat(lastResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Then("searching by label {string} returns the task titled {string}")
    public void searchingByLabelReturnsTheTaskTitled(final String label, final String title) {
        final ResponseEntity<List> response = restTemplate.getForEntity("/api/tasks?labels=" + label, List.class);
        lastListResponse = response.getBody();
        assertThat(lastListResponse)
                .extracting(task -> task.get("title"))
                .contains(title);
    }

    private void createTask(final String title) {
        final Map<String, Object> body = Map.of(
                "title", title,
                "description", "Description of " + title,
                "startDate", "2026-01-01",
                "endDate", "2026-01-31"
        );
        lastResponse = restTemplate.postForEntity("/api/tasks", body, Map.class);
    }

    private void changeStatus(final String title, final String status) {
        final Task task = findByTitle(title);
        lastResponse = restTemplate.exchange(
                "/api/tasks/" + task.id() + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(Map.of("status", status)),
                Map.class
        );
    }

    private Task findByTitle(final String title) {
        final List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(task -> task.title().equals(title))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No task titled '" + title + "' found"));
    }
}
