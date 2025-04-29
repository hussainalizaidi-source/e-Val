package com.e_val.e_Val.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuizCreationRequest {
    private String title;
    private String quizType;
    private String className;
    private String section;
    private Integer totalMarks;
    private List<QuestionRequest> questions;
}
