package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.request.GradeRequest;
import com.edutrack.edutrack.dto.response.SubmissionDTO;
import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.service.SubmissionService;
import com.edutrack.edutrack.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SubmissionService submissionService;
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "STUDENT")
    void submitAssignment_ShouldReturn201() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(submissionService.submitAssignment(any(), any(), any())).thenReturn(new SubmissionDTO());

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());

        mockMvc.perform(multipart("/api/assignments/1/submissions")
                .file(file))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void gradeSubmission_ShouldReturn200() throws Exception {
        GradeRequest request = new GradeRequest();
        request.setGrade(85.0);
        request.setFeedback("Good");

        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(submissionService.gradeSubmission(any(), any(), any())).thenReturn(new SubmissionDTO());

        mockMvc.perform(patch("/api/submissions/1/grade")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getMySubmissions_ShouldReturn200() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(submissionService.getSubmissionsByStudent(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/submissions/me"))
                .andExpect(status().isOk());
    }
}