package net.ayman.supplychainx.common.exception;

import net.ayman.supplychainx.supply.exception.MaterialAlreadyExistsException;
import net.ayman.supplychainx.supply.exception.MaterialInUseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Validation failed");
        resp.put("errors", errors);
        resp.put("status", HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistingException(EmailAlreadyExistException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflict");
        error.put("message", exception.getMessage());
        error.put("status", "409");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not found");
        error.put("message", exception.getMessage());
        error.put("status", "404");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaterialAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleMaterialAlreadyExistsException(MaterialAlreadyExistsException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflict");
        error.put("message", exception.getMessage());
        error.put("status", "409");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MaterialInUseException.class)
    public ResponseEntity<Map<String, String>> handleMaterialInUseException(MaterialInUseException exception) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", exception.getMessage());
        error.put("status", "400");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
