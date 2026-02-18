package com.example.taskapi.controller;

import com.example.taskapi.Task;
import com.example.taskapi.TaskStatus;
import com.example.taskapi.dto.TaskCreateRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.taskapi.dto.TaskUpdateRequest;


import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest req) {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setDueDate(req.getDueDate());
        task.setStatus(TaskStatus.TODO);

        Task saved = taskService.create(task);
        return toResponse(saved);
    }

    @GetMapping
    public List<TaskResponse> list() {
        return taskService.findAll().stream().map(this::toResponse).toList();
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse res = new TaskResponse();
        res.setId(task.getId());
        res.setTitle(task.getTitle());
        res.setDescription(task.getDescription());
        res.setStatus(task.getStatus());
        res.setDueDate(task.getDueDate());
        res.setCreatedAt(task.getCreatedAt());
        res.setUpdatedAt(task.getUpdatedAt());
        return res;
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id) {
        return toResponse(taskService.getById(id));
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest req) {
        Task task = taskService.getById(id);
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        task.setDueDate(req.getDueDate());

        return toResponse(taskService.update(task));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }



}
