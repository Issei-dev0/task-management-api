package com.example.taskapi.repository;

import com.example.taskapi.Task;
import com.example.taskapi.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    Page<Task> findByTitleContainingIgnoreCaseAndStatus(String title, TaskStatus status, Pageable pageable);
}
