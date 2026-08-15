package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.TaskRepository;

public final class DeleteTask {

    private final TaskRepository taskRepository;

    public DeleteTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void execute(String id) {
        taskRepository.deleteById(id);
    }
}
