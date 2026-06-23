package com.edutrack.edutrack.controller;

import com.edutrack.edutrack.config.BlacklistService;
import com.edutrack.edutrack.config.JwtService;
import com.edutrack.edutrack.dto.request.LoginRequest;
import com.edutrack.edutrack.dto.request.RegisterRequest;
import com.edutrack.edutrack.dto.response.UserDTO;
import com.edutrack.edutrack.entity.User;
import com.edutrack.edutrack.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private BlacklistService blacklistService;

    @Test
    void register_ShouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("pass123");
        request.setFullName("Test User");
        request.setRole(User.Role.STUDENT);

        UserDTO userDTO = UserDTO.builder().email("test@example.com").build();
        when(userService.register(any(), any(), any(), any())).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void login_ShouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("pass123");

        // Create a real UserDetails object
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("test@example.com")
                .password("pass123")
                .authorities("ROLE_STUDENT")
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void logout_WithoutToken_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }

    @Test
    void logout_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }
}