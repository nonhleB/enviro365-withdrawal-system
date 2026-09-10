package com.enviro.assessment.junior.nonhle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A request from an investor to withdraw money from a Portfolio.
 *
 * Business rules enforced on this entity (in the service layer, Day 3):
 *  - retirement product withdrawals require investor age > 65
 *  - amount must not exceed the portfolio balance
 *  - amount must not exceed 90% of the portfolio balance
 */
@Entity
@Table(name = "withdrawal_notices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate noticeDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WithdrawalStatus status;

    /**
     * Populated when status = REJECTED, explaining which rule failed.
     * Null/empty for PENDING or APPROVED notices.
     */
    private String rejectionReason;

    /**
     * Balance snapshot AFTER this withdrawal, calculated at creation time.
     * Storing this (rather than recalculating later) preserves an accurate
     * historical record even if the portfolio balance changes afterwards.
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal resultingBalance;
}
