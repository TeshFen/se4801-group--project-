package com.edutrack.edutrack.repository;

import com.edutrack.edutrack.entity.Course;
import com.edutrack.edutrack.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    private User instructor;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/edutrack");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
    }

    @BeforeEach
    void setUp() {
        instructor = userRepository.save(User.builder()
                .email("inst@test.com")
                .password("pass")
                .fullName("Instructor")
                .role(User.Role.INSTRUCTOR)
                .isActive(true)
                .build());
    }

    @Test
    void shouldSaveAndFindCourse() {
        Course course = Course.builder()
                .title("Java")
                .description("Learn Java")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .instructorId(instructor.getId())
                .build();
        course = courseRepository.save(course);

        var found = courseRepository.findById(course.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Java");
    }

    @Test
    void shouldSearchCoursesWithSpecification() {
        Course course = Course.builder()
                .title("Spring Boot")
                .description("REST API")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .instructorId(instructor.getId())
                .build();
        courseRepository.save(course);

        var result = courseRepository.findAll(PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isGreaterThan(0);
    }
}