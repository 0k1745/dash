package com.analaizer.taskmanager.bootstrap.cucumber;

import com.analaizer.taskmanager.bootstrap.TaskManagerApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(
        classes = TaskManagerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureTestRestTemplate
public class CucumberSpringConfiguration {
}
