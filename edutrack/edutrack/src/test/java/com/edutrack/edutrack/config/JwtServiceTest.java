package com.edutrack.edutrack.config;

import com.edutrack.edutrack.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    void extractClaim_ShouldWork() {
        UserDetails userDetails = User.builder()
                .email("test@ex.com")
                .password("pass")
                .role(User.Role.STUDENT)
                .build();
        String token = jwtService.generateToken(userDetails);
        String email = jwtService.extractClaim(token, Claims::getSubject);
        assertThat(email).isEqualTo("test@ex.com");
    }

    @Test
    void generateAndValidateToken_ShouldWork() {
        UserDetails userDetails = User.builder()
                .email("test@example.com")
                .password("pass")
                .role(User.Role.STUDENT)
                .build();

        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotEmpty();

        String email = jwtService.extractEmail(token);
        assertThat(email).isEqualTo("test@example.com");

        boolean isValid = jwtService.isTokenValid(token, userDetails);
        assertThat(isValid).isTrue();
    }
}