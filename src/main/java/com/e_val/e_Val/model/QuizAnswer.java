package com.e_val.e_Val.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuizAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    @JsonIgnoreProperties({"quiz", "student", "answers"})
    private QuizAttempt attempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnoreProperties({"quiz"})
    private Question question;

    private String answerText;
    private Integer answerIndex;
}