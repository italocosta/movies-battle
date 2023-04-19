package com.ada.moviesbattle.controller;

import com.ada.moviesbattle.GameApi;
import com.ada.moviesbattle.model.Game;
import com.ada.moviesbattle.model.Ranking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController implements GameApi {
    @Override
    public ResponseEntity<Game> getGame() {
        return null;
    }

    @Override
    public ResponseEntity<List<Ranking>> getGameRanking() {
        return null;
    }

    @Override
    public ResponseEntity<Game> startGame() {
        return null;
    }

    @Override
    public ResponseEntity<Game> stopGame() {
        return null;
    }
}
