package net.ayman.supplychainx.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Validation error");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistingException(EmailAlreadyExistException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflict");
        error.put("message", exception.getMessage());
        error.put("status", "409");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
}
