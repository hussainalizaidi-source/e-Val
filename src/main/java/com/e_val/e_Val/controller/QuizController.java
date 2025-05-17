package com.e_val.e_Val.controller;

import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.QuizAttempt;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.QuizAssignmentRequest;
import com.e_val.e_Val.model.dto.QuizCreationRequest;
import com.e_val.e_Val.model.dto.QuizSubmissionRequest;
import com.e_val.e_Val.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizCreationRequest request,
                                         @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(quizService.createQuiz(request, teacher.getEmail()));
    }

    @PatchMapping("/{quizId}/publish")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Quiz> publishQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.publishQuiz(quizId));
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Quiz>> getQuizzesByTeacher(@AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(quizService.getQuizzesByTeacher(teacher.getEmail()));
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Quiz> assignQuiz(@RequestBody QuizAssignmentRequest request,
                                          @AuthenticationPrincipal User teacher) {
        return ResponseEntity.ok(quizService.assignQuiz(request, teacher.getEmail()));
    }
    @GetMapping("/{quizId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Quiz> getQuiz(@PathVariable Long quizId, @AuthenticationPrincipal User student) {
        Quiz quiz = quizService.getQuizById(quizId, student.getEmail());
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/{quizId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<QuizAttempt> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizSubmissionRequest submission,
            @AuthenticationPrincipal User student) {
        QuizAttempt attempt = quizService.submitQuiz(quizId, submission, student.getEmail());
        return ResponseEntity.ok(attempt);
    }
    
    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Quiz>> getAvailableQuizzes(@AuthenticationPrincipal User student) {
        List<Quiz> quizzes = quizService.getAvailableQuizzes(student.getEmail());
        return ResponseEntity.ok(quizzes);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", ex.getMessage()));
    }
}