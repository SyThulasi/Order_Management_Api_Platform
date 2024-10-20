package org.example.zerobeta.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles exceptions globally for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom application exceptions
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation exceptions and return field-specific error messages
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handle missing request parameter exception
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        return new ResponseEntity<>(name + " parameter is missing", HttpStatus.BAD_REQUEST);
    }
    // Handle DataIntegrityViolationException (e.g., duplicate email)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return  new ResponseEntity<>("Duplicate entry detected: " + ex.getMessage(), HttpStatus.CONFLICT);
    }

    // Handle BadCredentialsException for login failures
    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>( "Invalid credentials: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
