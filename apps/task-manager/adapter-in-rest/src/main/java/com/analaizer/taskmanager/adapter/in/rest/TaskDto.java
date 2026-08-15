package com.analaizer.taskmanager.adapter.in.rest;

import com.analaizer.taskmanager.domain.Task;

public record TaskDto(String id, String title, boolean completed) {

    public static TaskDto from(Task task) {
        return new TaskDto(task.id(), task.title(), task.completed());
    }
}
