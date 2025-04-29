package com.e_val.e_Val.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.persistence.*;

import com.e_val.e_Val.model.enums.QuestionType;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String questionText;
    private Integer marks;
    
    @Enumerated(EnumType.STRING)
    private QuestionType type;
    
    // For MCQ
    @ElementCollection
    private List<String> options;
    private Integer correctOptionIndex;
    
    // For Short/Numerical
    private String correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
}