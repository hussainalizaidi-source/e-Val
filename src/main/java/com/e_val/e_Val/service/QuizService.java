package com.e_val.e_Val.service;

import com.e_val.e_Val.model.Class;
import com.e_val.e_Val.model.Question;
import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.QuizAnswer;
import com.e_val.e_Val.model.QuizAttempt;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.QuizAssignmentRequest;
import com.e_val.e_Val.model.dto.QuizCreationRequest;
import com.e_val.e_Val.model.dto.QuizSubmissionRequest;
import com.e_val.e_Val.model.enums.Role;
import com.e_val.e_Val.repository.ClassRepository;
import com.e_val.e_Val.repository.QuizRepository;
import com.e_val.e_Val.repository.UserRepository;
import com.e_val.e_Val.repository.QuestionRepository;
import com.e_val.e_Val.repository.QuizAttemptRepository;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuestionRepository questionRepository;
    private final ClassRepository classRepository;
    private final Logger log = LoggerFactory.getLogger(QuizService.class);

    public Quiz createQuiz(QuizCreationRequest request, String teacherEmail) {
        log.info("Creating quiz with title: {}, teacherEmail: {}", request.getTitle(), teacherEmail);

        User teacher = userRepository.findByEmail(teacherEmail)
                .filter(u -> u.getRole() == Role.TEACHER)
                .orElseThrow(() -> {
                    log.error("Teacher not found: {}", teacherEmail);
                    return new RuntimeException("Teacher not found");
                });

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
                .orElseThrow(() -> {
                    log.error("Quiz not found: ID {}", quizId);
                    return new RuntimeException("Quiz not found");
                });
        quiz.setPublished(true);
        Quiz savedQuiz = quizRepository.save(quiz);
        log.info("Quiz published: {}", savedQuiz.getId());
        return savedQuiz;
    }

    public List<Quiz> getQuizzesByTeacher(String teacherEmail) {
        log.info("Fetching quizzes for teacher: {}", teacherEmail);

        User teacher = userRepository.findByEmail(teacherEmail)
                .filter(u -> u.getRole() == Role.TEACHER)
                .orElseThrow(() -> {
                    log.error("Teacher not found: {}", teacherEmail);
                    return new RuntimeException("Teacher not found");
                });
        List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
        log.info("Found {} quizzes for teacher {}", quizzes.size(), teacherEmail);
        return quizzes;
    }

    @Transactional
    public Quiz assignQuiz(QuizAssignmentRequest request, String teacherEmail) {
        log.info("Assigning quiz ID: {} to class ID: {}, teacherEmail: {}", request.getQuizId(), request.getClassId(), teacherEmail);

        User teacher = userRepository.findByEmail(teacherEmail)
                .filter(u -> u.getRole() == Role.TEACHER)
                .orElseThrow(() -> {
                    log.error("Teacher not found: {}", teacherEmail);
                    return new RuntimeException("Teacher not found");
                });

        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> {
                    log.error("Quiz not found: ID {}", request.getQuizId());
                    return new RuntimeException("Quiz not found");
                });

        if (!quiz.getCreatedBy().getId().equals(teacher.getId())) {
            log.error("Quiz ID {} does not belong to teacher {}", request.getQuizId(), teacherEmail);
            throw new RuntimeException("Quiz does not belong to this teacher");
        }

        Class assignedClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> {
                    log.error("Class not found: ID {}", request.getClassId());
                    return new RuntimeException("Class not found");
                });

        if (assignedClass.getTeacher() == null || !assignedClass.getTeacher().getId().equals(teacher.getId())) {
            log.error("Teacher {} is not assigned to class ID {}", teacherEmail, request.getClassId());
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

    @Transactional
    public Quiz getQuizById(Long quizId, String studentEmail) {
        log.info("Fetching quiz ID {} for student {}", quizId, studentEmail);
        User student = userRepository.findByEmail(studentEmail)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> {
                    log.error("Student not found: {}", studentEmail);
                    return new RuntimeException("Student not found: " + studentEmail);
                });

        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> {
                    log.error("Quiz not found: ID {}", quizId);
                    return new RuntimeException("Quiz not found: ID " + quizId);
                });

        // Initialize questions to ensure they are loaded
        Hibernate.initialize(quiz.getQuestions());
        log.info("Fetched {} questions for quiz ID {}", quiz.getQuestions().size(), quizId);

        // Remove sensitive fields
        quiz.getQuestions().forEach(q -> {
            q.setCorrectAnswer(null);
            q.setCorrectOptionIndex(null);
        });

        // Check if quiz is assigned to student's class
        Class assignedClass = quiz.getAssignedClass();
        if (assignedClass == null) {
            log.error("Quiz ID {} has no assigned class", quizId);
            throw new RuntimeException("Quiz not assigned to any class");
        }

        boolean isAssigned = assignedClass.getStudents().contains(student);
        if (!isAssigned) {
            log.error("Quiz ID {} not assigned to student {}'s class", quizId, studentEmail);
            throw new RuntimeException("Quiz not assigned to student's class");
        }

        // Check if quiz is still available
        if (quiz.getEndTime().isBefore(LocalDateTime.now())) {
            log.error("Quiz ID {} has expired for student {}", quizId, studentEmail);
            throw new RuntimeException("Quiz has expired");
        }

        // Check if already attempted
        boolean hasAttempted = quizAttemptRepository.findByQuizAndStudent(quiz, student).isPresent();
        if (hasAttempted) {
            log.error("Quiz ID {} already attempted by student {}", quizId, studentEmail);
            throw new RuntimeException("Quiz already attempted");
        }

        log.info("Successfully fetched quiz ID {} for student {}", quizId, studentEmail);
        return quiz;
    }

    @Transactional
    public QuizAttempt submitQuiz(Long quizId, QuizSubmissionRequest submission, String studentEmail) {
        log.info("Submitting quiz ID {} for student {}", quizId, studentEmail);
        User student = userRepository.findByEmail(studentEmail)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> {
                    log.error("Student not found: {}", studentEmail);
                    return new RuntimeException("Student not found: " + studentEmail);
                });

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> {
                    log.error("Quiz not found: ID {}", quizId);
                    return new RuntimeException("Quiz not found: ID " + quizId);
                });

        // Validate quiz availability and attempt status
        if (quiz.getEndTime().isBefore(LocalDateTime.now())) {
            log.error("Quiz ID {} has expired for student {}", quizId, studentEmail);
            throw new RuntimeException("Quiz has expired");
        }
        boolean hasAttempted = quizAttemptRepository.findByQuizAndStudent(quiz, student).isPresent();
        if (hasAttempted) {
            log.error("Quiz ID {} already attempted by student {}", quizId, studentEmail);
            throw new RuntimeException("Quiz already attempted");
        }
        boolean isAssigned = quiz.getAssignedClass().getStudents().contains(student);
        if (!isAssigned) {
            log.error("Quiz ID {} not assigned to student {}'s class", quizId, studentEmail);
            throw new RuntimeException("Quiz not assigned to student's class");
        }

        // Validate submission
        if (submission.getAnswers() == null || submission.getAnswers().isEmpty()) {
            log.error("No answers provided for quiz ID {} by student {}", quizId, studentEmail);
            throw new RuntimeException("No answers provided");
        }

        // Create quiz attempt
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setScore(0); // Scoring logic can be added later

        // Process answers
        submission.getAnswers().forEach(answer -> {
            Question question = quiz.getQuestions().stream()
                    .filter(q -> q.getId().equals(answer.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.error("Question not found: ID {} for quiz ID {}", answer.getQuestionId(), quizId);
                        return new RuntimeException("Question not found: ID " + answer.getQuestionId());
                    });

            QuizAnswer quizAnswer = new QuizAnswer();
            quizAnswer.setAttempt(attempt);
            quizAnswer.setQuestion(question);
            quizAnswer.setAnswerText(answer.getAnswerText());
            quizAnswer.setAnswerIndex(answer.getAnswerIndex());
            attempt.getAnswers().add(quizAnswer);
        });

        quizAttemptRepository.save(attempt);
        log.info("Quiz attempt saved for quiz ID {} by student {}", quizId, studentEmail);
        return attempt;
    }

    public List<Quiz> getAvailableQuizzes(String studentEmail) {
        log.info("Fetching available quizzes for student {}", studentEmail);
        User student = userRepository.findByEmail(studentEmail)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> {
                    log.error("Student not found: {}", studentEmail);
                    return new RuntimeException("Student not found: " + studentEmail);
                });

        List<Quiz> quizzes = quizRepository.findAll().stream()
                .filter(quiz -> {
                    // Check if quiz is assigned to student's class
                    Class assignedClass = quiz.getAssignedClass();
                    if (assignedClass == null) {
                        log.warn("Quiz ID {} has no assigned class", quiz.getId());
                        return false;
                    }
                    boolean isAssigned = assignedClass.getStudents().contains(student);
                    if (!isAssigned) {
                        return false;
                    }

                    // Check if quiz is still available
                    boolean isActive = quiz.getEndTime().isAfter(LocalDateTime.now());
                    if (!isActive) {
                        return false;
                    }

                    // Check if not attempted
                    boolean hasAttempted = quizAttemptRepository.findByQuizAndStudent(quiz, student).isPresent();
                    return !hasAttempted;
                })
                .collect(Collectors.toList());

        log.info("Found {} available quizzes for student {}", quizzes.size(), studentEmail);
        return quizzes;
    }
}