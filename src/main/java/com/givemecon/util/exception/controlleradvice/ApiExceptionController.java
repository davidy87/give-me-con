package com.givemecon.util.exception.controlleradvice;

import com.givemecon.util.error.response.ErrorResponse;
import com.givemecon.util.error.response.MissingParameterErrorResponse;
import com.givemecon.util.error.response.ValidationErrorResponse;
import com.givemecon.util.exception.GivemeconException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
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

    @ExceptionHandler
    public ResponseEntity<Map<String, ErrorResponse>> givemeconExceptionHandler(GivemeconException e) {
        log.info("[Log] Caught {}", e.getClass().getSimpleName(), e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return createResponseEntity(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, ErrorResponse>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e,
                                                                                             Locale locale) {

        log.info("[Log] Caught {}", e.getClass().getSimpleName(), e);
        ErrorResponse errorResponse = makeErrorResponse(e.getBindingResult(), locale);
        return createResponseEntity(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, ErrorResponse>> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.info("[Log] Caught {}", e.getClass().getSimpleName(), e);
        ErrorResponse errorResponse = makeErrorResponse(e.getParameterName(), e.getParameterType());
        return createResponseEntity(errorResponse);
    }

    private ResponseEntity<Map<String, ErrorResponse>> createResponseEntity(ErrorResponse errorResponse) {
        return ResponseEntity.status(errorResponse.getStatus())
                .body(Map.of("error", errorResponse));
    }

    // MethodArgumentNotValidException 발생 시 호출
    private ErrorResponse makeErrorResponse(BindingResult bindingResult, Locale locale) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(NOT_VALID_ARGUMENT);

        for (FieldError fieldError: bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String message = messageSource.getMessage(fieldError, locale);
            errorResponse.addFieldErrorResponse(field, message);
        }

        return errorResponse;
    }

    // MissingServletRequestParameterException 발생 시 호출
    private ErrorResponse makeErrorResponse(String parameterName, String parameterType) {
        return new MissingParameterErrorResponse(MISSING_REQUEST_PARAMETER, parameterName, parameterType);
    }
}
