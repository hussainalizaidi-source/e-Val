package com.e_val.e_Val.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizAssignmentRequest {
    @NotNull
    private Long quizId;

    @NotNull
    private Long classId;

    @NotNull
    private LocalDateTime endTime;

    private Integer timeLimitMinutes; // Optional, defaults to quiz’s timeLimitMinutes
}