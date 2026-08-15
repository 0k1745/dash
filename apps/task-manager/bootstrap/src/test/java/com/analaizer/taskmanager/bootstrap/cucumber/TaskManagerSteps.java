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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskManagerSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    private ResponseEntity<Map> lastResponse;

    @Before
    public void resetTasks() {
        taskRepository.findAll().forEach(task -> taskRepository.deleteById(task.id()));
    }

    @Given("the task list is empty")
    public void theTaskListIsEmpty() {
        assertThat(taskRepository.findAll()).isEmpty();
    }

    @Given("I have created a task titled {string}")
    public void iHaveCreatedATaskTitled(String title) {
        createTask(title);
    }

    @And("I have completed the task titled {string}")
    public void iHaveCompletedTheTaskTitled(String title) {
        completeTask(title);
    }

    @When("I create a task titled {string}")
    public void iCreateATaskTitled(String title) {
        createTask(title);
    }

    @When("I complete the task titled {string}")
    public void iCompleteTheTaskTitled(String title) {
        completeTask(title);
    }

    @When("I complete the task titled {string} again")
    public void iCompleteTheTaskTitledAgain(String title) {
        completeTask(title);
    }

    @Then("the task list contains a task titled {string} that is not completed")
    public void theTaskListContainsATaskTitledThatIsNotCompleted(String title) {
        assertTaskState(title, false);
    }

    @Then("the task list contains a task titled {string} that is completed")
    public void theTaskListContainsATaskTitledThatIsCompleted(String title) {
        assertTaskState(title, true);
    }

    @Then("the request fails with a conflict")
    public void theRequestFailsWithAConflict() {
        assertThat(lastResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private void createTask(String title) {
        lastResponse = restTemplate.postForEntity("/api/tasks", Map.of("title", title), Map.class);
    }

    private void completeTask(String title) {
        Task task = findByTitle(title);
        lastResponse = restTemplate.exchange(
                "/api/tasks/" + task.id(),
                org.springframework.http.HttpMethod.PATCH,
                new org.springframework.http.HttpEntity<>(Map.of("completed", true)),
                Map.class
        );
    }

    private void assertTaskState(String title, boolean completed) {
        Task task = findByTitle(title);
        assertThat(task.completed()).isEqualTo(completed);
    }

    private Task findByTitle(String title) {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .filter(task -> task.title().equals(title))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No task titled '" + title + "' found"));
    }
}
