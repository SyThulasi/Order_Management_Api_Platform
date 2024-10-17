package org.example.zerobeta.Exception;

/**
 * Custom runtime exception to handle application-specific errors.
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
