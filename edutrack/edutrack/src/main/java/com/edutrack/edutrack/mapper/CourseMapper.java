package com.edutrack.edutrack.mapper;

import com.edutrack.edutrack.dto.request.CreateCourseRequest;
import com.edutrack.edutrack.dto.response.CourseDTO;
import com.edutrack.edutrack.entity.Course;
import com.edutrack.edutrack.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseDTO toDTO(Course course) {
        if (course == null) return null;

        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .instructorId(course.getInstructorId())
                .instructorName(course.getInstructor() != null ? course.getInstructor().getFullName() : null)
                .build();
    }

    public Course toEntity(CreateCourseRequest request, Long instructorId) {
        if (request == null) return null;

        return Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .instructorId(instructorId)
                .build();
    }
}
