package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import java.util.UUID;

public final class CreateTask {

    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(String title) {
        Task task = Task.create(UUID.randomUUID().toString(), title);
        return taskRepository.save(task);
    }
}
