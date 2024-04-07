package org.hrsys.exception;

import com.fasterxml.jackson.core.JsonParseException;
import org.hrsys.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;


import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidTokenException(InvalidTokenException e) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid token", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NotAuthoriseException.class)
    public ResponseEntity<ApiError> handleNotAuthoriseException(NotAuthoriseException e) {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Not Authorise", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "Access denied", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid request", List.of("The request parameter " + e.getName() + " should be of type " + e.getRequiredType().getSimpleName()));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid request", List.of("Invalid request body"));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid request", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "User not found", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiError> handleJsonParseException(JsonParseException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid JSON format", List.of(e.getOriginalMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> ((FieldError) error).getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Validation errors", errors);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, "Resource not found", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequestException(InvalidRequestException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Invalid request", List.of(e.getMessage()));
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}