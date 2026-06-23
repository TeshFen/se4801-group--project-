package com.edutrack.edutrack.mapper;

import com.edutrack.edutrack.dto.request.CreateAssignmentRequest;
import com.edutrack.edutrack.dto.response.AssignmentDTO;
import com.edutrack.edutrack.entity.Assignment;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {
    public AssignmentDTO toDTO(Assignment assignment) {
        if (assignment == null) return null;
        return AssignmentDTO.builder()
                .id(assignment.getId())
                .title(assignment.getTitle())
                .description(assignment.getDescription())
                .deadline(assignment.getDeadline())
                .maxScore(assignment.getMaxScore())
                .courseId(assignment.getCourseId())
                .build();
    }

    public Assignment toEntity(CreateAssignmentRequest request, Long courseId) {
        if (request == null) return null;
        return Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .maxScore(request.getMaxScore())
                .courseId(courseId)
                .build();
    }
}
