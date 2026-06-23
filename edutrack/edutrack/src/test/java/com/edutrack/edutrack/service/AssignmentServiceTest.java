package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.request.CreateAssignmentRequest;
import com.edutrack.edutrack.dto.request.UpdateAssignmentRequest;
import com.edutrack.edutrack.dto.response.AssignmentDTO;
import com.edutrack.edutrack.entity.Assignment;
import com.edutrack.edutrack.entity.Course;
import com.edutrack.edutrack.mapper.AssignmentMapper;
import com.edutrack.edutrack.repository.AssignmentRepository;
import com.edutrack.edutrack.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private AssignmentMapper assignmentMapper;
    @InjectMocks
    private AssignmentService assignmentService;

    private Course course;
    private Assignment assignment;
    private AssignmentDTO assignmentDTO;
    private CreateAssignmentRequest createRequest;
    private UpdateAssignmentRequest updateRequest;

    @BeforeEach
    void setUp() {
        course = Course.builder().id(1L).title("Test Course").instructorId(1L).build();
        assignment = Assignment.builder()
                .id(1L)
                .title("Assignment 1")
                .description("Test")
                .deadline(LocalDateTime.now().plusDays(7))
                .maxScore(100)
                .courseId(1L)
                .build();
        assignmentDTO = AssignmentDTO.builder()
                .id(1L)
                .title("Assignment 1")
                .courseId(1L)
                .build();
        createRequest = new CreateAssignmentRequest();
        createRequest.setTitle("New Assignment");
        createRequest.setDescription("Desc");
        createRequest.setDeadline(LocalDateTime.now().plusDays(7));
        createRequest.setMaxScore(100);
        updateRequest = new UpdateAssignmentRequest();
        updateRequest.setTitle("Updated Title");
    }

    @Test
    void createAssignment_ShouldSucceed() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(assignmentMapper.toEntity(createRequest, 1L)).thenReturn(assignment);
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
        when(assignmentMapper.toDTO(assignment)).thenReturn(assignmentDTO);

        AssignmentDTO result = assignmentService.createAssignment(1L, createRequest, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Assignment 1");
        verify(assignmentRepository).save(any(Assignment.class));
    }

    @Test
    void createAssignment_WhenNotInstructor_ShouldThrow() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> assignmentService.createAssignment(1L, createRequest, 99L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateAssignment_ShouldSucceed() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);
        when(assignmentMapper.toDTO(assignment)).thenReturn(assignmentDTO);

        AssignmentDTO result = assignmentService.updateAssignment(1L, updateRequest, 1L);
        assertThat(result.getTitle()).isEqualTo("Assignment 1");
        verify(assignmentRepository).save(any(Assignment.class));
    }

    @Test
    void deleteAssignment_ShouldSucceed() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(assignmentRepository).deleteById(1L);

        assignmentService.deleteAssignment(1L, 1L);
        verify(assignmentRepository).deleteById(1L);
    }

    @Test
    void getAssignmentsByCourse_ShouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Assignment> assignmentPage = new PageImpl<>(List.of(assignment));
        when(assignmentRepository.findByCourseId(1L, pageable)).thenReturn(assignmentPage);
        when(assignmentMapper.toDTO(assignment)).thenReturn(assignmentDTO);

        Page<AssignmentDTO> result = assignmentService.getAssignmentsByCourse(1L, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void updateAssignment_WhenAssignmentNotFound_ShouldThrow() {
        when(assignmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.updateAssignment(99L, updateRequest, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}