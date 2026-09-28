package com.example.taskmanager.config;

import com.example.taskmanager.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${app.name}")
    private String name;

    @Value("${app.max-tasks}")
    private int maxTasks;

    @Value("${app.default-priority}")
    private Task.Priority defaultPriority;

    public String getName() {
        return name;
    }

    public int getMaxTasks() {
        return maxTasks;
    }

    public Task.Priority getDefaultPriority() {
        return defaultPriority;
    }
}
