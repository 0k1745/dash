package com.analaizer.taskmanager.adapter.in.rest;

import com.analaizer.taskmanager.application.AddLabel;
import com.analaizer.taskmanager.application.ChangeTaskStatus;
import com.analaizer.taskmanager.application.CreateTask;
import com.analaizer.taskmanager.application.DeleteTask;
import com.analaizer.taskmanager.application.ListTasks;
import com.analaizer.taskmanager.application.RemoveLabel;
import com.analaizer.taskmanager.application.SearchTasksByLabels;
import com.analaizer.taskmanager.domain.Task;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ListTasks listTasks;
    private final CreateTask createTask;
    private final ChangeTaskStatus changeTaskStatus;
    private final AddLabel addLabel;
    private final RemoveLabel removeLabel;
    private final SearchTasksByLabels searchTasksByLabels;
    private final DeleteTask deleteTask;

    public TaskController(
            ListTasks listTasks,
            CreateTask createTask,
            ChangeTaskStatus changeTaskStatus,
            AddLabel addLabel,
            RemoveLabel removeLabel,
            SearchTasksByLabels searchTasksByLabels,
            DeleteTask deleteTask
    ) {
        this.listTasks = listTasks;
        this.createTask = createTask;
        this.changeTaskStatus = changeTaskStatus;
        this.addLabel = addLabel;
        this.removeLabel = removeLabel;
        this.searchTasksByLabels = searchTasksByLabels;
        this.deleteTask = deleteTask;
    }

    @GetMapping
    public List<TaskDto> findAll(@RequestParam(name = "labels", required = false) Set<String> labels) {
        List<Task> tasks = (labels == null || labels.isEmpty())
                ? listTasks.execute()
                : searchTasksByLabels.execute(labels);
        return tasks.stream().map(TaskDto::from).toList();
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskDto created = TaskDto.from(createTask.execute(
                request.title(),
                request.description(),
                request.startDate(),
                request.endDate(),
                request.budget()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}/status")
    public TaskDto changeStatus(@PathVariable String id, @Valid @RequestBody ChangeStatusRequest request) {
        return TaskDto.from(changeTaskStatus.execute(id, request.status()));
    }

    @PostMapping("/{id}/labels")
    public TaskDto addLabel(@PathVariable String id, @Valid @RequestBody LabelRequest request) {
        return TaskDto.from(addLabel.execute(id, request.label()));
    }

    @DeleteMapping("/{id}/labels/{label}")
    public TaskDto removeLabel(@PathVariable String id, @PathVariable String label) {
        return TaskDto.from(removeLabel.execute(id, label));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteTask.execute(id);
        return ResponseEntity.noContent().build();
    }
}
