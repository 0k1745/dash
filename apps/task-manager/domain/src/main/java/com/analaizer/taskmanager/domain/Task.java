package com.analaizer.taskmanager.domain;

import java.util.Objects;

public final class Task {

    private final String id;
    private final String title;
    private boolean completed;

    public Task(String id, String title, boolean completed) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.title = requireNonBlank(title);
        this.completed = completed;
    }

    public static Task create(String id, String title) {
        return new Task(id, title, false);
    }

    public void complete() {
        if (completed) {
            throw new IllegalStateException("Task " + id + " is already completed");
        }
        completed = true;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public boolean completed() {
        return completed;
    }

    private static String requireNonBlank(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        return title;
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
