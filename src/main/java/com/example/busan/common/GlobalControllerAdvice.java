package com.example.busan.common;

import com.example.busan.auth.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerAdvice.class);

    @ExceptionHandler
    public ResponseEntity<String> handle(final IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(final UnauthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(final Exception e) {
        log.error("Error: ", e);
        return ResponseEntity.internalServerError().build();
    }
}
