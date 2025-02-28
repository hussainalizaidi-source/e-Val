package com.e_val.e_Val.service;

import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserService userService;

    public Quiz createQuiz(Quiz quiz, String teacherEmail) {
        User teacher = userService.findByEmail(teacherEmail)
          .orElseThrow(() -> new RuntimeException("Teacher not found"));
        quiz.setCreatedBy(teacher);
        return quizRepository.save(quiz);
    }

    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
          .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }
}
