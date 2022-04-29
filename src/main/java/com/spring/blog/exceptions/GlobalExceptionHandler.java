package com.spring.blog.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.spring.blog.payload.ErrorDetails;

@ControllerAdvice
// Extending ResponseEntityExceptionHandler to handle exceptions for dto
// validation
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // handle specific exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException exception,
            WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // @ExceptionHandler(BlogAPIException.class)
    // public ResponseEntity<ErrorDetails> handleBlogAPIException(BlogAPIException
    // exception,
    // WebRequest webRequest) {
    // ErrorDetails errorDetails = new ErrorDetails(new Date(),
    // exception.getMessage(),
    // webRequest.getDescription(false));
    // return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    // }

    // global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception exception,
            WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), exception.getMessage(),
                webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        // returns a list of field errors
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return new ResponseEntity<>(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    // different way of implementing the above handleMethodArgumentNotValid method
    // @ExceptionHandler(MethodArgumentNotValidException.class)
    // public ResponseEntity<Object>
    // handleMethodArgumentNotValidException(MethodArgumentNotValidException
    // exception,
    // WebRequest webRequest){
    // Map<String, String> errors = new HashMap<>();
    // exception.getBindingResult().getAllErrors().forEach((error) ->{
    // String fieldName = ((FieldError)error).getField();
    // String message = error.getDefaultMessage();
    // errors.put(fieldName, message);
    // });
    // return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    // }
}

// Something Like this to find 400 bad requests

// @ControllerAdvice
// public class ErrorHandler {

// @ExceptionHandler(value = IllegalArgumentException.class)
// public ResponseEntity<ErrorResponse>
// illegalArgumentExceptionHandler(IllegalArgumentException e) {
// //ErrorResponse is the custom response model
// return new ResponseEntity<>(new ErrorResponse(ErrorCode.NOT_FOUND,
// e.getMessage()), HttpStatus.NOT_ACCEPTABLE);
// }

// }