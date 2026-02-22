package com.example.taskapi.service;

import com.example.taskapi.Task;
import com.example.taskapi.exception.ResourceNotFoundException;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void getById_throwsResourceNotFound_whenNotExists() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getById(999L)
        );

        assertTrue(ex.getMessage().contains("999"));
        verify(taskRepository).findById(999L);
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void create_callsSave_andReturnsSavedEntity() {
        Task input = new Task();
        input.setTitle("Write report");

        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Write report");

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.create(input);

        assertEquals(1L, result.getId());
        assertEquals("Write report", result.getTitle());

        // saveに渡した中身も確認（実務っぽい）
        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertEquals("Write report", captor.getValue().getTitle());

        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void delete_callsDeleteById() {
        doNothing().when(taskRepository).deleteById(10L);

        taskService.delete(10L);

        verify(taskRepository).deleteById(10L);
        verifyNoMoreInteractions(taskRepository);
    }
}