package com.e_val.e_Val.model;

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
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String quizType; // exam or quiz
    private String className;
    private String section;
    private Integer totalMarks;
    private LocalDateTime creationDate;
    private boolean isPublished;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User createdBy;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }
}
///quiz:
//id
//type
//class
//section
//teacher
//total marks
//date
////question:
//id
//type
//mcqAnswer
//Short answer
//Numerical anser
//marks