package com.enviro.assessment.junior.nonhle.controller;

import com.enviro.assessment.junior.nonhle.dto.PortfolioResponse;
import com.enviro.assessment.junior.nonhle.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    /**
     * GET /api/portfolios/investor/{investorId}
     * Powers the "portfolio dashboard" - returns every portfolio (product +
     * balance) belonging to one investor.
     */
    @GetMapping("/investor/{investorId}")
    public List<PortfolioResponse> getPortfoliosByInvestor(@PathVariable Long investorId) {
        return portfolioService.getPortfoliosByInvestor(investorId);
    }
}
