package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.request.CreateCourseRequest;
import com.edutrack.edutrack.dto.request.UpdateCourseRequest;
import com.edutrack.edutrack.dto.response.CourseDTO;
import com.edutrack.edutrack.dto.response.EnrollmentDTO;
import com.edutrack.edutrack.service.CourseService;
import com.edutrack.edutrack.service.EnrollmentService;
import com.edutrack.edutrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-auth")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all courses with pagination")
    public ResponseEntity<Page<CourseDTO>> getAllCourses(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single course by ID")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Create a new course")
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(request, userId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Update an existing course")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCourseRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long userId = userService.getCurrentUser().getId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ResponseEntity.ok(courseService.updateCourse(id, request, userId, isAdmin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Operation(summary = "Delete a course")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long userId = userService.getCurrentUser().getId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        courseService.deleteCourse(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search courses with filters")
    public ResponseEntity<Page<CourseDTO>> searchCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate minStartDate,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(courseService.searchCourses(keyword, instructorId, minStartDate, pageable));
    }

    @PostMapping("/{courseId}/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Enroll in a course")
    public ResponseEntity<EnrollmentDTO> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long studentId = userService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enrollStudent(studentId, courseId));
    }
}
