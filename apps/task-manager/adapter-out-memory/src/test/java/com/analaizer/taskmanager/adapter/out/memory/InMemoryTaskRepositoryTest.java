package com.analaizer.taskmanager.adapter.out.memory;

import com.analaizer.taskmanager.domain.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTaskRepositoryTest {

    private final InMemoryTaskRepository repository = new InMemoryTaskRepository();

    @Test
    void savesAndFindsATask() {
        Task task = Task.create("1", "Write the ADR");

        repository.save(task);

        assertEquals(task, repository.findById("1").orElseThrow());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deletesATask() {
        repository.save(Task.create("1", "Write the ADR"));

        repository.deleteById("1");

        assertTrue(repository.findAll().isEmpty());
    }
}
