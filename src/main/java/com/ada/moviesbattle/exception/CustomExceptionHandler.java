package com.ada.moviesbattle.exception;

import com.ada.moviesbattle.model.api.SingleStringResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({NoEntityFoundException.class})
    public ResponseEntity<SingleStringResponse> handleException(NoEntityFoundException ex) {
        return new ResponseEntity<>(new SingleStringResponse().message(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalStateException.class})
    public ResponseEntity<SingleStringResponse> handleException(IllegalStateException ex) {
        return new ResponseEntity<>(new SingleStringResponse().message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
