package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {

    List<Task> getAll(String status, String priority);

    Page<Task> getAll(int page, int size, String sortBy);

    Optional<Task> findById(Long id);

    Task create(Task task, String email);

    Optional<Task> update(Long id, Task task);

    Task updateStatus(Long id, Task.Status status);

    void delete(Long id);

    Map<String, Long> getStats();

    List<Task> search(String keyword);

    List<Task> getMyTasks(String email);
}
