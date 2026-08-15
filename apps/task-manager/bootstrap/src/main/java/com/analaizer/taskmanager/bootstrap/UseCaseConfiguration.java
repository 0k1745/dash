package com.analaizer.taskmanager.bootstrap;

import com.analaizer.taskmanager.application.CompleteTask;
import com.analaizer.taskmanager.application.CreateTask;
import com.analaizer.taskmanager.application.DeleteTask;
import com.analaizer.taskmanager.application.ListTasks;
import com.analaizer.taskmanager.domain.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Wires the application use cases to the TaskRepository port implementation
// provided by the configured outbound adapter (in-memory today).
@Configuration
public class UseCaseConfiguration {

    @Bean
    public ListTasks listTasks(TaskRepository taskRepository) {
        return new ListTasks(taskRepository);
    }

    @Bean
    public CreateTask createTask(TaskRepository taskRepository) {
        return new CreateTask(taskRepository);
    }

    @Bean
    public CompleteTask completeTask(TaskRepository taskRepository) {
        return new CompleteTask(taskRepository);
    }

    @Bean
    public DeleteTask deleteTask(TaskRepository taskRepository) {
        return new DeleteTask(taskRepository);
    }
}
