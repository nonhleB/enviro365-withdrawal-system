package com.enviro.assessment.junior.nonhle.controller;

import com.enviro.assessment.junior.nonhle.dto.WithdrawalRequest;
import com.enviro.assessment.junior.nonhle.dto.WithdrawalResponse;
import com.enviro.assessment.junior.nonhle .service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    /**
     * POST /api/withdrawals
     * Creates a withdrawal notice. Runs all business rule validation
     * (age > 65 for retirement, balance checks, 90% cap) before saving -
     * see WithdrawalService for the actual rules.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WithdrawalResponse createWithdrawal(@RequestBody WithdrawalRequest request) {
        return withdrawalService.createWithdrawal(request);
    }

    /**
     * GET /api/withdrawals/portfolio/{portfolioId}
     * Powers the "withdrawal history table" on the frontend.
     */
    @GetMapping("/portfolio/{portfolioId}")
    public List<WithdrawalResponse> getHistory(@PathVariable Long portfolioId) {
        return withdrawalService.getHistoryForPortfolio(portfolioId);
    }
}
