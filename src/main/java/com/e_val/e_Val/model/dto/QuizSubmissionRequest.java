package com.e_val.e_Val.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuizSubmissionRequest {
    private List<AnswerSubmission> answers;

    @Data
    public static class AnswerSubmission {
        private Long questionId;
        private String answerText;
        private Integer answerIndex;
    }
}