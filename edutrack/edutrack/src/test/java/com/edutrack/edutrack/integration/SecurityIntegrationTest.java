package com.edutrack.edutrack.integration;

import com.edutrack.edutrack.dto.request.LoginRequest;
import com.edutrack.edutrack.dto.request.RegisterRequest;
import com.edutrack.edutrack.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerAndLogin_ShouldWork() throws Exception {
        // Use a unique email to avoid conflicts with previous test runs
        String uniqueEmail = "test_" + System.currentTimeMillis() + "@example.com";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(uniqueEmail);
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setRole(User.Role.STUDENT);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(uniqueEmail);
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }
}