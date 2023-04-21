package com.ada.moviesbattle.repository;

import com.ada.moviesbattle.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
}
