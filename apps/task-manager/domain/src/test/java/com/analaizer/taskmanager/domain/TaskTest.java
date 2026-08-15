package com.analaizer.taskmanager.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    @Test
    void createsAnIncompleteTask() {
        Task task = Task.create("1", "Write the ADR");

        assertEquals("1", task.id());
        assertEquals("Write the ADR", task.title());
        assertFalse(task.completed());
    }

    @Test
    void completesATask() {
        Task task = Task.create("1", "Write the ADR");

        task.complete();

        assertTrue(task.completed());
    }

    @Test
    void cannotCompleteATaskTwice() {
        Task task = Task.create("1", "Write the ADR");
        task.complete();

        assertThrows(IllegalStateException.class, task::complete);
    }

    @Test
    void rejectsABlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> Task.create("1", "  "));
    }
}
