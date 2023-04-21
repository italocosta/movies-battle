package com.ada.moviesbattle.service;

import com.ada.moviesbattle.model.entity.Game;
import com.ada.moviesbattle.model.entity.Movie;
import com.ada.moviesbattle.model.entity.Quiz;
import com.ada.moviesbattle.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Service
public class QuizService {

    private final QuizRepository repository;
    private final MovieService movieService;
    private final GameService gameService;

    public List<com.ada.moviesbattle.model.api.Quiz> getActiveQuizOrNext(String user) {
        AtomicInteger index = new AtomicInteger();
        return getActiveQuiz(user)
                .orElseGet(() -> createNewQuiz(user))
                .getMovies()
                .stream()
                .sorted(Comparator.comparing(Movie::getTitle))
                .map(m -> new com.ada.moviesbattle.model.api.Quiz()
                        .title(m.getTitle())
                        .option(index.incrementAndGet()))
                .toList();

    }

    public boolean validateQuizReply(int selectedOption, String user) {
        Quiz quiz = getActiveQuiz(user)
                .orElseThrow(() -> new IllegalStateException("You don't have any session running up. Please, start a new one."));
        boolean correctAnswer = getHighestRatedMovie(quiz).equals(getSelectedMovie(quiz, selectedOption));
        quiz.setAnswer(selectedOption);
        quiz.setCorrect(correctAnswer);
        repository.save(quiz);

        return correctAnswer;
    }

    private Optional<Quiz> getActiveQuiz(String user) {
        Game game = gameService.findActiveGame(user);
        return repository.findByGameAndAnswerIsNull(game);
    }

    private Movie getHighestRatedMovie(Quiz quiz) {
        return quiz.getMovies()
                .stream()
                .max(Comparator.comparing(m -> m.getRating() * m.getVotesAmount()))
                .orElseThrow(RuntimeException::new);
    }

    private Movie getSelectedMovie(Quiz quiz, int selectedOption) {
        return quiz.getMovies()
                .stream()
                .sorted(Comparator.comparing(Movie::getTitle))
                .toList()
                .get(selectedOption - 1);
    }

    private Quiz createNewQuiz(String user) {
        Game game = gameService.findActiveGame(user);
        if (gameService.getAmountWrongQuizAnswer(game) >= gameService.getAmountWrongQuizAllowed()) {
            throw new IllegalStateException("The total allowed of incorrect answers was reached.");
        }
        Movie option1 = movieService.getRandomMovie();
        Movie option2 = movieService.getRandomMovie();
        if (option1.equals(option2) || pairOfMoviesAlreadyChallenged(option1, option2)) {
            return createNewQuiz(user);
        }
        return repository.save(Quiz.builder()
                .game(game)
                .movies(List.of(option1, option2))
                .build());
    }

    private boolean pairOfMoviesAlreadyChallenged(Movie option1, Movie option2) {
        return !repository.findAllByMoviesContaining(option1, option2).isEmpty();
    }
}
