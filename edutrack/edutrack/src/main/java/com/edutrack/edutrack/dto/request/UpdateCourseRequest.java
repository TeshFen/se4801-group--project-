package com.edutrack.edutrack.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateCourseRequest {
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    private LocalDate startDate;

    @Future
    private LocalDate endDate;
}
