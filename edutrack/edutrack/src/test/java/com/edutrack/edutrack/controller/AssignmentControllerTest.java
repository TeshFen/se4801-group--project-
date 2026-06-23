package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.request.CreateAssignmentRequest;
import com.edutrack.edutrack.dto.request.UpdateAssignmentRequest;
import com.edutrack.edutrack.dto.response.AssignmentDTO;
import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.service.AssignmentService;
import com.edutrack.edutrack.service.CourseService;
import com.edutrack.edutrack.service.EnrollmentService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AssignmentService assignmentService;
    @MockBean
    private CourseService courseService;
    @MockBean
    private EnrollmentService enrollmentService;
    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAssignmentsByCourse_ShouldReturn200() throws Exception {
        when(enrollmentService.isStudentEnrolled(any(), any())).thenReturn(true);
        when(assignmentService.getAssignmentsByCourse(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        // Mock the current user to avoid NPE in the controller
        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);

        mockMvc.perform(get("/api/courses/1/assignments"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void createAssignment_ShouldReturn201() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setTitle("New Assignment");
        request.setDescription("Desc");
        request.setDeadline(LocalDateTime.now().plusDays(7));
        request.setMaxScore(100);

        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(assignmentService.createAssignment(any(), any(), any())).thenReturn(new AssignmentDTO());

        mockMvc.perform(post("/api/courses/1/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void updateAssignment_ShouldReturn200() throws Exception {
        UpdateAssignmentRequest request = new UpdateAssignmentRequest();
        request.setTitle("Updated");

        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(assignmentService.updateAssignment(any(), any(), any())).thenReturn(new AssignmentDTO());

        mockMvc.perform(put("/api/assignments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void deleteAssignment_ShouldReturn204() throws Exception {
        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);

        mockMvc.perform(delete("/api/assignments/1"))
                .andExpect(status().isNoContent());
    }
}