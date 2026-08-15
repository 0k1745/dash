package com.analaizer.taskmanager.bootstrap;

import com.analaizer.taskmanager.application.AddLabel;
import com.analaizer.taskmanager.application.ChangeTaskStatus;
import com.analaizer.taskmanager.application.CreateTask;
import com.analaizer.taskmanager.application.DeleteTask;
import com.analaizer.taskmanager.application.ListTasks;
import com.analaizer.taskmanager.application.RemoveLabel;
import com.analaizer.taskmanager.application.SearchTasksByLabels;
import com.analaizer.taskmanager.domain.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Wires the application use cases to the TaskRepository port implementation
// provided by the configured outbound adapter (in-memory by default, GitHub
// when the "github" profile is active).
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
    public ChangeTaskStatus changeTaskStatus(TaskRepository taskRepository) {
        return new ChangeTaskStatus(taskRepository);
    }

    @Bean
    public AddLabel addLabel(TaskRepository taskRepository) {
        return new AddLabel(taskRepository);
    }

    @Bean
    public RemoveLabel removeLabel(TaskRepository taskRepository) {
        return new RemoveLabel(taskRepository);
    }

    @Bean
    public SearchTasksByLabels searchTasksByLabels(TaskRepository taskRepository) {
        return new SearchTasksByLabels(taskRepository);
    }

    @Bean
    public DeleteTask deleteTask(TaskRepository taskRepository) {
        return new DeleteTask(taskRepository);
    }
}
