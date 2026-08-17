package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import java.util.List;
import java.util.Set;

public final class SearchTasksByLabels {

    private final TaskRepository taskRepository;

    public SearchTasksByLabels(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // AND semantics: a task is only returned if it carries every requested label.
    public List<Task> execute(final Set<String> labels) {
        return taskRepository.searchByLabels(labels);
    }
}
