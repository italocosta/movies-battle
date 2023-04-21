package com.ada.moviesbattle.service;

import com.ada.moviesbattle.model.api.OMDbMovie;
import com.ada.moviesbattle.model.api.OMDbResponse;
import com.ada.moviesbattle.model.entity.Movie;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OMDbService {

    private final RestTemplate restTemplate;
    private final WordGeneratorService wordGeneratorService;
    private final ObjectMapper objectMapper;

    @Value("${movies-battle.omdbapi.url}")
    private String url;

    @Value("${movies-battle.omdbapi.param.apikey}")
    private String apiKey;

    @Value("${movies-battle.omdbapi.param.type}")
    private String type;

    public List<Movie> searchMovies(String search) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("apikey", apiKey)
                .queryParam("type", type)
                .queryParam("s", search);
        String response = restTemplate.getForObject(builder.toUriString(),String.class);

        if(StringUtils.hasText(response) && response.contains("\"Response\":\"True\"")) {
            List<Movie> moviesResponse = new ArrayList<>();
            try {
                OMDbResponse omDbResponse = objectMapper.readValue(response,OMDbResponse.class);
                omDbResponse.getMovies().forEach(m -> {
                    OMDbMovie movieWithDetails = getMovieDetail(m.getImdbId());
                    if(isMovieValid(movieWithDetails)) {
                        moviesResponse.add(Movie.of(movieWithDetails));
                    }
                });
                log.debug(omDbResponse.getMovies().toString());
            } catch (JsonProcessingException e) {
                log.error("Could not convert the response into java object. String {}",response);
            }
            return moviesResponse;
        }
        log.warn("No movie found searching for {}",search);
        return Collections.EMPTY_LIST;
    }

    public OMDbMovie getMovieDetail(String imdbId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("apikey", apiKey)
                .queryParam("i", imdbId);
        OMDbMovie response = restTemplate.getForObject(builder.toUriString(), OMDbMovie.class);

        if(response == null) {
            log.warn("No movie found with imdbId {}",imdbId);
        } else {
            log.debug(response.toString());
        }
        return response;
    }

    private boolean isMovieValid(OMDbMovie movie) {
        String NOT_APPLICABLE = "N/A";
        String NULL = "null";
        return movie != null
                && StringUtils.hasText(movie.getImdbId())
                && StringUtils.hasText(movie.getTitle())
                && StringUtils.hasText(movie.getImdbRating())
                && !NOT_APPLICABLE.equalsIgnoreCase(movie.getImdbRating())
                && !NULL.equalsIgnoreCase(movie.getImdbRating())
                && StringUtils.hasText(movie.getImdbVotes())
                && !NOT_APPLICABLE.equalsIgnoreCase(movie.getImdbVotes())
                && !NULL.equalsIgnoreCase(movie.getImdbVotes());
    }

}
