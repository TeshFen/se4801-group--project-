package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.request.GradeRequest;
import com.edutrack.edutrack.dto.response.SubmissionDTO;
import com.edutrack.edutrack.service.SubmissionService;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-auth")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserService userService;

    @PostMapping("/assignments/{assignmentId}/submissions")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit an assignment")
    public ResponseEntity<SubmissionDTO> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails currentUser) {

        Long studentId = userService.getCurrentUser().getId();
        // In a real implementation, you would upload the file to cloud storage
        // and get a file URL. Here we simulate with a placeholder.
        String fileUrl = "/uploads/" + file.getOriginalFilename();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(submissionService.submitAssignment(assignmentId, studentId, fileUrl));
    }

    @PatchMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Operation(summary = "Grade a submission")
    public ResponseEntity<SubmissionDTO> gradeSubmission(
            @PathVariable Long submissionId,
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long instructorId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(submissionService.gradeSubmission(submissionId, request, instructorId));
    }

    @GetMapping("/submissions/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get my submissions")
    public ResponseEntity<Page<SubmissionDTO>> getMySubmissions(
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserDetails currentUser) {
        Long studentId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(submissionService.getSubmissionsByStudent(studentId, pageable));
    }
}
