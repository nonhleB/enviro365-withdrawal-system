package com.enviro.assessment.junior.nonhle.exception;

/**
 * Thrown when a requested Investor/Portfolio/WithdrawalNotice doesn't exist.
 * Handled generically for now (Spring's default error response); Day 4 adds
 * a @ControllerAdvice to turn this into a clean 404 JSON response.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
