package com.edutrack.edutrack.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PlagiarismServiceTest {

    @InjectMocks
    private PlagiarismService plagiarismService;

    @Test
    void calculateSimilarity_ReturnsValueBetween0And1() {
        double score = plagiarismService.calculateSimilarity("someFile.txt");
        assertThat(score).isBetween(0.0, 1.0);
    }
}