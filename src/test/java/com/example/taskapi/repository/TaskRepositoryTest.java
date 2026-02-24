package com.example.taskapi.repository;

import com.example.taskapi.Task;
import com.example.taskapi.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

    @Test
    void findByTitleContainingIgnoreCase_returnsMatched() {
        taskRepository.save(make("Write report", TaskStatus.TODO));
        taskRepository.save(make("Buy milk", TaskStatus.TODO));
        taskRepository.save(make("REPORT review", TaskStatus.DONE));

        Page<Task> page = taskRepository.findByTitleContainingIgnoreCase(
                "report",
                PageRequest.of(0, 10)
        );

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().allMatch(t ->
                t.getTitle().toLowerCase().contains("report")
        ));
    }

    @Test
    void findByStatus_returnsMatched() {
        taskRepository.save(make("Write report", TaskStatus.TODO));
        taskRepository.save(make("Buy milk", TaskStatus.TODO));
        taskRepository.save(make("Cleanup", TaskStatus.DONE));

        Page<Task> page = taskRepository.findByStatus(
                TaskStatus.DONE,
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
        assertEquals(TaskStatus.DONE, page.getContent().get(0).getStatus());
        assertEquals("Cleanup", page.getContent().get(0).getTitle());
    }

    @Test
    void findByTitleContainingIgnoreCaseAndStatus_returnsMatched() {
        taskRepository.save(make("Write report", TaskStatus.TODO));
        taskRepository.save(make("REPORT review", TaskStatus.DONE));
        taskRepository.save(make("Report draft", TaskStatus.TODO));
        taskRepository.save(make("Buy milk", TaskStatus.TODO));

        Page<Task> page = taskRepository.findByTitleContainingIgnoreCaseAndStatus(
                "report",
                TaskStatus.TODO,
                PageRequest.of(0, 10)
        );

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().allMatch(t ->
                t.getTitle().toLowerCase().contains("report") && t.getStatus() == TaskStatus.TODO
        ));
    }

    private Task make(String title, TaskStatus status) {
        Task t = new Task();
        t.setTitle(title);
        t.setDescription("desc");
        t.setStatus(status);
        t.setDueDate(LocalDateTime.now().plusDays(1));
        return t;
    }
}
//クエリの正当性確認
//DBアクセス検証