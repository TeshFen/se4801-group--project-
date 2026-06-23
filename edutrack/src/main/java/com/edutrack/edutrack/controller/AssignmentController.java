package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.request.CreateAssignmentRequest;
import com.edutrack.edutrack.dto.request.UpdateAssignmentRequest;
import com.edutrack.edutrack.dto.response.AssignmentDTO;
import com.edutrack.edutrack.service.AssignmentService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-auth")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping("/courses/{courseId}/assignments")
    @Operation(summary = "Get assignments for a course")
    public ResponseEntity<Page<AssignmentDTO>> getAssignmentsByCourse(
            @PathVariable Long courseId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserDetails currentUser) {

        Long currentUserId = userService.getCurrentUser().getId();
        boolean isInstructor = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

        // Check if user is enrolled or is the instructor
        if (!isInstructor && !enrollmentService.isStudentEnrolled(currentUserId, courseId)) {
            // Check if user is the course instructor
            courseService.getCourseById(courseId); // Throws if not found
        }

        return ResponseEntity.ok(assignmentService.getAssignmentsByCourse(courseId, pageable));
    }

    @PostMapping("/courses/{courseId}/assignments")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Create an assignment for a course")
    public ResponseEntity<AssignmentDTO> createAssignment(
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAssignmentRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        Long instructorId = userService.getCurrentUser().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentService.createAssignment(courseId, request, instructorId));
    }

    @PutMapping("/assignments/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Update an assignment")
    public ResponseEntity<AssignmentDTO> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAssignmentRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long instructorId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(assignmentService.updateAssignment(id, request, instructorId));
    }

    @DeleteMapping("/assignments/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Delete an assignment")
    public ResponseEntity<Void> deleteAssignment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long instructorId = userService.getCurrentUser().getId();
        assignmentService.deleteAssignment(id, instructorId);
        return ResponseEntity.noContent().build();
    }
}
