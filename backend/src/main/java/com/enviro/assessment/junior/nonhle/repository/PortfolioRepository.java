package com.enviro.assessment.junior.nonhle.repository;

import com.enviro.assessment.junior.nonhle.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    /**
     * All portfolios belonging to one investor - this is how we'll build
     * the "investor portfolio (details + products)" endpoint.
     */
    List<Portfolio> findByInvestorId(Long investorId);
}
