package com.analaizer.taskmanager.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class Task {

    private final String id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private final Set<String> labels;
    private TaskStatus status;
    private BigDecimal budget;

    public Task(
            String id,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            Set<String> labels,
            TaskStatus status,
            BigDecimal budget
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = requireNonBlank(title, "title");
        this.description = Objects.requireNonNull(description, "description must not be null");
        this.startDate = Objects.requireNonNull(startDate, "startDate must not be null");
        this.endDate = requireEndNotBeforeStart(startDate, Objects.requireNonNull(endDate, "endDate must not be null"));
        this.labels = new HashSet<>(Objects.requireNonNull(labels, "labels must not be null"));
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.budget = budget;
    }

    public static Task create(String id, String title, String description, LocalDate startDate, LocalDate endDate, BigDecimal budget) {
        return new Task(id, title, description, startDate, endDate, Set.of(), TaskStatus.TODO, budget);
    }

    public void changeStatus(TaskStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        if (status == newStatus) {
            throw new IllegalStateException("Task " + id + " is already in status " + newStatus);
        }
        status = newStatus;
    }

    public void addLabel(String label) {
        labels.add(requireNonBlank(label, "label"));
    }

    public void removeLabel(String label) {
        labels.remove(label);
    }

    public boolean hasAllLabels(Set<String> requiredLabels) {
        return labels.containsAll(requiredLabels);
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public String description() {
        return description;
    }

    public LocalDate startDate() {
        return startDate;
    }

    public LocalDate endDate() {
        return endDate;
    }

    public Set<String> labels() {
        return Set.copyOf(labels);
    }

    public TaskStatus status() {
        return status;
    }

    public Optional<BigDecimal> budget() {
        return Optional.ofNullable(budget);
    }

    private static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static LocalDate requireEndNotBeforeStart(LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        return end;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Task task && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
