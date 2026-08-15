package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class CreateTask {

    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(String title, String description, LocalDate startDate, LocalDate endDate, BigDecimal budget) {
        Task task = Task.create(UUID.randomUUID().toString(), title, description, startDate, endDate, budget);
        return taskRepository.save(task);
    }
}
