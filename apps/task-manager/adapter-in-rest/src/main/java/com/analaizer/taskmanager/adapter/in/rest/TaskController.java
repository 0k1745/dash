package com.analaizer.taskmanager.adapter.in.rest;

import com.analaizer.taskmanager.application.CompleteTask;
import com.analaizer.taskmanager.application.CreateTask;
import com.analaizer.taskmanager.application.DeleteTask;
import com.analaizer.taskmanager.application.ListTasks;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ListTasks listTasks;
    private final CreateTask createTask;
    private final CompleteTask completeTask;
    private final DeleteTask deleteTask;

    public TaskController(ListTasks listTasks, CreateTask createTask, CompleteTask completeTask, DeleteTask deleteTask) {
        this.listTasks = listTasks;
        this.createTask = createTask;
        this.completeTask = completeTask;
        this.deleteTask = deleteTask;
    }

    @GetMapping
    public List<TaskDto> findAll() {
        return listTasks.execute().stream().map(TaskDto::from).toList();
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody CreateTaskRequest request) {
        TaskDto created = TaskDto.from(createTask.execute(request.title()));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public TaskDto complete(@PathVariable String id, @RequestBody CompleteTaskRequest request) {
        if (!request.completed()) {
            throw new IllegalArgumentException("Only completing a task is supported");
        }
        return TaskDto.from(completeTask.execute(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteTask.execute(id);
        return ResponseEntity.noContent().build();
    }
}
