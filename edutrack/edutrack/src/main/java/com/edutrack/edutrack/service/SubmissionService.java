package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.request.GradeRequest;
import com.edutrack.edutrack.dto.response.SubmissionDTO;
import com.edutrack.edutrack.entity.Assignment;
import com.edutrack.edutrack.entity.Submission;
import com.edutrack.edutrack.mapper.SubmissionMapper;
import com.edutrack.edutrack.repository.AssignmentRepository;
import com.edutrack.edutrack.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final EnrollmentService enrollmentService;
    private final PlagiarismService plagiarismService;
    private final SubmissionMapper submissionMapper;

    @Transactional
    public SubmissionDTO submitAssignment(Long assignmentId, Long studentId, String fileUrl) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        // Check enrollment
        if (!enrollmentService.isStudentEnrolled(studentId, assignment.getCourseId())) {
            throw new IllegalStateException("You must be enrolled in the course to submit assignments");
        }

        // Check if already submitted
        if (submissionRepository.findByStudentIdAndAssignmentId(studentId, assignmentId).isPresent()) {
            throw new IllegalStateException("You have already submitted this assignment");
        }

        // Check deadline
        if (LocalDateTime.now().isAfter(assignment.getDeadline())) {
            throw new IllegalStateException("Submission deadline has passed");
        }

        Submission submission = Submission.builder()
                .studentId(studentId)
                .assignmentId(assignmentId)
                .fileUrl(fileUrl)
                .similarityScore(plagiarismService.calculateSimilarity(fileUrl))
                .build();

        Submission saved = submissionRepository.save(submission);
        return submissionMapper.toDTO(saved);
    }

    @Transactional
    public SubmissionDTO gradeSubmission(Long submissionId, GradeRequest request, Long instructorId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        Assignment assignment = assignmentRepository.findById(submission.getAssignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        // Check if instructor owns the course
        if (!assignment.getCourseId().equals(instructorId)) {
            throw new IllegalStateException("You can only grade submissions for your own courses");
        }

        submissionRepository.updateGrade(submissionId, request.getGrade(), request.getFeedback());

        Submission updated = submissionRepository.findById(submissionId).orElseThrow();
        return submissionMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public Page<SubmissionDTO> getSubmissionsByAssignment(Long assignmentId, Pageable pageable) {
        return submissionRepository.findByAssignmentId(assignmentId, pageable)
                .map(submissionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<SubmissionDTO> getSubmissionsByStudent(Long studentId, Pageable pageable) {
        return submissionRepository.findByStudentId(studentId, pageable)
                .map(submissionMapper::toDTO);
    }
}
