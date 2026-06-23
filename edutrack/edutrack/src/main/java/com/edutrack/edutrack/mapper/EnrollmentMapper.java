package com.edutrack.edutrack.mapper;

import com.edutrack.edutrack.dto.response.EnrollmentDTO;
import com.edutrack.edutrack.entity.Enrollment;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentMapper {
    public EnrollmentDTO toDTO(Enrollment enrollment) {
        if (enrollment == null) return null;
        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .enrolledAt(enrollment.getEnrolledAt())
                .status(enrollment.getStatus().name())
                .build();
    }
}
