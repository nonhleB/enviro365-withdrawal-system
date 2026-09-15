package com.enviro.assessment.junior.nonhle.service;

import com.enviro.assessment.junior.nonhle.dto.PortfolioResponse;
import com.enviro.assessment.junior.nonhle.entity.Portfolio;
import com.enviro.assessment.junior.nonhle.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.nonhle.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor // Lombok generates a constructor for the final field below,
                          // which Spring uses for constructor injection - no @Autowired needed.
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    /**
     * All portfolios for one investor - powers the "portfolio dashboard".
     * @Transactional keeps the Hibernate session open long enough to
     * safely read investor.getFullName() / product.getName() (lazy
     * associations) before we map to the DTO.
     */
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfoliosByInvestor(Long investorId) {
        List<Portfolio> portfolios = portfolioRepository.findByInvestorId(investorId);
        return portfolios.stream()
                .map(PortfolioResponse::from)
                .toList();
    }

    /**
     * A single portfolio by its own id - used internally by the withdrawal
     * flow, and could back a "portfolio detail" screen later.
     */
    @Transactional(readOnly = true)
    public Portfolio getPortfolioEntityById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found with id: " + portfolioId));
    }
}
