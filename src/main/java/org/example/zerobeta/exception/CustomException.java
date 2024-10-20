package org.example.zerobeta.exception;

/**
 * Custom runtime exception to handle application-specific errors.
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
