package com.example.taskapi.controller;

import com.example.taskapi.Task;
import com.example.taskapi.TaskStatus;
import com.example.taskapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskService taskService;

    @Test
    void create_returns201() throws Exception {
        Task saved = new Task();
        saved.setId(1L);
        saved.setTitle("Write report");
        saved.setDescription("Finish monthly report");
        saved.setStatus(TaskStatus.TODO);

        when(taskService.create(ArgumentMatchers.any(Task.class))).thenReturn(saved);

        String json =
                "{\n" +
                        "  \"title\": \"Write report\",\n" +
                        "  \"description\": \"Finish monthly report\"\n" +
                        "}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Write report"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }
}
