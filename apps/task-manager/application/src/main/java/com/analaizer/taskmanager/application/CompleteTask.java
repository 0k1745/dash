package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskNotFoundException;
import com.analaizer.taskmanager.domain.TaskRepository;

public final class CompleteTask {

    private final TaskRepository taskRepository;

    public CompleteTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(String id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.complete();
        return taskRepository.save(task);
    }
}
