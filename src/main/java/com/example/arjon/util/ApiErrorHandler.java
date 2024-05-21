package com.example.arjon.util;

import com.example.arjon.model.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.example.arjon.util.Constant.GENERIC_AUTH_ERROR_MESSAGE;

/**
 * A generic api error handler
 */
@ControllerAdvice
public class ApiErrorHandler {

    // Handler for input validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError error = (FieldError) ex.getBindingResult().getAllErrors().stream().findFirst().get();
        String fieldName = error.getField();
        String errorMessage = error.getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(fieldName +" "+ errorMessage));
    }

    // Handler for jdbc schema and auth security errors
    @ExceptionHandler({DataIntegrityViolationException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(GENERIC_AUTH_ERROR_MESSAGE));
    }
}
