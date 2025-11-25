package com.smarttaskmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NegativeCasesTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createTaskWithoutAuthShouldReturn401() throws Exception {
        Map<String, String> task = Map.of("title", "NoAuth", "description", "no auth");
        String taskJson = objectMapper.writeValueAsString(task);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void loginWithInvalidCredentialsReturns401() throws Exception {
        Map<String, String> login = Map.of("username", "notreal", "password", "bad");
        String loginJson = objectMapper.writeValueAsString(login);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("invalid_credentials"));
    }

    @Test
    public void registerExistingUserReturnsBadRequest() throws Exception {
        // Register admin again (DataInitializer creates admin)
        Map<String, String> reg = Map.of("username", "admin", "password", "admin");
        String regJson = objectMapper.writeValueAsString(reg);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(regJson))
                .andExpect(status().isBadRequest());
    }
}
