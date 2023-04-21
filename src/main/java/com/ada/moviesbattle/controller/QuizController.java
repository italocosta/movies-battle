package com.ada.moviesbattle.controller;

import com.ada.moviesbattle.QuizApi;
import com.ada.moviesbattle.model.api.Quiz;
import com.ada.moviesbattle.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class QuizController implements QuizApi {

    private final QuizService service;

    private String getUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication().getName();
    }

    @Override
    public ResponseEntity<List<Quiz>> getQuiz() {
        return ResponseEntity.ok(service.getActiveQuizOrNext(getUsername()));
    }

    @Override
    public ResponseEntity<String> replyQuiz(Integer selectedOption) {
        return ResponseEntity.ok(service.validateQuizReply(selectedOption,getUsername()) ? "Correct answer!" : "Incorrect answer!");
    }
}
