package com.enviro.assessment.junior.nonhle.dto;

import com.enviro.assessment.junior.nonhle.entity.Portfolio;
import com.enviro.assessment.junior.nonhle.entity.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * What the "portfolio dashboard" screen consumes: investor + product +
 * balance flattened into one flat object, safe to serialize (no circular
 * references back to Investor or WithdrawalNotice lists).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {

    private Long portfolioId;
    private Long investorId;
    private String investorName;
    private int investorAge;
    private String productName;
    private ProductType productType;
    private BigDecimal balance;

    public static PortfolioResponse from(Portfolio portfolio) {
        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getInvestor().getId(),
                portfolio.getInvestor().getFullName(),
                portfolio.getInvestor().getAge(),
                portfolio.getProduct().getName(),
                portfolio.getProduct().getType(),
                portfolio.getBalance()
        );
    }
}
