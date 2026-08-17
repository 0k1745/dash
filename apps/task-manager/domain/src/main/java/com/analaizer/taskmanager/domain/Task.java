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
            final String id,
            final String title,
            final String description,
            final LocalDate startDate,
            final LocalDate endDate,
            final Set<String> labels,
            final TaskStatus status,
            final BigDecimal budget
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

    public static Task create(final String id, final String title, final String description, final LocalDate startDate, final LocalDate endDate, final BigDecimal budget) {
        return new Task(id, title, description, startDate, endDate, Set.of(), TaskStatus.TODO, budget);
    }

    public void changeStatus(final TaskStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");
        if (status == newStatus) {
            throw new IllegalStateException("Task " + id + " is already in status " + newStatus);
        }
        status = newStatus;
    }

    public void addLabel(final String label) {
        labels.add(requireNonBlank(label, "label"));
    }

    public void removeLabel(final String label) {
        labels.remove(label);
    }

    public boolean hasAllLabels(final Set<String> requiredLabels) {
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

    private static String requireNonBlank(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }

    private static LocalDate requireEndNotBeforeStart(final LocalDate start, final LocalDate end) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("endDate must not be before startDate");
        }
        return end;
    }

    @Override
    public boolean equals(final Object other) {
        return other instanceof Task task && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
