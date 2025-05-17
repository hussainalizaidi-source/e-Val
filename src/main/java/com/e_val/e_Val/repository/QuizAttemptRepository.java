package com.e_val.e_Val.repository;

import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.QuizAttempt;
import com.e_val.e_Val.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByQuizIn(List<Quiz> quizzes);
    Optional<QuizAttempt> findByQuizAndStudent(Quiz quiz, User student);
}