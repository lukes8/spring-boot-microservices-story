package com.lukestories.microservices.user_ws.web.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {

        return new ResponseEntity<>("some error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

//    @ExceptionHandler(NoResourceFoundException.class)
//    public final ResponseEntity<String> handleResourceNotFoundException(NoResourceFoundException ex, WebRequest request) {
//
//        return new ResponseEntity<>("some error", HttpStatus.NOT_FOUND);
//    }

    // Add other exception handlers as needed
}
