package com.e_val.e_Val.model;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Integer timeLimitMinutes;
    private boolean isPublished;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User createdBy;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;
}