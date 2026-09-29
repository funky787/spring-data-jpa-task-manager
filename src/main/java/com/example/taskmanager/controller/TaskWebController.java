package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/tasks")
public class TaskWebController {

    private final TaskService taskService;

    public TaskWebController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping
    public String list(Model model) {
        model.addAttribute(
                "tasks",
                taskService.getAll(null, null)
        );

        return "tasks/list";
    }


    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model
    ) {

        Task task = taskService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Задача не найдена")
                );

        model.addAttribute("task", task);

        return "tasks/detail";
    }


    @GetMapping("/new")
    public String newForm(Model model) {

        model.addAttribute(
                "taskRequest",
                new TaskRequest()
        );

        model.addAttribute(
                "priorities",
                Task.Priority.values()
        );

        model.addAttribute(
                "statuses",
                Task.Status.values()
        );

        return "tasks/form";
    }


    @PostMapping
    public String create(
            @Valid
            @ModelAttribute("taskRequest")
            TaskRequest request,

            BindingResult result,

            Authentication authentication,

            Model model
    ) {


        if (result.hasErrors()) {

            model.addAttribute(
                    "priorities",
                    Task.Priority.values()
            );

            model.addAttribute(
                    "statuses",
                    Task.Status.values()
            );

            return "tasks/form";
        }


        taskService.create(
                request.toTask(),
                authentication.getName()
        );

        return "redirect:/web/tasks";
    }


    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model
    ) {

        Task task = taskService.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Задача не найдена")
                );

        model.addAttribute(
                "taskRequest",
                new TaskRequest(task)
        );

        model.addAttribute(
                "taskId",
                id
        );

        model.addAttribute(
                "priorities",
                Task.Priority.values()
        );

        model.addAttribute(
                "statuses",
                Task.Status.values()
        );

        return "tasks/form";
    }


    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,

            @Valid
            @ModelAttribute("taskRequest")
            TaskRequest request,

            BindingResult result,

            Model model
    ) {


        if (result.hasErrors()) {

            model.addAttribute(
                    "taskId",
                    id
            );

            model.addAttribute(
                    "priorities",
                    Task.Priority.values()
            );

            model.addAttribute(
                    "statuses",
                    Task.Status.values()
            );

            return "tasks/form";
        }

        taskService.update(
                id,
                request.toTask()
        );

        return "redirect:/web/tasks/" + id;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id
    ) {

        taskService.delete(id);

        return "redirect:/web/tasks";
    }
}