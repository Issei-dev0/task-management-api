package com.example.taskapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create_then_getById_works_endToEnd() throws Exception {
        // POST（title必須）
        String createJson = """
                {
                  "title": "Write report",
                  "description": "Finish monthly report"
                }
                """;

        String responseBody = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Write report"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(responseBody).get("id").asLong();

        // GET /{id}
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Write report"));
    }

    @Test
    void getById_returns404_whenNotFound_endToEnd() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", 999999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void create_returns400_whenTitleBlank_endToEnd() throws Exception {
        String json = """
                {
                  "title": "",
                  "description": "x"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray());
    }

    @Test
    void update_updatesPersistedData_endToEnd() throws Exception {
        // まず作成
        String createJson = """
            {
              "title": "Write report",
              "description": "Finish monthly report"
            }
            """;

        String createdBody = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(createdBody).get("id").asLong();

        // 更新（あなたのUpdate DTOに合わせてフィールドを調整）
        String updateJson = """
            {
              "title": "Write report UPDATED",
              "description": "Updated desc",
              "status": "DONE"
            }
            """;

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Write report UPDATED"))
                .andExpect(jsonPath("$.status").value("DONE"));

        // DBに反映されているか（GETで再確認）
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Write report UPDATED"))
                .andExpect(jsonPath("$.description").value("Updated desc"))
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void delete_then_getReturns404_endToEnd() throws Exception {
        // まず作成
        String createJson = """
            {
              "title": "Temp task",
              "description": "to be deleted"
            }
            """;

        String createdBody = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(createdBody).get("id").asLong();

        // 削除（204ならisNoContent、200ならisOkに変える）
        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());

        // 削除後は404
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
//全レイヤー統合テスト