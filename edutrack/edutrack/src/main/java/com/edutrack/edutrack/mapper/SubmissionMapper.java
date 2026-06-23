package com.edutrack.edutrack.mapper;

import com.edutrack.edutrack.dto.response.SubmissionDTO;
import com.edutrack.edutrack.entity.Submission;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {
    public SubmissionDTO toDTO(Submission submission) {
        if (submission == null) return null;
        return SubmissionDTO.builder()
                .id(submission.getId())
                .studentId(submission.getStudentId())
                .assignmentId(submission.getAssignmentId())
                .fileUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .similarityScore(submission.getSimilarityScore())
                .build();
    }
}
