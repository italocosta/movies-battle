package com.ada.moviesbattle.service;

import com.ada.moviesbattle.exception.NoEntityFoundException;
import com.ada.moviesbattle.model.api.Ranking;
import com.ada.moviesbattle.model.entity.Game;
import com.ada.moviesbattle.model.enumarator.GameStatus;
import com.ada.moviesbattle.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRepository repository;

    @Value("${movies-battle.amount-wrong-quiz-allowed}")
    private int amountWrongQuizAllowed;

    @Transactional
    public com.ada.moviesbattle.model.api.Game startGame(String user) {
        try {
            findActiveGame(user);
            throw new IllegalStateException("You already have a game session up and running. Please, stop the current one or continue playing with that.");
        } catch (NoEntityFoundException ex) {
            Game game = Game.builder()
                    .user(user)
                    .creationTime(LocalDateTime.now())
                    .lastUpdate(LocalDateTime.now())
                    .status(GameStatus.RUNNING)
                    .build();
            return converToGameApi(repository.save(game));
        }
    }

    @Transactional
    public com.ada.moviesbattle.model.api.Game stopGame(String user) {
        Game game = findActiveGame(user);
        game.setStatus(GameStatus.FINISHED);
        game.setLastUpdate(LocalDateTime.now());
        game.setFinalScore(calculateScoreActiveGame(game));
        return converToGameApi(game);
    }

    public com.ada.moviesbattle.model.api.Game getActiveGame(String user) {
        return converToGameApi(findActiveGame(user));
    }

    public List<Ranking> getGameRanking() {
        return repository.findTop10ByStatusOrderByFinalScoreDesc(GameStatus.FINISHED)
                .stream()
                .map(g -> new Ranking().user(g.getUser()).score(g.getFinalScore()))
                .toList();
    }

    private com.ada.moviesbattle.model.api.Game converToGameApi(Game game) {
        int amountCorrectQuiz = getAmountCorrectQuizAnswer(game);
        int amountWrongQuiz = getAmountWrongQuizAnswer(game);
        return new com.ada.moviesbattle.model.api.Game()
                .user(game.getUser())
                .score(game.getFinalScore() != null
                        && game.getFinalScore().compareTo((float) 0) > 0
                        ? game.getFinalScore()
                        : calculateScoreActiveGame(game))
                .amountCorrectQuiz(amountCorrectQuiz)
                .amountWrongQuiz(amountWrongQuiz)
                .lastUpdateTime(game.getLastUpdate())
                .creationTime(game.getCreationTime())
                .remainingFailures(amountWrongQuizAllowed - amountWrongQuiz);
    }

    private float calculateScoreActiveGame(Game game) {
        int amountCorrectQuiz = getAmountCorrectQuizAnswer(game);
        int amountWrongQuiz = getAmountWrongQuizAnswer(game);
        if (amountCorrectQuiz == 0 && amountWrongQuiz == 0) {
            return 0;
        }
        float percentageCorrectQuiz = (float) amountCorrectQuiz / (amountCorrectQuiz + amountWrongQuiz);
        return (amountCorrectQuiz + amountWrongQuiz) * percentageCorrectQuiz;
    }

    public Game findActiveGame(String user) {
        return repository.findByUserAndStatus(user, GameStatus.RUNNING)
                .orElseThrow(() -> new NoEntityFoundException("You don't have any game session up and running. Please, start a new one."));
    }

    public int getAmountWrongQuizAnswer(Game game) {
        if (CollectionUtils.isEmpty(game.getQuizzes())) {
            return 0;
        }
        return (int) game.getQuizzes().stream().filter(q -> q.getCorrect() != null && !q.getCorrect()).count();
    }

    public int getAmountCorrectQuizAnswer(Game game) {
        if (CollectionUtils.isEmpty(game.getQuizzes())) {
            return 0;
        }
        return (int) game.getQuizzes().stream().filter(q -> q.getCorrect() != null && q.getCorrect()).count();
    }

    public int getAmountWrongQuizAllowed() {
        return amountWrongQuizAllowed;
    }
}
