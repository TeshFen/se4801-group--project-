package com.edutrack.edutrack.config;

import com.edutrack.edutrack.dto.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void handleNotFound() {
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(new EntityNotFoundException("Not found"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).contains("Not found");
    }

    @Test
    void handleAccessDenied() {
        ResponseEntity<ErrorResponse> response = handler.handleAccessDenied(new AccessDeniedException("Denied"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handleBadCredentials() {
        ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(new BadCredentialsException("bad"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void handleIllegalState() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalState(new IllegalStateException("state"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleMaxSizeException() {
        ResponseEntity<ErrorResponse> response = handler
                .handleMaxSizeException(new MaxUploadSizeExceededException(1024));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleGeneric() {
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(new RuntimeException("generic"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}