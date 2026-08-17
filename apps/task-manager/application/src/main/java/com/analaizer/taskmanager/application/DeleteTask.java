package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.TaskRepository;

public final class DeleteTask {

    private final TaskRepository taskRepository;

    public DeleteTask(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(final String id) {
        taskRepository.deleteById(id);
    }
}
