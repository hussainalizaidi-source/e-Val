package com.e_val.e_Val.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_val.e_Val.model.QuizAttempt;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.service.QuizAttemptService;

@RestController
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    @Autowired
    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<QuizAttempt>> getAttempts(@AuthenticationPrincipal User teacher) {
        List<QuizAttempt> attempts = quizAttemptService.getAttemptsByTeacher(teacher);
        return ResponseEntity.ok(attempts);
    }

    @PostMapping("/{attemptId}/score")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Void> updateScore(@PathVariable Long attemptId, 
                                           @RequestBody Map<String, Integer> body,
                                           @AuthenticationPrincipal User teacher) {
        quizAttemptService.updateScore(attemptId, body.get("score"), teacher);
        return ResponseEntity.ok().build();
    }
}