package com.analaizer.taskmanager.adapter.out.memory;

import com.analaizer.taskmanager.domain.Task;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskRepositoryTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    private final InMemoryTaskRepository repository = new InMemoryTaskRepository();

    @Test
    void savesAndFindsATask() {
        Task task = Task.create("1", "Write the ADR", "Describe the decision", START, END, null);

        repository.save(task);

        assertEquals(task, repository.findById("1").orElseThrow());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deletesATask() {
        repository.save(Task.create("1", "Write the ADR", "Describe the decision", START, END, null));

        repository.deleteById("1");

        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void searchesTasksMatchingAllRequestedLabels() {
        Task matching = Task.create("1", "Write the ADR", "Describe the decision", START, END, null);
        matching.addLabel("backend");
        matching.addLabel("urgent");
        Task partial = Task.create("2", "Update the README", "Describe the change", START, END, null);
        partial.addLabel("backend");
        repository.save(matching);
        repository.save(partial);

        List<Task> found = repository.searchByLabels(Set.of("backend", "urgent"));

        assertEquals(List.of(matching), found);
    }
}
