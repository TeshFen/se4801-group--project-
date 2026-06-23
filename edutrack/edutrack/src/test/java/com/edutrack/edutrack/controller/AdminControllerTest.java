package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_ShouldReturn200() throws Exception {
        when(userService.getAllUsers(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new UserDTO())));
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void toggleUserStatus_ShouldReturn200() throws Exception {
        doNothing().when(userService).toggleUserStatus(1L, false);
        mockMvc.perform(patch("/api/admin/users/1/disable?enable=false"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllUsers_AsStudent_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}