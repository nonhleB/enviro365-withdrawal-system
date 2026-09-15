package com.enviro.assessment.junior.nonhle.exception;

/**
 * Thrown when a withdrawal request breaks a business rule (age restriction,
 * insufficient balance, exceeds 90% cap). The message becomes the
 * rejectionReason shown to the user.
 */
public class InvalidWithdrawalException extends RuntimeException {
    public InvalidWithdrawalException(String message) {
        super(message);
    }
}
