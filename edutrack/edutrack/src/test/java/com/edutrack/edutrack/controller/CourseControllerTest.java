package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.request.CreateCourseRequest;
import com.edutrack.edutrack.dto.response.CourseDTO;
import com.edutrack.edutrack.dto.response.UserDTO;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;
    @MockBean
    private EnrollmentService enrollmentService;
    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void createCourse_ShouldReturn201() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Spring Boot");
        request.setDescription("REST API");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(3));

        UserDTO userDTO = UserDTO.builder().id(1L).build();
        when(userService.getCurrentUser()).thenReturn(userDTO);
        when(courseService.createCourse(any(), any(Long.class))).thenReturn(new CourseDTO());

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void createCourse_AsStudent_ShouldReturn403() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Test");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(1));

        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllCourses_WithoutAuth_ShouldReturn403() throws Exception { // Changed from 401 to 403
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getAllCourses_WithAuth_ShouldReturn200() throws Exception {
        when(courseService.getAllCourses(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk());
    }
}