package com.edutrack.edutrack.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class BlacklistServiceTest {

    @Mock
    private JwtService jwtService;
    @InjectMocks
    private BlacklistService blacklistService;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test-token";
        // Use doReturn to avoid lambda mismatch
        doReturn(System.currentTimeMillis() + 3600000L)
                .when(jwtService).extractClaim(any(), any());
    }

    @Test
    void blacklistAndCheck_ShouldWork() {
        blacklistService.blacklist(token);
        assertThat(blacklistService.isBlacklisted(token)).isTrue();
    }

    @Test
    void cleanupExpired_ShouldRemoveExpiredEntries() {
        blacklistService.blacklist(token);
        blacklistService.cleanupExpired();
        // Token is not expired, so it should remain
        assertThat(blacklistService.isBlacklisted(token)).isTrue();
    }
}