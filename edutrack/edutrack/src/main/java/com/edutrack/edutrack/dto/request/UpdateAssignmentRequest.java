package com.edutrack.edutrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateAssignmentRequest {
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @Future
    private LocalDateTime deadline;

    @Min(1)
    @Max(100)
    private Integer maxScore;
}
