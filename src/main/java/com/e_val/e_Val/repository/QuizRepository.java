package com.e_val.e_Val.repository;


import com.e_val.e_Val.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByIsPublishedTrue();
}