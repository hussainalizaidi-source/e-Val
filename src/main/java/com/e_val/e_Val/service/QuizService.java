package com.e_val.e_Val.service;

import com.e_val.e_Val.model.Question;
import com.e_val.e_Val.model.Quiz;
import com.e_val.e_Val.model.User;
import com.e_val.e_Val.model.dto.QuizCreationRequest;
import com.e_val.e_Val.repository.QuizRepository;
import com.e_val.e_Val.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final UserService userService;
    private final QuestionRepository questionRepository;

    public Quiz createQuiz(QuizCreationRequest request, String teacherEmail) {
        User teacher = userService.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        
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
            
            switch(qRequest.getType()) {
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
        
        return savedQuiz;
    }

    public Quiz publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
          .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setPublished(true);
        return quizRepository.save(quiz);
    }
}
