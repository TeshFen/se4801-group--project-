package com.edutrack.edutrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long assignmentId;
    private String assignmentTitle;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private Double grade;
    private String feedback;
    private Double similarityScore;
}
