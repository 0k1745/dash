package com.analaizer.taskmanager.adapter.out.memory;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryTaskRepository implements TaskRepository {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public List<Task> findAll() {
        return List.copyOf(tasks.values());
    }

    @Override
    public Optional<Task> findById(final String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> searchByLabels(final Set<String> labels) {
        return tasks.values().stream()
                .filter(task -> task.hasAllLabels(labels))
                .toList();
    }

    @Override
    public Task save(final Task task) {
        tasks.put(task.id(), task);
        return task;
    }

    @Override
    public void deleteById(final String id) {
        tasks.remove(id);
    }
}
