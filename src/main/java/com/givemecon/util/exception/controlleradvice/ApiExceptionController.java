package com.givemecon.util.exception.controlleradvice;

import com.givemecon.util.error.response.ErrorResponse;
import com.givemecon.util.error.response.ValidationErrorResponse;
import com.givemecon.util.exception.concrete.EntityNotFoundException;
import com.givemecon.util.exception.concrete.FileProcessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;
import java.util.Map;

import static com.givemecon.util.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionController {

    private final MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

        return ResponseEntity.status(errorResponse.getStatus())
                .body(Map.of("error", errorResponse));
    }

    @ExceptionHandler(FileProcessException.class)
    public ResponseEntity<?> fileProcessExceptionHandler(FileProcessException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());

        return ResponseEntity.status(errorResponse.getStatus())
                .body(Map.of("error", errorResponse));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e,
                                                                    HttpServletRequest request) {

        ErrorResponse errorResponse = makeErrorResponse(e.getBindingResult(), request.getLocale());
        return ResponseEntity.status(e.getStatusCode())
                .body(Map.of("error", errorResponse));
    }

    private ErrorResponse makeErrorResponse(BindingResult bindingResult, Locale locale) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(NOT_VALID_ARGUMENT);

        for (FieldError fieldError: bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String message = messageSource.getMessage(fieldError, locale);
            errorResponse.addFieldErrorResponse(field, message);
        }

        return errorResponse;
    }
}
