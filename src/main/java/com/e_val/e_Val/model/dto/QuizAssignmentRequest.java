package com.e_val.e_Val.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class QuizAssignmentRequest {
    @NotNull
    private Long quizId;

    @NotNull
    private Long classId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XXX")
    private LocalDateTime endTime;

    private Integer timeLimitMinutes; // Optional, defaults to quiz’s timeLimitMinutes
}