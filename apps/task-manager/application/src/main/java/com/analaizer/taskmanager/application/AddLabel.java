package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskNotFoundException;
import com.analaizer.taskmanager.domain.TaskRepository;

public final class AddLabel {

    private final TaskRepository taskRepository;

    public AddLabel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(String id, String label) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        task.addLabel(label);
        return taskRepository.save(task);
    }
}
