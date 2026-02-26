package com.example.taskapi.controller;

import com.example.taskapi.Task;
import com.example.taskapi.TaskStatus;
import com.example.taskapi.dto.TaskCreateRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.dto.TaskUpdateRequest;
import com.example.taskapi.service.TaskService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ✅ 作成（ADMINのみ）
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody TaskCreateRequest req)
    {
        Task task = new Task();
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setDueDate(req.getDueDate());
        task.setStatus(TaskStatus.TODO);

        Task saved = taskService.create(task);
        return toResponse(saved);
    }

    // ✅ 一覧（ページング）
    // /api/tasks?page=0&size=10&sort=createdAt,desc
    @GetMapping
    public Page<TaskResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) TaskStatus status,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt")Pageable pageable
    ) {
        return taskService.search(q, status, pageable)
                .map(this::toResponse);
    }


    // ✅ 詳細
    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable Long id) {
        return toResponse(taskService.getById(id));
    }

    // ✅ 更新（ADMINのみ）
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse update(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest req) {
        Task task = taskService.getById(id);

        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        if (req.getStatus() != null) {
            task.setStatus(req.getStatus());
        }
        task.setDueDate(req.getDueDate());

        Task saved = taskService.update(task);
        return toResponse(saved);
    }

    // ✅ 削除（ADMINのみ）
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        // 存在しない場合も404にしたいなら、先にgetByIdしてからdeleteする
        taskService.getById(id);
        taskService.delete(id);
    }

    // --- Entity -> Response DTO 変換 ---
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
}
