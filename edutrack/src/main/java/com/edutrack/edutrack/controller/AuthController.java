package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.config.BlacklistService;
import com.edutrack.edutrack.config.JwtService;
import com.edutrack.edutrack.dto.request.LoginRequest;
import com.edutrack.edutrack.dto.request.RegisterRequest;
import com.edutrack.edutrack.dto.response.AuthResponse;
import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.entity.User;
import com.edutrack.edutrack.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BlacklistService blacklistService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = userService.register(
                request.getEmail(),
                request.getPassword(),
                request.getFullName(),
                request.getRole()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        // Extract role from user details
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .email(userDetails.getUsername())
                .role(role)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            blacklistService.blacklist(token);
        }
        return ResponseEntity.ok().build();
    }
}
