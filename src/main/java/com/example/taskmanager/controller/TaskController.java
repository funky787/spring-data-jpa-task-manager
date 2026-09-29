package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;


    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }




    @GetMapping
    public Object getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {


        if (status != null || priority != null) {
            return taskService.getAll(status, priority);
        }


        if (page != null || size != null || sort != null) {

            int pageValue = page == null ? 0 : page;
            int sizeValue = size == null ? 10 : size;
            String sortValue = sort == null ? "id" : sort;

            return taskService.getAll(
                    pageValue,
                    sizeValue,
                    sortValue
            );
        }

        return taskService.getAll(null, null);
    }




    @GetMapping("/search")
    public List<Task> search(
            @RequestParam String keyword) {

        return taskService.search(keyword);
    }




    @GetMapping("/stats")
    public Map<String, Long> getStats() {

        return taskService.getStats();
    }




    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(
            @PathVariable Long id) {

        return taskService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }




    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<Task> create(
            @RequestBody Task task,
            Authentication authentication) {

        String email =
                authentication.getName();

        Task created =
                taskService.create(task, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }




    @PutMapping("/{id}")
    public ResponseEntity<Task> update(
            @PathVariable Long id,
            @RequestBody Task task) {

        return taskService.update(id, task)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }




    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String value = body.get("status");

        if (value == null) {
            throw new IllegalArgumentException(
                    "Поле status обязательно"
            );
        }

        Task.Status status;

        try {

            status = Task.Status.valueOf(
                    value.toUpperCase()
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalArgumentException(
                    "Неизвестный status: "
                            + value
                            + ". Допустимо: NEW, IN_PROGRESS, DONE"
            );
        }

        return ResponseEntity.ok(
                taskService.updateStatus(id, status)
        );
    }




    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        taskService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }




    @GetMapping("/my")
    public List<Task> getMyTasks(
            Authentication authentication) {

        String email =
                authentication.getName();

        return taskService.getMyTasks(email);
    }
}