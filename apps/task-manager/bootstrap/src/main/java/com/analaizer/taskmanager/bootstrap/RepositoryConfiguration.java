package com.analaizer.taskmanager.bootstrap;

import com.analaizer.taskmanager.adapter.out.memory.InMemoryTaskRepository;
import com.analaizer.taskmanager.domain.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfiguration {

    @Bean
    public TaskRepository taskRepository() {
        return new InMemoryTaskRepository();
    }
}
