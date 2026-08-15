package com.analaizer.taskmanager.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

// Port implemented by outbound adapters (e.g. an in-memory store, or the
// GitHub-backed adapter). deleteById removes the task from the backing
// store as seen through this port; an adapter may implement that as a soft
// delete (e.g. the GitHub adapter closes the issue instead of deleting it,
// since the GitHub API cannot delete issues without special permissions).
public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(String id);

    // AND semantics: a task is only returned if it carries every label in `labels`.
    List<Task> searchByLabels(Set<String> labels);

    Task save(Task task);

    void deleteById(String id);
}
