package com.edutrack.edutrack.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateAssignmentRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Future
    private LocalDateTime deadline;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer maxScore;
}
