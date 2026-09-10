package com.enviro.assessment.junior.nonhle.repository;

import com.enviro.assessment.junior.nonhle.entity.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    /**
     * All withdrawal notices for one portfolio - powers the
     * "withdrawal history table" on the frontend.
     */
    List<WithdrawalNotice> findByPortfolioId(Long portfolioId);

    /**
     * All withdrawal notices for one investor (across all their portfolios) -
     * used for the CSV export with date-range filtering (Day 4).
     */
    List<WithdrawalNotice> findByPortfolioInvestorIdAndNoticeDateBetween(
            Long investorId, LocalDate from, LocalDate to);
}
