package com.analaizer.taskmanager.domain;

public final class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task not found: " + id);
    }
}
