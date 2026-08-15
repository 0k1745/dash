package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskNotFoundException;
import com.analaizer.taskmanager.domain.TaskRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompleteTaskTest {

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final CompleteTask completeTask = new CompleteTask(taskRepository);

    @Test
    void completesAnExistingTask() {
        Task task = Task.create("1", "Write the ADR");
        when(taskRepository.findById("1")).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task completed = completeTask.execute("1");

        assertTrue(completed.completed());
    }

    @Test
    void failsWhenTaskDoesNotExist() {
        when(taskRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> completeTask.execute("missing"));
    }
}
