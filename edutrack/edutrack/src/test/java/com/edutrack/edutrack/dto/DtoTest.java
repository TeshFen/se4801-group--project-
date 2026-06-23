package com.edutrack.edutrack.dto;

import com.edutrack.edutrack.dto.request.*;
import com.edutrack.edutrack.dto.response.*;
import com.edutrack.edutrack.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DtoTest {

    @Test
    void testUserDTO() {
        UserDTO dto = UserDTO.builder()
                .id(1L).email("a@b.com").fullName("Name").role(User.Role.STUDENT)
                .isActive(true).createdAt(LocalDateTime.now()).build();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("a@b.com");
    }

    @Test
    void testCourseDTO() {
        CourseDTO dto = CourseDTO.builder()
                .id(1L).title("T").description("D").startDate(LocalDate.now())
                .endDate(LocalDate.now()).instructorId(1L).instructorName("I").build();
        assertThat(dto.getTitle()).isEqualTo("T");
    }

    @Test
    void testEnrollmentDTO() {
        EnrollmentDTO dto = EnrollmentDTO.builder()
                .id(1L).studentId(1L).studentName("S").courseId(1L).courseTitle("C")
                .enrolledAt(LocalDateTime.now()).status("ACTIVE").build();
        assertThat(dto.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void testAssignmentDTO() {
        AssignmentDTO dto = AssignmentDTO.builder()
                .id(1L).title("A").description("D").deadline(LocalDateTime.now())
                .maxScore(100).courseId(1L).courseTitle("C").build();
        assertThat(dto.getMaxScore()).isEqualTo(100);
    }

    @Test
    void testSubmissionDTO() {
        SubmissionDTO dto = SubmissionDTO.builder()
                .id(1L).studentId(1L).studentName("S").assignmentId(1L).assignmentTitle("A")
                .fileUrl("url").submittedAt(LocalDateTime.now()).grade(85.0)
                .feedback("good").similarityScore(0.2).build();
        assertThat(dto.getGrade()).isEqualTo(85.0);
    }

    @Test
    void testAuthResponse() {
        AuthResponse dto = AuthResponse.builder()
                .token("tok").email("a@b.com").role("STUDENT").build();
        assertThat(dto.getRole()).isEqualTo("STUDENT");
    }

    @Test
    void testErrorResponse() {
        ErrorResponse dto = ErrorResponse.builder()
                .timestamp(LocalDateTime.now()).status(400).error("Bad").message("msg").path("/").build();
        assertThat(dto.getStatus()).isEqualTo(400);
    }

    @Test
    void testRegisterRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("e");
        req.setPassword("p");
        req.setFullName("n");
        req.setRole(User.Role.ADMIN);
        assertThat(req.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void testLoginRequest() {
        LoginRequest req = new LoginRequest();
        req.setEmail("e");
        req.setPassword("p");
        assertThat(req.getEmail()).isEqualTo("e");
    }

    @Test
    void testCreateCourseRequest() {
        CreateCourseRequest req = new CreateCourseRequest();
        req.setTitle("T");
        req.setDescription("D");
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now());
        assertThat(req.getTitle()).isEqualTo("T");
    }

    @Test
    void testUpdateCourseRequest() {
        UpdateCourseRequest req = new UpdateCourseRequest();
        req.setTitle("T");
        req.setDescription("D");
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now());
        assertThat(req.getEndDate()).isNotNull();
    }

    @Test
    void testCreateAssignmentRequest() {
        CreateAssignmentRequest req = new CreateAssignmentRequest();
        req.setTitle("A");
        req.setDescription("D");
        req.setDeadline(LocalDateTime.now());
        req.setMaxScore(100);
        assertThat(req.getMaxScore()).isEqualTo(100);
    }

    @Test
    void testUpdateAssignmentRequest() {
        UpdateAssignmentRequest req = new UpdateAssignmentRequest();
        req.setTitle("A");
        req.setDescription("D");
        req.setDeadline(LocalDateTime.now());
        req.setMaxScore(90);
        assertThat(req.getMaxScore()).isEqualTo(90);
    }

    @Test
    void testGradeRequest() {
        GradeRequest req = new GradeRequest();
        req.setGrade(85.0);
        req.setFeedback("ok");
        assertThat(req.getFeedback()).isEqualTo("ok");
    }
}