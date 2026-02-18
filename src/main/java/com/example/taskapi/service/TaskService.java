package com.example.taskapi.service;

import com.example.taskapi.Task;
import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.taskapi.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) {
        log.info("Creating task title={}", task.getTitle());
        return taskRepository.save(task);
    }

    // ✅ 追加：ページング版（Controllerは今後こっちを使う）
    public Page<Task> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    // （任意）残してOK。将来消してもいい
    public List<Task> findAll() {return taskRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public Task update(Task task) {
        log.info("Updating task id={}", task.getId());
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        log.info("Deleting task id={}", id);
        taskRepository.deleteById(id);
    }

    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));
    }

    public Page<Task> search(String q, TaskStatus status, Pageable pageable) {

        boolean hasQ = (q != null && !q.isBlank());
        boolean hasStatus = (status != null);

        if (hasQ && hasStatus) {
            return taskRepository.findByTitleContainingIgnoreCaseAndStatus(q, status, pageable);
        }
        if (hasQ) {
            return taskRepository.findByTitleContainingIgnoreCase(q, pageable);
        }
        if (hasStatus) {
            return taskRepository.findByStatus(status, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);


}
