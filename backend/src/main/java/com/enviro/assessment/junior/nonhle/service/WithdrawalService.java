package com.enviro.assessment.junior.nonhle.service;

import com.enviro.assessment.junior.nonhle.dto.WithdrawalRequest;
import com.enviro.assessment.junior.nonhle.dto.WithdrawalResponse;
import com.enviro.assessment.junior.nonhle.entity.Portfolio;
import com.enviro.assessment.junior.nonhle.entity.WithdrawalNotice;
import com.enviro.assessment.junior.nonhle.entity.WithdrawalStatus;
import com.enviro.assessment.junior.nonhle.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.nonhle.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.nonhle.repository.PortfolioRepository;
import com.enviro.assessment.junior.nonhle.repository.WithdrawalNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Owns the withdrawal business rules from the assessment brief:
 *  1. Retirement withdrawals only allowed if investor age > 65
 *  2. Withdrawal must not exceed the portfolio balance
 *  3. Withdrawal must not exceed 90% of the portfolio balance
 *
 * Design decision: invalid requests are REJECTED at the API boundary
 * (thrown as InvalidWithdrawalException -> 400 response with a clear
 * message), rather than silently saved as a REJECTED WithdrawalNotice.
 * This gives the user immediate, actionable feedback instead of a
 * "successful" API call that secretly failed business validation.
 */
@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private static final BigDecimal MAX_WITHDRAWAL_PERCENTAGE = new BigDecimal("0.90");
    private static final int MINIMUM_RETIREMENT_AGE = 65;

    private final WithdrawalNoticeRepository withdrawalNoticeRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional
    public WithdrawalResponse createWithdrawal(WithdrawalRequest request) {
        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found with id: " + request.getPortfolioId()));

        validateWithdrawal(portfolio, request.getAmount());

        BigDecimal newBalance = portfolio.getBalance().subtract(request.getAmount());

        WithdrawalNotice notice = WithdrawalNotice.builder()
                .portfolio(portfolio)
                .amount(request.getAmount())
                .noticeDate(LocalDate.now())
                .status(WithdrawalStatus.APPROVED)
                .resultingBalance(newBalance)
                .build();

        // Update the portfolio balance now that the withdrawal is approved.
        portfolio.setBalance(newBalance);
        portfolioRepository.save(portfolio);

        WithdrawalNotice saved = withdrawalNoticeRepository.save(notice);
        return WithdrawalResponse.from(saved);
    }

    /**
     * Runs all three business rules in turn. Throws on the FIRST failure
     * with a specific, actionable message - this is what becomes the
     * rejectionReason / error message shown to the user.
     */
    private void validateWithdrawal(Portfolio portfolio, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWithdrawalException("Withdrawal amount must be greater than zero.");
        }

        // Rule 1: retirement withdrawals only allowed if age > 65
        if (portfolio.getProduct().isRetirementProduct()) {
            int age = portfolio.getInvestor().getAge();
            if (age <= MINIMUM_RETIREMENT_AGE) {
                throw new InvalidWithdrawalException(
                        "Retirement withdrawals are only allowed for investors over age "
                                + MINIMUM_RETIREMENT_AGE + ". Investor is currently " + age + ".");
            }
        }

        // Rule 2: withdrawal must not exceed the available balance
        if (amount.compareTo(portfolio.getBalance()) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (" + amount + ") exceeds available balance ("
                            + portfolio.getBalance() + ").");
        }

        // Rule 3: withdrawal must not exceed 90% of the balance
        BigDecimal maxAllowed = portfolio.getBalance().multiply(MAX_WITHDRAWAL_PERCENTAGE);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (" + amount + ") exceeds the maximum allowed (90% of balance = "
                            + maxAllowed + ").");
        }
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponse> getHistoryForPortfolio(Long portfolioId) {
        return withdrawalNoticeRepository.findByPortfolioId(portfolioId).stream()
                .map(WithdrawalResponse::from)
                .toList();
    }
}
