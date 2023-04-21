package com.ada.moviesbattle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WordGeneratorService {

    private final RestTemplate restTemplate;

    @Value("${movies-battle.word.generator.url}")
    private String url;

    @Value("${movies-battle.word.generator.param.quantity}")
    private String quantity;

    @Value("${movies-battle.word.generator.param.wordType}")
    private String wordType;

    public List<String> generateWords() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
                .queryParam("quantity", quantity)
                .queryParam("wordType", wordType);
        List<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, null,
                new ParameterizedTypeReference<List<String>>() {
                }).getBody();
        if(!CollectionUtils.isEmpty(response)) {
            log.debug(response.toString());
            return response;
        }
        throw new RestClientException("No words received");
    }
}
