package com.xm.recommendation.api;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class RecommendationControllerAdvisor {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessage> handleInvalidCountry(final NoSuchElementException countryException) {
        final ErrorMessage errorMessage = new ErrorMessage(countryException.getMessage());
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
