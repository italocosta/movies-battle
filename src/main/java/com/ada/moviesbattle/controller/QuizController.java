package com.ada.moviesbattle.controller;

import com.ada.moviesbattle.QuizApi;
import com.ada.moviesbattle.model.Quiz;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuizController implements QuizApi {
    @Override
    public ResponseEntity<List<Quiz>> getQuiz() {
        return null;
    }

    @Override
    public ResponseEntity<List<Quiz>> replyQuiz(Integer selectedOption) {
        return null;
    }
}
