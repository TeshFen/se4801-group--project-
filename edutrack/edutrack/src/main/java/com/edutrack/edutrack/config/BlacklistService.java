package com.edutrack.edutrack.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BlacklistService {

    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();
    private final JwtService jwtService;

    public void blacklist(String token) {
        long expiry = jwtService.extractClaim(token, claims -> claims.getExpiration().getTime());
        long ttl = expiry - System.currentTimeMillis();
        if (ttl > 0) {
            blacklist.put(token, ttl);
        }
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    // Optional: Cleanup expired entries (can be called by a scheduled task)
    public void cleanupExpired() {
        blacklist.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }
}
