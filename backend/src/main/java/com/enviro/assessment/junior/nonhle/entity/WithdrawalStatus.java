package com.enviro.assessment.junior.nonhle.entity;

/**
 * Lifecycle status of a withdrawal notice.
 *
 * PENDING  - just created, not yet validated/processed
 * APPROVED - passed all business rule validation
 * REJECTED - failed validation (e.g. exceeded balance, age restriction)
 */
public enum WithdrawalStatus {
    PENDING,
    APPROVED,
    REJECTED
}
