package com.enviro.assessment.junior.nonhle.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request body for POST /api/withdrawals.
 * Kept separate from the WithdrawalNotice entity so the API contract
 * doesn't leak internal fields (status, resultingBalance) that the
 * CLIENT shouldn't be setting - those are calculated server-side.
 *
 * Validation here catches malformed requests (missing/invalid fields)
 * BEFORE they reach the service layer's business rule checks - a
 * "shallow" check for well-formedness vs. the service layer's "deep"
 * business logic validation (age, balance, 90% cap).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {

    @NotNull(message = "portfolioId is required")
    private Long portfolioId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private BigDecimal amount;
}
