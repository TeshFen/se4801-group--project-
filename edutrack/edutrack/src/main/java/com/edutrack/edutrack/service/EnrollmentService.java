package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.response.EnrollmentDTO;
import com.edutrack.edutrack.entity.Course;
import com.edutrack.edutrack.entity.Enrollment;
import com.edutrack.edutrack.entity.User;
import com.edutrack.edutrack.mapper.EnrollmentMapper;
import com.edutrack.edutrack.repository.CourseRepository;
import com.edutrack.edutrack.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Transactional
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        // Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        // Check if already enrolled
        if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
            throw new IllegalStateException("Student already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(courseId)
                .status(Enrollment.Status.ACTIVE)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        return enrollmentMapper.toDTO(saved);
    }

    @Transactional
    public void dropEnrollment(Long enrollmentId, Long studentId, boolean isAdmin) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + enrollmentId));

        if (!isAdmin && !enrollment.getStudentId().equals(studentId)) {
            throw new IllegalStateException("You can only drop your own enrollments");
        }

        enrollmentRepository.updateStatus(enrollmentId, Enrollment.Status.DROPPED);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> getEnrollmentsByStudent(Long studentId, Pageable pageable) {
        return enrollmentRepository.findByStudentId(studentId, pageable)
                .map(enrollmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<EnrollmentDTO> getEnrollmentsByCourse(Long courseId, Pageable pageable) {
        return enrollmentRepository.findByCourseId(courseId, pageable)
                .map(enrollmentMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(
                studentId, courseId, Enrollment.Status.ACTIVE);
    }
}
