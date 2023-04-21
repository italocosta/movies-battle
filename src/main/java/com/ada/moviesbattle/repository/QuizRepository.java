package com.ada.moviesbattle.repository;

import com.ada.moviesbattle.model.entity.Game;
import com.ada.moviesbattle.model.entity.Movie;
import com.ada.moviesbattle.model.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByGameAndAnswerIsNull(Game game);

    @Query("Select q from Quiz q WHERE :option1 in elements(q.movies) and :option2 in elements(q.movies)")
    List<Quiz> findAllByMoviesContaining(@Param("option1") Movie option1, @Param("option2") Movie option2);
}
