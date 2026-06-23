package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.response.EnrollmentDTO;
import com.edutrack.edutrack.entity.Course;
import com.edutrack.edutrack.entity.Enrollment;
import com.edutrack.edutrack.mapper.EnrollmentMapper;
import com.edutrack.edutrack.repository.CourseRepository;
import com.edutrack.edutrack.repository.EnrollmentRepository;
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
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentMapper enrollmentMapper;
    @InjectMocks
    private EnrollmentService enrollmentService;

    private Course course;
    private Enrollment enrollment;
    private EnrollmentDTO enrollmentDTO;

    @BeforeEach
    void setUp() {
        course = Course.builder().id(1L).title("Test Course").build();
        enrollment = Enrollment.builder()
                .id(1L)
                .studentId(1L)
                .courseId(1L)
                .enrolledAt(LocalDateTime.now())
                .status(Enrollment.Status.ACTIVE)
                .build();
        enrollmentDTO = EnrollmentDTO.builder()
                .id(1L)
                .studentId(1L)
                .courseId(1L)
                .status("ACTIVE")
                .build();
    }

    @Test
    void enrollStudent_ShouldSucceed() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);
        when(enrollmentMapper.toDTO(enrollment)).thenReturn(enrollmentDTO);

        EnrollmentDTO result = enrollmentService.enrollStudent(1L, 1L);
        assertThat(result).isNotNull();
        assertThat(result.getStudentId()).isEqualTo(1L);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enrollStudent_WhenCourseNotFound_ShouldThrow() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void enrollStudent_WhenAlreadyEnrolled_ShouldThrow() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.enrollStudent(1L, 1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void dropEnrollment_ShouldSucceed() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        doNothing().when(enrollmentRepository).updateStatus(1L, Enrollment.Status.DROPPED);

        enrollmentService.dropEnrollment(1L, 1L, false);
        verify(enrollmentRepository).updateStatus(1L, Enrollment.Status.DROPPED);
    }

    @Test
    void dropEnrollment_WhenNotOwnerAndNotAdmin_ShouldThrow() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.dropEnrollment(1L, 99L, false))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void getEnrollmentsByStudent_ShouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Enrollment> enrollmentPage = new PageImpl<>(List.of(enrollment));
        when(enrollmentRepository.findByStudentId(1L, pageable)).thenReturn(enrollmentPage);
        when(enrollmentMapper.toDTO(enrollment)).thenReturn(enrollmentDTO);

        Page<EnrollmentDTO> result = enrollmentService.getEnrollmentsByStudent(1L, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void isStudentEnrolled_ShouldReturnTrue() {
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndStatus(1L, 1L, Enrollment.Status.ACTIVE))
                .thenReturn(true);

        boolean enrolled = enrollmentService.isStudentEnrolled(1L, 1L);
        assertThat(enrolled).isTrue();
    }

    @Test
    void getEnrollmentsByCourse_ShouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Enrollment> enrollmentPage = new PageImpl<>(List.of(enrollment));
        when(enrollmentRepository.findByCourseId(1L, pageable)).thenReturn(enrollmentPage);
        when(enrollmentMapper.toDTO(enrollment)).thenReturn(enrollmentDTO);

        Page<EnrollmentDTO> result = enrollmentService.getEnrollmentsByCourse(1L, pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}