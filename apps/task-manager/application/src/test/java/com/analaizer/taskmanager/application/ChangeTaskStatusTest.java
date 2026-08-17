package com.analaizer.taskmanager.application;

import com.analaizer.taskmanager.domain.Task;
import com.analaizer.taskmanager.domain.TaskNotFoundException;
import com.analaizer.taskmanager.domain.TaskRepository;
import com.analaizer.taskmanager.domain.TaskStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChangeTaskStatusTest {

    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final ChangeTaskStatus changeTaskStatus = new ChangeTaskStatus(taskRepository);

    @Test
    void changesTheStatusOfAnExistingTask() {
        final Task task = Task.create("1", "Write the ADR", "Describe the decision",
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), null);
        when(taskRepository.findById("1")).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final Task updated = changeTaskStatus.execute("1", TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, updated.status());
    }

    @Test
    void failsWhenTaskDoesNotExist() {
        when(taskRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> changeTaskStatus.execute("missing", TaskStatus.DONE));
    }
}
