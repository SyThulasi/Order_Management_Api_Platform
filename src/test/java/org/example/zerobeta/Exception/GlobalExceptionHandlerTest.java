package org.example.zerobeta.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleCustomException() {
        String errorMessage = "Custom error message";
        CustomException customException = new CustomException(errorMessage);

        ResponseEntity<String> response = globalExceptionHandler.handleCustomException(customException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errorMessage, response.getBody());
    }

    @Test
    void testHandleGeneralException() {
        Exception exception = new Exception("General error");

        ResponseEntity<String> response = globalExceptionHandler.handleGeneralException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An error occurred", response.getBody());
    }

    @Test
    void testHandleValidationExceptions() {
        FieldError fieldError = new FieldError("objectName", "fieldName", "Field error message");

        List<org.springframework.validation.ObjectError> errors = new ArrayList<>();
        errors.add(fieldError);

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getAllErrors()).thenReturn(errors);

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("fieldName", "Field error message");
        assertEquals(expectedErrors, response.getBody());
    }

    @Test
    void testHandleMissingParams() {
        String paramName = "testParam";
        MissingServletRequestParameterException exception = new MissingServletRequestParameterException(paramName, "String");

        ResponseEntity<String> response = globalExceptionHandler.handleMissingParams(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(paramName + " parameter is missing", response.getBody());
    }

    @Test
    void testHandleDataIntegrityViolationException() {
        String errorMessage = "Duplicate entry";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(errorMessage);

        ResponseEntity<String> response = globalExceptionHandler.handleDataIntegrityViolationException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Duplicate entry detected: " + errorMessage, response.getBody());
    }

    @Test
    void testHandleBadCredentialsException() {
        String errorMessage = "Bad credentials";
        BadCredentialsException exception = new BadCredentialsException(errorMessage);

        ResponseEntity<String> response = globalExceptionHandler.handleBadCredentialsException(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials: " + errorMessage, response.getBody());
    }
}