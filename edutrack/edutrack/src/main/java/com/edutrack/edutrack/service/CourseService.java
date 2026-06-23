package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.request.CreateCourseRequest;
import com.edutrack.edutrack.dto.request.UpdateCourseRequest;
import com.edutrack.edutrack.dto.response.CourseDTO;
import com.edutrack.edutrack.entity.Course;
import com.edutrack.edutrack.entity.User;
import com.edutrack.edutrack.mapper.CourseMapper;
import com.edutrack.edutrack.repository.CourseRepository;
import com.edutrack.edutrack.repository.EnrollmentRepository;
import com.edutrack.edutrack.specification.CourseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<CourseDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable).map(courseMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findByIdWithInstructor(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        return courseMapper.toDTO(course);
    }

    @Transactional
    public CourseDTO createCourse(CreateCourseRequest request, Long instructorId) {
        User instructor = userService.getUserEntityById(instructorId);
        if (instructor.getRole() != User.Role.INSTRUCTOR && instructor.getRole() != User.Role.ADMIN) {
            throw new IllegalStateException("Only instructors can create courses");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalStateException("End date must be after start date");
        }
        Course course = courseMapper.toEntity(request, instructorId);
        Course saved = courseRepository.save(course);
        return courseMapper.toDTO(saved);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, UpdateCourseRequest request, Long currentUserId, boolean isAdmin) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        if (!isAdmin && !course.getInstructorId().equals(currentUserId)) {
            throw new IllegalStateException("You can only update your own courses");
        }
        if (request.getTitle() != null)
            course.setTitle(request.getTitle());
        if (request.getDescription() != null)
            course.setDescription(request.getDescription());
        if (request.getStartDate() != null)
            course.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) {
            if (request.getEndDate().isBefore(course.getStartDate())) {
                throw new IllegalStateException("End date must be after start date");
            }
            course.setEndDate(request.getEndDate());
        }
        Course saved = courseRepository.save(course);
        return courseMapper.toDTO(saved);
    }

    @Transactional
    public void deleteCourse(Long id, Long currentUserId, boolean isAdmin) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + id));
        if (!isAdmin && !course.getInstructorId().equals(currentUserId)) {
            throw new IllegalStateException("You can only delete your own courses");
        }
        enrollmentRepository.deleteByCourseId(id);
        courseRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<CourseDTO> searchCourses(String keyword, Long instructorId, LocalDate minStartDate, Pageable pageable) {
        Specification<Course> spec = CourseSpecification.searchCourses(keyword, instructorId, minStartDate);
        return courseRepository.findAll(spec, pageable).map(courseMapper::toDTO);
    }
}