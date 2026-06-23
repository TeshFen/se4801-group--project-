package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.entity.User;
import com.edutrack.edutrack.repository.UserRepository;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encoded")
                .fullName("Test User")
                .role(User.Role.STUDENT)
                .isActive(true)
                .build();
    }

    @Test
    void register_ShouldSucceed() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.register("test@example.com", "pass123", "Test User", User.Role.STUDENT);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WhenEmailExists_ShouldThrow() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("test@example.com", "pass123", "Test User", User.Role.STUDENT))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void getUserEntityById_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserEntityById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getUserEntityById_WhenNotFound_ShouldThrow() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserEntityById(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toggleUserStatus_ShouldWork() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).setActiveStatus(1L, false);

        userService.toggleUserStatus(1L, false);
        verify(userRepository).setActiveStatus(1L, false);
    }

    @Test
    void getCurrentUser_ShouldReturnUserDTO() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("pass")
                .authorities("ROLE_STUDENT")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDTO result = userService.getCurrentUser();
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> result = userService.getAllUsers(pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}