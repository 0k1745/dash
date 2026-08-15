package com.analaizer.taskmanager.adapter.in.rest;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record TaskDto(
        String id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Set<String> labels,
        TaskStatus status,
        BigDecimal budget
) {

    public static TaskDto from(Task task) {
        return new TaskDto(
                task.id(),
                task.title(),
                task.description(),
                task.startDate(),
                task.endDate(),
                task.labels(),
                task.status(),
                task.budget().orElse(null)
        );
    }
}
