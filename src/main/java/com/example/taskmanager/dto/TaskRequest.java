package com.example.taskmanager.dto;

import com.example.taskmanager.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskRequest {

    @NotBlank(message = "Название не должно быть пустым")
    @Size(max = 100, message = "Название не должно быть длиннее 100 символов")
    private String title;

    @Size(max = 500, message = "Описание не должно быть длиннее 500 символов")
    private String description;

    private Task.Priority priority = Task.Priority.MEDIUM;

    private Task.Status status = Task.Status.NEW;

    public TaskRequest() {
    }

    public TaskRequest(Task task) {
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.priority = task.getPriority();
        this.status = task.getStatus();
    }

    public Task toTask() {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setPriority(priority);
        task.setStatus(status);
        return task;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Task.Priority getPriority() {
        return priority;
    }

    public void setPriority(Task.Priority priority) {
        this.priority = priority;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }
}