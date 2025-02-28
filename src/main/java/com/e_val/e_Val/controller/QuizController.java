package com.e_val.e_Val.controller;


import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz, 
                                          @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(quizService.createQuiz(quiz, teacher.getEmail()));
    }

    @PatchMapping("/{quizId}/publish")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Quiz> publishQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.publishQuiz(quizId));
    }
}
