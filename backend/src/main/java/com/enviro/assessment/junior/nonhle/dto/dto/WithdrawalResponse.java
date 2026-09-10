package com.enviro.assessment.junior.nonhle.dto;

import com.enviro.assessment.junior.nonhle.entity.WithdrawalNotice;
import com.enviro.assessment.junior.nonhle.entity.WithdrawalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * What the API returns after a withdrawal is created (or when listing
 * withdrawal history). Flattens the entity into something simple for the
 * frontend, and avoids serializing the full Portfolio -> Investor ->
 * Portfolios... circular relationship.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalResponse {

    private Long id;
    private Long portfolioId;
    private BigDecimal amount;
    private LocalDate noticeDate;
    private WithdrawalStatus status;
    private String rejectionReason;
    private BigDecimal resultingBalance;

    public static WithdrawalResponse from(WithdrawalNotice notice) {
        return new WithdrawalResponse(
                notice.getId(),
                notice.getPortfolio().getId(),
                notice.getAmount(),
                notice.getNoticeDate(),
                notice.getStatus(),
                notice.getRejectionReason(),
                notice.getResultingBalance()
        );
    }
}
