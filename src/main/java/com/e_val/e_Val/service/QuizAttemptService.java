package com.e_val.e_Val.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.QuizAttempt;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.repository.QuizAttemptRepository;
import com.e_val.e_Val.repository.QuizRepository;

@Service
public class QuizAttemptService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public QuizAttemptService(QuizAttemptRepository quizAttemptRepository, QuizRepository quizRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizRepository = quizRepository;
    }

    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsByTeacher(User teacher) {
        List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
        return quizAttemptRepository.findByQuizIn(quizzes);
    }

    @Transactional
    public void updateScore(Long attemptId, Integer score, User teacher) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new RuntimeException("Attempt not found: " + attemptId));
        Quiz quiz = attempt.getQuiz();
        if (!quiz.getCreatedBy().getId().equals(teacher.getId())) {
            throw new RuntimeException("Attempt does not belong to teacher's quiz");
        }
        if (score < 0 || score > quiz.getTotalMarks()) {
            throw new RuntimeException("Invalid score: " + score);
        }
        attempt.setScore(score);
        quizAttemptRepository.save(attempt);
    }
}