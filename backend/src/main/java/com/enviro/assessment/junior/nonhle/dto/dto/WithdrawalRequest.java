package com.enviro.assessment.junior.nonhle.dto;

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
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequest {

    private Long portfolioId;
    private BigDecimal amount;
}
