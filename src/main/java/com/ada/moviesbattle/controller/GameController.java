package com.ada.moviesbattle.controller;

import com.ada.moviesbattle.GameApi;
import com.ada.moviesbattle.model.api.Game;
import com.ada.moviesbattle.model.api.Ranking;
import com.ada.moviesbattle.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GameController implements GameApi {

    private final GameService gameService;

    private String getUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return securityContext.getAuthentication().getName();
    }

    @Override
    public ResponseEntity<Game> getGame() {
        return ResponseEntity.ok(gameService.getActiveGame(getUsername()));
    }

    @Override
    public ResponseEntity<List<Ranking>> getGameRanking() {
        return ResponseEntity.ok(gameService.getGameRanking());
    }

    @Override
    public ResponseEntity<Game> startGame() {
        return ResponseEntity.ok(gameService.startGame(getUsername()));
    }

    @Override
    public ResponseEntity<Game> stopGame() {
        return ResponseEntity.ok(gameService.stopGame(getUsername()));
    }
}
