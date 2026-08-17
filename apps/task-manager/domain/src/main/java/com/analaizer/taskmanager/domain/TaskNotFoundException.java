package com.analaizer.taskmanager.domain;

public final class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(final String id) {
        super("Task not found: " + id);
    }
}
