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
import static org.mockito.Mockito.verifyNoInteractions;
import com.example.taskapi.exception.ResourceNotFoundException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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

    @Test
    void create_returns400_whenTitleIsBlank() throws Exception {
        // serviceは呼ばれない想定だが、念のためstub不要（呼ばれたらテストで検知するならverifyする）
        String json =
                "{\n" +
                        "  \"title\": \"\",\n" +
                        "  \"description\": \"Finish monthly report\"\n" +
                        "}";

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details[0].field").value("title"));
                verifyNoInteractions(taskService);
    }

    @Test
    void get_returns404_whenTaskNotFound() throws Exception {
        // serviceが例外を投げる想定（Controllerはそれを受けて404になる）
        when(taskService.getById(999L))
                .thenThrow(new ResourceNotFoundException("Task not found: 999"));

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task not found: 999"));
    }
}
/*
HTTP 201が返るか
JSONの中身が正しいか
Serviceはモック化
→Webレイヤーの検証
 */