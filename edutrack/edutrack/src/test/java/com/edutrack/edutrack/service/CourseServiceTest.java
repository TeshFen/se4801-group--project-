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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseMapper courseMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private CourseService courseService;

    private User instructor;
    private Course course;
    private CourseDTO courseDTO;
    private CreateCourseRequest createRequest;
    private UpdateCourseRequest updateRequest;

    @BeforeEach
    void setUp() {
        instructor = User.builder()
                .id(1L)
                .email("inst@test.com")
                .role(User.Role.INSTRUCTOR)
                .fullName("Instructor")
                .build();

        course = Course.builder()
                .id(1L)
                .title("Test Course")
                .description("Desc")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .instructorId(1L)
                .build();

        courseDTO = CourseDTO.builder()
                .id(1L)
                .title("Test Course")
                .instructorId(1L)
                .build();

        createRequest = new CreateCourseRequest();
        createRequest.setTitle("New Course");
        createRequest.setDescription("New Desc");
        createRequest.setStartDate(LocalDate.now());
        createRequest.setEndDate(LocalDate.now().plusMonths(3));

        updateRequest = new UpdateCourseRequest();
        updateRequest.setTitle("Updated Title");
    }

    @Test
    void createCourse_ShouldSucceed() {
        when(userService.getUserEntityById(1L)).thenReturn(instructor);
        when(courseMapper.toEntity(createRequest, 1L)).thenReturn(course);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(courseMapper.toDTO(course)).thenReturn(courseDTO);

        CourseDTO result = courseService.createCourse(createRequest, 1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Course");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_WithInvalidDates_ShouldThrow() {
        createRequest.setEndDate(LocalDate.now().minusDays(1));
        when(userService.getUserEntityById(1L)).thenReturn(instructor);

        assertThatThrownBy(() -> courseService.createCourse(createRequest, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("End date must be after start date");
    }

    @Test
    void getCourseById_WhenExists_ShouldReturn() {
        when(courseRepository.findByIdWithInstructor(1L)).thenReturn(Optional.of(course));
        when(courseMapper.toDTO(course)).thenReturn(courseDTO);

        CourseDTO result = courseService.getCourseById(1L);
        assertThat(result).isNotNull();
    }

    @Test
    void getCourseById_WhenNotExists_ShouldThrow() {
        when(courseRepository.findByIdWithInstructor(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteCourse_WhenOwner_ShouldDelete() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        doNothing().when(enrollmentRepository).deleteByCourseId(1L);
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L, 1L, false);

        verify(enrollmentRepository).deleteByCourseId(1L);
        verify(courseRepository).deleteById(1L);
    }

    @Test
    void deleteCourse_WhenNotOwner_ShouldThrow() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> courseService.deleteCourse(1L, 99L, false))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void updateCourse_WhenDatesInvalid_ShouldThrow() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        UpdateCourseRequest updateRequest = new UpdateCourseRequest();
        updateRequest.setEndDate(LocalDate.now().minusDays(1));
        assertThatThrownBy(() -> courseService.updateCourse(1L, updateRequest, 1L, false))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void searchCourses_ShouldWork() {
        Pageable pageable = PageRequest.of(0, 10);
        Specification<Course> spec = CourseSpecification.searchCourses("java", null, null);
        when(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(course)));
        when(courseMapper.toDTO(course)).thenReturn(courseDTO);
        Page<CourseDTO> result = courseService.searchCourses("java", null, null, pageable);
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }
}