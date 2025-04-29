package com.e_val.e_Val.service;

import com.e_val.e_Val.model.Class;
import com.e_val.e_Val.model.Question;
import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.QuizAssignmentRequest;
import com.e_val.e_Val.model.dto.QuizCreationRequest;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.ClassRepository;
import com.e_val.e_Val.repository.QuizRepository;
import com.e_val.e_Val.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final ClassRepository classRepository;
    private final Logger log = LoggerFactory.getLogger(QuizService.class);

    public Quiz createQuiz(QuizCreationRequest request, String teacherEmail) {
        log.info("Creating quiz with title: {}, teacherEmail: {}", request.getTitle(), teacherEmail);

        User teacher = userService.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setQuizType(request.getQuizType());
        quiz.setClassName(request.getClassName());
        quiz.setSection(request.getSection());
        quiz.setTotalMarks(request.getTotalMarks());
        quiz.setCreatedBy(teacher);

        Quiz savedQuiz = quizRepository.save(quiz);

        request.getQuestions().forEach(qRequest -> {
            Question question = new Question();
            question.setQuestionText(qRequest.getQuestionText());
            question.setMarks(qRequest.getMarks());
            question.setType(qRequest.getType());
            question.setQuiz(savedQuiz);

            switch (qRequest.getType()) {
                case MCQ:
                    question.setOptions(qRequest.getOptions());
                    question.setCorrectOptionIndex(qRequest.getCorrectOptionIndex());
                    break;
                case SHORT:
                case NUMERICAL:
                    question.setCorrectAnswer(qRequest.getCorrectAnswer());
                    break;
            }

            questionRepository.save(question);
        });

        log.info("Quiz created with ID: {}", savedQuiz.getId());
        return savedQuiz;
    }

    public Quiz publishQuiz(Long quizId) {
        log.info("Publishing quiz with ID: {}", quizId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(true);
        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Quiz published: {}", savedQuiz.getId());
        return savedQuiz;
    }

    public List<Quiz> getQuizzesByTeacher(String teacherEmail) {
        log.info("Fetching quizzes for teacher: {}", teacherEmail);

        User teacher = userService.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
        log.info("Found {} quizzes for teacher {}", quizzes.size(), teacherEmail);
        return quizzes;
    }

    @Transactional
    public Quiz assignQuiz(QuizAssignmentRequest request, String teacherEmail) {
        log.info("Assigning quiz ID: {} to class ID: {}, teacherEmail: {}", request.getQuizId(), request.getClassId(), teacherEmail);

        User teacher = userService.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        if (!quiz.getCreatedBy().getId().equals(teacher.getId())) {
            throw new RuntimeException("Quiz does not belong to this teacher");
        }

        Class assignedClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (assignedClass.getTeacher() == null || !assignedClass.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Teacher is not assigned to this class");
        }

        quiz.setAssignedClass(assignedClass);
        quiz.setClassName(assignedClass.getName());
        quiz.setSection(assignedClass.getSection());
        quiz.setEndTime(request.getEndTime());
        if (request.getTimeLimitMinutes() != null) {
            quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
        }
        quiz.setPublished(true);

        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Quiz assigned: {}", savedQuiz.getId());
        return savedQuiz;
    }
}