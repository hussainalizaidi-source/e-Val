package com.e_val.e_Val.repository;


import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByIsPublishedTrue();
    List<Quiz> findByCreatedBy(User createdBy);
    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :quizId")
    Optional<Quiz> findByIdWithQuestions(@Param("quizId") Long quizId);
}