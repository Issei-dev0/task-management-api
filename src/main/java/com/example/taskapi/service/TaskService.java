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
import com.example.taskapi.security.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;



@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task create(Task task) {
        String me = SecurityUtil.currentUsername();
        if (me == null) {
            throw new AccessDeniedException("Unauthenticated");
        }
        task.setOwnerUsername(me);
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
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));

        String me = SecurityUtil.currentUsername();

        // 未認証はここに来ない想定だが、念のため
        if (me == null) {
            throw new AccessDeniedException("Unauthenticated");
        }

        // ADMINは全許可。それ以外は所有者だけ
        if (!SecurityUtil.isAdmin() && !me.equals(task.getOwnerUsername())) {
            throw new AccessDeniedException("Not your task");
        }

        return task;
    }

    public Page<Task> search(String q, TaskStatus status, Pageable pageable) {

        String me = SecurityUtil.currentUsername();
        if (me == null) {
            throw new IllegalStateException("Authentication not found in security context");
        }

        boolean admin = SecurityUtil.isAdmin();
        boolean hasQ = (q != null && !q.isBlank());
        boolean hasStatus = (status != null);

        // ADMIN は全件検索（今まで通り）
        if (admin) {
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

        // USER は「自分のタスクだけ」
        if (hasQ && hasStatus) {
            return taskRepository.findByOwnerUsernameAndTitleContainingIgnoreCaseAndStatus(me, q, status, pageable);
        }
        if (hasQ) {
            return taskRepository.findByOwnerUsernameAndTitleContainingIgnoreCase(me, q, pageable);
        }
        if (hasStatus) {
            return taskRepository.findByOwnerUsernameAndStatus(me, status, pageable);
        }
        return taskRepository.findByOwnerUsername(me, pageable);
    }

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);


}
