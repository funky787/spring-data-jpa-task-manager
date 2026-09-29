package com.example.taskmanager.service;

import com.example.taskmanager.config.AppProperties;
import com.example.taskmanager.exception.TaskNotFoundException;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final AppProperties properties;
    private final UserRepository userRepository;

    public TaskServiceImpl(
            TaskRepository repository,
            AppProperties properties,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.properties = properties;
        this.userRepository = userRepository;
    }
    @Override
    public List<Task> getAll(String status, String priority) {
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasPriority = priority != null && !priority.isBlank();

        if (hasStatus && hasPriority) {
            return repository.findByStatusAndPriority(
                    parseStatus(status),
                    parsePriority(priority)
            );
        }

        if (hasStatus) {
            return repository.findByStatus(parseStatus(status));
        }

        if (hasPriority) {
            return repository.findByPriority(parsePriority(priority));
        }

        return repository.findAll();
    }

    @Override
    public Page<Task> getAll(int page, int size, String sortBy) {
        if (page < 0) {
            throw new IllegalArgumentException("page не может быть меньше 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size должен быть больше 0");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return repository.findAll(pageable);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Task create(Task task, String email) {
        validateTask(task);

        if (repository.count() >= properties.getMaxTasks()) {
            throw new IllegalArgumentException(
                    "Достигнут лимит задач: " + properties.getMaxTasks());
        }

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Пользователь не найден: " + email
                        )
                );

        task.setId(null);

        if (task.getPriority() == null) {
            task.setPriority(properties.getDefaultPriority());
        }

        if (task.getStatus() == null) {
            task.setStatus(Task.Status.NEW);
        }

        task.setOwner(owner);

        return repository.save(task);
    }

    @Override
    public Optional<Task> update(Long id, Task task) {
        Optional<Task> existing = repository.findById(id);

        if (existing.isEmpty()) {
            return Optional.empty();
        }

        validateTask(task);

        Task oldTask = existing.get();


        oldTask.setTitle(task.getTitle());
        oldTask.setDescription(task.getDescription());

        if (task.getPriority() != null) {
            oldTask.setPriority(task.getPriority());
        } else {
            oldTask.setPriority(properties.getDefaultPriority());
        }

        if (task.getStatus() != null) {
            oldTask.setStatus(task.getStatus());
        }

        return Optional.of(repository.save(oldTask));
    }

    @Override
    public Task updateStatus(Long id, Task.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Поле status обязательно");
        }

        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setStatus(status);
        return repository.save(task);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }

        repository.deleteById(id);
    }

    @Override
    public Map<String, Long> getStats() {
        Map<String, Long> result = new LinkedHashMap<>();


        Arrays.stream(Task.Status.values())
                .forEach(status -> result.put(status.name(), 0L));


        for (Object[] row : repository.countGroupedByStatus()) {
            Task.Status status = (Task.Status) row[0];
            Long count = (Long) row[1];
            result.put(status.name(), count);
        }

        return result;
    }

    @Override
    public List<Task> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword не может быть пустым");
        }

        return repository.searchByKeyword(keyword);
    }

    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Тело запроса не передано");
        }

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Название задачи не может быть пустым");
        }
    }

    private Task.Status parseStatus(String status) {
        try {
            return Task.Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Неизвестный status: " + status +
                            ". Допустимо: NEW, IN_PROGRESS, DONE");
        }
    }

    private Task.Priority parsePriority(String priority) {
        try {
            return Task.Priority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Неизвестный priority: " + priority +
                            ". Допустимо: LOW, MEDIUM, HIGH");
        }
    }

    @Override
    public List<Task> getMyTasks(String email) {
        return repository.findByOwnerEmail(email);
    }
}
