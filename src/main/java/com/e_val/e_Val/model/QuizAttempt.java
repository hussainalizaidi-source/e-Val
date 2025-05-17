package com.e_val.e_Val.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    @JsonIgnoreProperties({"questions", "createdBy", "assignedClass"})
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties({"password", "resetToken"})
    private User student;

    private LocalDateTime attemptDate;
    private Integer score;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> answers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        attemptDate = LocalDateTime.now();
    }
}