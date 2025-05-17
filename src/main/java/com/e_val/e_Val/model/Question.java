package com.e_val.e_Val.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

import com.e_val.e_Val.model.enums.QuestionType;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text")
    private String questionText;
    private Integer marks;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    // For MCQ
    @ElementCollection
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "options")
    private List<String> options = new ArrayList<>();

    private Integer correctOptionIndex;

    // For Short/Numerical
    private String correctAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @JsonBackReference
    private Quiz quiz;
}