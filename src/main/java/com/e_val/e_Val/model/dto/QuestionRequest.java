package com.e_val.e_Val.model.dto;

import java.util.List;

import com.e_val.e_Val.model.enums.QuestionType;

import lombok.Data;

@Data
public class QuestionRequest {
    private QuestionType type;
    private String questionText;
    private Integer marks;
    private List<String> options;
    private Integer correctOptionIndex;
    private String correctAnswer;
}