package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class CreateTask {

    private final TaskRepository taskRepository;

    public CreateTask(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task execute(final String title, final String description, final LocalDate startDate, final LocalDate endDate, final BigDecimal budget) {
        final Task task = Task.create(UUID.randomUUID().toString(), title, description, startDate, endDate, budget);
        return taskRepository.save(task);
    }
}
