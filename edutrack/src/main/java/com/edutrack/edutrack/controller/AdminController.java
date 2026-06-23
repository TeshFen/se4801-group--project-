package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-auth")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Get all users (admin only)")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PatchMapping("/users/{userId}/disable")
    @Operation(summary = "Disable or enable a user")
    public ResponseEntity<Void> toggleUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean enable) {
        userService.toggleUserStatus(userId, enable);
        return ResponseEntity.ok().build();
    }
}
