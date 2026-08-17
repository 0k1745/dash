package com.analaizer.taskmanager.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.analaizer.taskmanager")
public class TaskManagerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
