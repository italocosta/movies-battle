package com.ada.moviesbattle.service;

import com.ada.moviesbattle.exception.NoEntityFoundException;
import com.ada.moviesbattle.model.api.Ranking;
import com.ada.moviesbattle.model.entity.Game;
import com.ada.moviesbattle.model.entity.Quiz;
import com.ada.moviesbattle.model.enumarator.GameStatus;
import com.ada.moviesbattle.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @InjectMocks
    private GameService service;
    @Mock
    private GameRepository repository;

    private final String USER_WITH_NO_GAME_RUNNING = "user";
    private final String USER_WITH_GAME_RUNNING = "admin";
    private final String USER_WITH_GAME_RUNNING_WITH_QUIZ = "user2";
    private final int AMOUNT_WRONG_QUIZ_ALLOWED = 3;

    @Before
    public void setUp(){
        ReflectionTestUtils.setField(service, "amountWrongQuizAllowed",AMOUNT_WRONG_QUIZ_ALLOWED);

        when(repository.findByUserAndStatus(USER_WITH_NO_GAME_RUNNING, GameStatus.RUNNING))
                .thenThrow(new NoEntityFoundException("You don't have any game session up and running. Please, start a new one."));
    }

    @Test
    public void startGame_success() {
        doReturn(Game.builder()
                .user(USER_WITH_NO_GAME_RUNNING)
                .status(GameStatus.RUNNING)
                .lastUpdate(LocalDateTime.now())
                .creationTime(LocalDateTime.now())
                .build())
                .when(repository).save(any(Game.class));

        com.ada.moviesbattle.model.api.Game gameApi = service.startGame(USER_WITH_NO_GAME_RUNNING);

        assertThat(gameApi).isNotNull();
        assertThat(gameApi.getUser()).isEqualTo(USER_WITH_NO_GAME_RUNNING);
        assertThat(gameApi.getScore()).isZero();
        assertThat(gameApi.getAmountCorrectQuiz()).isZero();
        assertThat(gameApi.getAmountWrongQuiz()).isZero();
        assertThat(gameApi.getRemainingFailures()).isEqualTo(3);
    }

    @Test(expected = IllegalStateException.class)
    public void startGame_exceptionGameAlreadyStared() {
        when(repository.findByUserAndStatus(USER_WITH_GAME_RUNNING, GameStatus.RUNNING))
                .thenReturn(Optional.of(Game.builder()
                        .user(USER_WITH_NO_GAME_RUNNING)
                        .status(GameStatus.RUNNING)
                        .lastUpdate(LocalDateTime.now())
                        .creationTime(LocalDateTime.now())
                        .build()));
        service.startGame(USER_WITH_GAME_RUNNING);
    }

    @Test
    public void stopGame_success() {
        when(repository.findByUserAndStatus(USER_WITH_GAME_RUNNING, GameStatus.RUNNING))
                .thenReturn(Optional.of(Game.builder()
                        .user(USER_WITH_GAME_RUNNING)
                        .status(GameStatus.RUNNING)
                        .lastUpdate(LocalDateTime.now())
                        .creationTime(LocalDateTime.now())
                        .build()));

        com.ada.moviesbattle.model.api.Game gameApi = service.stopGame(USER_WITH_GAME_RUNNING);

        assertThat(gameApi).isNotNull();
        assertThat(gameApi.getUser()).isEqualTo(USER_WITH_GAME_RUNNING);
        assertThat(gameApi.getScore()).isZero();
        assertThat(gameApi.getAmountCorrectQuiz()).isZero();
        assertThat(gameApi.getAmountWrongQuiz()).isZero();
        assertThat(gameApi.getRemainingFailures()).isEqualTo(3);
    }

    @Test(expected = NoEntityFoundException.class)
    public void stopGame_exceptionNoGameRunning() {
        service.stopGame(USER_WITH_NO_GAME_RUNNING);
    }

    @Test
    public void getGameRanking() {
        List<Game> games = List.of(Game.builder()
                        .user("userRanking1")
                        .status(GameStatus.FINISHED)
                        .lastUpdate(LocalDateTime.now())
                        .creationTime(LocalDateTime.now())
                        .finalScore(3f)
                        .quizzes(Set.of(
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(false).build()))
                        .build(),
                Game.builder()
                        .user("userRanking2")
                        .status(GameStatus.FINISHED)
                        .lastUpdate(LocalDateTime.now())
                        .creationTime(LocalDateTime.now())
                        .finalScore(2f)
                        .quizzes(Set.of(
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(false).build(),
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(false).build()))
                        .build());

        when(repository.findTop10ByStatusOrderByFinalScoreDesc(GameStatus.FINISHED))
                .thenReturn(games);

        List<Ranking> gameRanking = service.getGameRanking();

        List<Ranking> expectedResult = List.of(new Ranking().user("userRanking1").score(3f),
                new Ranking().user("userRanking2").score(2f));

        assertThat(gameRanking)
                .hasSize(2)
                .containsExactlyElementsOf(expectedResult);
    }

    @Test
    public void getActiveGame_success(){
        when(repository.findByUserAndStatus(USER_WITH_GAME_RUNNING_WITH_QUIZ, GameStatus.RUNNING))
                .thenReturn(Optional.of(Game.builder()
                        .user(USER_WITH_GAME_RUNNING_WITH_QUIZ)
                        .status(GameStatus.RUNNING)
                        .lastUpdate(LocalDateTime.now())
                        .creationTime(LocalDateTime.now())
                        .quizzes(Set.of(
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(true).build(),
                                Quiz.builder().correct(false).build()))
                        .build()));

        com.ada.moviesbattle.model.api.Game gameApi = service.getActiveGame(USER_WITH_GAME_RUNNING_WITH_QUIZ);

        assertThat(gameApi).isNotNull();
        assertThat(gameApi.getUser()).isEqualTo(USER_WITH_GAME_RUNNING_WITH_QUIZ);
        assertThat(gameApi.getScore()).isEqualTo(3);
        assertThat(gameApi.getAmountCorrectQuiz()).isEqualTo(3);
        assertThat(gameApi.getAmountWrongQuiz()).isEqualTo(1);
        assertThat(gameApi.getRemainingFailures()).isEqualTo(2);
    }

    @Test
    public void getAmountWrongQuizAnswer() {
        int amountWrongQuizAnswer = service.getAmountWrongQuizAnswer(Game.builder()
                .quizzes(Set.of(
                        Quiz.builder().correct(true).build(),
                        Quiz.builder().correct(true).build(),
                        Quiz.builder().correct(true).build(),
                        Quiz.builder().correct(false).build()
                )).build());

        assertThat(amountWrongQuizAnswer).isEqualTo(1);
    }

    @Test
    public void getAmountCorrectQuizAnswer() {
        int amountCorrectQuizAnswer = service.getAmountCorrectQuizAnswer(Game.builder()
                .quizzes(Set.of(
                        Quiz.builder().correct(true).build(),
                        Quiz.builder().correct(true).build(),
                        Quiz.builder().correct(true).build(),
                        Quiz.builder().correct(false).build()
                )).build());

        assertThat(amountCorrectQuizAnswer).isEqualTo(3);
    }

    @Test
    public void getAmountWrongQuizAllowed() {
        assertThat(service.getAmountWrongQuizAllowed()).isEqualTo(AMOUNT_WRONG_QUIZ_ALLOWED);
    }
}