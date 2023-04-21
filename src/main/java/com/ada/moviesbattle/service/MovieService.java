package com.ada.moviesbattle.service;

import com.ada.moviesbattle.model.entity.Movie;
import com.ada.moviesbattle.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieService {

    private final MovieRepository repository;
    private final OMDbService omDbService;
    private final WordGeneratorService wordGeneratorService;

    private Map<String, Movie> movies = new HashMap();

    @PostConstruct
    private void loadMovies() {
        List<Movie> moviesList = repository.findAll();
        if(CollectionUtils.isEmpty(moviesList)) {
            moviesList = loadMoviesFromOMDb(moviesList);
            repository.saveAll(moviesList);
        }
        movies = moviesList.stream()
                .collect(Collectors.toMap(Movie::getId, m -> m,(oldest, newest) -> oldest));
    }

    public Movie getRandomMovie() {
        Random random = new Random();
        List<String> keyList = new ArrayList<>(movies.keySet());
        int randomIndex = random.nextInt(keyList.size());
        String randomKey = keyList.get(randomIndex);
        return movies.get(randomKey);
    }

    private List<Movie> loadMoviesFromOMDb(List<Movie> moviesList) {
        wordGeneratorService.generateWords().forEach(search -> moviesList.addAll(omDbService.searchMovies(search)));
        log.info("Total amount of movies loaded: {}", moviesList.size());
        if (moviesList.size() < 100) {
            return loadMoviesFromOMDb(moviesList);
        }
        return moviesList;
    }
}
