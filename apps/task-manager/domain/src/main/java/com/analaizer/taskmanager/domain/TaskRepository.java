package com.analaizer.taskmanager.domain;

import java.util.List;
import java.util.Optional;

// Port implemented by outbound adapters (e.g. an in-memory store).
public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(String id);

    Task save(Task task);

    void deleteById(String id);
}
