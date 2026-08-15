package com.analaizer.taskmanager.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void createsATaskInTodoStatusWithNoLabels() {
        Task task = Task.create("1", "Write the ADR", "Describe the decision", START, END, null);

        assertEquals("1", task.id());
        assertEquals("Write the ADR", task.title());
        assertEquals(TaskStatus.TODO, task.status());
        assertTrue(task.labels().isEmpty());
        assertTrue(task.budget().isEmpty());
    }

    @Test
    void keepsAnOptionalBudgetWhenProvided() {
        Task task = Task.create("1", "Write the ADR", "Describe the decision", START, END, new BigDecimal("500"));

        assertEquals(new BigDecimal("500"), task.budget().orElseThrow());
    }

    @Test
    void changesStatus() {
        Task task = Task.create("1", "Write the ADR", "Describe the decision", START, END, null);

        task.changeStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.status());
    }

    @Test
    void rejectsTransitioningToTheSameStatus() {
        Task task = Task.create("1", "Write the ADR", "Describe the decision", START, END, null);
        task.changeStatus(TaskStatus.DONE);

        assertThrows(IllegalStateException.class, () -> task.changeStatus(TaskStatus.DONE));
    }

    @Test
    void addsAndRemovesLabelsDynamically() {
        Task task = Task.create("1", "Write the ADR", "Describe the decision", START, END, null);

        task.addLabel("docs");
        task.addLabel("urgent");
        assertTrue(task.hasAllLabels(Set.of("docs", "urgent")));

        task.removeLabel("urgent");
        assertFalse(task.hasAllLabels(Set.of("docs", "urgent")));
    }

    @Test
    void rejectsABlankTitle() {
        assertThrows(IllegalArgumentException.class,
                () -> Task.create("1", "  ", "Describe the decision", START, END, null));
    }

    @Test
    void rejectsAnEndDateBeforeTheStartDate() {
        assertThrows(IllegalArgumentException.class,
                () -> Task.create("1", "Write the ADR", "Describe the decision", END, START, null));
    }
}
