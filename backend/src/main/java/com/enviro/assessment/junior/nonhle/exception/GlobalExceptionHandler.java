package com.enviro.assessment.junior.nonhle.exception;

import com.enviro.assessment.junior.nonhle.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Catches exceptions thrown anywhere in the controller/service layers and
 * converts them into a consistent ErrorResponse JSON shape, instead of
 * Spring's default generic error page/body (or a raw 500 stack trace leak).
 *
 * This is what turns the "ugly 500s" from Day 3 into proper 400/404
 * responses with clear, actionable messages.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * A withdrawal broke a business rule (age restriction, balance,
     * 90% cap, or invalid amount) -> 400 Bad Request.
     */
    @ExceptionHandler(InvalidWithdrawalException.class)
    public ResponseEntity<ErrorResponse> handleInvalidWithdrawal(
            InvalidWithdrawalException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Requested investor/portfolio/withdrawal doesn't exist -> 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Bean Validation (@Valid) failures on request DTOs - e.g. a missing
     * portfolioId or a negative amount caught by annotations before the
     * request even reaches the service layer. Collects ALL field errors
     * into one readable message rather than just the first one.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Fallback for anything unexpected - still returns our clean JSON shape
     * instead of leaking a raw stack trace to the client, while the full
     * trace still prints in the server console for debugging.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
