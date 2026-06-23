package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.request.GradeRequest;
import com.edutrack.edutrack.dto.response.SubmissionDTO;
import com.edutrack.edutrack.entity.Assignment;
import com.edutrack.edutrack.entity.Submission;
import com.edutrack.edutrack.mapper.SubmissionMapper;
import com.edutrack.edutrack.repository.AssignmentRepository;
import com.edutrack.edutrack.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private PlagiarismService plagiarismService;
    @Mock
    private SubmissionMapper submissionMapper;
    @InjectMocks
    private SubmissionService submissionService;

    private Assignment assignment;
    private Submission submission;
    private SubmissionDTO submissionDTO;
    private GradeRequest gradeRequest;

    @BeforeEach
    void setUp() {
        assignment = Assignment.builder()
                .id(1L)
                .title("Assignment 1")
                .deadline(LocalDateTime.now().plusDays(7))
                .maxScore(100)
                .courseId(1L)
                .build();
        submission = Submission.builder()
                .id(1L)
                .studentId(1L)
                .assignmentId(1L)
                .fileUrl("uploads/file.txt")
                .submittedAt(LocalDateTime.now())
                .grade(null)
                .feedback(null)
                .similarityScore(0.1)
                .build();
        submissionDTO = SubmissionDTO.builder()
                .id(1L)
                .studentId(1L)
                .assignmentId(1L)
                .fileUrl("uploads/file.txt")
                .build();
        gradeRequest = new GradeRequest();
        gradeRequest.setGrade(85.0);
        gradeRequest.setFeedback("Good work");
    }

    @Test
    void submitAssignment_ShouldSucceed() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(enrollmentService.isStudentEnrolled(1L, 1L)).thenReturn(true);
        when(submissionRepository.findByStudentIdAndAssignmentId(1L, 1L)).thenReturn(Optional.empty());
        when(plagiarismService.calculateSimilarity("uploads/file.txt")).thenReturn(0.1);
        when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        when(submissionMapper.toDTO(submission)).thenReturn(submissionDTO);

        SubmissionDTO result = submissionService.submitAssignment(1L, 1L, "uploads/file.txt");
        assertThat(result).isNotNull();
        verify(submissionRepository).save(any(Submission.class));
    }

    @Test
    void submitAssignment_WhenNotEnrolled_ShouldThrow() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(enrollmentService.isStudentEnrolled(1L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> submissionService.submitAssignment(1L, 1L, "file.txt"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void submitAssignment_WhenAlreadySubmitted_ShouldThrow() {
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        when(enrollmentService.isStudentEnrolled(1L, 1L)).thenReturn(true);
        when(submissionRepository.findByStudentIdAndAssignmentId(1L, 1L)).thenReturn(Optional.of(submission));

        assertThatThrownBy(() -> submissionService.submitAssignment(1L, 1L, "file.txt"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void gradeSubmission_ShouldSucceed() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
        doNothing().when(submissionRepository).updateGrade(1L, 85.0, "Good work");
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(submissionMapper.toDTO(submission)).thenReturn(submissionDTO);

        SubmissionDTO result = submissionService.gradeSubmission(1L, gradeRequest, 1L);
        assertThat(result).isNotNull();
        verify(submissionRepository).updateGrade(1L, 85.0, "Good work");
    }

    @Test
    void gradeSubmission_WhenAssignmentNotFound_ShouldThrow() {
        when(submissionRepository.findById(1L)).thenReturn(Optional.of(submission));
        when(assignmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> submissionService.gradeSubmission(1L, gradeRequest, 1L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}