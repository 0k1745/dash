package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskNotFoundException;
import com.analaizer.taskmanager.domain.TaskRepository;

public final class RemoveLabel {

    private final TaskRepository taskRepository;

    public RemoveLabel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(String id, String label) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.removeLabel(label);
        return taskRepository.save(task);
    }
}
