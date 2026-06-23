package com.edutrack.edutrack.service;

import org.springframework.stereotype.Service;

@Service
public class PlagiarismService {
    // Simple placeholder – in real app compare with existing submissions
    public Double calculateSimilarity(String fileUrl) {
        // For now return random low similarity
        return Math.random() * 0.3; // 0-30%
    }
}
