package com.edutrack.edutrack.service;

import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.entity.User;
import com.edutrack.edutrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO register(String email, String password, String fullName, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
        return toDTO(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional
    public void toggleUserStatus(Long userId, boolean enable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.setActiveStatus(userId, enable);
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
