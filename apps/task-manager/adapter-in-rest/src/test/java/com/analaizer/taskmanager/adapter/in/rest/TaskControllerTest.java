package com.analaizer.taskmanager.adapter.in.rest;

import com.analaizer.taskmanager.application.AddLabel;
import com.analaizer.taskmanager.application.ChangeTaskStatus;
import com.analaizer.taskmanager.application.CreateTask;
import com.analaizer.taskmanager.application.DeleteTask;
import com.analaizer.taskmanager.application.ListTasks;
import com.analaizer.taskmanager.application.RemoveLabel;
import com.analaizer.taskmanager.application.SearchTasksByLabels;
import com.analaizer.taskmanager.domain.Task;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListTasks listTasks;

    @MockitoBean
    private CreateTask createTask;

    @MockitoBean
    private ChangeTaskStatus changeTaskStatus;

    @MockitoBean
    private AddLabel addLabel;

    @MockitoBean
    private RemoveLabel removeLabel;

    @MockitoBean
    private SearchTasksByLabels searchTasksByLabels;

    @MockitoBean
    private DeleteTask deleteTask;

    @Test
    void listsTasks() throws Exception {
        when(listTasks.execute()).thenReturn(
                List.of(Task.create("1", "Write the ADR", "Describe the decision", START, END, null)));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"id\":\"1\",\"title\":\"Write the ADR\",\"description\":\"Describe the decision\","
                                + "\"startDate\":\"2026-01-01\",\"endDate\":\"2026-01-31\",\"labels\":[],"
                                + "\"status\":\"TODO\",\"budget\":null}]"));
    }

    @Test
    void rejectsABlankTitleOnCreate() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"description\":\"desc\",\"startDate\":\"2026-01-01\",\"endDate\":\"2026-01-31\"}"))
                .andExpect(status().isBadRequest());
    }
}
