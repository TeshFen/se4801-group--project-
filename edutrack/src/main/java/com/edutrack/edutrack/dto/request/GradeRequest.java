package com.edutrack.edutrack.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GradeRequest {
    @Min(value = 0, message = "Grade must be at least 0")
    @Max(value = 100, message = "Grade must not exceed 100")
    private Double grade;

    @Size(max = 500, message = "Feedback must not exceed 500 characters")
    private String feedback;
}
