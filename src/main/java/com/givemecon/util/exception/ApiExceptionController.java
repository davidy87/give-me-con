package com.givemecon.util.exception;

import com.givemecon.util.error.ErrorResponse;
import com.givemecon.util.error.FieldErrorResponse;
import com.givemecon.util.error.ValidationErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(e.getStatus())
                .code(e.getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getStatus())
                .body(Map.of("error", errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = makeErrorResponse(e.getBindingResult());

        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", errorResponse));
    }

    private ErrorResponse makeErrorResponse(BindingResult bindingResult) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(NOT_VALID_ARGUMENT);

        for (FieldError fieldError: bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.isBindingFailure() ? "Type Mismatch Error" : fieldError.getDefaultMessage();
            errorResponse.addFieldError(new FieldErrorResponse(field, message));
        }

        return errorResponse;
    }
}
